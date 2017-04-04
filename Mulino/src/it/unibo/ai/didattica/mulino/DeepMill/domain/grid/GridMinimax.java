package it.unibo.ai.didattica.mulino.DeepMill.domain.grid;

import fr.avianey.minimax4j.Minimax;
import it.unibo.ai.didattica.mulino.DeepMill.domain.MillMinimax;
import it.unibo.ai.didattica.mulino.domain.State;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GridMinimax extends MillMinimax<GridMove, Integer, GridMinimax> {

    public static final int FREE     = 0;
    public static final int PLAYER_W = 1;
    public static final int PLAYER_B = 2;

    private static final int Z_SIZE = 3;
    private static final int X_SIZE = 8;

    private final int[] played = new int[3];
    private final int[] count = new int[3];
    private final int[][] grid = new int[Z_SIZE][X_SIZE];

    private int currentPlayer;
    private int opponentPlayer;

    public GridMinimax(Minimax.Algorithm algo, boolean useHeuristic) {
        super(algo, useHeuristic);

        for (int z = 0; z < Z_SIZE; z++) {
            for (int x = 0; x < X_SIZE; x++) {
                this.grid[z][x] = FREE;
            }
        }

        this.played[FREE] = PIECES * 2;
        this.played[PLAYER_W] = 0;
        this.played[PLAYER_B] = 0;

        this.count[FREE] = Z_SIZE * X_SIZE;
        this.count[PLAYER_W] = 0;
        this.count[PLAYER_B] = 0;

        this.currentPlayer = PLAYER_W;
        this.opponentPlayer = PLAYER_B;
    }

    @Override
    public GridMinimax fromState(State state) {
        GridMinimax ia =  new GridMinimax(this.getAlgo(), this.isUsingHeuristic());
        ia.updateState(state);
        ia.currentPlayer = this.currentPlayer;
        ia.opponentPlayer = this.opponentPlayer;
        ia.next();

        return ia;
    }

    protected void setPlayed(int white, int black) {
        played[FREE] = PIECES * 2 - (white + black);
        played[PLAYER_W] = white;
        played[PLAYER_B] = black;
    }

    protected void setCount(int white, int black) {
        count[FREE] = Z_SIZE * X_SIZE - (white + black);
        count[PLAYER_W] = white;
        count[PLAYER_B] = black;
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
        List<GridMove> moves = new ArrayList<>(3 * count[FREE]);
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
            setGridPosition(FREE, fromX, fromZ);

            boolean willMill = willMill(player, Integer.MAX_VALUE, Integer.MAX_VALUE, toX, toZ);

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
    protected boolean isQuiet() {
        return true;
    }

    @Override
    public double evaluate() {
        if (this.hasWon(currentPlayer)) { // Se vinco e' la mossa migliore
            return this.maxEvaluateValue();
        } else if (this.hasWon(opponentPlayer)) { // Se perdo e' la mossa peggiore
            return -this.maxEvaluateValue();
        }

        // Il numero di spostamenti di un pezzo potrebbe essere un buon indicatore per discriminare quando si e' in parita'
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
                        if(this.isMill(player,(x == 7 ? 0 : x+1), z) && this.isMill(player, x-1, z)) //mill orizzontale
                            numberOfMorrises++;

                    }
                }
            }
        }
        //System.out.println("Number of morrises player ("+ player+ ") : "+ numberOfMorrises);
        return numberOfMorrises;
    }
    
    private boolean pieceIsBlocked(int z, int x){
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
        if (this.played[player] == PIECES && this.count[player] == 3) {
            return 0;
        }

        int totBlocked = 0;

        for (int z = 0; z < Z_SIZE; z++) {
            for (int x = 1; x < X_SIZE; x+=1) {
                if(grid[z][x] == player) //ciclo sulle pedine del giocatore
                    if (this.pieceIsBlocked(z, x)) {
                        totBlocked++;
                    }
            }
        }

        //System.out.println("Number of pieces blocked player ("+ player+ ") : "+ totBlocked);
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
    public Integer getTransposition() {
        return null;
    }

    @Override
    public String toString() {
        String result = "";
        result += "7 " + grid[0][6] + "--------" + grid[0][5] + "--------" + grid[0][4] + "\n";
        result += "6 |--" + grid[1][6] + "-----" + grid[1][5] + "-----" + grid[1][4] + "--|\n";
        result += "5 |--|--" + grid[2][6] + "--" + grid[2][5] + "--" + grid[2][4] + "--|--|\n";
        result += "4 " + grid[0][7] + "--" + grid[1][7] + "--" + grid[2][7] + "     " + grid[2][3] + "--" + grid[1][3] + "--" + grid[0][3] +"\n";
        result += "3 |--|--" + grid[1][0] + "--" + grid[2][1] + "--" + grid[2][2] + "--|--|\n";
        result += "2 |--" + grid[1][0] + "-----" + grid[1][1] + "-----" + grid[1][2] + "--|\n";
        result += "1 " + grid[0][0] + "--------" + grid[0][1] + "--------" + grid[0][2] + "\n";
        result += "  a  b  c  d  e  f  g\n";
        result += "White Played Checkers: " + played[PLAYER_W] + ";\n";
        result += "Black Played Checkers: " + played[PLAYER_B] + ";\n";
        result += "White Checkers On Board: " + count[PLAYER_W] + ";\n";
        result += "Black Checkers On Board: " + count[PLAYER_B] + ";\n";
        result += "Current player: " + currentPlayer + ";\n";
        result += "Opponent player: " + opponentPlayer + ";\n";
        result += "\n";

        result += "Number of morrises player ("+ PLAYER_W+ ") : "+ this.numberOfMorrises(PLAYER_W)+ "\n";
        result += "Number of morrises player ("+ PLAYER_B+ ") : "+ this.numberOfMorrises(PLAYER_B)+ "\n";
        result += "Number of pieces blocked player ("+ PLAYER_W+ ") : "+ this.numberOfPiecesBlocked(PLAYER_W)+ "\n";
        result += "Number of pieces blocked player ("+ PLAYER_B+ ") : "+ this.numberOfPiecesBlocked(PLAYER_B)+ "\n";

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GridMinimax)) return false;

        GridMinimax that = (GridMinimax) o;

        if (currentPlayer != that.currentPlayer) return false;
        if (opponentPlayer != that.opponentPlayer) return false;
        if (!Arrays.equals(played, that.played)) return false;
        if (!Arrays.equals(count, that.count)) return false;
        return Arrays.deepEquals(grid, that.grid);

    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(played);
        result = 31 * result + Arrays.hashCode(count);
        result = 31 * result + Arrays.deepHashCode(grid);
        result = 31 * result + currentPlayer;
        result = 31 * result + opponentPlayer;
        return result;
    }

}