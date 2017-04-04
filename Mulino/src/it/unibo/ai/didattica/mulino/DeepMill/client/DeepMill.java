package it.unibo.ai.didattica.mulino.DeepMill.client;

import java.io.IOException;

import fr.avianey.minimax4j.Minimax;
import it.unibo.ai.didattica.mulino.DeepMill.client.players.EvaluatorPlayer;
import it.unibo.ai.didattica.mulino.DeepMill.client.players.HumanPlayer;
import it.unibo.ai.didattica.mulino.DeepMill.client.players.IAPlayer;
import it.unibo.ai.didattica.mulino.DeepMill.client.players.ReplayPlayer;
import it.unibo.ai.didattica.mulino.DeepMill.domain.bitboard.BitBoardMinimax;
import it.unibo.ai.didattica.mulino.actions.Action;
import it.unibo.ai.didattica.mulino.actions.Phase1Action;
import it.unibo.ai.didattica.mulino.actions.Phase2Action;
import it.unibo.ai.didattica.mulino.actions.PhaseFinalAction;
import it.unibo.ai.didattica.mulino.client.MulinoClient;
import it.unibo.ai.didattica.mulino.domain.State;
import it.unibo.ai.didattica.mulino.DeepMill.domain.grid.GridMinimax;

public class DeepMill extends MulinoClient {

    public DeepMill(State.Checker player) throws IOException {
        super(player);
    }

    public void write(String actionString, State.Phase phase) throws IOException, ClassNotFoundException {
        Action action = null;
        switch (phase) {
            case FIRST:
                Phase1Action phase1Action = new Phase1Action();
                phase1Action.setPutPosition(actionString.substring(0, 2));
                if (actionString.length() == 4) {
                    phase1Action.setRemoveOpponentChecker(actionString.substring(2, 4));
                } else {
                    phase1Action.setRemoveOpponentChecker(null);
                }
                action = phase1Action;
                break;
            case SECOND:
                Phase2Action phase2Action = new Phase2Action();
                phase2Action.setFrom(actionString.substring(0, 2));
                phase2Action.setTo(actionString.substring(2, 4));
                if (actionString.length() == 6) {
                    phase2Action.setRemoveOpponentChecker(actionString.substring(4, 6));
                } else {
                    phase2Action.setRemoveOpponentChecker(null);
                }
                action = phase2Action;
                break;
            case FINAL:
                PhaseFinalAction phaseFinalAction = new PhaseFinalAction();
                phaseFinalAction.setFrom(actionString.substring(0, 2));
                phaseFinalAction.setTo(actionString.substring(2, 4));
                if (actionString.length() == 6) {
                    phaseFinalAction.setRemoveOpponentChecker(actionString.substring(4, 6));
                } else {
                    phaseFinalAction.setRemoveOpponentChecker(null);
                }
                action = phaseFinalAction;
                break;
        }
        this.write(action);
    }

    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("Usage:");
            System.out.println("-w --white     Set the player to White");
            System.out.println("-b --black     Set the player to Black");
            System.out.println("-h --human     Set the player as Human");
            System.out.println("-i --ia        Set the player as IA");
            System.out.println("-a <algorithm> Set the IA algorithm: minimax, alpha_beta, negamax, negascout");
            System.out.println("-d <depth>     Set the IA algorithm's max search depth (default is no limit)");
            System.out.println("-t <time>      Search w/ iterative deepening for <time> seconds");
            System.out.println("--no-heuristic Disable the algorithm's heuristic");
            System.out.println("--debug        Print the debug output to standard output");
            System.out.println("--grid         Use grid state");
            System.out.println("--bit          Use bit state");
            System.out.println("-r <filename>  Replay a recorded game");
            System.exit(-1);
        }

        State.Checker playerColor = State.Checker.WHITE;
        Minimax.Algorithm algorithm = Minimax.Algorithm.NEGASCOUT;
        int depth = 0;
        int maxTime = 58;
        boolean debug = false;
        boolean bitState = true;
        boolean useHeuristic = true;
        boolean iaPlayer = true;
        boolean evaluator = false;
        String replay = null;
        for (int i = 0; i < args.length; i++) {
            switch (args[i].toLowerCase()) {
                case "-w":
                case "--white":
                    playerColor = State.Checker.WHITE;
                    break;
                case "-b":
                case "--black":
                    playerColor = State.Checker.BLACK;
                    break;
                case "-h":
                case "--human":
                    iaPlayer = false;
                    break;
                case "-i":
                case "--ia":
                    iaPlayer = true;
                    break;
                case "-a":
                    switch (args[++i].toLowerCase()) {
                        case "minimax":
                            algorithm = Minimax.Algorithm.MINIMAX;
                            break;
                        case "alpha_beta":
                            algorithm = Minimax.Algorithm.ALPHA_BETA;
                            break;
                        case "negamax":
                            algorithm = Minimax.Algorithm.NEGAMAX;
                            break;
                        case "negascout":
                            algorithm = Minimax.Algorithm.NEGASCOUT;
                            break;
                    }
                    break;
                case "-d":
                    depth = Integer.parseInt(args[++i]);
                    break;
                case "-t":
                    maxTime = Integer.parseInt(args[++i]);
                    break;
                case "--no-heuristic":
                    useHeuristic = false;
                    break;
                case "--debug":
                    debug = true;
                    break;
                case "--grid":
                    bitState = false;
                    break;
                case "--bit":
                    bitState = true;
                    break;
                case "-r":
                    replay = args[++i];
                    break;
                case "--evaluator":
                    evaluator = true;
                    break;
            }
        }

        if (evaluator) {
            EvaluatorPlayer whitePlayer;
            EvaluatorPlayer blackPlayer;
            if (bitState) {
                whitePlayer = new EvaluatorPlayer<>(State.Checker.WHITE, new BitBoardMinimax(algorithm, useHeuristic));
                blackPlayer = new EvaluatorPlayer<>(State.Checker.BLACK, new BitBoardMinimax(algorithm, useHeuristic));
            } else {
                whitePlayer = new EvaluatorPlayer<>(State.Checker.WHITE, new GridMinimax(algorithm, useHeuristic));
                blackPlayer = new EvaluatorPlayer<>(State.Checker.BLACK, new GridMinimax(algorithm, useHeuristic));
            }

            whitePlayer.start();
            blackPlayer.start();
        } else if (replay != null) {
            ReplayPlayer whitePlayer = new ReplayPlayer(State.Checker.WHITE, replay, maxTime);
            whitePlayer.start();

            ReplayPlayer blackPlayer = new ReplayPlayer(State.Checker.BLACK, replay, maxTime);
            blackPlayer.start();
        } else if (iaPlayer) {
            IAPlayer player;
            if (bitState) {
                player = new IAPlayer<>(playerColor, new BitBoardMinimax(algorithm, useHeuristic), depth, maxTime);
            } else {
                player = new IAPlayer<>(playerColor, new GridMinimax(algorithm, useHeuristic), depth, maxTime);
            }

            player.setDebug(debug);
            player.start();
        } else {
            HumanPlayer player = new HumanPlayer(playerColor);
            player.start();
        }
    }

}