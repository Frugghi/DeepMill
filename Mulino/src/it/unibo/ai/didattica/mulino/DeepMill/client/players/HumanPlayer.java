package it.unibo.ai.didattica.mulino.DeepMill.client.players;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class HumanPlayer extends Player {

    private BufferedReader in;

    public HumanPlayer(it.unibo.ai.didattica.mulino.domain.State.Checker color) {
        super(color);

        this.in = new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    protected String doMove() {
        try {
            return in.readLine();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String toString() {
        return "Human";
    }
}
