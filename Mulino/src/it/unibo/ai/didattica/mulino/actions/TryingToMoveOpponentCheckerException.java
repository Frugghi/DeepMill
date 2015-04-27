package it.unibo.ai.didattica.mulino.actions;

public class TryingToMoveOpponentCheckerException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TryingToMoveOpponentCheckerException(Action action) {
		super("You are trying to move your opponent checker: " + action.toString());
	}
}
