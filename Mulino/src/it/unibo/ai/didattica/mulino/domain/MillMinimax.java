package it.unibo.ai.didattica.mulino.domain;

import fr.avianey.minimax4j.Minimax;

import java.util.ArrayList;
import java.util.List;

public class MillMinimax extends Minimax<MillMove> {

    private static final int FREE     = 0;
    private static final int PLAYER_W = 1;
    private static final int PLAYER_B = 2;

    private static final int PIECES = 9;

    private static final int Z_SIZE = 3;
    private static final int X_SIZE = 8;

    private final int[] played;
    private final int[] count;
    private final int[][] grid;

    private int currentPlayer;
    private int opponentPlayer;

    public MillMinimax(Algorithm algo) {
        super(algo);

        /*
         * per iterare sulle X partiamo da 2 e arriviamo fino a X_SIZE + 2
         * i metodi di questa classe accettano una X incrementata di 2
         * decrementare la X quando si crea una mossa
         */
        this.grid = new int[Z_SIZE][X_SIZE + 4];
        this.played = new int[3];
        this.count = new int[3];

        newGame();
    }

    public void newGame() {
        for (int z = 0; z < Z_SIZE; z++) {
            for (int x = 0; x < X_SIZE + 4; x++) {
                grid[z][x] = FREE;
            }
        }

        played[FREE] = PIECES * 2;
        played[PLAYER_W] = 0;
        played[PLAYER_B] = 0;

        count[FREE] = Z_SIZE * X_SIZE;
        count[PLAYER_W] = 0;
        count[PLAYER_B] = 0;

        currentPlayer = PLAYER_W;
        opponentPlayer = PLAYER_B;
    }

    @Override
    public boolean isOver() {
        return hasWon(PLAYER_W) || hasWon(PLAYER_B);
    }

    private boolean hasWon(int player) {
        return played[PLAYER_W] == PIECES && played[PLAYER_B] == PIECES && count[3 - player] <= 2;
    }

    private void setGridPosition(int player, int x, int z) {
        grid[z][x + 2] = player;
        if (x < 2) {
            grid[z][X_SIZE + x + 2] = FREE;
        } else if (x > X_SIZE - 2) {
            grid[z][X_SIZE + x + 2] = FREE;
        }
    }

    @Override
    public void makeMove(MillMove move) {
        setGridPosition(currentPlayer, move.getToX(), move.getToZ());

        if (move.isPutMove()) {
            played[FREE]--;
            played[currentPlayer]++;

            count[FREE]--;
            count[currentPlayer]++;
        } else {
            setGridPosition(FREE, move.getFromX(), move.getFromZ());
        }

        if (move.isRemoveMove()) {
            count[FREE]++;
            count[opponentPlayer]--;

            setGridPosition(FREE, move.getRemoveX(), move.getRemoveZ());
        }

        next();
    }

    @Override
    public void unmakeMove(MillMove move) {
        setGridPosition(FREE, move.getToX(), move.getToZ());

        if (move.isPutMove()) {
            played[FREE]++;
            played[currentPlayer]--;

            count[FREE]++;
            count[currentPlayer]--;
        } else {
            setGridPosition(currentPlayer, move.getFromX(), move.getFromZ());
        }

        if (move.isRemoveMove()) {
            count[FREE]--;
            count[opponentPlayer]++;

            setGridPosition(opponentPlayer, move.getRemoveX(), move.getRemoveZ());
        }

        previous();
    }

