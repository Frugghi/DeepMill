package it.unibo.ai.didattica.mulino.domain;

import fr.avianey.minimax4j.Move;

public abstract class MillMove implements Move {

    public abstract String toStringMove();
    public abstract boolean isPutMove();
    public abstract boolean isRemoveMove();

}