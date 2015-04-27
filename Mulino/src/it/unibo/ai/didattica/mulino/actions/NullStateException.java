package it.unibo.ai.didattica.mulino.actions;

public class NullStateException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NullStateException() {
		super("State is null!");
	}
}
