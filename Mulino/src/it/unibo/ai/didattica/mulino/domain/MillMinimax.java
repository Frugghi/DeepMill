package it.unibo.ai.didattica.mulino.domain;

import it.unibo.ai.didattica.mulino.implementation.IterativeDeepeningMinimax;

public abstract class MillMinimax<M extends MillMove, T extends Comparable<T>> extends IterativeDeepeningMinimax<M, T> {

    public static final int PIECES = 9;

    public MillMinimax(Algorithm algo, boolean useHeuristic) {
        super(algo, useHeuristic);
    }

    protected void updateState(State state) {
        for (String position : state.getPositions()) {
            this.setGridPosition(state.getBoard().get(position), position);
        }

        this.setCount(state.getWhiteCheckersOnBoard(), state.getBlackCheckersOnBoard());
        this.setPlayed(PIECES - state.getWhiteCheckers(), PIECES - state.getBlackCheckers());
    }

    public abstract MillMinimax fromState(State state);
    protected abstract void setPlayed(int white, int black);
    protected abstract void setCount(int white, int black);
    protected abstract void setGridPosition(State.Checker player, String position);

}