    @Override
    public List<MillMove> getPossibleMoves() {
        List<MillMove> moves = new ArrayList<MillMove>(3 * count[FREE]);
        if (played[currentPlayer] < PIECES) {
            for (int toZ = 0; toZ < Z_SIZE; toZ++) {
                for (int toX = 2; toX < X_SIZE + 2; toX++) {
                    this.addMoves(moves, Integer.MAX_VALUE, Integer.MAX_VALUE, toX, toZ);
                }
            }
        } else if (count[currentPlayer] == 3) {
            for (int fromZ = 0; fromZ < Z_SIZE; fromZ++) {
                for (int fromX = 2; fromX < X_SIZE + 2; fromX++) {
                    if (grid[fromZ][fromX] == currentPlayer) {
                        for (int toZ = 0; toZ < Z_SIZE; toZ++) {
                            for (int toX = 2; toX < X_SIZE + 2; toX++) {
                                this.addMoves(moves, fromX, fromZ, toX, toZ);
                            }
                        }
                    }
                }
            }
        } else {
            for (int fromZ = 0; fromZ < Z_SIZE; fromZ++) {
                for (int fromX = 2; fromX < X_SIZE + 2; fromX++) {
                    if (grid[fromZ][fromX] == currentPlayer) {
                        if (fromX % 2 == 1) {
                            switch (fromZ) {
                                case 0:
                                    this.addMoves(moves, fromX, fromZ, fromX, 1);
                                    break;
                                case Z_SIZE - 1:
                                    this.addMoves(moves, fromX, fromZ, fromX, fromZ - 1);
                                    break;
                                default:
                                    this.addMoves(moves, fromX, fromZ, fromX, fromZ + 1);
                                    this.addMoves(moves, fromX, fromZ, fromX, fromZ - 1);
                                    break;
                            }
                            this.addMoves(moves, fromX, fromZ, fromX + 1, fromZ);
                            this.addMoves(moves, fromX, fromZ, fromX - 1, fromZ);
                        } else {
                            switch (fromX) {
                                case 2: // 0 + delta
                                    this.addMoves(moves, fromX, fromZ, fromX + 1, fromZ);
                                    this.addMoves(moves, fromX, fromZ, X_SIZE + 1, fromZ); // X_SIZE - 1 + delta
                                    break;
                                case X_SIZE + 1: // X_SIZE - 1 + delta
                                    this.addMoves(moves, fromX, fromZ, 2, fromZ); // 0 + delta
                                    this.addMoves(moves, fromX, fromZ, fromX - 1, fromZ);
                                    break;
                                default:
                                    this.addMoves(moves, fromX, fromZ, fromX + 1, fromZ);
                                    this.addMoves(moves, fromX, fromZ, fromX - 1, fromZ);
                                    break;
                            }
                        }
                    }
                }
            }
        }

        return moves;
    }

    private boolean isMill(int player, int x, int z) {
        if (x % 2 == 1) {
            return (grid[z][x - 1] == player && grid[z][x] == player && grid[z][x + 1] == player) ||
                    (grid[0][x] == player && grid[1][x] == player && grid[2][x] == player);
        } else {
            return grid[z][x] == player && (
                    (grid[z][x - 2] == player && grid[z][x - 1] == player) ||
                    (grid[z][x + 1] == player && grid[z][x + 2] == player));
        }
    }

    private boolean willMill(int player, int fromX, int fromZ, int toX, int toZ) {
        if (fromZ == Integer.MAX_VALUE) {
            if (toX % 2 == 1) {
                boolean willMill = true;
                for (int i = 0; i < Z_SIZE; i++) {
                    if (i != toZ && grid[i][toX] != player) {
                        willMill = false;
                        break;
                    }
                }
                if (willMill) {
                    return true;
                } else {
                    return grid[toZ][toX - 1] == player && grid[toZ][toX + 1] == player;
                }
            } else {
               return (grid[toZ][toX - 2] == player && grid[toZ][toX - 1] == player) ||
                       (grid[toZ][toX + 1] == player && grid[toZ][toX + 2] == player);
            }
        } else { // Modifichiamo la board per questo calcolo... non penso sia una grande idea!!
            boolean willMill = false;

            setGridPosition(FREE, fromX - 2, fromZ);

            willMill = willMill(player, Integer.MAX_VALUE, Integer.MAX_VALUE, toX, toZ);

            setGridPosition(player, fromX - 2, fromZ);

            return willMill;
        }
    }

    private void addMoves(List<MillMove> moves, int fromX, int fromZ, int toX, int toZ) {
        if (grid[toZ][toX] == FREE) {
            if (this.willMill(currentPlayer, fromX, fromZ, toX, toZ)) {
                for (int removeZ = 0; removeZ < Z_SIZE; removeZ++) {
                    for (int removeX = 2; removeX < X_SIZE + 2; removeX++) {
                        if (grid[removeZ][removeX] == opponentPlayer && !this.isMill(opponentPlayer, removeX, removeZ)) {
                            moves.add(0, new MillMove(currentPlayer, fromX - 2, fromZ, toX - 2, toZ, removeX - 2, removeZ));
                        }
                    }
                }
            } else {
                moves.add(new MillMove(currentPlayer, fromX - 2, fromZ, toX - 2, toZ));
            }
        }
    }

    @Override
    public double evaluate() {
        //TODO: implementare evaluate()

        /* Idee:
         * - il top per noi è fare un tris
         * - vogliamo molto evitare i tris avversari
         * - ci piacciono posizioni in cui con la mossa successiva
         *   potremmo arrivare ad una situazione in cui nella mossa
         *   dopo avremo un tris
         * - se cerchiamo di fare sempre tris l'avversario ci blocca subito
         */

        return 0;
    }

    @Override
    public double maxEvaluateValue() {
        //TODO: Mettere il massimo restituito da evaluate()
        return 0;
    }

    @Override
    public void next() {
        currentPlayer = 3 - currentPlayer;
        opponentPlayer = 3 - opponentPlayer;
    }

    @Override
    public void previous() {
        currentPlayer = 3 - currentPlayer;
        opponentPlayer = 3 - opponentPlayer;
    }

}
