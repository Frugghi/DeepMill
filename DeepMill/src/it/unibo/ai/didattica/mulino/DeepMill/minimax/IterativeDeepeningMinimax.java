package it.unibo.ai.didattica.mulino.DeepMill.minimax;

public abstract class IterativeDeepeningMinimax<M extends InvertibleMove<M>, T extends Comparable<T>> extends HeuristicMinimax<M, T> {

    protected byte depth;
    private long timeoutTime = Long.MAX_VALUE;

    public IterativeDeepeningMinimax(Algorithm algo, boolean useHeuristic) {
        super(algo, useHeuristic);
    }

    public byte iterativeDeepeningIncrease() {
        return 1;
    }

    public M getBestMove(final int maxDepth, final int maxTime) {
        Thread timeout = new Thread() {
            public void run() {
                try {
                    Thread.sleep(maxTime);
                    IterativeDeepeningMinimax.this.setAbort(true);
                } catch (InterruptedException e) {
                    System.out.println("Timeout thread is alive!");
                }
            }
        };
        this.timeoutTime = System.currentTimeMillis() + maxTime;
        timeout.start();

        M bestMove = this.getBestMove(maxDepth);

        this.timeoutTime = Long.MAX_VALUE;

        if (timeout.isAlive()) {
            timeout.interrupt();
        }
        System.out.println("Reached depth: " + (this.depth - 1));

        return bestMove;
    }

    @Override
    public M getBestMove(final int maxDepth) {
        if (this.isOver()) {
            return null;
        }

        this.purgeHeuristic(2);

        long time = System.currentTimeMillis();
        long lastIteration;

        M bestMove = null;
        for (this.depth = this.iterativeDeepeningIncrease(); this.depth != maxDepth && !this.shouldAbort() && this.depth < 50; this.depth += this.iterativeDeepeningIncrease()) {
            System.out.print("Depth " + this.depth);

            lastIteration = System.currentTimeMillis();
            M move = super.getBestMove(this.depth);

            if (!this.shouldAbort() && move != null) {
                bestMove = move;

                long now = System.currentTimeMillis();
                if (now - lastIteration > this.timeoutTime - now) {
                    System.out.print("... completed! " + (now - time) + "ms ");
                    this.printStatistics();
                    this.depth++;
                    break;
                }

                this.makeMove(bestMove);
                this.setAbort(this.isOver());
                this.unmakeMove(bestMove);

                System.out.print("... done! " + (now - time) + "ms - Best move: " + bestMove.toString() + " - ");
                this.printStatistics();
            } else {
                System.out.println("... aborted!");
            }
        }

        this.setAbort(false);
        return bestMove;
    }

}
