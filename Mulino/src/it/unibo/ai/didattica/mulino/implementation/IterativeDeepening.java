package it.unibo.ai.didattica.mulino.implementation;

import it.unibo.ai.didattica.mulino.domain.MillMinimax;
import it.unibo.ai.didattica.mulino.domain.MillMove;

public class IterativeDeepening extends Thread {

    private static int turn = 0;

    private MillMove move;
    private MillMinimax<MillMove> state;
    private int minDepth;

    public IterativeDeepening(MillMinimax<MillMove> state) {
        this(state, 1);
    }

    public IterativeDeepening(MillMinimax<MillMove> state, int minDepth) {
        super("Thread turn " + (++turn));
        this.state = state.cloneState();
        this.minDepth = minDepth;
    }

    public MillMove getBestMove() {
        return move;
    }

    public void run()
    {
        int depth =  minDepth;
        while (!Thread.interrupted()) {
            move = (MillMove)state.getBestMove(depth);
            state.makeMove(move);

            System.out.println(this.getName() + " - DEBUGMILL: Depth " + depth + ", Best move " + move.toString() + "\n" + state.toString());

            state.unmakeMove(move);

            depth++;
        }

        System.out.println(this.getName() + " terminating...");
    }

}
