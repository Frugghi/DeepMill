package it.unibo.ai.didattica.mulino.domain;

import it.unibo.ai.didattica.mulino.minimax.InvertibleMove;

public interface MillMove<M> extends InvertibleMove<M> {

    String toStringMove();
    boolean isPutMove();
    boolean isRemoveMove();

}