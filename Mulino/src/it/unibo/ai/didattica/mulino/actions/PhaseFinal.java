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
				, FromAndToAreNotConnectedException
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

		// if the move complete a mill, then an enemy checker is removed
		if (Util.hasCompletedTriple(result, currentAction.getTo(), checker))
			Util.removeOpponentChecker(result, checker, currentAction.getRemoveOpponentChecker());
		
		
		
		// REMOVED BECAUSE BUGGED...
		// check if this move allows to remove an opponent checker
//		if (Util.hasCompletedTriple(result, currentAction.getTo(), checker)) {
//			System.out.println("Player " + checker.toString() + " WIN!!!");
//			System.exit(100);
//		}
		
		
		// from the wiki:
		// "Vince il primo giocatore che lascia l'avversario con meno di tre pezzi in gioco o senza possibilità di muovere."
		// condition "senza possibilità di muovere" will be captured by the timeout
		// condition "con meno di tre pezzi" is captured here
		int enemyCheckers;
		if (checker == State.Checker.WHITE)
			enemyCheckers = result.getBlackCheckersOnBoard();
		else
			enemyCheckers = result.getWhiteCheckersOnBoard();
		if (enemyCheckers < 3) {
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
			, FromAndToAreNotConnectedException
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
		
		// we are in the FINAL phase, but only the player who has three checker can move them freely...
		int numOfCheckers;
		if (checker == State.Checker.WHITE)
			numOfCheckers = currentState.getWhiteCheckersOnBoard();
		else
			numOfCheckers = currentState.getBlackCheckersOnBoard();
		if (numOfCheckers > 3) {
			if (!Util.areAdiacent(from, to))
				throw new FromAndToAreNotConnectedException(action);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
