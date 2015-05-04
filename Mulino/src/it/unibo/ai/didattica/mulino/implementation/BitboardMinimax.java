package it.unibo.ai.didattica.mulino.implementation;

import it.unibo.ai.didattica.mulino.domain.MillMinimax;
import it.unibo.ai.didattica.mulino.domain.State;

import java.util.ArrayList;
import java.util.List;

public class BitboardMinimax extends MillMinimax<BitboardMove> {

    public static final int PLAYER_W = 0;
    public static final int PLAYER_B = 1;
    public static final int FREE = 2;

    protected static final int A1 = 1 << 0;
    protected static final int B2 = 1 << 1;
    protected static final int C3 = 1 << 2;
    protected static final int D1 = 1 << 3;
    protected static final int D2 = 1 << 4;
    protected static final int D3 = 1 << 5;

    protected static final int G1 = 1 << 6;
    protected static final int F2 = 1 << 7;
    protected static final int E3 = 1 << 8;
    protected static final int G4 = 1 << 9;
    protected static final int F4 = 1 << 10;
    protected static final int E4 = 1 << 11;

    protected static final int G7 = 1 << 12;
    protected static final int F6 = 1 << 13;
    protected static final int E5 = 1 << 14;
    protected static final int D7 = 1 << 15;
    protected static final int D6 = 1 << 16;
    protected static final int D5 = 1 << 17;

    protected static final int A7 = 1 << 18;
    protected static final int B6 = 1 << 19;
    protected static final int C5 = 1 << 20;
    protected static final int A4 = 1 << 21;
    protected static final int B4 = 1 << 22;
    protected static final int C4 = 1 << 23;
    
    private static final int MILL_1 = A1 | D1 | G1;
    private static final int MILL_2 = B2 | D2 | F2;
    private static final int MILL_3 = C3 | D3 | E3;
    private static final int MILL_4_1 = A4 | B4 | C4;
    private static final int MILL_4_2 = E4 | F4 | G4;
    private static final int MILL_5 = C5 | D5 | E5;
    private static final int MILL_6 = B6 | D6 | F6;
    private static final int MILL_7 = A7 | D7 | G7;
    private static final int MILL_A = A1 | A4 | A7;
    private static final int MILL_B = B2 | B4 | B6;
    private static final int MILL_C = C3 | C4 | C5;
    private static final int MILL_D_1 = D1 | D2 | D3;
    private static final int MILL_D_2 = D4 | D5 | D6;
    private static final int MILL_E = E3 | E4 | E5;
    private static final int MILL_F = F2 | F4 | F6;
    private static final int MILL_G = G1 | G4 | G7;

    private final int[] board;
    private final int[] played;
    private final int[] count;

    private int currentPlayer;
    private int opponentPlayer;

    public BitboardMinimax(Algorithm algo) {
        super(algo);

        this.board = new int[2];
        this.board[PLAYER_W] = 0;
        this.board[PLAYER_B] = 0;

        this.played = new int[2];
        this.played[PLAYER_W] = 0;
        this.played[PLAYER_B] = 0;

        this.count = new int[2];
        this.count[PLAYER_W] = 0;
        this.count[PLAYER_B] = 0;

        this.currentPlayer = PLAYER_W;
        this.opponentPlayer = PLAYER_B;
    }

    @Override
    public void setPlayed(int white, int black) {
        this.played[PLAYER_W] = white;
        this.played[PLAYER_B] = black;
    }

    @Override
    public void setCount(int white, int black) {
        this.count[PLAYER_W] = white;
        this.count[PLAYER_B] = black;
    }

    @Override
    public void setGridPosition(State.Checker player, String position) {
        int i = BitboardMove.string2bitpattern(position);

        switch (player) {
            case WHITE:
                this.board[PLAYER_W] |= i;
                this.board[PLAYER_B] &= ~i;
                break;
            case BLACK:
                this.board[PLAYER_W] &= ~i;
                this.board[PLAYER_B] |= i;
                break;
            case EMPTY:
                this.board[PLAYER_W] &= ~i;
                this.board[PLAYER_B] &= ~i;
                break;
        }
    }

    private void setGridPosition(int player, int i) {
        if (player == FREE) {
            this.board[PLAYER_W] &= ~i;
            this.board[PLAYER_B] &= ~i;
        } else {
            this.board[player] |= i;
        }
    }

    @Override
    public BitboardMinimax cloneState() {
        BitboardMinimax clone = new BitboardMinimax(this.getAlgo());

        clone.currentPlayer = this.currentPlayer;
        clone.opponentPlayer = this.opponentPlayer;

        clone.board[PLAYER_W] = this.board[PLAYER_W];
        clone.board[PLAYER_B] = this.board[PLAYER_B];

        clone.count[PLAYER_W] = this.count[PLAYER_W];
        clone.count[PLAYER_B] = this.count[PLAYER_B];

        clone.played[PLAYER_W] = this.played[PLAYER_W];
        clone.played[PLAYER_B] = this.played[PLAYER_B];

        return clone;
    }

