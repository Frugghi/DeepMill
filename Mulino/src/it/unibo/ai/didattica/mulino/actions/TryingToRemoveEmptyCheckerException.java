package it.unibo.ai.didattica.mulino.actions;

public class TryingToRemoveEmptyCheckerException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TryingToRemoveEmptyCheckerException() {
		super("You are trying to remove your own checker...");
	}
}
