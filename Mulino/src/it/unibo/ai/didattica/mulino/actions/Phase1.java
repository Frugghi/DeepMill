package it.unibo.ai.didattica.mulino.actions;

import it.unibo.ai.didattica.mulino.domain.State;

public class Phase1 {
	
	public static State applyMove(State currentState, Action genericAction, State.Checker checker)
		throws 	WrongPhaseException
				, PositionNotEmptyException
				, NullCheckerException
				, NoMoreCheckersAvailableException
				, WrongPositionException
				, TryingToRemoveOwnCheckerException
				, TryingToRemoveEmptyCheckerException
				, NullStateException
				, TryingToRemoveCheckerInTripleException {
		
		Phase1Action currentAction = null;
		if (genericAction instanceof Phase1Action)
			currentAction = (Phase1Action) genericAction;
		else
			System.exit(-2);
		
		// initial checks
		initialChecks(currentState, currentAction.getPutPosition(), checker);
		
		// generate the new State
		State result = currentState.clone();

		// put the checker on the board
		result.getBoard().put(currentAction.getPutPosition(), checker);
		
		switch (checker) {
			case WHITE :
				result.setWhiteCheckers(result.getWhiteCheckers()-1);
				result.setWhiteCheckersOnBoard(result.getWhiteCheckersOnBoard()+1);
				break;
			case BLACK :
				result.setBlackCheckers(result.getBlackCheckers()-1);
				result.setBlackCheckersOnBoard(result.getBlackCheckersOnBoard()+1);
				break;
			default:
				throw new NullCheckerException();
		}
		
		// check if this move allows to remove an opponent checker
		if (Util.hasCompletedTriple(result, currentAction.getPutPosition(), checker))
			Util.removeOpponentChecker(result, checker, currentAction.getRemoveOpponentChecker());
		
		// set the phase
		if (result.getWhiteCheckers() == 0 && result.getBlackCheckers() == 0)
			result.setCurrentPhase(State.Phase.SECOND);
		else
			result.setCurrentPhase(State.Phase.FIRST);
		return result;
	}
	
	
	
	
	private static void initialChecks(State currentState, String position, State.Checker checker)
			throws 	NullStateException
			, WrongPhaseException
			, WrongPositionException
			, PositionNotEmptyException
			, NullCheckerException
			, NoMoreCheckersAvailableException {
		// initial checks
		if (currentState == null)
			throw new NullStateException();
		if (currentState.getCurrentPhase()!= State.Phase.FIRST)
			throw new WrongPhaseException(currentState.getCurrentPhase(), State.Phase.FIRST);
		if (currentState.getBoard().get(position) == null)
			throw new WrongPositionException(position);
		if (currentState.getBoard().get(position) != State.Checker.EMPTY)
			throw new PositionNotEmptyException(position);
		if (checker == null || checker == State.Checker.EMPTY)
			throw new NullCheckerException();
		switch (checker) {
			case WHITE :
				if (currentState.getWhiteCheckers()<=0)
					throw new NoMoreCheckersAvailableException(checker);
				break;
			case BLACK :
				if (currentState.getBlackCheckers()<=0)
					throw new NoMoreCheckersAvailableException(checker);
				break;
			default:
				throw new NullCheckerException();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
