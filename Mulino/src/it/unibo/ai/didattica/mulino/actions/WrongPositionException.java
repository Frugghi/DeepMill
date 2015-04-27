package it.unibo.ai.didattica.mulino.actions;

public class WrongPositionException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public WrongPositionException(String position) {
		super("Position " + position + " does not exist!");
	}
}
