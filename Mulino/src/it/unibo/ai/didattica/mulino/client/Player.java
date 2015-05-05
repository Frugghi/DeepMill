package it.unibo.ai.didattica.mulino.client;

import fr.avianey.minimax4j.Minimax;
import it.unibo.ai.didattica.mulino.domain.MillMinimax;
import it.unibo.ai.didattica.mulino.domain.MillMove;
import it.unibo.ai.didattica.mulino.implementation.BitboardMinimax;
import it.unibo.ai.didattica.mulino.implementation.GridMinimax;
import it.unibo.ai.didattica.mulino.implementation.IterativeDeepening;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Player extends Thread {

    public enum Behaviour {
        HUMAN ("Human"),
        IA ("DeepMill");
        private final String name;
        private Behaviour(String s) { name = s; }
        public boolean equalsName(String otherName){ return (otherName == null)? false:name.equals(otherName); }
        public String toString(){ return name; }
    }

    private Behaviour behaviour;
    private it.unibo.ai.didattica.mulino.domain.State.Checker color;
    private MillMinimax ia;
    private int depth;
    private int maxTime;
    private boolean debug;

    public Player(Behaviour behaviour, it.unibo.ai.didattica.mulino.domain.State.Checker color, MillMinimax ia, int depth, int maxTime, boolean debug) {
        this.behaviour = behaviour;
        this.color = color;
        this.ia = ia;
        this.depth = depth;
        this.maxTime = maxTime;
        this.debug = debug;
    }

    public void run() {
        boolean myTurn = (color == it.unibo.ai.didattica.mulino.domain.State.Checker.WHITE);
        String actionString = "";
        it.unibo.ai.didattica.mulino.domain.State currentState = null;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        MillMove move;

        MulinoClient client = null;
        try {
            client = new MulinoClient(color);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        System.out.println("Hello " + behaviour.toString() + "!");
        if (behaviour == Behaviour.IA && debug) {
            System.out.println("Algorithm: " + ia.getAlgo() + ", Depth: " + depth);
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
                    ia.updateState(currentState);
                    ia.next();

                    if (debug) System.out.println("DEEPMILL DEBUG: \n" + ia.toString());
                }
            }

            System.out.println("Player " + client.getPlayer().toString() + ", do your move: ");
            switch (behaviour) {
                case IA:

                    if (depth == 0) {
                        IterativeDeepening iterativeDeepening = new IterativeDeepening(ia);
                        iterativeDeepening.setDebug(debug);
                        iterativeDeepening.start();

                        try {
                            Thread.sleep(this.maxTime * 1000);
                        } catch (InterruptedException ie) {
                            ie.printStackTrace();
                        }

                        iterativeDeepening.terminate();

                        move = iterativeDeepening.getBestMove();

                    } else {
                        move = (MillMove)ia.getBestMove(depth);
                    }

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
