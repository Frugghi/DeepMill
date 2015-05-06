package it.unibo.ai.didattica.mulino.implementation;

import it.unibo.ai.didattica.mulino.domain.MillMove;

public class BitBoardMove implements MillMove {

    private int player;

    private int to;
    private int from;
    private int remove;

    public BitBoardMove(int player, int from, int to, int remove) {
        this.player = player;
        this.from = from;
        this.to = to;
        this.remove = remove;
    }

    public BitBoardMove(int player, int from, int to) {
        this(player, from, to, Integer.MAX_VALUE);
    }

    public BitBoardMove(int player, int to) {
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
        return (this.player == BitBoardMinimax.PLAYER_W ? "W" : "B") + " from (" + this.from + ") to (" + this.to + ") and remove (" + this.remove + ")";
    }

    @Override
    public String toStringMove() {
        return bitPattern2string(this.from) + bitPattern2string(this.to) + bitPattern2string(this.remove);
    }

    private String bitPattern2string(int bitPattern) {
        switch (bitPattern) {
            case BitBoardMinimax.A1: return "a1";
            case BitBoardMinimax.A4: return "a4";
            case BitBoardMinimax.A7: return "a7";
            case BitBoardMinimax.B2: return "b2";
            case BitBoardMinimax.B4: return "b4";
            case BitBoardMinimax.B6: return "b6";
            case BitBoardMinimax.C3: return "c3";
            case BitBoardMinimax.C4: return "c4";
            case BitBoardMinimax.C5: return "c5";
            case BitBoardMinimax.D1: return "d1";
            case BitBoardMinimax.D2: return "d2";
            case BitBoardMinimax.D3: return "d3";
            case BitBoardMinimax.D5: return "d5";
            case BitBoardMinimax.D6: return "d6";
            case BitBoardMinimax.D7: return "d7";
            case BitBoardMinimax.E3: return "e3";
            case BitBoardMinimax.E4: return "e4";
            case BitBoardMinimax.E5: return "e5";
            case BitBoardMinimax.F2: return "f2";
            case BitBoardMinimax.F4: return "f4";
            case BitBoardMinimax.F6: return "f6";
            case BitBoardMinimax.G1: return "g1";
            case BitBoardMinimax.G4: return "g4";
            case BitBoardMinimax.G7: return "g7";
            default:
                if (bitPattern != Integer.MAX_VALUE) {
                    System.err.println("Unknown move: " + bitPattern);
                }
                return "";
        }
    }

    protected static int string2bitPattern(String position) {
        switch (position.toLowerCase()) {
            case "a1": return BitBoardMinimax.A1;
            case "a4": return BitBoardMinimax.A4;
            case "a7": return BitBoardMinimax.A7;
            case "b2": return BitBoardMinimax.B2;
            case "b4": return BitBoardMinimax.B4;
            case "b6": return BitBoardMinimax.B6;
            case "c3": return BitBoardMinimax.C3;
            case "c4": return BitBoardMinimax.C4;
            case "c5": return BitBoardMinimax.C5;
            case "d1": return BitBoardMinimax.D1;
            case "d2": return BitBoardMinimax.D2;
            case "d3": return BitBoardMinimax.D3;
            case "d5": return BitBoardMinimax.D5;
            case "d6": return BitBoardMinimax.D6;
            case "d7": return BitBoardMinimax.D7;
            case "e3": return BitBoardMinimax.E3;
            case "e4": return BitBoardMinimax.E4;
            case "e5": return BitBoardMinimax.E5;
            case "f2": return BitBoardMinimax.F2;
            case "f4": return BitBoardMinimax.F4;
            case "f6": return BitBoardMinimax.F6;
            case "g1": return BitBoardMinimax.G1;
            case "g4": return BitBoardMinimax.G4;
            case "g7": return BitBoardMinimax.G7;
            default:
                System.err.println("Unknown move: " + position);
                return 0;
        }
    }

}