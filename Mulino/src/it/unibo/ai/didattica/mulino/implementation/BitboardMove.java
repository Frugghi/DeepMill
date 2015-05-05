package it.unibo.ai.didattica.mulino.implementation;

import it.unibo.ai.didattica.mulino.domain.MillMove;

public class BitboardMove extends MillMove {

    private int player;

    private int to;
    private int from;
    private int remove;

    public BitboardMove(int player, int from, int to, int remove) {
        this.player = player;
        this.from = from;
        this.to = to;
        this.remove = remove;
    }

    public BitboardMove(int player, int from, int to) {
        this(player, from, to, Integer.MAX_VALUE);
    }

    public BitboardMove(int player, int to) {
        this(player, Integer.MAX_VALUE, to, Integer.MAX_VALUE);
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public int getRemove() {
        return remove;
    }

    public void setRemove(int remove) {
        this.remove = remove;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public boolean isPutMove() {
        return this.from == Integer.MAX_VALUE;
    }

    public boolean isRemoveMove() {
        return this.remove != Integer.MAX_VALUE;
    }

    public String toString() {
        return (this.player == BitboardMinimax.PLAYER_W ? "W" : "B") + " from (" + this.from + ") to (" + this.to + ") and remove (" + this.remove + ")";
    }

    @Override
    public String toStringMove() {
        return bitpattern2string(this.from) + bitpattern2string(this.to) + bitpattern2string(this.remove);
    }

    private String bitpattern2string(int bitPattern) {
        switch (bitPattern) {
            case BitboardMinimax.A1: return "a1";
            case BitboardMinimax.A4: return "a4";
            case BitboardMinimax.A7: return "a7";
            case BitboardMinimax.B2: return "b2";
            case BitboardMinimax.B4: return "b4";
            case BitboardMinimax.B6: return "b6";
            case BitboardMinimax.C3: return "c3";
            case BitboardMinimax.C4: return "c4";
            case BitboardMinimax.C5: return "c5";
            case BitboardMinimax.D1: return "d1";
            case BitboardMinimax.D2: return "d2";
            case BitboardMinimax.D3: return "d3";
            case BitboardMinimax.D5: return "d5";
            case BitboardMinimax.D6: return "d6";
            case BitboardMinimax.D7: return "d7";
            case BitboardMinimax.E3: return "e3";
            case BitboardMinimax.E4: return "e4";
            case BitboardMinimax.E5: return "e5";
            case BitboardMinimax.F2: return "f2";
            case BitboardMinimax.F4: return "f4";
            case BitboardMinimax.F6: return "f6";
            case BitboardMinimax.G1: return "g1";
            case BitboardMinimax.G4: return "g4";
            case BitboardMinimax.G7: return "g7";
            default:
                System.err.println("Unknown move: " + bitPattern);
                return "";
        }
    }

    protected static int string2bitpattern(String position) {
        switch (position.toLowerCase()) {
            case "a1": return BitboardMinimax.A1;
            case "a4": return BitboardMinimax.A4;
            case "a7": return BitboardMinimax.A7;
            case "b2": return BitboardMinimax.B2;
            case "b4": return BitboardMinimax.B4;
            case "b6": return BitboardMinimax.B6;
            case "c3": return BitboardMinimax.C3;
            case "c4": return BitboardMinimax.C4;
            case "c5": return BitboardMinimax.C5;
            case "d1": return BitboardMinimax.D1;
            case "d2": return BitboardMinimax.D2;
            case "d3": return BitboardMinimax.D3;
            case "d5": return BitboardMinimax.D5;
            case "d6": return BitboardMinimax.D6;
            case "d7": return BitboardMinimax.D7;
            case "e3": return BitboardMinimax.E3;
            case "e4": return BitboardMinimax.E4;
            case "e5": return BitboardMinimax.E5;
            case "f2": return BitboardMinimax.F2;
            case "f4": return BitboardMinimax.F4;
            case "f6": return BitboardMinimax.F6;
            case "g1": return BitboardMinimax.G1;
            case "g4": return BitboardMinimax.G4;
            case "g7": return BitboardMinimax.G7;
            default:
                System.err.println("Unknown move: " + position);
                return 0;
        }
    }

}
