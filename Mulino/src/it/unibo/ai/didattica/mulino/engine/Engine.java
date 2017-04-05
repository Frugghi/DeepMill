
package it.unibo.ai.didattica.mulino.engine;

import java.io.IOException;
import java.util.*;

import it.unibo.ai.didattica.mulino.actions.Action;
import it.unibo.ai.didattica.mulino.actions.Phase1;
import it.unibo.ai.didattica.mulino.actions.Phase2;
import it.unibo.ai.didattica.mulino.actions.PhaseFinal;
import it.unibo.ai.didattica.mulino.domain.State;
import it.unibo.ai.didattica.mulino.domain.State.Checker;
import it.unibo.ai.didattica.mulino.gui.GUI;

/**
 * Server per il gioco del mulino
 * 
 * @author Federico Chesani, Andrea Galassi
 *
 */
public class Engine {

	private static State currentState = null;
	private static State.Checker currentPlayer;
	private static Action currentAction;
	private static int timeout;
	private static int cacheSize;

	private int whiteMoves;
	private int blackMoves;

	private TCPMulino whiteSocket;
	private TCPMulino blackSocket;

	private GUI theGui;

	public Engine(int timeout) {
		this(timeout, 16);
	}

	public Engine() {
		this(60, 16);
	}

	/**
	 * Crea una partita del gioco del mulino
	 * 
	 * @param timeout
	 *            Il numero di secondi concessi a ogni giocatore per effettuare
	 *            la sua mossa
	 * @param cacheSize
	 *            Il numero di stati mantenuti in memoria per verificare il
	 *            verificarsi di eventuali loop
	 */
	public Engine(int timeout, int cacheSize) {
		System.out.println("Launching game with timeout " + timeout);
		currentState = new State();
		// currentState.getBoard().put("g1", State.Checker.WHITE);
		currentPlayer = State.Checker.WHITE;

		Engine.timeout = timeout;
		Engine.cacheSize = cacheSize;
		theGui = new GUI();
		theGui.update(currentState);
	}

	public void run() throws IOException {
		Date starttime = new Date();
		Thread t;
		System.out.println("Waiting for connections...");

		// Stabilisce le connessioni con i due giocatori.
		// Prima il bianco poi il nero
		whiteSocket = new TCPMulino(State.Checker.WHITE);
		whiteSocket.writeState(currentState);
		blackSocket = new TCPMulino(State.Checker.BLACK);
		blackSocket.writeState(currentState);
		System.out.println(currentState.toString());

		TCPInput whiteRunner = new TCPInput(whiteSocket);
		TCPInput blackRunner = new TCPInput(blackSocket);

		whiteMoves = 0;
		blackMoves = 0;

		TCPInput tin = null;

		List<State> recentstates = new LinkedList<State>();

		while (true) {
			currentAction = null;

			System.out.println("Waiting for " + currentPlayer.toString() + " move...");

			// nel caso la partita duri più di 5 ore viene terminata
			Date thistime = new Date();
			long hoursoccurred = (thistime.getTime() - starttime.getTime()) / 60 / 60 / 1000;
			if (hoursoccurred > 5) {
				System.out.println("\n\nToo much time is passed since the begininng!!! Hours: " + hoursoccurred);
			}

			// attende la mossa di un giocatore
			switch (currentPlayer) {
			case WHITE:
				tin = whiteRunner;
				break;
			case BLACK:
				tin = blackRunner;
				break;
			default:
				System.exit(4);
			}
			t = new Thread(tin);
			t.start();

			try {
				int counter;
				counter = 0;
				while (counter < timeout && t.isAlive()) {
					Thread.sleep(1000);
					counter++;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (t.isAlive()) {
				System.out.println("Timeout!!!!");
				System.out.println("Player " + currentPlayer.toString() + " has lost!!!");
				System.exit(0);
			}
			try {
				int moves = 0;
				if (currentPlayer == Checker.WHITE) {
					whiteMoves++;
					moves = whiteMoves;
				} else if (currentPlayer == Checker.BLACK) {
					blackMoves++;
					moves = blackMoves;
				}

				// se non viene scelta una mossa, il giocatore di turno perde
				if (currentAction == null) {
					System.out.println("Player " + currentPlayer.toString() + "has not chosen move n" + moves);
					if (currentPlayer == Checker.BLACK)
						System.out.println("Player W WIN!!!");
					else if (currentPlayer == Checker.WHITE)
						System.out.println("Player B WIN!!!");
					System.exit(0);
				}

				System.out.println(
						"Player " + currentPlayer.toString() + " move n" + moves + ": " + currentAction.toString());
				switch (currentState.getCurrentPhase()) {
				case FIRST:
					currentState = Phase1.applyMove(currentState, currentAction, currentPlayer);
					break;
				case SECOND:
					currentState = Phase2.applyMove(currentState, currentAction, currentPlayer);
					break;
				case FINAL:
					currentState = PhaseFinal.applyMove(currentState, currentAction, currentPlayer);
					break;
				default:
					System.out.println("Phase not recognized...");
					System.exit(-10);
				}

				// LOOP CHECK WITH STATES
				// A cache of n states. If the same state appears, exit

				if (recentstates.contains(currentState)) {
					System.out.println("\n\nLOOOP!!!");
					System.out.println(currentState);
					System.out.println(
							"Same state " + (recentstates.size() - recentstates.indexOf(currentState)) + " state ago");
					System.exit(0);
				}
				recentstates.add(currentState);
				if (recentstates.size() > cacheSize) {
					recentstates.remove(0);
				}

				whiteSocket.writeState(currentState);
				blackSocket.writeState(currentState);
				System.out.println(currentState.toString());
				theGui.update(currentState);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
			currentPlayer = (currentPlayer == State.Checker.WHITE) ? State.Checker.BLACK : State.Checker.WHITE;
		}

		// System.out.println("Game finished! State:");
		// System.out.println(currentState.toString());
	}

	public static void main(String[] args) throws IOException {

		int timeout = 60;
		int cacheSize = 16;
		if (args.length > 1 && args[1] != null) {
			String arg1 = "" + args[1];

			try {
				timeout = Integer.parseInt(arg1);
			} catch (Exception e) {
				System.out.println("Wrong timeout format. Using default: " + timeout);
			}
			if (args.length > 2 && args[2] != null) {
				String arg2 = "" + args[2];

				try {
					cacheSize = Integer.parseInt(arg2);
				} catch (Exception e) {
					System.out.println("Wrong cacheSize format. Using default: " + cacheSize);
				}

			}
		}
		Engine eee = null;
		eee = new Engine(timeout, cacheSize);
		eee.run();

	}

	private class TCPInput implements Runnable {
		private TCPMulino theSocket;

		public TCPInput(TCPMulino theSocket) {
			this.theSocket = theSocket;
		}

		public void run() {
			try {
				currentAction = theSocket.readAction();
			} catch (Exception e) {
			}
		}
	}

}
