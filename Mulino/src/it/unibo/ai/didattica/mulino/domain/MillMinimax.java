package it.unibo.ai.didattica.mulino.domain;

public interface MillMinimax<M extends MillMove> {

    void setPlayed(int white, int black);
    void setCount(int white, int black);
    void setGridPosition(State.Checker player, String position);
    int maxPlayedPieces();

    void next();
    void makeMove(M move);
    void unmakeMove(M move);
    M getBestMove(int depth, int maxTime);

    Object cloneState();

}