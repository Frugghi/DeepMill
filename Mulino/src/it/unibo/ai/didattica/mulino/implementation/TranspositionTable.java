package it.unibo.ai.didattica.mulino.implementation;

import fr.avianey.minimax4j.Move;

import java.util.HashMap;
import java.util.Map;

public class TranspositionTable<T extends Comparable<T>, M extends Move> {

    public enum EntryType {
        LOWER_BOUND, UPPER_BOUND, EXACT_SCORE;
    }

    public static final class Entry<M extends Move> {
        public double score;
        public M move;
        public EntryType type;
        public int depth;
    }

    private Map<T, Entry<M>> table = new HashMap<>();
    private int lowerBoundHits = 0;
    private int upperBoundHits = 0;
    private int exactScoreHits = 0;

    public int getTableHits() {
        return this.lowerBoundHits + this.upperBoundHits + this.exactScoreHits;
    }
    public int getLowerBoundHits() {
        return this.lowerBoundHits;
    }
    public int getUpperBoundHits() {
        return this.upperBoundHits;
    }
    public int getExactScoreHits() {
        return this.exactScoreHits;
    }

    public Entry<M> get(T hash, int depth) {
        Entry<M> entry = this.table.get(hash);
        if (entry != null && entry.depth >= depth) {
            switch (entry.type) {
                case UPPER_BOUND:
                    this.upperBoundHits++;
                    break;
                case LOWER_BOUND:
                    this.lowerBoundHits++;
                    break;
                case EXACT_SCORE:
                    this.exactScoreHits++;
                    break;
            }

            return entry;
        }

        return null;
    }

    public void put(T hash, Entry<M> entry) {
        this.table.put(hash, entry);
    }

    public void clear() {
        this.table.clear();
        this.upperBoundHits = 0;
        this.lowerBoundHits = 0;
        this.exactScoreHits = 0;
    }

    public int size() {
        return this.table.size();
    }

}
