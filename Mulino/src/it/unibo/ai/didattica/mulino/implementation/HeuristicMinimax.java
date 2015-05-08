package it.unibo.ai.didattica.mulino.implementation;

import fr.avianey.minimax4j.Minimax;
import fr.avianey.minimax4j.Move;

import java.util.*;

public abstract class HeuristicMinimax<M extends Move, T extends Comparable<T>> extends Minimax<M> {

    private int maxDepth;
    private Map<Integer, List<Move>> killerMoves = new HashMap<>();
    private Map<T, TranspositionTableEntry> transpositionTable = new HashMap<>();
    private int currentDepth;
    private int nodesCount;
    private boolean useHeuristic;
    private boolean abort;

    private final Comparator<Move> KillerComparator = new Comparator<Move>() {

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

    private static final class MoveWrapper<M extends Move> {
        public M move;
    }

    private static enum ScoreType {
        LOWER_BOUND, UPPER_BOUND, EXACT_SCORE;
    }

    private static final class TranspositionTableEntry<M extends Move> {
        public double score;
        public M move;
        public ScoreType type;
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

    public boolean shouldAbort() {
        return this.abort;
    }

    public void setAbort(boolean abort) {
        this.abort = abort;
    }

    public M getBestMove(final int depth) {
        this.setAbort(false);

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

    public void purgeHeuristic(int minDepth) {
        /*
         * Se abbiamo delle killer moves da prima
         * shiftiamo la depth per riutilizzarle.
         */
        Map<Integer, List<Move>> killerMoves = new HashMap<>();
        for (Integer key : this.killerMoves.keySet()) {
            if (key >= minDepth) {
                killerMoves.put(key - minDepth, this.killerMoves.get(key));
            }
        }
        this.killerMoves = killerMoves;

        this.transpositionTable.clear();
    }

    private List<M> sortMoves(List<M> moves) {
        Collections.sort(moves, KillerComparator);

        return moves;
    }

    public abstract T getTransposition();

    public Collection<T> getSymetricTranspositions() {
        return Collections.singleton(getTransposition());
    }

    /*
     * Negascout
     * w/ Killer Heuristic
     */

    private double negascout(final MoveWrapper<M> wrapper, final int depth, final double alpha, final double beta) {
        this.nodesCount++;

        if (depth == 0 || isOver()) {
            return evaluate();
        }

        this.currentDepth = this.maxDepth - depth;
        if (this.killerMoves.get(this.currentDepth) == null) {
            this.killerMoves.put(this.currentDepth, new ArrayList<Move>());
        }

        List<M> moves = sortMoves(getPossibleMoves());

        double a = alpha;
        double b = beta;
        M bestMove = null;
        if (moves.isEmpty()) {
            next();
            double score = negascoutScore(true, depth, a, beta, b);
            previous();
            return score;
        }

        double score;
        boolean first = true;
        for (M move : moves) {
            makeMove(move);
            score = negascoutScore(first, depth, a, beta, b);
            unmakeMove(move);
            if (score > a) {
                a = score;
                bestMove = move;

                List<Move> currentDepthKillerMoves = this.killerMoves.get(this.currentDepth);
                if (!this.abort && !currentDepthKillerMoves.contains(move)) {
                    if (currentDepthKillerMoves.size() >= 2) {
                        currentDepthKillerMoves.remove(1);
                    }
                    currentDepthKillerMoves.add(0, move);
                }

                if (a >= beta) {
                    break;
                }
            }
            b = a + 1;
            first = false;
        }
        if (wrapper != null) {
            wrapper.move = bestMove;
        }

        TranspositionTableEntry entry = new TranspositionTableEntry();
        entry.move = bestMove;
        entry.score = a;
        entry.type = (a <= alpha ?  ScoreType.LOWER_BOUND :
                     (b >= beta  ?  ScoreType.UPPER_BOUND :
                                    ScoreType.EXACT_SCORE));

        return a;
    }

    @Override
    protected double minimaxScore(int depth, int who) {
        if (this.abort) {
            return this.maxEvaluateValue();
        } else {
            return super.minimaxScore(depth, who);
        }
    }

    @Override
    protected double alphabetaScore(int depth, int who, double alpha, double beta) {
        if (this.abort) {
            return this.maxEvaluateValue();
        } else {
            return super.alphabetaScore(depth, who, alpha, beta);
        }
    }

    @Override
    protected double negamaxScore(int depth, double alpha, double beta) {
        if (this.abort) {
            return this.maxEvaluateValue();
        } else {
            return super.negamaxScore(depth, alpha, beta);
        }
    }

    @Override
    protected double negascoutScore(final boolean first, final int depth, final double alpha, final double beta, final double b) {
        if (this.abort) {
            return this.maxEvaluateValue();
        }

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

}
