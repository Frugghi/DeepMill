package it.unibo.ai.didattica.mulino.implementation;

import fr.avianey.minimax4j.Minimax;
import fr.avianey.minimax4j.Move;

import java.util.List;

public abstract class IterativeDeepeningMinimax<M extends Move> extends Minimax<M> {

    protected int depth;
    private boolean abort;

    public IterativeDeepeningMinimax(Algorithm algo) {
        super(algo);

        this.abort = false;
    }

    public M getBestMove(final int maxDepth, final int maxTime) {
        Thread timeout = new Thread(){
            public void run() {
                try {
                    Thread.sleep(maxTime);
                    IterativeDeepeningMinimax.this.abort = true;
                } catch (InterruptedException e) { }
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
        this.abort = false;

        M bestMove = null;
        for (this.depth = 1; this.depth != maxDepth && !this.abort; this.depth++) {
            System.out.print("Depth " + this.depth + "... done! " + this.abort);

            M move = super.getBestMove(this.depth);

            if (!this.abort) {
                bestMove = move;
                System.out.println("... done!");
            } else {
                System.out.println("... aborted!");
            }
        }

        this.abort = false;
        return bestMove;
    }

    @Override
    protected double minimaxScore(int depth, int who) {
        if (this.abort) {
            return this.maxEvaluateValue();
        } else {
            // salvare lo score + depth bla bla
            return super.minimaxScore(depth, who);
        }
    }

    @Override
    protected double alphabetaScore(int depth, int who, double alpha, double beta) {
        if (this.abort) {
            return this.maxEvaluateValue();
        } else {
            // salvare lo score + depth bla bla
            return super.alphabetaScore(depth, who, alpha, beta);
        }
    }

    @Override
    protected double negamaxScore(int depth, double alpha, double beta) {
        if (this.abort) {
            return this.maxEvaluateValue();
        } else {
            // salvare lo score + depth bla bla
            return super.negamaxScore(depth, alpha, beta);
        }
    }

    @Override
    protected double negascoutScore(boolean first, int depth, double alpha, double beta, double b) {
        if (this.abort) {
            return this.maxEvaluateValue();
        } else {
            // salvare lo score + depth bla bla
            return super.negascoutScore(first, depth, alpha, beta, b);
        }
    }

    public List<M> getOrderedPossibleMoves(List<M> moves) {

        // ordinare le mosse secondo i punteggi salvati

        return moves;
    }

}
