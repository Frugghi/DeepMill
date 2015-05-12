package it.unibo.ai.didattica.mulino.minimax;

import fr.avianey.minimax4j.Move;

public interface InvertibleMove<M> extends Move {

    M inverse();

}
