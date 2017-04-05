package it.unibo.ai.didattica.mulino.actions;



public class TryingToRemoveCheckerInTripleException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TryingToRemoveCheckerInTripleException(String asked, String available) {
		super("It has been asked to remove checker in pos. " + asked + ", but in pos. " + available + " there is one checker available!");
	}
}
