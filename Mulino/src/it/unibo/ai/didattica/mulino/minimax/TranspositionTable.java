package it.unibo.ai.didattica.mulino.minimax;

import fr.avianey.minimax4j.Move;
import org.apache.commons.collections4.map.LRUMap;

import java.util.LinkedHashMap;
import java.util.Map;

public class TranspositionTable<T extends Comparable<T>, M extends Move> {

    public static final int MAX_SIZE = (int)Math.pow(2, 22);

    public enum EntryType {
        LOWER_BOUND, UPPER_BOUND, EXACT_SCORE;
    }

    public static final class Entry<M extends Move> {
        public double score;
        public M move;
        public EntryType type;
        public byte depth;
    }

    private Map<T, Entry<M>> table = new LRUMap<T, Entry<M>>(TranspositionTable.MAX_SIZE);
    private int lowerBoundHits = 0;
    private int upperBoundHits = 0;
    private int exactScoreHits = 0;
    private int missedHits = 0;

    public int getTableHits() {
        return this.lowerBoundHits + this.upperBoundHits + this.exactScoreHits;
    }
    public double getHitRatio() {
        int totalHits = this.getTableHits();
        if (totalHits == 0 && this.missedHits == 0) {
            return 0;
        } else {
            return ((double)totalHits / (double)(totalHits + this.missedHits));
        }
    }

    public Entry<M> get(final T hash, final byte depth) {
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
        } else {
            this.missedHits++;
        }

        return null;
    }

    public void put(final T hash, final Entry<M> entry) {
        Entry<M> oldEntry = this.table.get(hash);
        if (oldEntry == null || oldEntry.depth <= entry.depth) {
            this.table.put(hash, entry);
        }
    }

    public void clear() {
        this.table.clear();
        this.upperBoundHits = 0;
        this.lowerBoundHits = 0;
        this.exactScoreHits = 0;
        this.missedHits = 0;
    }

    public int size() {
        return this.table.size();
    }

}
