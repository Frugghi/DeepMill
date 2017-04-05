package it.unibo.ai.didattica.mulino.domain;

import java.io.Serializable;
import java.util.HashMap;

public class State implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum Phase {
		FIRST("First"), SECOND("Second"), FINAL("Final");
		private final String name;

		private Phase(String s) {
			name = s;
		}

		public boolean equalsName(String otherName) {
			return (otherName == null) ? false : name.equals(otherName);
		}

		public String toString() {
			return name;
		}
	}

	public enum Checker {
		EMPTY('O'), WHITE('W'), BLACK('B');
		private final char checker;

		private Checker(char s) {
			checker = s;
		}

		public boolean equalsChecker(char otherChecker) {
			return otherChecker == checker;
		}

		public String toString() {
			return "" + checker;
		}

	}

	/**
	 * the board with the checkers over it
	 */
	private HashMap<String, Checker> board = new HashMap<>();

	/**
	 * current phase of the game
	 */
	private Phase currentPhase = Phase.FIRST;

	/**
	 * positions on the board
	 */
	public final String[] positions = { "a1", "a4", "a7", "b2", "b4", "b6", "c3", "c4", "c5", "d1", "d2", "d3", "d5",
			"d6", "d7", "e3", "e4", "e5", "f2", "f4", "f6", "g1", "g4", "g7" };

	private int whiteCheckers = 9;
	private int blackCheckers = 9;

	private int whiteCheckersOnBoard = 0;
	private int blackCheckersOnBoard = 0;

	public State() {
		// init the board
		for (String s : positions)
			board.put(s, Checker.EMPTY);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + blackCheckers;
		result = prime * result + blackCheckersOnBoard;
		result = prime * result + ((board == null) ? 0 : board.hashCode());
		result = prime * result + ((currentPhase == null) ? 0 : currentPhase.hashCode());
		result = prime * result + whiteCheckers;
		result = prime * result + whiteCheckersOnBoard;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		State other = (State) obj;
		if (blackCheckers != other.blackCheckers)
			return false;
		if (blackCheckersOnBoard != other.blackCheckersOnBoard)
			return false;
		if (board == null) {
			if (other.board != null)
				return false;
		} else if (!board.equals(other.board))
			return false;
		if (currentPhase != other.currentPhase)
			return false;
		if (whiteCheckers != other.whiteCheckers)
			return false;
		if (whiteCheckersOnBoard != other.whiteCheckersOnBoard)
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append("7 " + board.get("a7") + "--------" + board.get("d7") + "--------" + board.get("g7") + "\n");
		result.append("6 |--" + board.get("b6") + "-----" + board.get("d6") + "-----" + board.get("f6") + "--|\n");
		result.append("5 |--|--" + board.get("c5") + "--" + board.get("d5") + "--" + board.get("e5") + "--|--|\n");
		result.append("4 " + board.get("a4") + "--" + board.get("b4") + "--" + board.get("c4") + "     "
				+ board.get("e4") + "--" + board.get("f4") + "--" + board.get("g4") + "\n");
		result.append("3 |--|--" + board.get("c3") + "--" + board.get("d3") + "--" + board.get("e3") + "--|--|\n");
		result.append("2 |--" + board.get("b2") + "-----" + board.get("d2") + "-----" + board.get("f2") + "--|\n");
		result.append("1 " + board.get("a1") + "--------" + board.get("d1") + "--------" + board.get("g1") + "\n");
		result.append("  a  b  c  d  e  f  g\n");
		result.append("Phase: " + currentPhase.toString() + ";\n");
		result.append("White Checkers: " + whiteCheckers + ";\n");
		result.append("Black Checkers: " + blackCheckers + ";\n");
		result.append("White Checkers On Board: " + whiteCheckersOnBoard + ";\n");
		result.append("Black Checkers On Board: " + blackCheckersOnBoard + ";\n");
		return result.toString();
	}

	// getters and setters
	public HashMap<String, Checker> getBoard() {
		return board;
	}

	public void setBoard(HashMap<String, Checker> hashMap) {
		this.board = hashMap;
	}

	public String[] getPositions() {
		return positions;
	}

	public Phase getCurrentPhase() {
		return currentPhase;
	}

	public void setCurrentPhase(Phase currentPhase) {
		this.currentPhase = currentPhase;
	}

	public int getWhiteCheckers() {
		return whiteCheckers;
	}

	public void setWhiteCheckers(int whiteCheckers) {
		this.whiteCheckers = whiteCheckers;
	}

	public int getBlackCheckers() {
		return blackCheckers;
	}

	public void setBlackCheckers(int blackCheckers) {
		this.blackCheckers = blackCheckers;
	}

	public int getWhiteCheckersOnBoard() {
		return whiteCheckersOnBoard;
	}

	public void setWhiteCheckersOnBoard(int whiteCheckersOnBoard) {
		this.whiteCheckersOnBoard = whiteCheckersOnBoard;
	}

	public int getBlackCheckersOnBoard() {
		return blackCheckersOnBoard;
	}

	public void setBlackCheckersOnBoard(int blackCheckersOnBoard) {
		this.blackCheckersOnBoard = blackCheckersOnBoard;
	}

	public State clone() {
		// generate the new State
		State result = new State();

		// replicate the current board
		result.getBoard().putAll(this.getBoard());

		// update the checkers available to the players
		result.setWhiteCheckers(this.getWhiteCheckers());
		result.setBlackCheckers(this.getBlackCheckers());
		result.setWhiteCheckersOnBoard(this.getWhiteCheckersOnBoard());
		result.setBlackCheckersOnBoard(this.getBlackCheckersOnBoard());

		// update the phase
		result.setCurrentPhase(this.getCurrentPhase());

		return result;
	}

	public static void main(String[] args) {
		State aState = new State();
		System.out.println(aState.toString());
	}

}
