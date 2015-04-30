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
        return (this.player == MillMove.PLAYER_W ? "W" : "B") + " from (" + fromX + ", " + fromZ + ") to (" + toX + ", " + toZ + ") and remove (" + removeX + ", " + removeZ + ")";
    }

    public String toStandardMove() {
        return coordinates2String(fromX, fromZ) + coordinates2String(toX, toZ) + coordinates2String(removeX, removeZ);
    }

    private static String coordinates2String(int x, int z) {
        switch (x) {
            case 0: return ('a' + z) + "" + (z + 1);
            case 1: return 'd' + "" + (z + 1);
            case 2: return ('g' - z) + "" + (z + 1);
            case 3: return ('g' - z) + "4";
            case 4: return ('g' - z) + "" + (7 - z);
            case 5: return 'd' + "" + (7 - z);
            case 6: return ('a' + z) + "" + (7 - z);
            case 7: return ('a' + z) + "4";
            default: return "";
        }
    }

    public static int[] string2Coordinates(String _coordinates) {
        String coordinates = _coordinates.toLowerCase();
        int z = Integer.MAX_VALUE;
        int x = Integer.MAX_VALUE;

        switch (coordinates.charAt(1)) {
            case '1':
            case '7':
                z = 0;
                break;
            case '2':
            case '6':
                z = 1;
                break;
            case '3':
            case '5':
                z = 2;
                break;
            default:
                switch (coordinates.charAt(0)) {
                    case 'a':
                    case 'g':
                        z = 0;
                        break;
                    case 'b':
                    case 'f':
                        z = 1;
                        break;
                    case 'c':
                    case 'e':
                        z = 2;
                        break;
                }
                break;
        }

        switch (coordinates.charAt(1)) {
            case '1':
            case '2':
            case '3':
                switch (coordinates.charAt(0)) {
                    case 'a':
                    case 'b':
                    case 'c':
                        x = 0;
                        break;
                    case 'd':
                        x = 1;
                        break;
                    case 'e':
                    case 'f':
                    case 'g':
                        x = 2;
                        break;
                }
                break;
            case '4':
                switch (coordinates.charAt(0)) {
                    case 'a':
                    case 'b':
                    case 'c':
                        x = 7;
                        break;
                    case 'e':
                    case 'f':
                    case 'g':
                        x = 3;
                        break;
                }
                break;
            case '5':
            case '6':
            case '7':
                switch (coordinates.charAt(0)) {
                    case 'a':
                    case 'b':
                    case 'c':
                        x = 6;
                        break;
                    case 'd':
                        x = 5;
                        break;
                    case 'e':
                    case 'f':
                    case 'g':
                        x = 4;
                        break;
                }
                break;
        }

        return new int[]{x, z};
    }

}
