package it.unibo.ai.didattica.mulino.implementation;

import it.unibo.ai.didattica.mulino.domain.MillMinimax;
import it.unibo.ai.didattica.mulino.domain.State;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BitboardMinimax extends MillMinimax<BitboardMove> {

    public static final int PLAYER_W = 0;
    public static final int PLAYER_B = 1;
    public static final int FREE = 2;

    private static final int _A1 = 0;  protected static final int A1 = 1 << _A1;
    private static final int _B2 = 1;  protected static final int B2 = 1 << _B2;
    private static final int _C3 = 2;  protected static final int C3 = 1 << _C3;
    private static final int _D1 = 3;  protected static final int D1 = 1 << _D1;
    private static final int _D2 = 4;  protected static final int D2 = 1 << _D2;
    private static final int _D3 = 5;  protected static final int D3 = 1 << _D3;

    private static final int _G1 = 6;  protected static final int G1 = 1 << _G1;
    private static final int _F2 = 7;  protected static final int F2 = 1 << _F2;
    private static final int _E3 = 8;  protected static final int E3 = 1 << _E3;
    private static final int _G4 = 9;  protected static final int G4 = 1 << _G4;
    private static final int _F4 = 10; protected static final int F4 = 1 << _F4;
    private static final int _E4 = 11; protected static final int E4 = 1 << _E4;

    private static final int _G7 = 12; protected static final int G7 = 1 << _G7;
    private static final int _F6 = 13; protected static final int F6 = 1 << _F6;
    private static final int _E5 = 14; protected static final int E5 = 1 << _E5;
    private static final int _D7 = 15; protected static final int D7 = 1 << _D7;
    private static final int _D6 = 16; protected static final int D6 = 1 << _D6;
    private static final int _D5 = 17; protected static final int D5 = 1 << _D5;

    private static final int _A7 = 18; protected static final int A7 = 1 << _A7;
    private static final int _B6 = 19; protected static final int B6 = 1 << _B6;
    private static final int _C5 = 20; protected static final int C5 = 1 << _C5;
    private static final int _A4 = 21; protected static final int A4 = 1 << _A4;
    private static final int _B4 = 22; protected static final int B4 = 1 << _B4;
    private static final int _C4 = 23; protected static final int C4 = 1 << _C4;
    
    private static final int MILL_1 =   A1 | D1 | G1;
    private static final int MILL_2 =   B2 | D2 | F2;
    private static final int MILL_3 =   C3 | D3 | E3;
    private static final int MILL_4_1 = A4 | B4 | C4;
    private static final int MILL_4_2 = E4 | F4 | G4;
    private static final int MILL_5 =   C5 | D5 | E5;
    private static final int MILL_6 =   B6 | D6 | F6;
    private static final int MILL_7 =   A7 | D7 | G7;
    private static final int MILL_A =   A1 | A4 | A7;
    private static final int MILL_B =   B2 | B4 | B6;
    private static final int MILL_C =   C3 | C4 | C5;
    private static final int MILL_D_1 = D1 | D2 | D3;
    private static final int MILL_D_2 = D5 | D6 | D7;
    private static final int MILL_E =   E3 | E4 | E5;
    private static final int MILL_F =   F2 | F4 | F6;
    private static final int MILL_G =   G1 | G4 | G7;

    private static final int[] ALL_MILLS = { MILL_1, MILL_2, MILL_3, MILL_4_1, MILL_4_2, MILL_5, MILL_6, MILL_7,
                                             MILL_A, MILL_B, MILL_C, MILL_D_1, MILL_D_2, MILL_E, MILL_F, MILL_G };
    private static final int[][] MILLS = {
            { MILL_1, MILL_A },   // A1
            { MILL_2, MILL_B },   // B2
            { MILL_3, MILL_C },   // C3
            { MILL_1, MILL_D_1 }, // D1
            { MILL_2, MILL_D_1 }, // D2
            { MILL_3, MILL_D_1 }, // D3
            { MILL_1, MILL_G },   // G1
            { MILL_2, MILL_F },   // F2
            { MILL_3, MILL_E },   // E3
            { MILL_4_2, MILL_G }, // G4
            { MILL_4_2, MILL_F }, // F4
            { MILL_4_2, MILL_E }, // E4
            { MILL_7, MILL_G },   // G7
            { MILL_6, MILL_F },   // F6
            { MILL_5, MILL_E },   // E5
            { MILL_7, MILL_D_2 }, // D7
            { MILL_6, MILL_D_2 }, // D6
            { MILL_5, MILL_D_2 }, // D5
            { MILL_7, MILL_A },   // A7
            { MILL_6, MILL_B },   // B6
            { MILL_5, MILL_C },   // C5
            { MILL_4_1, MILL_A }, // A4
            { MILL_4_1, MILL_B }, // B4
            { MILL_4_1, MILL_C }  // C4
    };
    private static final int[][] BIT_MOVES = {
            { A4, D1 },         // A1
            { B4, D2 },         // B2
            { C4, D3 },         // C3
            { A1, D2, G1 },     // D1
            { D1, B2, D3, F2 }, // D2
            { C3, D2, E3 },     // D3
            { D1, G4 },         // G1
            { D2, F4 },         // F2
            { D3, E4 },         // E3
            { G1, F4, G7 },     // G4
            { F2, E4, F6, G4 }, // F4
            { E3, E5, F4 },     // E4
            { G4, D7 },         // G7
            { F4, D6 },         // F6
            { E4, D5 },         // E5
            { D6, A7, G7 },     // D7
            { D5, B6, D7, F6 }, // D6
            { C5, D6, E5 },     // D5
            { A4, D7 },         // A7
            { B4, D6 },         // B6
            { C4, D5 },         // C5
            { A1, A7, B4 },     // A4
            { B2, A4, B6, C4 }, // B4
            { C3, B4, C5 }      // C4
    };
    private static final int[][] INT_MOVES = {
            { _A4, _D1 },           // A1
            { _B4, _D2 },           // B2
            { _C4, _D3 },           // C3
            { _A1, _D2, _G1 },      // D1
            { _D1, _B2, _D3, _F2 }, // D2
            { _C3, _D2, _E3 },      // D3
            { _D1, _G4 },           // G1
            { _D2, _F4 },           // F2
            { _D3, _E4 },           // E3
            { _G1, _F4, _G7 },      // G4
            { _F2, _E4, _F6, _G4 }, // F4
            { _E3, _E5, _F4 },      // E4
            { _G4, _D7 },           // G7
            { _F4, _D6 },           // F6
            { _E4, _D5 },           // E5
            { _D6, _A7, _G7 },      // D7
            { _D5, _B6, _D7, _F6 }, // D6
            { _C5, _D6, _E5 },      // D5
            { _A4, _D7 },           // A7
            { _B4, _D6 },           // B6
            { _C4, _D5 },           // C5
            { _A1, _A7, _B4 },      // A4
            { _B2, _A4, _B6, _C4 }, // B4
            { _C3, _B4, _C5 }       // C4
    };

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
        return super.isOver() || this.hasWon(PLAYER_W) || this.hasWon(PLAYER_B);
    }

    private boolean hasWon(int player) {
        return (this.played[PLAYER_B] + this.played[PLAYER_W]) == PIECES * 2 &&
                (this.count[1 - player] <= 2 ||                                  // L'avversario ha meno di 3 pezzi
                numberOfPiecesBlocked(1 - player) == this.count[1 - player]);    // L'avversario non puo' muoversi
    }

    @Override
    public void makeMove(BitboardMove move) {
        super.makeMove(move);

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

        this.next();
    }

    @Override
    public void unmakeMove(BitboardMove move) {
        super.unmakeMove(move);

        this.previous();

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
        List<BitboardMove> moves = new ArrayList<>(3 * (24 - this.count[PLAYER_W] - this.count[PLAYER_B]));

        if (this.played[this.currentPlayer] < PIECES) { // Fase 1
            for (int to = 0; to < 24; to++) {
                this.addMoves(moves, to);
            }
        } else if (this.count[this.currentPlayer] == 3) { // Fase 3
            for (int from = 0; from < 24; from++) {
                if (((this.board[this.currentPlayer] >>> from) & 1) == 1) {
                    for (int to = 0; to < 24; to++) {
                        this.addMoves(moves, from, to);
                    }
                }
            }
        } else { // Fase 2
            for (int from = 0; from < 24; from++) {
                if (((this.board[this.currentPlayer] >>> from) & 1) == 1) {
                    for (int i = 0; i < INT_MOVES[from].length; i++) {
                        this.addMoves(moves, from, INT_MOVES[from][i]);
                    }
                }
            }
        }

        return moves;
    }

    private void addMoves(List<BitboardMove> moves, int to) {
        this.addMoves(moves, Integer.MAX_VALUE, to);
    }

    private void addMoves(List<BitboardMove> moves, int from, int to) {
        int emptyBoard = this.board[PLAYER_W] | this.board[PLAYER_B];
        if (((emptyBoard >>> to) & 1) == 0) {
            if (this.willMill(this.currentPlayer, from, to)) {
                boolean onlyMills = this.onlyMills(this.opponentPlayer);
                int opponentBoard = this.board[this.opponentPlayer];
                for (int remove = 0; remove < 24; remove++) {
                    if (((opponentBoard >>> remove) & 1) == 1 && (onlyMills || !this.isMill(opponentBoard, remove))) {
                        moves.add(0, new BitboardMove(this.currentPlayer, from == Integer.MAX_VALUE ? Integer.MAX_VALUE : (1 << from), (1 << to), (1 << remove)));
                    }
                }
            } else {
                moves.add(new BitboardMove(this.currentPlayer, from == Integer.MAX_VALUE ? Integer.MAX_VALUE : (1 << from), (1 << to)));
            }
        }
    }

    private boolean willMill(int player, int from, int to) {
        int board = this.board[player] | (1 << to);
        if (from != Integer.MAX_VALUE) {
            board &= ~(1 << from);
        }

        return this.isMill(board, to);
    }

    private boolean isMill(int board, int pos) {
        for (int i = 0; i < MILLS[pos].length; i++) {
            int mill = MILLS[pos][i];
            if ((board & mill) == mill) {
                return true;
            }
        }

        return false;
    }

    private boolean onlyMills(int player) {
        int board = this.board[player];
        for (int i = 0; i < 24; i++) {
            if (((board >>> i) & 1) == 1 && !this.isMill(board, i)) {
                return false;
            }
        }

        return this.count[player] > 0;
    }

    @Override
    public double evaluate() {
        if (this.hasWon(currentPlayer)) { // Se vinco e' la mossa migliore
            return this.maxEvaluateValue();
        } else if (this.hasWon(opponentPlayer)) { // Se perdo e' la mossa peggiore
            return -this.maxEvaluateValue();
        }

        int lastMove = (this.lastMove != null && this.lastMove.isRemoveMove() ? 1 : 0);

        if (this.played[PLAYER_B] < PIECES && this.played[PLAYER_W] < PIECES) { // Fase 1
            return 18 * lastMove +
                    26 * (this.count[this.currentPlayer] - this.count[this.opponentPlayer]) +
                     1 * (this.numberOfPiecesBlocked(this.opponentPlayer) - this.numberOfPiecesBlocked(this.currentPlayer)) +
                     7 * (this.numberOf3PiecesConfiguration(this.currentPlayer) - this.numberOf3PiecesConfiguration(this.opponentPlayer)) +
                     9 * (this.count[this.currentPlayer] - this.count[this.opponentPlayer]);
        } else { // Fase 2 + 3
            return 14 * lastMove +
                    43 * (this.count[this.currentPlayer] - this.count[this.opponentPlayer]) +
                    10 * (this.numberOfPiecesBlocked(this.opponentPlayer) - this.numberOfPiecesBlocked(this.currentPlayer)) +
                    11 * (this.count[this.currentPlayer] - this.count[this.opponentPlayer]) +
                     8 * (this.numberOfDoubleMorrises(this.currentPlayer) - this.numberOfDoubleMorrises(this.opponentPlayer));
        }
    }

    private int numberOfMorrises(int player) {
        int board = this.board[player];
        int numberOfMorrises = 0;

        for (int i = 0; i < ALL_MILLS.length; i++) {
            if ((board & ALL_MILLS[i]) == ALL_MILLS[i]) {
                numberOfMorrises++;
            }
        }

        return numberOfMorrises;
    }

    private boolean pieceIsBlocked(int board, int piece){
        int moves = 0;
        for (int i = 0; i < BIT_MOVES[piece].length; i++) {
            moves |= BIT_MOVES[piece][i];
        }

        return (board & moves) == moves;
    }

    private int numberOfPiecesBlocked(int player){
        int emptyBoard = this.board[PLAYER_W] | this.board[PLAYER_B];
        int totBlocked = 0;

        for (int i = 0; i < 24; i++) {
            if (((this.board[player] >>> i) & 1) == 1 && this.pieceIsBlocked(emptyBoard, i)) {
                totBlocked++;
            }
        }

        return totBlocked;
    }

    private int numberOfDoubleMorrises(int player) {
        int board = this.board[player];
        int totDoubleMorris = 0;
        for (int pos = 0; pos < 24; pos++) {
            int mill = 0;
            for (int i = 0; i < MILLS[pos].length; i++) {
                mill |= MILLS[pos][i];
            }
            if ((board & mill) == mill) {
                totDoubleMorris++;
            }
        }

        return totDoubleMorris;
    }

    private int numberOf3PiecesConfiguration(int player) {
        int board = this.board[player];
        int opponentBoard = this.board[1 - player];
        int tot3piecesConfiguration = 0;
        for (int pos = 0; pos < 24; pos++) {
            if (((board >>> pos) & 1) == 1) {
                boolean possibileConfiguration = true;
                for (int i = 0; i < MILLS[pos].length; i++) {
                    int pieces = board & MILLS[pos][i];
                    int opponentPieces = opponentBoard & MILLS[pos][i];
                    if (opponentPieces != 0 || pieces == MILLS[pos][i] || pieces == (1 << pos)) {
                        possibileConfiguration = false;
                        break;
                    }
                }
                if (possibileConfiguration) {
                    tot3piecesConfiguration++;
                }
            }
        }

        return tot3piecesConfiguration;
    }

    @Override
    public double maxEvaluateValue() {
        return Integer.MAX_VALUE;
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
        result.append("6 |  " + this.playerString(B6) + "-----" + this.playerString(D6) + "-----" + this.playerString(F6) + "  |\n");
        result.append("5 |  |  " + this.playerString(C5) + "--" + this.playerString(D5) + "--" + this.playerString(E5) + "  |  |\n");
        result.append("4 " + this.playerString(A4) + "--" + this.playerString(B4) + "--" + this.playerString(C4) + "     " + this.playerString(E4) + "--" + this.playerString(F4) + "--" + this.playerString(G4) +"\n");
        result.append("3 |  |  " + this.playerString(C3) + "--" + this.playerString(D3) + "--" + this.playerString(E3) + "  |  |\n");
        result.append("2 |  " + this.playerString(B2) + "-----" + this.playerString(D2) + "-----" + this.playerString(F2) + "  |\n");
        result.append("1 " + this.playerString(A1) + "--------" + this.playerString(D1) + "--------" + this.playerString(G1) + "\n");
        result.append("  a  b  c  d  e  f  g\n");
        result.append("White Played Checkers: " + this.played[PLAYER_W] + ";\n");
        result.append("Black Played Checkers: " + this.played[PLAYER_B] + ";\n");
        result.append("White Checkers On Board: " + this.count[PLAYER_W] + ";\n");
        result.append("Black Checkers On Board: " + this.count[PLAYER_B] + ";\n");
        result.append("Current player: " + (this.currentPlayer == PLAYER_W ? "W" : "B") + ";\n");
        result.append("Opponent player: " + (this.opponentPlayer == PLAYER_W ? "W" : "B") + ";\n");
        result.append("\n");

        result.append("Number of morrises player (W) : " + this.numberOfMorrises(PLAYER_W) + "\n");
        result.append("Number of morrises player (B) : " + this.numberOfMorrises(PLAYER_B) + "\n");
        result.append("Number of double morrises player (W) : " + this.numberOfDoubleMorrises(PLAYER_W) + "\n");
        result.append("Number of double morrises player (B) : " + this.numberOfDoubleMorrises(PLAYER_B) + "\n");
        result.append("Number of pieces blocked player (W) : " + this.numberOfPiecesBlocked(PLAYER_W) + "\n");
        result.append("Number of pieces blocked player (B) : " + this.numberOfPiecesBlocked(PLAYER_B )+ "\n");
        result.append("Number of 3 pieces configurations player (W) : " + this.numberOf3PiecesConfiguration(PLAYER_W) + "\n");
        result.append("Number of 3 pieces configurations player (B) : " + this.numberOf3PiecesConfiguration(PLAYER_B )+ "\n");

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BitboardMinimax that = (BitboardMinimax) o;

        if (this.currentPlayer != that.currentPlayer) return false;
        if (this.opponentPlayer != that.opponentPlayer) return false;
        if (!Arrays.equals(this.board, that.board)) return false;
        if (!Arrays.equals(this.played, that.played)) return false;
        return Arrays.equals(this.count, that.count);

    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(this.board);
        result = 31 * result + Arrays.hashCode(this.played);
        result = 31 * result + Arrays.hashCode(this.count);
        result = 31 * result + this.currentPlayer;
        result = 31 * result + this.opponentPlayer;
        return result;
    }
}
