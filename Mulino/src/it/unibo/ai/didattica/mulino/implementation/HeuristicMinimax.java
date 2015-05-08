package it.unibo.ai.didattica.mulino.implementation;

import fr.avianey.minimax4j.Minimax;
import fr.avianey.minimax4j.Move;

import java.util.*;

public abstract class HeuristicMinimax<M extends Move> extends Minimax<M> {

    private int maxDepth;
    private Map<Integer, List<Move>> killerMoves = new HashMap<>();
    private int currentDepth;
    private int nodesCount;
    private boolean useHeuristic;

    final Comparator<Move> KillerComparator = new Comparator<Move>() {

        @Override
        public int compare(Move o1, Move o2) {
            List<Move> killerMoves = HeuristicMinimax.this.killerMoves.get(HeuristicMinimax.this.currentDepth);
            boolean o1isKillerMove = killerMoves.contains(o1);
            boolean o2isKillerMove = killerMoves.contains(o2);
            if (o1isKillerMove == true && o2isKillerMove == true) {
                return killerMoves.indexOf(o1) < killerMoves.indexOf(o2) ? -1 : 1;
            } else if (o1isKillerMove == false && o2isKillerMove == false) {
                return 0;
            } else if (o1isKillerMove) {
                return -1;
            } else {
                return 1;
            }
        }
    };

    static final class MoveWrapper<M extends Move> {
        public M move;
    }

    public HeuristicMinimax(Algorithm algo, boolean useHeuristic) {
        super(algo);

        this.useHeuristic = useHeuristic;
    }

    public int getNodesCount() {
        return this.nodesCount;
    }

    public boolean isUsingHeuristic() {
        return this.useHeuristic;
    }

    public void setUseHeuristic(boolean useHeuristic) {
        this.useHeuristic = useHeuristic;
    }

    public M getBestMove(final int depth) {
        if (depth <= 0) {
            throw new IllegalArgumentException("Search depth MUST be > 0");
        }

        this.nodesCount = 0;
        this.maxDepth = depth;

        if (!this.useHeuristic) {
            return super.getBestMove(depth);
        }

        MoveWrapper<M> wrapper = new MoveWrapper<>();
        switch (this.getAlgo()) {
            case NEGASCOUT:
                negascout(wrapper, depth, -maxEvaluateValue(), maxEvaluateValue());
                break;
            default:
                return super.getBestMove(depth);
        }

        return wrapper.move;
    }

    /*
     * Negascout
     * w/ Killer Heuristic
     */

    private double negascout(final MoveWrapper<M> wrapper, final int depth, double alpha, double beta) {
        this.nodesCount++;

        if (depth == 0 || isOver()) {
            return evaluate();
        }

        this.currentDepth = this.maxDepth - depth;
        if (this.killerMoves.get(this.currentDepth) == null) {
            this.killerMoves.put(this.currentDepth, new ArrayList<Move>());
        }

        List<M> moves = sortMoves(getPossibleMoves());

        double b = beta;
        M bestMove = null;
        if (moves.isEmpty()) {
            next();
            double score = negascoutScore(true, depth, alpha, beta, b);
            previous();
            return score;
        } else {
            double score;
            boolean first = true;
            for (M move : moves) {
                makeMove(move);
                score = negascoutScore(first, depth, alpha, beta, b);
                unmakeMove(move);
                if (score > alpha) {
                    alpha = score;
                    bestMove = move;

                    List<Move> currentDepthKillerMoves = this.killerMoves.get(this.currentDepth);
                    if (!currentDepthKillerMoves.contains(move)) {
                        if (currentDepthKillerMoves.size() >= 2) {
                            currentDepthKillerMoves.remove(1);
                        }
                        currentDepthKillerMoves.add(0, move);
                    }

                    if (alpha >= beta) {
                        break;
                    }
                }
                b = alpha + 1;
                first = false;
            }
            if (wrapper != null) {
                wrapper.move = bestMove;
            }
            return alpha;
        }
    }

    @Override
    protected double negascoutScore(final boolean first, final int depth, final double alpha, final double beta, final double b) {
        if (!this.useHeuristic) {
            return super.negascoutScore(first, depth, alpha, beta, b);
        }

        double score = -negascout(null, depth - 1, -b, -alpha);
        if (!first && alpha < score && score < beta) {
            // fails high... full re-search
            score = -negascout(null, depth - 1, -beta, -alpha);
        }
        return score;
    }

    private List<M> sortMoves(List<M> moves) {
        Collections.sort(moves, KillerComparator);

        return moves;
    }

}
