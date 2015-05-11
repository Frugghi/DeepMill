package it.unibo.ai.didattica.mulino.implementation;

import it.unibo.ai.didattica.mulino.domain.MillMinimax;
import it.unibo.ai.didattica.mulino.domain.State;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class BitBoardMinimax extends MillMinimax<BitBoardMove, Long> {

    public static final byte PLAYER_W = 0;
    public static final byte PLAYER_B = 1;
    public static final byte FREE     = 2;

    protected static final byte A1 = 0;
    protected static final byte B2 = 1;
    protected static final byte C3 = 2;
    protected static final byte D1 = 3;
    protected static final byte D2 = 4;
    protected static final byte D3 = 5;

    protected static final byte G1 = 6;
    protected static final byte F2 = 7;
    protected static final byte E3 = 8;
    protected static final byte G4 = 9;
    protected static final byte F4 = 10;
    protected static final byte E4 = 11;

    protected static final byte G7 = 12;
    protected static final byte F6 = 13;
    protected static final byte E5 = 14;
    protected static final byte D7 = 15;
    protected static final byte D6 = 16;
    protected static final byte D5 = 17;

    protected static final byte A7 = 18;
    protected static final byte B6 = 19;
    protected static final byte C5 = 20;
    protected static final byte A4 = 21;
    protected static final byte B4 = 22;
    protected static final byte C4 = 23;
    
    private static final int MILL_1 =   (1 << A1) | (1 << D1) | (1 << G1);
    private static final int MILL_2 =   (1 << B2) | (1 << D2) | (1 << F2);
    private static final int MILL_3 =   (1 << C3) | (1 << D3) | (1 << E3);
    private static final int MILL_4_1 = (1 << A4) | (1 << B4) | (1 << C4);
    private static final int MILL_4_2 = (1 << E4) | (1 << F4) | (1 << G4);
    private static final int MILL_5 =   (1 << C5) | (1 << D5) | (1 << E5);
    private static final int MILL_6 =   (1 << B6) | (1 << D6) | (1 << F6);
    private static final int MILL_7 =   (1 << A7) | (1 << D7) | (1 << G7);
    private static final int MILL_A =   (1 << A1) | (1 << A4) | (1 << A7);
    private static final int MILL_B =   (1 << B2) | (1 << B4) | (1 << B6);
    private static final int MILL_C =   (1 << C3) | (1 << C4) | (1 << C5);
    private static final int MILL_D_1 = (1 << D1) | (1 << D2) | (1 << D3);
    private static final int MILL_D_2 = (1 << D5) | (1 << D6) | (1 << D7);
    private static final int MILL_E =   (1 << E3) | (1 << E4) | (1 << E5);
    private static final int MILL_F =   (1 << F2) | (1 << F4) | (1 << F6);
    private static final int MILL_G =   (1 << G1) | (1 << G4) | (1 << G7);

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
    private static final byte[][] MOVES = {
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

    private final int[] board = new int[2];
    private final int[] played = new int[2];
    private final int[] count = new int[2];

    private byte currentPlayer;
    private byte opponentPlayer;

    public BitBoardMinimax(Algorithm algo, boolean useHeuristic) {
        super(algo, useHeuristic);

        this.board[PLAYER_W] = 0;
        this.board[PLAYER_B] = 0;

        this.played[PLAYER_W] = 0;
        this.played[PLAYER_B] = 0;

        this.count[PLAYER_W] = 0;
        this.count[PLAYER_B] = 0;

        this.currentPlayer = PLAYER_W;
        this.opponentPlayer = PLAYER_B;
    }

    @Override
    public MillMinimax fromState(State state) {
        BitBoardMinimax ia =  new BitBoardMinimax(this.getAlgo(), this.isUsingHeuristic());
        ia.updateState(state);
        ia.currentPlayer = this.currentPlayer;
        ia.opponentPlayer = this.opponentPlayer;
        ia.next();

        return ia;
    }

    protected void setPlayed(int white, int black) {
        this.played[PLAYER_W] = white;
        this.played[PLAYER_B] = black;
    }

    protected void setCount(int white, int black) {
        this.count[PLAYER_W] = white;
        this.count[PLAYER_B] = black;
    }

    protected void setGridPosition(State.Checker player, String position) {
        int i = (1 << BitBoardMove.string2byte(position));

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

    private void setGridPosition(byte player, int i) {
        if (player == FREE) {
            this.board[PLAYER_W] &= ~i;
            this.board[PLAYER_B] &= ~i;
        } else {
            this.board[player] |= i;
        }
    }

    @Override
    public boolean isOver() {
        return this.hasWon(PLAYER_W) || this.hasWon(PLAYER_B) || this.isADraw();
    }

    private boolean hasWon(byte player) {
        return this.phase1completed() &&
                (this.count[1 - player] <= 2 ||                                        // L'avversario ha meno di 3 pezzi
                numberOfPiecesBlocked((byte) (1 - player)) == this.count[1 - player]); // L'avversario non puo' muoversi
    }

    private boolean isADraw() {
        return this.movesHistory.size() > 12 &&
                this.movesHistory.get(0).equals(this.movesHistory.get(4)) && this.movesHistory.get(0).equals(this.movesHistory.get(8)) &&
                this.movesHistory.get(1).equals(this.movesHistory.get(5)) && this.movesHistory.get(1).equals(this.movesHistory.get(9)) &&
                this.movesHistory.get(2).equals(this.movesHistory.get(6)) && this.movesHistory.get(2).equals(this.movesHistory.get(10)) &&
                this.movesHistory.get(3).equals(this.movesHistory.get(7)) && this.movesHistory.get(3).equals(this.movesHistory.get(11));
    }

    private boolean phase1completed() {
        return (this.played[PLAYER_B] + this.played[PLAYER_W]) == PIECES * 2;
    }

    @Override
    public boolean shouldAvoidRepetitions() {
        return this.phase1completed();
    }

    @Override
    public void makeMove(BitBoardMove move) {
        this.movesHistory.add(0, move);

        this.setGridPosition(this.currentPlayer, (1 << move.getTo()));

        if (move.isPutMove()) {
            this.played[this.currentPlayer]++;
            this.count[this.currentPlayer]++;
        } else {
            this.setGridPosition(FREE, (1 << move.getFrom()));
        }

        if (move.isRemoveMove()) {
            this.count[this.opponentPlayer]--;

            this.setGridPosition(FREE, (1 << move.getRemove()));
        }

        this.next();
    }

    @Override
    public void unmakeMove(BitBoardMove move) {
    	this.movesHistory.remove(0);

        this.previous();
        
        this.setGridPosition(FREE, (1 << move.getTo()));

        if (move.isPutMove()) {
            this.played[this.currentPlayer]--;
            this.count[this.currentPlayer]--;
        } else {
            this.setGridPosition(this.currentPlayer, (1 << move.getFrom()));
        }

        if (move.isRemoveMove()) {
            this.count[this.opponentPlayer]++;

            this.setGridPosition(this.opponentPlayer, (1 << move.getRemove()));
        }
    }

    @Override
    public List<BitBoardMove> getPossibleMoves() {
        List<BitBoardMove> moves = new ArrayList<>(3 * (24 - this.count[PLAYER_W] - this.count[PLAYER_B]));

        if (this.played[this.currentPlayer] < PIECES) { // Fase 1
            for (byte to = 0; to < 24; to++) {
                this.addMoves(moves, to);
            }
        } else if (this.count[this.currentPlayer] == 3) { // Fase 3
            for (byte from = 0; from < 24; from++) {
                if (((this.board[this.currentPlayer] >>> from) & 1) == 1) {
                    for (byte to = 0; to < 24; to++) {
                        this.addMoves(moves, from, to);
                    }
                }
            }
        } else { // Fase 2
            for (byte from = 0; from < 24; from++) {
                if (((this.board[this.currentPlayer] >>> from) & 1) == 1) {
                    for (byte to : MOVES[from]) {
                        this.addMoves(moves, from, to);
                    }
                }
            }
        }

        return moves;
    }

    private void addMoves(List<BitBoardMove> moves, byte to) {
        this.addMoves(moves, Byte.MAX_VALUE, to);
    }

    private void addMoves(List<BitBoardMove> moves, byte from, byte to) {
        int completeBoard = this.board[PLAYER_W] | this.board[PLAYER_B];
        if (((completeBoard >>> to) & 1) == 0) {
            if (this.willMill(this.currentPlayer, from, to)) {
                boolean onlyMills = this.onlyMills(this.opponentPlayer);
                int opponentBoard = this.board[this.opponentPlayer];
                for (byte remove = 0; remove < 24; remove++) {
                    if (((opponentBoard >>> remove) & 1) == 1 && (onlyMills || !this.isMill(opponentBoard, remove))) {
                        moves.add(0, new BitBoardMove(this.currentPlayer, from == Byte.MAX_VALUE ? Byte.MAX_VALUE : from, to, remove));
                    }
                }
            } else {
                moves.add(new BitBoardMove(this.currentPlayer, from == Byte.MAX_VALUE ? Byte.MAX_VALUE : from, to));
            }
        }
    }

    private boolean willMill(byte player, byte from, byte to) {
        int board = this.board[player] | (1 << to);
        if (from != Byte.MAX_VALUE) {
            board &= ~(1 << from);
        }

        return this.isMill(board, to);
    }

    private boolean isMill(int board, byte pos) {
        for (int mill : MILLS[pos]) {
            if ((board & mill) == mill) {
                return true;
            }
        }

        return false;
    }

    private boolean onlyMills(byte player) {
        int board = this.board[player];
        for (byte i = 0; i < 24; i++) {
            if (((board >>> i) & 1) == 1 && !this.isMill(board, i)) {
                return false;
            }
        }

        return this.count[player] > 0;
    }

    @Override
    protected boolean isQuiet() {
        if (!this.phase1completed()) {
            boolean lastMoveBlockedMill = false;
            int opponentBoard = this.board[this.currentPlayer];

            for (int mill : MILLS[this.movesHistory.get(0).getTo()]) {
                if (Integer.bitCount((opponentBoard & mill)) == 2) {
                    lastMoveBlockedMill = true;
                    break;
                }
            }

            return !lastMoveBlockedMill || this.numberOf2PiecesConfiguration(this.currentPlayer) == 0;
        } else {
            return true;
        }
    }

    @Override
    public double evaluate() {
        if (this.hasWon(currentPlayer)) { // Se vinco e' la mossa migliore
            return this.maxEvaluateValue();
        } else if (this.hasWon(opponentPlayer)) { // Se perdo e' la mossa peggiore
            return -this.maxEvaluateValue();
        } else if (this.isADraw()) {
            return 0;
        }

        int lastIsRemoveMove = (!this.movesHistory.isEmpty() && this.movesHistory.get(0).isRemoveMove() ? 1 : 0);

        if (!this.phase1completed()) { // Fase 1
            return 18 * lastIsRemoveMove +
                    26 * (this.count[this.currentPlayer] - this.count[this.opponentPlayer]) +
                     1 * (this.numberOfPiecesBlocked(this.opponentPlayer) - this.numberOfPiecesBlocked(this.currentPlayer)) +
                     9 * (this.numberOfMorrises(this.currentPlayer) - this.numberOfMorrises(this.opponentPlayer)) +
                    10 * (this.numberOf2PiecesConfiguration(this.currentPlayer) - this.numberOf2PiecesConfiguration(this.opponentPlayer)) +
                     7 * (this.numberOf3PiecesConfiguration(this.currentPlayer) - this.numberOf3PiecesConfiguration(this.opponentPlayer));
        } else if (this.count[PLAYER_B] == 3 || this.count[PLAYER_W] == 3) { // Fase 3
            return 16 * lastIsRemoveMove +
                    10 * (this.numberOf2PiecesConfiguration(this.currentPlayer) - this.numberOf2PiecesConfiguration(this.opponentPlayer)) +
                     1 * (this.numberOf3PiecesConfiguration(this.currentPlayer) - this.numberOf3PiecesConfiguration(this.opponentPlayer));
        } else { // Fase 2
            return 14 * lastIsRemoveMove +
                    43 * (this.count[this.currentPlayer] - this.count[this.opponentPlayer]) +
                    10 * (this.numberOfPiecesBlocked(this.opponentPlayer) - this.numberOfPiecesBlocked(this.currentPlayer)) +
                    11 * (this.numberOfMorrises(this.currentPlayer) - this.numberOfMorrises(this.opponentPlayer)) +
                     8 * (this.numberOfDoubleMorrises(this.currentPlayer) - this.numberOfDoubleMorrises(this.opponentPlayer));
        }
    }

    private int numberOfImpossibleMorrises() {
        int blackBoard = this.board[PLAYER_B];
        int whiteBoard = this.board[PLAYER_W];
        int numberOfImpossibleMorrieses = 0;
        for (int mill : ALL_MILLS) {
            if ((whiteBoard & mill) != 0 && (blackBoard & mill) != 0) {
                numberOfImpossibleMorrieses++;
            }
        }

        return numberOfImpossibleMorrieses;
    }

    private int numberOfMorrises(int player) {
        int board = this.board[player];
        int numberOfMorrises = 0;

        for (int mill :  ALL_MILLS) {
            if ((board & mill) == mill) {
                numberOfMorrises++;
            }
        }

        return numberOfMorrises;
    }

    private boolean pieceIsBlocked(int board, byte piece){
        int moves = 0;
        for (byte move : MOVES[piece]) {
            moves |= (1 << move);
        }

        return (board & moves) == moves;
    }

    private int numberOfPiecesBlocked(byte player){
        int emptyBoard = this.board[PLAYER_W] | this.board[PLAYER_B];
        int totBlocked = 0;

        for (byte i = 0; i < 24; i++) {
            if (((this.board[player] >>> i) & 1) == 1 && this.pieceIsBlocked(emptyBoard, i)) {
                totBlocked++;
            }
        }

        return totBlocked;
    }

    private int numberOfDoubleMorrises(byte player) {
        int board = this.board[player];
        int totDoubleMorris = 0;
        for (byte pos = 0; pos < 24; pos++) {
            int doubleMill = 0;
            for (int mill : MILLS[pos]) {
                doubleMill |= mill;
            }
            if ((board & doubleMill) == doubleMill) {
                totDoubleMorris++;
            }
        }

        return totDoubleMorris;
    }

    private int numberOf3PiecesConfiguration(byte player) {
        int board = this.board[player];
        int opponentBoard = this.board[1 - player];
        int tot3piecesConfiguration = 0;
        for (byte pos = 0; pos < 24; pos++) {
            if (((board >>> pos) & 1) == 1) {
                boolean possibileConfiguration = true;
                for (int mill : MILLS[pos]) {
                    int pieces = board & mill;
                    int opponentPieces = opponentBoard & mill;
                    if (opponentPieces != 0 || pieces == mill || pieces == (1 << pos)) {
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

    private int numberOf2PiecesConfiguration(byte player) {
        int board = this.board[player];
        int opponentBoard = this.board[1 - player];
        int tot2piecesConfiguration = 0;
        for (int mill : ALL_MILLS) {
            if ((opponentBoard & mill) == 0 && Integer.bitCount((board & mill)) == 2) {
                tot2piecesConfiguration++;
            }
        }

        return tot2piecesConfiguration;
    }

    private int numberOfPotential3PiecesConfiguration(byte player) {
        int board = this.board[player];
        int opponentBoard = this.board[1 - player];
        int totPotential3piecesConfiguration = 0;
        for (byte pos = 0; pos < 24; pos++) {
            if (((board >>> pos) & 1) == 1) {
                for (int mill : MILLS[pos]) {
                    if ((opponentBoard & mill) == 0 && (board & mill) == (1 << pos)) {
                        int count = 0;
                        for (byte i = 0; i < 24; i++) {
                            if (i == pos) {
                                continue;
                            }

                            if (((mill >>> i) & 1) == 1) {
                                count++;

                                for (int otherMill : MILLS[i]) {
                                    if (otherMill == mill) {
                                        continue;
                                    }

                                    if ((opponentBoard & otherMill) == 0 && Integer.bitCount((board & otherMill)) == 1) {
                                        totPotential3piecesConfiguration++;
                                    }
                                }
                            }

                            if (count == 2) {
                                break;
                            }
                        }
                    }
                }
            }
        }

        return totPotential3piecesConfiguration / 2;
    }

    @Override
    public double maxEvaluateValue() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void next() {
        this.currentPlayer = (byte) (1 - this.currentPlayer);
        this.opponentPlayer = (byte) (1 - this.opponentPlayer);
    }

    @Override
    public void previous() {
        this.currentPlayer = (byte) (1 - this.currentPlayer);
        this.opponentPlayer = (byte) (1 - this.opponentPlayer);
    }

    public Long getTransposition() {
        long whiteBoard = this.board[PLAYER_W];
        long blackBoard = this.board[PLAYER_B];
        long phase = (this.phase1completed() ? (this.count[PLAYER_W] == 3 || this.count[PLAYER_B] == 3 ? 3 : 2) : 1);

        long hash = this.currentPlayer;
        hash |= (phase << 1);
        hash |= (whiteBoard <<  3); // [0..23]  white board
        hash |= (blackBoard << 27); // [24..47] black board

        return hash;
    }

    public Collection<Long> getSymetricTranspositions() {
        List<Long> transpositions = new ArrayList<>();
        long whiteBoard = this.board[PLAYER_W];
        long blackBoard = this.board[PLAYER_B];

        long hash = this.currentPlayer;
        hash |= (whiteBoard <<  1); // [0..23]  white board
        hash |= (blackBoard << 25); // [24..47] black board

        transpositions.add(hash);

        for (int i = 1; i < 4; i++) {
            whiteBoard = this.rotateBoard(whiteBoard);
            blackBoard = this.rotateBoard(blackBoard);

            hash = this.currentPlayer;
            hash |= (whiteBoard <<  1); // [0..23]  white board
            hash |= (blackBoard << 25); // [24..47] black board

            transpositions.add(hash);
        }

        return transpositions;
    }

    private int rotateBoard(int board) {
        int first = (board & 0b00111111);
        return ((board >>> 6) & (first << 18));
    }

    private long rotateBoard(long board) {
        long first = (board & 0b00111111);
        return ((board >>> 6) & (first << 18));
    }

    @Override
    public String toString() {
        String result = "";
        result += "7 " + this.playerString(A7) + "--------" + this.playerString(D7) + "--------" + this.playerString(G7) + "\n";
        result += "6 |  " + this.playerString(B6) + "-----" + this.playerString(D6) + "-----" + this.playerString(F6) + "  |\n";
        result += "5 |  |  " + this.playerString(C5) + "--" + this.playerString(D5) + "--" + this.playerString(E5) + "  |  |\n";
        result += "4 " + this.playerString(A4) + "--" + this.playerString(B4) + "--" + this.playerString(C4) + "     " + this.playerString(E4) + "--" + this.playerString(F4) + "--" + this.playerString(G4) +"\n";
        result += "3 |  |  " + this.playerString(C3) + "--" + this.playerString(D3) + "--" + this.playerString(E3) + "  |  |\n";
        result += "2 |  " + this.playerString(B2) + "-----" + this.playerString(D2) + "-----" + this.playerString(F2) + "  |\n";
        result += "1 " + this.playerString(A1) + "--------" + this.playerString(D1) + "--------" + this.playerString(G1) + "\n";
        result += "  a  b  c  d  e  f  g\n";
        result += "White Played Checkers: " + this.played[PLAYER_W] + ";\n";
        result += "Black Played Checkers: " + this.played[PLAYER_B] + ";\n";
        result += "White Checkers On Board: " + this.count[PLAYER_W] + ";\n";
        result += "Black Checkers On Board: " + this.count[PLAYER_B] + ";\n";
        result += "Current player: " + (this.currentPlayer == PLAYER_W ? "W" : "B") + ";\n";
        result += "Opponent player: " + (this.opponentPlayer == PLAYER_W ? "W" : "B") + ";\n";
        result += "\n";

        result += "Number of morrises player (W): " + this.numberOfMorrises(PLAYER_W) + "\n";
        result += "Number of morrises player (B): " + this.numberOfMorrises(PLAYER_B) + "\n";
        result += "Number of double morrises player (W): " + this.numberOfDoubleMorrises(PLAYER_W) + "\n";
        result += "Number of double morrises player (B): " + this.numberOfDoubleMorrises(PLAYER_B) + "\n";
        result += "Number of pieces blocked player (W): " + this.numberOfPiecesBlocked(PLAYER_W) + "\n";
        result += "Number of pieces blocked player (B): " + this.numberOfPiecesBlocked(PLAYER_B )+ "\n";
        result += "Number of 2 pieces configurations player (W): " + this.numberOf2PiecesConfiguration(PLAYER_W) + "\n";
        result += "Number of 2 pieces configurations player (B): " + this.numberOf2PiecesConfiguration(PLAYER_B) + "\n";
        result += "Number of 3 pieces configurations player (W): " + this.numberOf3PiecesConfiguration(PLAYER_W) + "\n";
        result += "Number of 3 pieces configurations player (B): " + this.numberOf3PiecesConfiguration(PLAYER_B) + "\n";
        result += "Number of potential 3 pieces configurations player (W): " + this.numberOfPotential3PiecesConfiguration(PLAYER_W) + "\n";
        result += "Number of potential 3 pieces configurations player (B): " + this.numberOfPotential3PiecesConfiguration(PLAYER_B) + "\n";
        result += "\n";

        result += "Moves history: ";
        for (BitBoardMove move : this.movesHistory) {
            result += move.toStringMove() + ", ";
        }
        result += "\n";

        return result;
    }

    private String playerString(byte i) {
        if (((this.board[PLAYER_W] >>> i) & 1) == 1) {
            return "W";
        } else if (((this.board[PLAYER_B] >>> i) & 1) == 1) {
            return "B";
        } else {
            return "O";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BitBoardMinimax that = (BitBoardMinimax) o;

        if (this.currentPlayer != that.currentPlayer) return false;
        if (this.opponentPlayer != that.opponentPlayer) return false;
        return Arrays.equals(this.board, that.board) && Arrays.equals(this.played, that.played) && Arrays.equals(this.count, that.count);
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