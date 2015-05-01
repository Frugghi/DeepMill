package it.unibo.ai.didattica.mulino.client;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import fr.avianey.minimax4j.Minimax;
import it.unibo.ai.didattica.mulino.actions.Action;
import it.unibo.ai.didattica.mulino.actions.Phase1Action;
import it.unibo.ai.didattica.mulino.actions.Phase2Action;
import it.unibo.ai.didattica.mulino.actions.PhaseFinalAction;
import it.unibo.ai.didattica.mulino.domain.State;
import it.unibo.ai.didattica.mulino.engine.TCPMulino;

public class MulinoClient {

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

    public void write(String actionString, State.Phase phase) throws IOException, ClassNotFoundException {
        Action action = null;
        switch (phase) {
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
        this.write(action);
    }

    public State read() throws ClassNotFoundException, IOException {
        return (State) in.readObject();
    }

    public State.Checker getPlayer() { return player; }
    public void setPlayer(State.Checker player) { this.player = player; }

    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("Usage:");
            System.out.println("-w --white     Set the player to White");
            System.out.println("-b --black     Set the player to Black");
            System.out.println("-h --human     Set the player as Human");
            System.out.println("-i --ia        Set the player as IA");
            System.out.println("-a <algorithm> Set the IA algorithm: minimax, alpha_beta, negamax, negascout");
            System.out.println("-d <depth>     Set the IA algorithm's search depth (override iterative deepening)");
            System.out.println("-t <time>      Search w/ iterative deepening for <time> seconds");
            System.exit(-1);
        }

        Player.Behaviour behaviour = Player.Behaviour.IA;
        State.Checker playerColor = State.Checker.WHITE;
        Minimax.Algorithm algorithm = Minimax.Algorithm.NEGASCOUT;
        int depth = 0;
        int maxTime = 58;
        for (int i = 0; i < args.length; i++) {
            switch (args[i].toLowerCase()) {
                case "-w":
                case "--white":
                    playerColor = State.Checker.WHITE;
                    break;
                case "-b":
                case "--black":
                    playerColor = State.Checker.BLACK;
                    break;
                case "-h":
                case "--human":
                    behaviour = Player.Behaviour.HUMAN;
                    break;
                case "-i":
                case "--ia":
                    behaviour = Player.Behaviour.IA;
                    break;
                case "-a":
                    switch (args[++i].toLowerCase()) {
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
                    break;
                case "-d":
                    depth = Integer.parseInt(args[++i]);
                    break;
                case "-t":
                    maxTime = Integer.parseInt(args[++i]);
                    break;
            }
        }

        Player player = new Player(behaviour, playerColor, algorithm, depth, maxTime);
        player.start();
    }

}