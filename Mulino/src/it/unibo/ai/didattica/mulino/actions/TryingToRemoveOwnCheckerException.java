package it.unibo.ai.didattica.mulino.actions;

public class TryingToRemoveOwnCheckerException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TryingToRemoveOwnCheckerException() {
		super("You are trying to remove your own checker...");
	}
}
