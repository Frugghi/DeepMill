package it.unibo.ai.didattica.mulino.client;

import fr.avianey.minimax4j.Minimax;
import it.unibo.ai.didattica.mulino.domain.MillMinimax;
import it.unibo.ai.didattica.mulino.domain.MillMove;
import it.unibo.ai.didattica.mulino.domain.State;

import java.io.*;

public abstract class Player extends Thread {

    private it.unibo.ai.didattica.mulino.domain.State.Checker color;
    protected MulinoClient client;
    protected it.unibo.ai.didattica.mulino.domain.State currentState;

    public Player(it.unibo.ai.didattica.mulino.domain.State.Checker color) {
        this.color = color;
    }

    public void run() {
        boolean myTurn = (color == it.unibo.ai.didattica.mulino.domain.State.Checker.WHITE);
        String actionString = "";

        try {
            client = new MulinoClient(color);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        System.out.println("Hello " + this.toString() + "!");
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
                this.updateState();
            }

            System.out.println("Player " + client.getPlayer().toString() + ", do your move: ");
            actionString = this.doMove();

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

            System.gc();
        }
    }

    protected void updateState() {
        System.out.println("Waiting for your opponent move...");
        try {
            this.currentState = this.client.read();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        System.out.println("Your Opponent did his move, and the result is:\n" + this.currentState.toString());
    }

    protected abstract String doMove();

}
