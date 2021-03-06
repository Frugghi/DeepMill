package it.unibo.ai.didattica.mulino.DeepMill.client.players;

import it.unibo.ai.didattica.mulino.DeepMill.domain.MillMinimax;
import it.unibo.ai.didattica.mulino.DeepMill.domain.MillMove;

public class IAPlayer<IA extends MillMinimax<M, ?, IA>, M extends MillMove<M>> extends Player {

    private IA ia;
    private int depth;
    private int maxTime;
    private boolean debug;

    public IAPlayer(it.unibo.ai.didattica.mulino.domain.State.Checker color, IA ia, int depth, int maxTime) {
        super(color);

        this.depth = depth;
        this.maxTime = maxTime;
        this.ia = ia;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    @Override
    protected void initState() {
        super.initState();

        ia = ia.fromState(this.currentState);
        ia.previous();
    }

    @Override
    protected void nextMove() {
        super.nextMove();

        IA state = ia.fromState(this.currentState);
        if (debug) System.out.println("DEEPMILL DEBUG:\nOld state:\n" + ia);

        for(M m : ia.getPossibleMoves()){
            ia.makeMove(m);
            if (state.equals(ia)) {
                break;
            }
            ia.unmakeMove(m);
        }

        if (!state.equals(ia)) {
            System.err.println("Cannot find the move...");
            ia = state;
        }

        if (debug) System.out.println("DEEPMILL DEBUG:\nConverted state:\n" + ia);
    }

    @Override
    protected String doMove() {
        String actionString;
        long startTime = System.currentTimeMillis();
        M move = ia.getBestMove(depth, 1000 * maxTime);
        System.out.println("Time elapsed: " + (System.currentTimeMillis() - startTime) + "ms");

        if (debug) System.out.println("DEEPMILL DEBUG: " + move);
        if (move == null) {
            actionString = "GGWP";
        } else {
            ia.makeMove(move);
            actionString = move.toStringMove();
        }

        System.out.println(actionString);
        if (debug) System.out.println("DEEPMILL DEBUG: \n" + ia);

        return actionString;
    }

    @Override
    public String toString() {
        return "DeepMill";
    }
}
