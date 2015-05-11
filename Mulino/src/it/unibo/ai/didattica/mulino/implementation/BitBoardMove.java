package it.unibo.ai.didattica.mulino.implementation;

import it.unibo.ai.didattica.mulino.domain.MillMove;

public class BitBoardMove implements MillMove {

    private byte player;

    private byte to;
    private byte from;
    private byte remove;

    public BitBoardMove(byte player, byte from, byte to, byte remove) {
        this.player = player;
        this.from = from;
        this.to = to;
        this.remove = remove;
    }

    public BitBoardMove(byte player, byte from, byte to) {
        this(player, from, to, Byte.MAX_VALUE);
    }

    public BitBoardMove(byte player, byte to) {
        this(player, Byte.MAX_VALUE, to, Byte.MAX_VALUE);
    }

    public int getTo() {
        return to;
    }

    public void setTo(byte to) {
        this.to = to;
    }

    public int getRemove() {
        return remove;
    }

    public void setRemove(byte remove) {
        this.remove = remove;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(byte from) {
        this.from = from;
    }

    public boolean isPutMove() {
        return this.from == Byte.MAX_VALUE;
    }

    public boolean isRemoveMove() {
        return this.remove != Byte.MAX_VALUE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BitBoardMove that = (BitBoardMove) o;

        if (player != that.player) return false;
        if (to != that.to) return false;
        if (from != that.from) return false;
        return remove == that.remove;
    }

    @Override
    public int hashCode() {
        int result = player;
        result = 31 * result + to;
        result = 31 * result + from;
        result = 31 * result + remove;
        return result;
    }

    public String toString() {
        return (this.player == BitBoardMinimax.PLAYER_W ? "W" : "B") + ":(" + this.from + ", " + this.to + ", " + this.remove + ")";
    }

    @Override
    public String toStringMove() {
        return bitPattern2string(this.from) + bitPattern2string(this.to) + bitPattern2string(this.remove);
    }

    private String bitPattern2string(int bitPattern) {
        switch (bitPattern) {
            case BitBoardMinimax._A1: return "a1";
            case BitBoardMinimax._A4: return "a4";
            case BitBoardMinimax._A7: return "a7";
            case BitBoardMinimax._B2: return "b2";
            case BitBoardMinimax._B4: return "b4";
            case BitBoardMinimax._B6: return "b6";
            case BitBoardMinimax._C3: return "c3";
            case BitBoardMinimax._C4: return "c4";
            case BitBoardMinimax._C5: return "c5";
            case BitBoardMinimax._D1: return "d1";
            case BitBoardMinimax._D2: return "d2";
            case BitBoardMinimax._D3: return "d3";
            case BitBoardMinimax._D5: return "d5";
            case BitBoardMinimax._D6: return "d6";
            case BitBoardMinimax._D7: return "d7";
            case BitBoardMinimax._E3: return "e3";
            case BitBoardMinimax._E4: return "e4";
            case BitBoardMinimax._E5: return "e5";
            case BitBoardMinimax._F2: return "f2";
            case BitBoardMinimax._F4: return "f4";
            case BitBoardMinimax._F6: return "f6";
            case BitBoardMinimax._G1: return "g1";
            case BitBoardMinimax._G4: return "g4";
            case BitBoardMinimax._G7: return "g7";
            default:
                if (bitPattern != Byte.MAX_VALUE) {
                    System.err.println("Unknown move: " + bitPattern);
                }
                return "";
        }
    }

    public static byte string2byte(String position) {
        switch (position.toLowerCase()) {
            case "a1": return BitBoardMinimax._A1;
            case "a4": return BitBoardMinimax._A4;
            case "a7": return BitBoardMinimax._A7;
            case "b2": return BitBoardMinimax._B2;
            case "b4": return BitBoardMinimax._B4;
            case "b6": return BitBoardMinimax._B6;
            case "c3": return BitBoardMinimax._C3;
            case "c4": return BitBoardMinimax._C4;
            case "c5": return BitBoardMinimax._C5;
            case "d1": return BitBoardMinimax._D1;
            case "d2": return BitBoardMinimax._D2;
            case "d3": return BitBoardMinimax._D3;
            case "d5": return BitBoardMinimax._D5;
            case "d6": return BitBoardMinimax._D6;
            case "d7": return BitBoardMinimax._D7;
            case "e3": return BitBoardMinimax._E3;
            case "e4": return BitBoardMinimax._E4;
            case "e5": return BitBoardMinimax._E5;
            case "f2": return BitBoardMinimax._F2;
            case "f4": return BitBoardMinimax._F4;
            case "f6": return BitBoardMinimax._F6;
            case "g1": return BitBoardMinimax._G1;
            case "g4": return BitBoardMinimax._G4;
            case "g7": return BitBoardMinimax._G7;
            default:
                System.err.println("Unknown move: " + position);
                return 0;
        }
    }

}