    @Override
    public boolean isOver() {
        return this.hasWon(PLAYER_W) || this.hasWon(PLAYER_B);
    }

    private boolean hasWon(int player) {
        return false;
    }

    @Override
    public void makeMove(BitboardMove move) {
        this.setGridPosition(this.currentPlayer, move.getTo());

        if (move.isPutMove()) {
            this.played[this.currentPlayer]++;
            this.count[this.currentPlayer]++;
        } else {
            this.setGridPosition(FREE, move.getFrom());
        }

        if (move.isRemoveMove()) {
            this.count[this.opponentPlayer]--;

            this.setGridPosition(FREE, move.getRemove());
        }

        next();
    }

    @Override
    public void unmakeMove(BitboardMove move) {
        previous();

        this.setGridPosition(FREE, move.getTo());

        if (move.isPutMove()) {
            this.played[this.currentPlayer]--;
            this.count[this.currentPlayer]--;
        } else {
            this.setGridPosition(this.currentPlayer, move.getFrom());
        }

        if (move.isRemoveMove()) {
            this.count[this.opponentPlayer]++;

            this.setGridPosition(this.opponentPlayer, move.getRemove());
        }
    }

    @Override
    public List<BitboardMove> getPossibleMoves() {
        List<BitboardMove> moves = new ArrayList<BitboardMove>(3 * (24 - count[PLAYER_W] - count[PLAYER_B]));

        if (this.played[this.currentPlayer] < PIECES) { // Fase 1
            int emptyBoard = this.board[PLAYER_W] & this.board[PLAYER_B];
            for (int i = 0; i < 24; i++) {
                if (((emptyBoard >> i) & 0x0001) == 0) {
                    moves.add(new BitboardMove(this.currentPlayer, 1 << i));
                }
            }
        } else if (this.count[this.currentPlayer] == 3) { // Fase 3

        } else { // Fase 2

        }

        return moves;
    }

    @Override
    public double evaluate() {
        return 0;
    }

    @Override
    public double maxEvaluateValue() {
        return 0;
    }

    @Override
    public void next() {
        this.currentPlayer = 1 - this.currentPlayer;
        this.opponentPlayer = 1 - this.opponentPlayer;
    }

    @Override
    public void previous() {
        this.currentPlayer = 1 - this.currentPlayer;
        this.opponentPlayer = 1 - this.opponentPlayer;
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append("7 " + this.playerString(A7) + "--------" + this.playerString(D7) + "--------" + this.playerString(G7) + "\n");
        result.append("6 |--" + this.playerString(B6) + "-----" + this.playerString(D6) + "-----" + this.playerString(F6) + "--|\n");
        result.append("5 |--|--" + this.playerString(C5) + "--" + this.playerString(D5) + "--" + this.playerString(E5) + "--|--|\n");
        result.append("4 " + this.playerString(A4) + "--" + this.playerString(B4) + "--" + this.playerString(C4) + "     " + this.playerString(E4) + "--" + this.playerString(F4) + "--" + this.playerString(G4) +"\n");
        result.append("3 |--|--" + this.playerString(C3) + "--" + this.playerString(D3) + "--" + this.playerString(E3) + "--|--|\n");
        result.append("2 |--" + this.playerString(B2) + "-----" + this.playerString(D2) + "-----" + this.playerString(F2) + "--|\n");
        result.append("1 " + this.playerString(A1) + "--------" + this.playerString(D1) + "--------" + this.playerString(G1) + "\n");
        result.append("  a  b  c  d  e  f  g\n");
        result.append("White Played Checkers: " + this.played[PLAYER_W] + ";\n");
        result.append("Black Played Checkers: " + this.played[PLAYER_B] + ";\n");
        result.append("White Checkers On Board: " + this.count[PLAYER_W] + ";\n");
        result.append("Black Checkers On Board: " + this.count[PLAYER_B] + ";\n");
        result.append("Current player: " + (this.currentPlayer == PLAYER_W ? "W" : "B") + ";\n");
        result.append("Opponent player: " + (this.opponentPlayer == PLAYER_W ? "W" : "B") + ";\n");

        return result.toString();
    }

    private String playerString(int i) {
        if ((this.board[PLAYER_W] & i) == i) {
            return "W";
        } else if ((this.board[PLAYER_B] & i) == i) {
            return "B";
        } else {
            return "O";
        }
    }

}
