package it.unibo.ai.didattica.mulino.implementation;

import it.unibo.ai.didattica.mulino.domain.MillMinimax;
import it.unibo.ai.didattica.mulino.domain.MillMove;

public class IterativeDeepening extends Thread {

    private static int turn = 0;

    private MillMove move;
    private MillMinimax<MillMove> state;
    private int minDepth;
    private boolean debug;

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

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void run()
    {
        int depth =  minDepth;
        while (!Thread.interrupted()) {
            MillMove bestMove = state.getBestMove(depth);

			if (bestMove != null) {
                move = bestMove;

                state.makeMove(bestMove);

                if (debug) System.out.println(this.getName() + " - DEBUGMILL: Depth " + depth + ", Best move " + bestMove.toString() + "\n" + state.toString());

                state.unmakeMove(bestMove);

                depth++;
            }
        }

        if (debug) System.out.println(this.getName() + " terminating...");
    }

    public void terminate() {
        this.interrupt();
        state.abort = true;
    }

}
