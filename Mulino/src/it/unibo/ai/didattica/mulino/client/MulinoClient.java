package it.unibo.ai.didattica.mulino.client;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import fr.avianey.minimax4j.Minimax;
import it.unibo.ai.didattica.mulino.actions.Action;
import it.unibo.ai.didattica.mulino.actions.Phase1Action;
import it.unibo.ai.didattica.mulino.actions.Phase2Action;
import it.unibo.ai.didattica.mulino.actions.PhaseFinalAction;
import it.unibo.ai.didattica.mulino.domain.MillMinimax;
import it.unibo.ai.didattica.mulino.domain.MillMove;
import it.unibo.ai.didattica.mulino.implementation.GridMinimax;
import it.unibo.ai.didattica.mulino.implementation.GridMove;
import it.unibo.ai.didattica.mulino.domain.State;
import it.unibo.ai.didattica.mulino.engine.TCPMulino;

public class MulinoClient {

    public enum Player {
        HUMAN ("Human"),
        IA ("DeepMill");
        private final String name;
        private Player(String s) { name = s; }
        public boolean equalsName(String otherName){ return (otherName == null)? false:name.equals(otherName); }
        public String toString(){ return name; }
    }

    private State.Checker player;
    private Socket playerSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public MulinoClient(State.Checker player) throws UnknownHostException, IOException {
        this.player = player;
        int port = 0;
        switch (player) {
            case WHITE:
                port = TCPMulino.whiteSocket;
                break;
            case BLACK:
                port = TCPMulino.blackSocket;
                break;
            default:
                System.exit(5);
        }
        playerSocket = new Socket("localhost", port);
        out = new ObjectOutputStream(playerSocket.getOutputStream());
        in = new ObjectInputStream(new BufferedInputStream(playerSocket.getInputStream()));
    }

    public void write(Action action) throws IOException, ClassNotFoundException {
        out.writeObject(action);
    }

    public State read() throws ClassNotFoundException, IOException {
        return (State) in.readObject();
    }

    public State.Checker getPlayer() { return player; }
    public void setPlayer(State.Checker player) { this.player = player; }

    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {

        if (args.length == 0) {
            System.out.println("You must specify which player you are (Wthie or Black)!");
            System.exit(-1);
        }
        System.out.print("Selected client:");
        for (String arg : args) {
            System.out.print(" " + arg);
        }
        System.out.println();

        State.Checker playerColor = "White".equalsIgnoreCase(args[0]) ? State.Checker.WHITE : State.Checker.BLACK;
        Player player = Player.IA;
        if (args.length > 1) {
            player = "Human".equalsIgnoreCase(args[1]) ? Player.HUMAN : Player.IA;
        }

        Minimax.Algorithm algorithm = Minimax.Algorithm.NEGASCOUT;
        if (args.length > 2) {
            switch (args[2].toLowerCase()) {
                case "minimax":
                    algorithm = Minimax.Algorithm.MINIMAX;
                    break;
                case "alpha_beta":
                    algorithm = Minimax.Algorithm.ALPHA_BETA;
                    break;
                case "negamax":
                    algorithm = Minimax.Algorithm.NEGAMAX;
                    break;
                case "negascout":
                    algorithm = Minimax.Algorithm.NEGASCOUT;
                    break;
            }
        }

        boolean myTurn = playerColor == State.Checker.WHITE;
        String actionString = "";
        Action action = null;
        State currentState = null;
        BufferedReader in = new BufferedReader( new InputStreamReader(System.in));

        MillMinimax ia = new GridMinimax(algorithm);
        MillMove move;
        int depth = 10;

        MulinoClient client = new MulinoClient(playerColor);
        System.out.println("Hello " + player.toString() + "!");
        System.out.println("You are player " + client.getPlayer().toString() + "!");
        System.out.println("Current state:");
        currentState = client.read();
        System.out.println(currentState.toString());

        while (true) {
            if (!myTurn) {
                System.out.println("Waiting for your opponent move...");
                currentState = client.read();
                System.out.println("Your Opponent did his move, and the result is: ");
                System.out.println(currentState.toString());

                if (player == Player.IA) {
                    for (String position : currentState.getPositions()) {
                        ia.setGridPosition(currentState.getBoard().get(position), position);
                    }

                    ia.setCount(currentState.getWhiteCheckersOnBoard(), currentState.getBlackCheckersOnBoard());
                    ia.setPlayed(MillMinimax.PIECES - currentState.getWhiteCheckers(), MillMinimax.PIECES - currentState.getBlackCheckers());

                    ia.next();

                    System.out.println("DEEPMILL DEBUG: \n" + ia.toString());
                }
            }
            System.out.println("Player " + client.getPlayer().toString() + ", do your move: ");
            switch (player) {
                case IA:
                    move = (MillMove)ia.getBestMove(depth);
                    System.out.println("DEEPMILL DEBUG: " + move.toString());
                    if (move == null) {
                        actionString = "GGWP";
                    } else {
                        ia.makeMove(move);
                        actionString = move.toStringMove();
                    }

                    System.out.println(actionString);
                    System.out.println("DEEPMILL DEBUG: \n" + ia.toString());
                    break;
                case HUMAN:
                    actionString = in.readLine();
                    break;
            }

            switch (currentState.getCurrentPhase()) {
                case FIRST:
                    Phase1Action phase1Action = new Phase1Action();
                    phase1Action.setPutPosition(actionString.substring(0, 2));
                    if (actionString.length() == 4) {
                        phase1Action.setRemoveOpponentChecker(actionString.substring(2, 4));
                    } else {
                        phase1Action.setRemoveOpponentChecker(null);
                    }
                    action = phase1Action;
                    break;
                case SECOND:
                    Phase2Action phase2Action = new Phase2Action();
                    phase2Action.setFrom(actionString.substring(0, 2));
                    phase2Action.setTo(actionString.substring(2, 4));
                    if (actionString.length() == 6) {
                        phase2Action.setRemoveOpponentChecker(actionString.substring(4, 6));
                    } else {
                        phase2Action.setRemoveOpponentChecker(null);
                    }
                    action = phase2Action;
                    break;
                case FINAL:
                    PhaseFinalAction phaseFinalAction = new PhaseFinalAction();
                    phaseFinalAction.setFrom(actionString.substring(0, 2));
                    phaseFinalAction.setTo(actionString.substring(2, 4));
                    if (actionString.length() == 6) {
                        phaseFinalAction.setRemoveOpponentChecker(actionString.substring(4, 6));
                    } else {
                        phaseFinalAction.setRemoveOpponentChecker(null);
                    }
                    action = phaseFinalAction;
                    break;
            }
            client.write(action);
            currentState = client.read();
            System.out.println("Effect of your move: ");
            System.out.println(currentState.toString());
            myTurn = false;
        }
    }

}
