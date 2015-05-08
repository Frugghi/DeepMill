package it.unibo.ai.didattica.mulino.client;

import fr.avianey.minimax4j.Minimax;
import it.unibo.ai.didattica.mulino.domain.MillMinimax;
import it.unibo.ai.didattica.mulino.domain.MillMove;

import java.io.*;

public class Player extends Thread {

    public enum Behaviour {
        HUMAN ("Human"),
        IA ("DeepMill"),
        REPLAY ("Replay");
        private final String name;
        Behaviour(String s) { name = s; }
        public String toString(){ return name; }
    }

    private Behaviour behaviour;
    private it.unibo.ai.didattica.mulino.domain.State.Checker color;
    private MillMinimax ia;
    private int depth;
    private int maxTime;
    private boolean debug;
    private String replay;

    public Player(it.unibo.ai.didattica.mulino.domain.State.Checker color) {
        this.behaviour = Behaviour.HUMAN;
        this.color = color;
    }

    public Player(it.unibo.ai.didattica.mulino.domain.State.Checker color, MillMinimax ia, int depth, int maxTime) {
        this.behaviour = Behaviour.IA;
        this.color = color;
        this.ia = ia;
        this.depth = depth;
        this.maxTime = maxTime;
    }

    public Player(it.unibo.ai.didattica.mulino.domain.State.Checker color, String replay, int maxTime) {
        this.behaviour = Behaviour.REPLAY;
        this.color = color;
        this.replay = replay;
        this.maxTime = maxTime;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void run() {
        boolean myTurn = (color == it.unibo.ai.didattica.mulino.domain.State.Checker.WHITE);
        String actionString = "";
        it.unibo.ai.didattica.mulino.domain.State currentState;
        BufferedReader in;
        switch (behaviour) {
            case HUMAN:
                in = new BufferedReader(new InputStreamReader(System.in));
                break;
            case REPLAY:
                try {
                    in = new BufferedReader(new FileReader(replay));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return;
                }
                break;
            default:
                in = null;
                break;
        }

        MillMove move;

        MulinoClient client;
        try {
            client = new MulinoClient(color);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        System.out.println("Hello " + behaviour.toString() + "!");
        if (behaviour == Behaviour.IA && debug) {
            System.out.println("Algorithm: " + ((Minimax)ia).getAlgo() + ", Depth: " + depth);
        }
        System.out.println("You are player " + client.getPlayer().toString() + "!");
        System.out.println("Current state:");
        try {
            currentState = client.read();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        System.out.println(currentState.toString());

        while (true) {
            if (!myTurn) {
                System.out.println("Waiting for your opponent move...");
                try {
                    currentState = client.read();
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                System.out.println("Your Opponent did his move, and the result is:\n" + currentState.toString());

                if (behaviour == Behaviour.IA) {
                    for (String position : currentState.getPositions()) {
                        ia.setGridPosition(currentState.getBoard().get(position), position);
                    }

                    ia.setCount(currentState.getWhiteCheckersOnBoard(), currentState.getBlackCheckersOnBoard());
                    ia.setPlayed(ia.maxPlayedPieces() - currentState.getWhiteCheckers(), ia.maxPlayedPieces() - currentState.getBlackCheckers());
                    ia.next();

                    if (debug) System.out.println("DEEPMILL DEBUG: \n" + ia.toString());
                }
            }

            System.out.println("Player " + client.getPlayer().toString() + ", do your move: ");
            switch (behaviour) {
                case IA:
                    long startTime = System.currentTimeMillis();
                    move = ia.getBestMove(depth, 1000 * maxTime);
                    System.out.println("Time elapsed: " + (System.currentTimeMillis() - startTime) + "ms");

                    if (debug) System.out.println("DEEPMILL DEBUG: " + move.toString());
                    if (move == null) {
                        actionString = "GGWP";
                    } else {
                        ia.makeMove(move);
                        actionString = move.toStringMove();
                    }

                    System.out.println(actionString);
                    if (debug) System.out.println("DEEPMILL DEBUG: \n" + ia.toString());
                    break;
                case HUMAN:
                    try {
                        actionString = in.readLine();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                    break;
                case REPLAY:
                    try {
                        try {
                            Thread.sleep(this.maxTime * 1000);
                        } catch (InterruptedException ie) {
                            ie.printStackTrace();
                        }

                        while (!(actionString = in.readLine()).startsWith("Player " + client.getPlayer().toString() + " move: ")) {

                        }
                        actionString = actionString.substring(("Player " + client.getPlayer().toString() + " move: ").length());
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                    break;
            }
            try {
                client.write(actionString, currentState.getCurrentPhase());
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            try {
                currentState = client.read();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            System.out.println("Effect of your move: \n" + currentState.toString());

            myTurn = false;
        }
    }

}
