package it.unibo.ai.didattica.mulino.actions;



import it.unibo.ai.didattica.mulino.domain.State;

public class PhaseFinal {
	
	public static State applyMove(State currentState, Action genericAction, State.Checker checker)
				throws NullStateException
				, WrongPhaseException
				, NullActionException
				, WrongPositionException
				, TryingToMoveOpponentCheckerException
				, FromAndToAreEqualsException
				, PositionNotEmptyException
				, NullCheckerException
				, TryingToRemoveOwnCheckerException
				, TryingToRemoveEmptyCheckerException
				, TryingToRemoveCheckerInTripleException
				{
		
		PhaseFinalAction currentAction = null;
		if (genericAction instanceof PhaseFinalAction)
			currentAction = (PhaseFinalAction) genericAction;
		else {
			System.out.println("Engine was expecting PhaseFinalAction instance, but received a different action...");
			System.exit(-4);
		}
		
		// initial checks
		initialChecks(currentState, currentAction, checker);
		
		
		// generate the new State
		State result = currentState.clone();
		
		// move the checker on the board
		result.getBoard().put(currentAction.getTo(), checker);
		result.getBoard().put(currentAction.getFrom(), State.Checker.EMPTY);

		
		// check if this move allows to remove an opponent checker
		if (Util.hasCompletedTriple(result, currentAction.getTo(), checker)) {
			System.out.println("Player " + checker.toString() + " WIN!!!");
			System.exit(100);
		}
		return result;
	}
	
	
	
	
	private static void initialChecks(State currentState, PhaseFinalAction action, State.Checker checker)
			throws 	NullStateException
			, WrongPhaseException
			, NullActionException
			, WrongPositionException
			, TryingToMoveOpponentCheckerException
			, FromAndToAreEqualsException
			, PositionNotEmptyException
			, NullCheckerException
			{
		
		// initial checks
		if (currentState == null)
			throw new NullStateException();
		if (currentState.getCurrentPhase()!= State.Phase.FINAL)
			throw new WrongPhaseException(currentState.getCurrentPhase(), State.Phase.FINAL);
		
		// check the from position
		if (action == null)
			throw new NullActionException();
		String from = action.getFrom();
		if (from == null || "".equals(from))
			throw new WrongPositionException(from);
		State.Checker boardChecker = currentState.getBoard().get(from);
		if (boardChecker == null)
			throw new WrongPositionException(from);
		
		// check the current player
		if (checker == null || checker == State.Checker.EMPTY)
			throw new NullCheckerException();
		if (boardChecker != checker)
			throw new TryingToMoveOpponentCheckerException(action);
		
		// check the "to" position, only condition it must be empty
		String to = action.getTo();
		if (to == null || "".equals(to))
			throw new WrongPositionException(to);
		if (to.equals(from))
			throw new FromAndToAreEqualsException(action);
		State.Checker toChecker = currentState.getBoard().get(to);
		if (toChecker == null)
			throw new WrongPositionException(to);
		if (toChecker != State.Checker.EMPTY)
			throw new PositionNotEmptyException(to);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
