package it.unibo.ai.didattica.mulino.implementation;

import fr.avianey.minimax4j.Minimax;
import fr.avianey.minimax4j.Move;

import java.util.List;

public abstract class IterativeDeepeningMinimax<M extends Move> extends Minimax<M> {

    protected int depth;
    private boolean abort = false;

    @Override
    public M getBestMove(int maxDepth) {
        M bestMove = null;
        for (this.depth = 1; this.depth != maxDepth && !this.abort; this.depth++) {
            M move = super.getBestMove(this.depth);

            if (!this.abort) {
                bestMove = move;
            }
        }

        this.abort = false;
        return bestMove;
    }

    @Override
    protected double minimaxScore(int depth, int who) {
        double score = super.minimaxScore(this.abort ? 0 : depth, who);

        // salvare lo score + depth bla bla

        return score;
    }

    @Override
    protected double alphabetaScore(int depth, int who, double alpha, double beta) {
        double score = super.alphabetaScore(this.abort ? 0 : depth, who, alpha, beta);

        // salvare lo score + depth bla bla

        return score;
    }

    @Override
    protected double negamaxScore(int depth, double alpha, double beta) {
        double score = super.negamaxScore(this.abort ? 0 : depth, alpha, beta);

        // salvare lo score + depth bla bla

        return score;
    }

    @Override
    protected double negascoutScore(boolean first, int depth, double alpha, double beta, double b) {
        double score = super.negascoutScore(first, this.abort ? 0 : depth, alpha, beta, b);

        // salvare lo score + depth bla bla

        return score;
    }

    public List<M> getOrderedPossibleMoves(List<M> moves) {

        // ordinare le mosse secondo i punteggi salvati

        return moves;
    }

    public void terminate() {
        this.abort = true;
    }

}
