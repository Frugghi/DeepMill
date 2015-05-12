package it.unibo.ai.didattica.mulino.client.players;

import java.io.*;

public class ReplayPlayer extends Player {

    private BufferedReader in;
    private int moveTime;

    public ReplayPlayer(it.unibo.ai.didattica.mulino.domain.State.Checker color, String replay, int moveTime) {
        super(color);

        this.moveTime = moveTime;
        try {
            this.in = new BufferedReader(new FileReader(replay));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String doMove() {
        try {
            try {
                sleep(this.moveTime * 1000);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }

            String actionString;
            while ((actionString = in.readLine()) != null) {
                if (actionString.startsWith("Player " + this.client.getPlayer().toString() + " move: ")) {
                    break;
                }
            }
            if (actionString != null) {
                actionString = actionString.substring(("Player " + this.client.getPlayer().toString() + " move: ").length());
            }

            return actionString;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String toString() {
        return "Replay player";
    }
}
