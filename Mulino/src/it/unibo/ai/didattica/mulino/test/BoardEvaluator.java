package it.unibo.ai.didattica.mulino.test;

import fr.avianey.minimax4j.Minimax;
import it.unibo.ai.didattica.mulino.implementation.BitBoardMinimax;
import it.unibo.ai.didattica.mulino.implementation.BitBoardMove;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class BoardEvaluator {

    public static void main(String[] args) {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("BOARD EVALUATOR:");
        System.out.println("1 - Load board");
        System.out.println("2 - Evaluate board");
        System.out.println("3 - Generate possible moves");
        System.out.println("4 - Unmake last move");
        System.out.println("5 - Clear board");
        System.out.println("6 - Exit");

        BitBoardMinimax ia = new BitBoardMinimax(Minimax.Algorithm.NEGASCOUT, true);
        String action;
        try {
            while ((action = in.readLine()) != null) {
                switch (action) {
                    case "1":
                        byte player = 0;
                        String m;
                        while ((m = in.readLine()).length() == 2) {
                            BitBoardMove move = new BitBoardMove(player, BitBoardMove.string2byte(m));
                            ia.makeMove(move);
                            player = (byte) (1 - player);
                        }
                        break;
                    case "2":
                        System.out.println(ia.toString());
                        break;
                    case "3":
                        List<BitBoardMove> moves = ia.getPossibleMoves();
                        System.out.println(moves.size() + " moves:");
                        for (BitBoardMove move : moves) {
                            System.out.println(" - " + move.toStringMove());
                        }
                        break;
                    case "4":
                        ia.unmakeMove(ia.getLastMove());
                        break;
                    case "5":
                        ia = new BitBoardMinimax(ia.getAlgo(), ia.isUsingHeuristic());
                        break;
                    case "6":
                        System.exit(0);
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
