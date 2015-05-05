package it.unibo.ai.didattica.mulino.domain;

import fr.avianey.minimax4j.Move;

public interface MillMove extends Move {

    String toStringMove();
    boolean isPutMove();
    boolean isRemoveMove();

}