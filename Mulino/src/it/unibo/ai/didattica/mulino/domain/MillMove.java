package it.unibo.ai.didattica.mulino.domain;

import fr.avianey.minimax4j.Move;

public class MillMove implements Move {

    static final byte PLAYER_W = 1;
    static final byte PLAYER_B = 2;

    private int player;

    private int fromX;
    private int fromZ;

    private int toX;
    private int toZ;

    private int removeX;
    private int removeZ;

    public MillMove(int player, int fromX, int fromZ, int toX, int toZ, int removeX, int removeZ) {
        this.player = player;
        this.fromX = fromX;
        this.fromZ = fromZ;
        this.toX = toX;
        this.toZ = toZ;
        this.removeX = removeX;
        this.removeZ = removeZ;
    }

    public MillMove(int player, int fromX, int fromZ, int toX, int toZ) {
        this(player, fromX, fromZ, toX, toZ, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    public MillMove(int player, int toX, int toZ) {
        this(player, Integer.MAX_VALUE, Integer.MAX_VALUE, toX, toZ, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    public int getFromX() {
        return fromX;
    }

    public void setFromX(int fromX) {
        this.fromX = fromX;
    }

    public int getFromZ() {
        return fromZ;
    }

    public void setFromZ(int fromZ) {
        this.fromZ = fromZ;
    }

    public int getToX() {
        return toX;
    }

    public void setToX(int toX) {
        this.toX = toX;
    }

    public int getToZ() {
        return toZ;
    }

    public void setToZ(int toZ) {
        this.toZ = toZ;
    }

    public int getRemoveX() {
        return removeX;
    }

    public void setRemoveX(int removeX) {
        this.removeX = removeX;
    }

    public int getRemoveZ() {
        return removeZ;
    }

    public void setRemoveZ(int removeZ) {
        this.removeZ = removeZ;
    }

    public int getPlayer() {
        return player;
    }

    public void setPlayer(int player) {
        this.player = player;
    }

    public boolean isPutMove() {
        return this.fromZ == Integer.MAX_VALUE;
    }

    public boolean isRemoveMove() {
        return this.removeZ != Integer.MAX_VALUE;
    }

    public String toString() {
        return (this.player == MillMove.PLAYER_W ? "W" : "B") + " from (" + fromX + "" + fromZ + ") to (" + toX + "" + toZ + ") and remove (" + removeX + "" + removeZ + ")";
    }

}
