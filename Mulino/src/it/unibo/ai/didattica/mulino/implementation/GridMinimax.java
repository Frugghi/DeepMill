package it.unibo.ai.didattica.mulino.implementation;

import it.unibo.ai.didattica.mulino.domain.MillMinimax;
import it.unibo.ai.didattica.mulino.domain.State;

import java.util.ArrayList;
import java.util.List;

public class GridMinimax extends MillMinimax<GridMove> {

    public static final int FREE     = 0;
    public static final int PLAYER_W = 1;
    public static final int PLAYER_B = 2;

    private static final int Z_SIZE = 3;
    private static final int X_SIZE = 8;

    private final int[] played;
    private final int[] count;
    private final int[][] grid;

    private int currentPlayer;
    private int opponentPlayer;

    public GridMinimax(Algorithm algo) {
        super(algo);

        this.grid = new int[Z_SIZE][X_SIZE];
        this.played = new int[3];
        this.count = new int[3];

        newGame();
    }

    public void newGame() {
        for (int z = 0; z < Z_SIZE; z++) {
            for (int x = 0; x < X_SIZE; x++) {
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

    public void setPlayed(int white, int black) {
        played[FREE] = PIECES * 2 - (white + black);
        played[PLAYER_W] = white;
        played[PLAYER_B] = black;
    }

    public int[] getPlayed() {
        return played;
    }

    public void setCount(int white, int black) {
        count[FREE] = Z_SIZE * X_SIZE - (white + black);
        count[PLAYER_W] = white;
        count[PLAYER_B] = black;
    }

    public int[] getCount() {
        return count;
    }

    @Override
    public boolean isOver() {
        return hasWon(PLAYER_W) || hasWon(PLAYER_B);
    }

    private boolean hasWon(int player) {
        return played[FREE] == 0 && 
                (count[3 - player] <= 2 ||                                  // L'avversario ha meno di 3 pezzi
                numberOfPiecesBlocked(3 - player) == count[3 - player]);    // L'avversario non può muoversi
    }

    private void setGridPosition(int player, int x, int z) {
        grid[z][x] = player;
    }

    public void setGridPosition(State.Checker player, String position) {
        int[] coordinates = GridMove.string2Coordinates(position);

        int checker = GridMinimax.FREE;
        switch (player) {
            case WHITE:
                checker = GridMinimax.PLAYER_W;
                break;
            case BLACK:
                checker = GridMinimax.PLAYER_B;
                break;
            case EMPTY:
                checker = GridMinimax.FREE;
                break;
        }

        this.setGridPosition(checker, coordinates[0], coordinates[1]);
    }

    @Override
    public void makeMove(GridMove move) {
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
    public void unmakeMove(GridMove move) {
        previous();

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
    }

    @Override
    public List<GridMove> getPossibleMoves() {
        List<GridMove> moves = new ArrayList<GridMove>(3 * count[FREE]);
        if (played[currentPlayer] < PIECES) { // Fase 1
            for (int toZ = 0; toZ < Z_SIZE; toZ++) {
                for (int toX = 0; toX < X_SIZE; toX++) {
                    this.addMoves(moves, Integer.MAX_VALUE, Integer.MAX_VALUE, toX, toZ);
                }
            }
        } else if (count[currentPlayer] == 3) { // Fase 3
            for (int fromZ = 0; fromZ < Z_SIZE; fromZ++) {
                for (int fromX = 0; fromX < X_SIZE; fromX++) {
                    if (grid[fromZ][fromX] == currentPlayer) {
                        for (int toZ = 0; toZ < Z_SIZE; toZ++) {
                            for (int toX = 0; toX < X_SIZE; toX++) {
                                this.addMoves(moves, fromX, fromZ, toX, toZ);
                            }
                        }
                    }
                }
            }
        } else { // Fase 2
            for (int fromZ = 0; fromZ < Z_SIZE; fromZ++) {
                for (int fromX = 0; fromX < X_SIZE; fromX++) {
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
                            switch (fromX) {
                                case X_SIZE - 1:
                                    this.addMoves(moves, fromX, fromZ, 0, fromZ);
                                    break;
                                default:
                                    this.addMoves(moves, fromX, fromZ, fromX + 1, fromZ);
                                    break;
                            }
                            this.addMoves(moves, fromX, fromZ, fromX - 1, fromZ);
                        } else {
                            switch (fromX) {
                                case 0:
                                    this.addMoves(moves, fromX, fromZ, fromX + 1, fromZ);
                                    this.addMoves(moves, fromX, fromZ, X_SIZE - 1, fromZ);
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
            switch (x) {
                case X_SIZE - 1:
                    return (grid[z][x - 1] == player && grid[z][x] == player && grid[z][0] == player) ||
                            (grid[0][x] == player && grid[1][x] == player && grid[2][x] == player);
                default:
                    return (grid[z][x - 1] == player && grid[z][x] == player && grid[z][x + 1] == player) ||
                            (grid[0][x] == player && grid[1][x] == player && grid[2][x] == player);
            }
        } else {
            switch (x) {
                case 0:
                    return grid[z][x] == player && (
                            (grid[z][X_SIZE - 2] == player && grid[z][X_SIZE - 1] == player) || (grid[z][x + 1] == player && grid[z][x + 2] == player));
                case X_SIZE - 2:
                    return grid[z][x] == player && (
                            (grid[z][x - 2] == player && grid[z][x - 1] == player) || (grid[z][x + 1] == player && grid[z][0] == player));
                default:
                    return grid[z][x] == player && (
                            (grid[z][x - 2] == player && grid[z][x - 1] == player) || (grid[z][x + 1] == player && grid[z][x + 2] == player));
            }
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
                    switch (toX) {
                        case X_SIZE - 1:
                            return grid[toZ][toX - 1] == player && grid[toZ][0] == player;
                        default:
                            return grid[toZ][toX - 1] == player && grid[toZ][toX + 1] == player;
                    }
                }
            } else {
                switch (toX) {
                    case 0:
                        return (grid[toZ][X_SIZE - 2] == player && grid[toZ][X_SIZE - 1] == player) || (grid[toZ][toX + 1] == player && grid[toZ][toX + 2] == player);
                    case X_SIZE - 2:
                        return (grid[toZ][toX - 2] == player && grid[toZ][toX - 1] == player) || (grid[toZ][toX + 1] == player && grid[toZ][0] == player);
                    default:
                        return (grid[toZ][toX - 2] == player && grid[toZ][toX - 1] == player) || (grid[toZ][toX + 1] == player && grid[toZ][toX + 2] == player);
                }
            }
        } else { // Modifichiamo la board per questo calcolo... non penso sia una grande idea!!
            boolean willMill = false;

            setGridPosition(FREE, fromX, fromZ);

            willMill = willMill(player, Integer.MAX_VALUE, Integer.MAX_VALUE, toX, toZ);

            setGridPosition(player, fromX, fromZ);

            return willMill;
        }
    }

    private void addMoves(List<GridMove> moves, int fromX, int fromZ, int toX, int toZ) {
        if (grid[toZ][toX] == FREE) {
            if (this.willMill(currentPlayer, fromX, fromZ, toX, toZ)) {
                for (int removeZ = 0; removeZ < Z_SIZE; removeZ++) {
                    for (int removeX = 0; removeX < X_SIZE; removeX++) {
                        if (grid[removeZ][removeX] == opponentPlayer && !this.isMill(opponentPlayer, removeX, removeZ)) {
                            moves.add(0, new GridMove(currentPlayer, fromX, fromZ, toX, toZ, removeX, removeZ));
                        }
                    }
                }
            } else {
                moves.add(new GridMove(currentPlayer, fromX, fromZ, toX, toZ));
            }
        }
    }

    @Override
    public double evaluate() {
        if (this.hasWon(currentPlayer)) { // Se vinco � la mossa migliore
            return this.maxEvaluateValue();
        } else if (this.hasWon(opponentPlayer)) { // Se perdo � la mossa peggiore
            return -this.maxEvaluateValue();
        }

        // Il numero di spostamenti di un pezzo potrebbe essere un buon indicatore per discriminare quando si � in parit�
        int[] availableMoves = new int[]{0, 0, 0};
        for (int z = 0; z < Z_SIZE; z++) {
            for (int x = 0; x < X_SIZE; x++) {
                if (x < X_SIZE - 1) {
                    if (grid[z][x] !=  grid[z][x + 1] && (grid[z][x] == FREE || grid[z][x + 1] == FREE)) {
                        availableMoves[Math.max(grid[z][x], grid[z][x + 1])]++;
                    }
                } else if (grid[z][x] !=  grid[z][0] && (grid[z][x] == FREE || grid[z][0] == FREE)) {
                    availableMoves[Math.max(grid[z][x], grid[z][0])]++;
                }

                if (x % 2 == 1 && z < Z_SIZE - 1 && grid[z][x] !=  grid[z + 1][x] && (grid[z][x] == FREE || grid[z + 1][x] == FREE)) {
                    availableMoves[Math.max(grid[z][x], grid[z + 1][x])]++;
                }
            }
        }
        
        return 5 * (count[currentPlayer] - count[opponentPlayer]) //diff numero pezzi
                + (availableMoves[currentPlayer] - availableMoves[opponentPlayer])
                + (this.numberOfMorrises(currentPlayer) - this.numberOfMorrises(opponentPlayer))  //diff numero di morris
                + (this.numberOfPiecesBlocked(currentPlayer) - this.numberOfPiecesBlocked(opponentPlayer)); //diff pezzi bloccati
    }

    @Override
    public double maxEvaluateValue() {
        return 100;
    }
    

    private int numberOfMorrises(int player){
        int numberOfMorrises = 0;
        for (int z = 0; z < Z_SIZE; z++) {
            for (int x = 1; x < X_SIZE; x+=2) {

                if(z==0 || z==2){ //quadrati piu esterno e quello piu interno
                    if(this.isMill(player, x, z))
                        numberOfMorrises++; //basta guardare la cella a meta di ogni lato
                }
                else{
                    //quadrato intermedio
                    if(this.isMill(player, x, z)){
                        if(this.isMill(player, x, z+1) && this.isMill(player, x, z-1)) //mill verticale
                            numberOfMorrises++;
                        if(this.isMill(player, x+1, z) && this.isMill(player, x-1, z)) //mill orizzontale
                            numberOfMorrises++;

                    }
                }
            }
        }
        System.out.println("Number of morrises player ("+ player+ ") : "+ numberOfMorrises);
        return numberOfMorrises;
    }
    
    private boolean pieceIsBlocked(int player, int z, int x){
        if(x % 2 == 0){ //siamo negli angoli
                if(grid[z][x == 0 ? 7 : x-1] != FREE && grid[z][x+1] != FREE)
                    return true;
        }
        else{//siamo al centro del lato
            if(grid[z][x == 7 ? 0 : x+1] != FREE && grid[z][x-1] != FREE){ //controllo gli angoli adiacenti

                switch(z){
                case 0: //esterno
                    if(grid[z+1][x] != FREE)
                        return true;
                    break;
                case 1: //intermedio
                    if(grid[z+1][x] != FREE && grid[z-1][x] != FREE)
                        return true;
                    break;
                case 2: //interno
                    if(grid[z-1][x] != FREE)
                        return true;
                    break;
                default:
                        return false;

                }

            }
        }
        return false;
    }
    
    private int numberOfPiecesBlocked(int player){
        int totBlocked = 0;

        for (int z = 0; z < Z_SIZE; z++) {
            for (int x = 1; x < X_SIZE; x+=1) {
                if(grid[z][x] == player) //ciclo sulle pedine del giocatore
                    if(this.pieceIsBlocked(player, z, x))
                        totBlocked++;
            }
        }

        System.out.println("Number of pieces blocked player ("+ player+ ") : "+ totBlocked);
        return totBlocked;
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

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append("7 " + grid[0][6] + "--------" + grid[0][5] + "--------" + grid[0][4] + "\n");
        result.append("6 |--" + grid[1][6] + "-----" + grid[1][5] + "-----" + grid[1][4] + "--|\n");
        result.append("5 |--|--" + grid[2][6] + "--" + grid[2][5] + "--" + grid[2][4] + "--|--|\n");
        result.append("4 " + grid[0][7] + "--" + grid[1][7] + "--" + grid[2][7] + "     " + grid[2][3] + "--" + grid[1][3] + "--" + grid[0][3] +"\n");
        result.append("3 |--|--" + grid[1][0] + "--" + grid[2][1] + "--" + grid[2][2] + "--|--|\n");
        result.append("2 |--" + grid[1][0] + "-----" + grid[1][1] + "-----" + grid[1][2] + "--|\n");
        result.append("1 " + grid[0][0] + "--------" + grid[0][1] + "--------" + grid[0][2] + "\n");
        result.append("  a  b  c  d  e  f  g\n");
        result.append("White Played Checkers: " + played[PLAYER_W] + ";\n");
        result.append("Black Played Checkers: " + played[PLAYER_B] + ";\n");
        result.append("White Checkers On Board: " + count[PLAYER_W] + ";\n");
        result.append("Black Checkers On Board: " + count[PLAYER_B] + ";\n");
        result.append("Current player: " + currentPlayer + ";\n");
        result.append("Opponent player: " + opponentPlayer + ";\n");

        return result.toString();
    }

    public GridMinimax cloneState() {
        GridMinimax clone = new GridMinimax(this.getAlgo());

        for (int z = 0; z < Z_SIZE; z++) {
            for (int x = 0; x < X_SIZE; x++) {
                clone.grid[z][x] = this.grid[z][x];
            }
        }

        clone.currentPlayer = this.currentPlayer;
        clone.opponentPlayer = this.opponentPlayer;

        clone.count[FREE] = this.count[FREE];
        clone.count[PLAYER_W] = this.count[PLAYER_W];
        clone.count[PLAYER_B] = this.count[PLAYER_B];

        clone.played[FREE] = this.played[FREE];
        clone.played[PLAYER_W] = this.played[PLAYER_W];
        clone.played[PLAYER_B] = this.played[PLAYER_B];

        return clone;
    }

}