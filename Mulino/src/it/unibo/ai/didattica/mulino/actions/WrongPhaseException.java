package it.unibo.ai.didattica.mulino.actions;

import it.unibo.ai.didattica.mulino.domain.State;

public class WrongPhaseException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public WrongPhaseException(State.Phase requested, State.Phase found) {
		super("Requeste to apply an action of Phase " + requested.toString() +
				" to a state in Phase " + found.toString() + "!");
	}
}
