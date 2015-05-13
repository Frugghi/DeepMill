package it.unibo.ai.didattica.mulino.client.players;

import it.unibo.ai.didattica.mulino.domain.MillMinimax;
import it.unibo.ai.didattica.mulino.domain.MillMove;

public class EvaluatorPlayer<IA extends MillMinimax<M, ?, IA>, M extends MillMove<M>> extends Player {

    private IA ia;

    public EvaluatorPlayer(it.unibo.ai.didattica.mulino.domain.State.Checker color, IA ia) {
        super(color);

        this.ia = ia;
    }

    @Override
    protected void initState() {
        try {
            currentState = client.read();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ia = ia.fromState(this.currentState);

        if (color == it.unibo.ai.didattica.mulino.domain.State.Checker.WHITE) {
            ia.previous();
        }

        System.out.println(ia.toString());
        System.out.println("Total score: " + ia.evaluate());

        if (color != it.unibo.ai.didattica.mulino.domain.State.Checker.WHITE) {
            System.exit(0);
        }
    }

    @Override
    protected String doMove() {
        return "DONE";
    }

    @Override
    public String toString() {
        return "Board Evaluator";
    }

}
