package it.unibo.ai.didattica.mulino.DeepMill.domain;

import it.unibo.ai.didattica.mulino.DeepMill.minimax.InvertibleMove;

public interface MillMove<M> extends InvertibleMove<M> {

    String toStringMove();
    boolean isPutMove();
    boolean isRemoveMove();

}