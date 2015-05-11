package it.unibo.ai.didattica.mulino.implementation;

import fr.avianey.minimax4j.Move;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class IterativeDeepeningMinimax<M extends Move, T extends Comparable<T>> extends HeuristicMinimax<M, T> {

    protected int depth;

    public IterativeDeepeningMinimax(Algorithm algo, boolean useHeuristic) {
        super(algo, useHeuristic);
    }

    public M getBestMove(final int maxDepth, final int maxTime) {
        Thread timeout = new Thread() {
            public void run() {
                try {
                    Thread.sleep(maxTime);
                    IterativeDeepeningMinimax.this.setAbort(true);
                } catch (InterruptedException e) {
                }
            }
        };
        timeout.start();

        M bestMove = this.getBestMove(maxDepth);

        if (timeout.isAlive()) {
            System.out.println("Timeout thread is alive!");
            timeout.interrupt();
        }
        System.out.println("Reached depth: " + (this.depth - 1));

        return bestMove;
    }

    @Override
    public M getBestMove(final int maxDepth) {
        this.purgeHeuristic(2);

        long time = System.currentTimeMillis();

        M bestMove = null;
        for (this.depth = 1; this.depth != maxDepth && !this.shouldAbort(); this.depth++) {
            System.out.print("Depth " + this.depth);

            M move = super.getBestMove(this.depth);

            if (move == null) {
                System.err.println("Best move is null at depth " + this.depth);
                this.purgeHeuristic(0);
                move = super.getBestMove(this.depth);
            }

            if (!this.shouldAbort() && move != null) {
                bestMove = move;

                this.makeMove(bestMove);
                this.setAbort(this.isOver());
                this.unmakeMove(bestMove);

                System.out.print("... done! " + (System.currentTimeMillis() - time) + "ms ");
                this.printStatistics();
            } else {
                System.out.println("... aborted!");
            }
        }

        this.setAbort(false);
        return bestMove;
    }

}
