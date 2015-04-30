package it.unibo.ai.didattica.mulino.client;

import fr.avianey.minimax4j.Minimax;
import it.unibo.ai.didattica.mulino.domain.MillMinimax;
import it.unibo.ai.didattica.mulino.domain.MillMove;
import it.unibo.ai.didattica.mulino.domain.State;
import it.unibo.ai.didattica.mulino.implementation.GridMinimax;

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
    private Minimax.Algorithm algorithm;
    private int depth;
    private MulinoClient client;

    public Player(Behaviour behaviour, it.unibo.ai.didattica.mulino.domain.State.Checker color, Minimax.Algorithm algorithm, int depth) {
        this.behaviour = behaviour;
        this.color = color;
        this.algorithm = algorithm;
        this.depth = depth;
    }

    public void run() {
        boolean myTurn = (color == it.unibo.ai.didattica.mulino.domain.State.Checker.WHITE);
        String actionString = "";
        it.unibo.ai.didattica.mulino.domain.State currentState = null;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        MillMinimax ia = new GridMinimax(algorithm);
        MillMove move;

        MulinoClient client = null;
        try {
            client = new MulinoClient(color);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        System.out.println("Hello " + behaviour.toString() + "!");
        if (behaviour == Behaviour.IA) {
            System.out.println("Algorithm: " + algorithm + ", Depth: " + depth);
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

                    System.out.println("DEEPMILL DEBUG: \n" + ia.toString());
                }
            }

            System.out.println("Player " + client.getPlayer().toString() + ", do your move: ");
            switch (behaviour) {
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
