package it.unibo.ai.didattica.mulino.actions;

import it.unibo.ai.didattica.mulino.domain.State;

public class Phase2 {
	
	public static State applyMove(State currentState, Action genericAction, State.Checker checker)
				throws NullStateException
				, WrongPhaseException
				, NullActionException
				, WrongPositionException
				, TryingToMoveOpponentCheckerException
				, FromAndToAreEqualsException
				, FromAndToAreNotConnectedException
				, PositionNotEmptyException
				, NullCheckerException
				, TryingToRemoveOwnCheckerException
				, TryingToRemoveEmptyCheckerException
				, TryingToRemoveCheckerInTripleException
				{
		
		Phase2Action currentAction = null;
		if (genericAction instanceof Phase2Action)
			currentAction = (Phase2Action) genericAction;
		else {
			System.out.println("Engine expecting a Phase2Action, found something different...");
			System.exit(-3);
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
			int opponentCheckersOnBoard = (checker== State.Checker.WHITE) ? result.getBlackCheckersOnBoard() : result.getWhiteCheckersOnBoard();
			if (opponentCheckersOnBoard > 3)
				Util.removeOpponentChecker(result, checker, currentAction.getRemoveOpponentChecker());
			else {
				System.out.println("Something deeply wrong happened: the Engine reports the game is in Phase 2, however there are only three (or less) checkers of a player...");
				System.exit(-5);
			}
		}
		
		
		
		// set the phase
		if (result.getWhiteCheckersOnBoard() == 3 || result.getBlackCheckersOnBoard() == 3)
			result.setCurrentPhase(State.Phase.FINAL);
		return result;
	}
	
	
	
	
	private static void initialChecks(State currentState, Phase2Action action, State.Checker checker)
			throws 	NullStateException
			, WrongPhaseException
			, NullActionException
			, WrongPositionException
			, TryingToMoveOpponentCheckerException
			, FromAndToAreEqualsException
			, FromAndToAreNotConnectedException
			, PositionNotEmptyException
			, NullCheckerException
			{
		
		// initial checks
		if (currentState == null)
			throw new NullStateException();
		if (currentState.getCurrentPhase()!= State.Phase.SECOND)
			throw new WrongPhaseException(currentState.getCurrentPhase(), State.Phase.SECOND);
		
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
		
		// check the "to" position, it must be connected to "from" and empty
		String to = action.getTo();
		if (to == null || "".equals(to))
			throw new WrongPositionException(to);
		if (to.equals(from))
			throw new FromAndToAreEqualsException(action);
		// Bug fixing: the current solution does not verify properly the concept of "adiacent" tiles
		//String[] hSet = Util.getHSet(from);
		//String[] vSet = Util.getVSet(from);
		//if (!Arrays.asList(hSet).contains(to) && !Arrays.asList(vSet).contains(to))
			//throw new FromAndToAreNotConnectedException(action);
		if (!Util.areAdiacent(from, to))
			throw new FromAndToAreNotConnectedException(action);
		
		State.Checker toChecker = currentState.getBoard().get(to);
		if (toChecker == null)
			throw new WrongPositionException(to);
		if (toChecker != State.Checker.EMPTY)
			throw new PositionNotEmptyException(to);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
