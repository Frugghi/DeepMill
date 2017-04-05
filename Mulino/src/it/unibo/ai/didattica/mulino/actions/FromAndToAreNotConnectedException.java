package it.unibo.ai.didattica.mulino.actions;

public class FromAndToAreNotConnectedException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FromAndToAreNotConnectedException(Action action) {
		super("The from and to position are the same: " + action.toString());
	}
}
