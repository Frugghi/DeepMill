package it.unibo.ai.didattica.mulino.domain;

import fr.avianey.minimax4j.Minimax;

public abstract class MillMinimax<T extends MillMove> extends Minimax<T> {

    public static final int PIECES = 9;

    public abstract void setPlayed(int white, int black);
    public abstract void setCount(int white, int black);
    public abstract void setGridPosition(State.Checker player, String position);

    public MillMinimax(Algorithm algo) {
        super(algo);
    }

    public void updateState(State state) {
        for (String position : state.getPositions()) {
            this.setGridPosition(state.getBoard().get(position), position);
        }

        this.setCount(state.getWhiteCheckersOnBoard(), state.getBlackCheckersOnBoard());
        this.setPlayed(MillMinimax.PIECES - state.getWhiteCheckers(), MillMinimax.PIECES - state.getBlackCheckers());
    }

}