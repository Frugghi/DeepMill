package it.unibo.ai.didattica.mulino.minimax;

import fr.avianey.minimax4j.Minimax;
import fr.avianey.minimax4j.Move;

import java.util.*;

public abstract class HeuristicMinimax<M extends InvertibleMove<M>, T extends Comparable<T>> extends Minimax<M> {

    private int maxDepth;
    private Map<Integer, List<Move>> killerMoves = new HashMap<>();
    private TranspositionTable<T, M> transpositionTable = new TranspositionTable<>();
    private int currentDepth;
    private int nodesCount;
    private int quiescenceNodesCount;
    private boolean useHeuristic;
    private boolean abort;
    protected List<M> movesHistory = new ArrayList<> ();

    private final Comparator<Move> KillerComparator = new Comparator<Move>() {
        @Override
        public int compare(Move o1, Move o2) {
            List<Move> killerMoves = HeuristicMinimax.this.killerMoves.get(HeuristicMinimax.this.currentDepth);
            boolean o1isKillerMove = killerMoves.contains(o1);
            boolean o2isKillerMove = killerMoves.contains(o2);
            if (o1isKillerMove && o2isKillerMove) {
                return killerMoves.indexOf(o1) < killerMoves.indexOf(o2) ? -1 : 1;
            } else if (!o1isKillerMove && !o2isKillerMove) {
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

    public HeuristicMinimax(Algorithm algo, boolean useHeuristic) {
        super(algo);

        this.useHeuristic = useHeuristic;
    }

    public boolean isUsingHeuristic() {
        return this.useHeuristic;
    }

    public boolean shouldAbort() {
        return this.abort;
    }

    public void setAbort(boolean abort) {
        this.abort = abort;
    }

    public M getLastMove() {
        return this.movesHistory.get(0);
    }

    public M getBestMove(final int depth) {
        this.setAbort(false);

        if (depth <= 0) {
            throw new IllegalArgumentException("Search depth MUST be > 0");
        }

        //this.transpositionTable.clear();

        this.nodesCount = 0;
        this.quiescenceNodesCount = 0;
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

    private List<M> sortMoves(List<M> moves, int depth) {
        Collections.sort(moves, KillerComparator);

        T hash = this.getTransposition();
        if (hash != null) {
            TranspositionTable.Entry<M> entry = this.transpositionTable.get(hash, depth);
            if (entry != null) {
                int index = moves.indexOf(entry.move);

                if (index > 0) {
                    moves.add(0, moves.remove(index));
                }
            }
        }

        if (this.shouldAvoidRepetitions() && this.movesHistory.size() >= 2) {
            M inverseMove = this.movesHistory.get(1).inverse();
            if (inverseMove != null) {
                int index = moves.indexOf(this.movesHistory.get(1));

                if (index != -1) {
                    moves.add(moves.remove(index));
                }
            }
        }

        return moves;
    }

    public boolean shouldAvoidRepetitions() {
        return false;
    }

    public abstract T getTransposition();

    /*
     * Negascout
     * w/ Killer Heuristic, Transposition Table and Quiescence search
     */

    private double negascout(final MoveWrapper<M> wrapper, final int depth, final double alpha, final double beta) {
        this.nodesCount++;

        if (this.abort) {
            return this.maxEvaluateValue();
        }

        if (depth == 0) {
            if (isQuiet() || isOver()) {
                return evaluate();
            } else {
                return quiescence(alpha, beta);
            }
        }

        this.currentDepth = this.maxDepth - depth;

        double a = alpha;
        double b = beta;

        T hash = this.getTransposition();
        if (hash != null && wrapper == null) {
            TranspositionTable.Entry<M> entry = this.transpositionTable.get(hash, depth);
            if (entry != null) {
                switch (entry.type) {
                    case LOWER_BOUND:
                        a = Math.max(a, entry.score);
                        break;
                    case UPPER_BOUND:
                        b = Math.min(b, entry.score);
                        break;
                    case EXACT_SCORE:
                        return entry.score;
                }

                if (a >= b) {
                    return a;
                }
            }
        }

        if (this.killerMoves.get(this.currentDepth) == null) {
            this.killerMoves.put(this.currentDepth, new ArrayList<Move>());
        }

        List<M> moves = sortMoves(getPossibleMoves(), depth);

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

            if (this.abort) {
                break;
            }

            if (score > a) {
                a = score;
                bestMove = move;

                List<Move> currentDepthKillerMoves = this.killerMoves.get(this.currentDepth);
                if (currentDepthKillerMoves == null) {
                    currentDepthKillerMoves = new ArrayList<>();
                    this.killerMoves.put(this.currentDepth, currentDepthKillerMoves);
                }
                if (move != null && !currentDepthKillerMoves.contains(move)) {
                    if (currentDepthKillerMoves.size() >= 2) {
                        currentDepthKillerMoves.remove(1);
                    }
                    currentDepthKillerMoves.add(0, move);
                }

                if (a >= beta) {
                    break;
                }
            } else if (first) {
                bestMove = move;
            }

            b = a + 1;
            first = false;
        }
        if (wrapper != null) {
            wrapper.move = bestMove;
        }

        if (hash != null && !this.abort) {
            TranspositionTable.Entry<M> entry = new TranspositionTable.Entry<>();
            entry.move = bestMove;
            entry.depth = depth;
            entry.score = a;
            entry.type = (a <= alpha ? TranspositionTable.EntryType.UPPER_BOUND :
                         (a >= beta  ? TranspositionTable.EntryType.LOWER_BOUND :
                                       TranspositionTable.EntryType.EXACT_SCORE));
            this.transpositionTable.put(hash, entry);
        }

        return a;
    }

    private double quiescence(final double alpha, final double beta) {
        this.quiescenceNodesCount++;

        if (this.abort) {
            return this.maxEvaluateValue();
        }

        if (isQuiet() || isOver()) {
            return evaluate();
        }

        double a = alpha;
        double b = beta;

        List<M> moves = this.getPossibleMoves();

        if (moves.isEmpty()) {
            next();
            double score = quiescenceScore(true, a, beta, b);
            previous();
            return score;
        }

        double score;
        boolean first = true;
        for (M move : moves) {
            makeMove(move);
            score = quiescenceScore(first, a, beta, b);
            unmakeMove(move);
            if (this.abort) {
                break;
            }

            if (score > a) {
                a = score;

                if (a >= beta) {
                    break;
                }
            }
            b = a + 1;
            first = false;
        }

        return a;
    }

    protected double quiescenceScore(final boolean first, final double alpha, final double beta, final double b) {
        if (this.abort) {
            return this.maxEvaluateValue();
        }

        double score = -quiescence(-b, -alpha);
        if (!this.abort && !first && alpha < score && score < beta) {
            // fails high... full re-search
            score = -quiescence(-beta, -alpha);
        }
        return score;
    }

    protected abstract boolean isQuiet();

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
        if (!this.abort && !first && alpha < score && score < beta) {
            // fails high... full re-search
            score = -negascout(null, depth - 1, -beta, -alpha);
        }
        return score;
    }

    public void printStatistics() {
        System.out.println("[NODES = " + this.nodesCount + ", QNODES = " + this.quiescenceNodesCount + ", TT HITS: " + (int)(this.transpositionTable.getHitRatio() * 100) + "%, TT SIZE: " + this.transpositionTable.size() + "]");
    }

}
