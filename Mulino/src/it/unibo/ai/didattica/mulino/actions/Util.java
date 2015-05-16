package it.unibo.ai.didattica.mulino.actions;

import java.util.Arrays;
import java.util.HashMap;

import it.unibo.ai.didattica.mulino.domain.State;

public class Util {
	
	private static HashMap<String,String[]> adiacentTilesMap;
	static {
		adiacentTilesMap = new HashMap<String,String[]>();
		adiacentTilesMap.put("a1", new String[]{"d1", "a4"});
		adiacentTilesMap.put("d1", new String[]{"a1", "d2", "g1"});
		adiacentTilesMap.put("g1", new String[]{"d1", "g4"});
		
		adiacentTilesMap.put("b2", new String[]{"b4","d2"});
		adiacentTilesMap.put("d2", new String[]{"b2", "d3", "f2", "d1"});
		adiacentTilesMap.put("f2", new String[]{"d2", "f4"});
			
		adiacentTilesMap.put("c3", new String[]{"c4", "d3"});
		adiacentTilesMap.put("d3", new String[]{"c3", "d2", "e3"});
		adiacentTilesMap.put("e3", new String[]{"d3", "e4"});
		
		adiacentTilesMap.put("a4", new String[]{"a1", "b4", "a7"});
		adiacentTilesMap.put("b4", new String[]{"a4", "b6", "c4", "b2"});
		adiacentTilesMap.put("c4", new String[]{"b4", "c5", "c3"});
		
		adiacentTilesMap.put("e4", new String[]{"e5", "f4", "e3"});
		adiacentTilesMap.put("f4", new String[]{"e4", "f6", "g4", "f2"});
		adiacentTilesMap.put("g4", new String[]{"f4", "g7", "g1"});
		
		adiacentTilesMap.put("c5", new String[]{"d5", "c4"});
		adiacentTilesMap.put("d5", new String[]{"c5", "d6", "e5"});
		adiacentTilesMap.put("e5", new String[]{"d5", "e4"});
		
		adiacentTilesMap.put("b6", new String[]{"d6", "b4"});
		adiacentTilesMap.put("d6", new String[]{"b6", "d7", "f6", "d5"});
		adiacentTilesMap.put("f6", new String[]{"d6", "f4"});
		
		adiacentTilesMap.put("a7",new String[]{"d7", "a4"});
		adiacentTilesMap.put("d7", new String[]{"a7", "g7", "d6"});
		adiacentTilesMap.put("g7", new String[]{"d7", "g4"});
	}
	
	
	/**
	 * Given a position, it returns an array of three positions
	 * representing the vertical triple the input position belongs to.
	 *  
	 * @param pos
	 * @return
	 */
	public static String[] getVSet(String pos) {
		String[] set = new String[3];
		char column = pos.charAt(0);
		char row = pos.charAt(1);
		if (column == 'a' || column == 'g') {
			set[0] = column + "1";
			set[1] = column + "4";
			set[2] = column + "7";
		}
		if (column == 'b' || column == 'f') {
			set[0] = column + "2";
			set[1] = column + "4";
			set[2] = column + "6";
		}
		if (column == 'c' || column == 'e') {
			set[0] = column + "3";
			set[1] = column + "4";
			set[2] = column + "5";
		}
		if (column == 'd' && row < '4') {
			set[0] = column + "1";
			set[1] = column + "2";
			set[2] = column + "3";
		}
		if (column == 'd' && row > '4') {
			set[0] = column + "5";
			set[1] = column + "6";
			set[2] = column + "7";
		}
		return set;
	}
	
	/**
	 * Given a position, it returns an array of three positions
	 * representing the horizontal triple the input position belongs to.
	 *  
	 * @param pos
	 * @return
	 */
	public static String[] getHSet(String pos) {
		String[] set = new String[3];
		char column = pos.charAt(0);
		char row = pos.charAt(1);
		// count over the rows:
		if (row == '1' || row == '7') {
			set[0] = "a" + row;
			set[1] = "d" + row;
			set[2] = "g" + row;
		}
		if (row == '2' || row == '6') {
			set[0] = "b" + row;
			set[1] = "d" + row;
			set[2] = "f" + row;
		}
		if (row == '3' || row == '5') {
			set[0] = "c" + row;
			set[1] = "d" + row;
			set[2] = "e" + row;
		}
		if (row == '4' && column < 'd') {
			set[0] = "a" + row;
			set[1] = "b" + row;
			set[2] = "c" + row;
		}
		if (row == '4' && column > 'd') {
			set[0] = "e" + row;
			set[1] = "f" + row;
			set[2] = "g" + row;
		}
		return set;
	}
	
	
	/**
	 * provides the set of adiacent tiles of a tile
	 * @param aTile string-based position of the interested tile
	 * @return the set of adiacent tiles
	 * 
	 */
	public static String[] getAdiacentTiles(String aTile) throws WrongPositionException{
		String[] result = Util.adiacentTilesMap.get(aTile);
		if (result == null)
			throw new WrongPositionException(aTile);
		return result;
	}
	/**
	 * return true if from and to are adiacent positions
	 * @param from
	 * @param to
	 * @return
	 * @throws WrongPositionException
	 */
	public static boolean areAdiacent(String from, String to) throws WrongPositionException {
		String[] friends = Util.getAdiacentTiles(from);
		if (Arrays.asList(friends).contains(to))
			return true;
		else
			return false;
	}
	
	
	/**
	 * Given a state, and the position where a checker has been put/move,
	 * it returns true if the action generated a vertical/horizontal triple
	 * @param newState
	 * @param position
	 * @param checker
	 * @return
	 */
	public static boolean hasCompletedTriple(State newState, String position, State.Checker checker) {	
		if (isInVTriple(newState, position))
			return true;
		if (isInHTriple(newState, position))
			return true;		
		return false;
	}

	
	/**
	 * returns true if the given position belongs to a vertical triple
	 * @param aState
	 * @param position
	 * @return
	 */
	public static boolean isInVTriple(State aState, String position) {
		int alignedV = 0;
		String [] vSet = getVSet(position);
		for (String s: vSet)
			if (aState.getBoard().get(s) == aState.getBoard().get(position))
				alignedV++;
		if (alignedV == 3)
			return true;
		else
			return false;
	}
	
	
	/**
	 * returns true if the given position belongs to a horizontal triple
	 * @param aState
	 * @param position
	 * @return
	 */
	public static boolean isInHTriple(State aState, String position) {
		int alignedH = 0;
		String [] hSet = getHSet(position);
		for (String s: hSet)
			if (aState.getBoard().get(s) == aState.getBoard().get(position))
				alignedH++;
		if (alignedH == 3)
			return true;
		else
			return false;
	}
	
	
	/**
	 * removes from the newState the opponent checker specified in willRemovePosition
	 * @param newState
	 * @param checker
	 * @param willRemovePosition
	 * @throws WrongPositionException
	 * @throws TryingToRemoveOwnCheckerException
	 * @throws TryingToRemoveEmptyCheckerException
	 * @throws TryingToRemoveCheckerInTripleException
	 */
	public static void removeOpponentChecker( State newState, State.Checker checker, String willRemovePosition)
			throws 	WrongPositionException
					, TryingToRemoveOwnCheckerException
					, TryingToRemoveEmptyCheckerException
					, TryingToRemoveCheckerInTripleException {
		if (newState.getBoard().get(willRemovePosition) == null)
			throw new WrongPositionException(willRemovePosition);
		if (newState.getBoard().get(willRemovePosition) == checker)
			throw new TryingToRemoveOwnCheckerException();
		if (newState.getBoard().get(willRemovePosition) == State.Checker.EMPTY)
			throw new TryingToRemoveEmptyCheckerException();
		
//		I pezzi allineati non possono essere eliminati finche' ne esistono altri non allineati.
		State.Checker opponent = (checker== State.Checker.WHITE) ? State.Checker.BLACK :State.Checker.WHITE;
		if (Util.isInVTriple(newState, willRemovePosition) || Util.isInHTriple(newState, willRemovePosition)) {
			for (String s: newState.getBoard().keySet())
				if (newState.getBoard().get(s) == opponent &&
						!Util.isInVTriple(newState,s) &&
						!Util.isInHTriple(newState,s))
					throw new TryingToRemoveCheckerInTripleException(willRemovePosition, s);
		}
		newState.getBoard().put(willRemovePosition, State.Checker.EMPTY);
		if (opponent == State.Checker.WHITE)
			newState.setWhiteCheckersOnBoard(newState.getWhiteCheckersOnBoard()-1);
		else
			if (opponent == State.Checker.BLACK)
				newState.setBlackCheckersOnBoard(newState.getBlackCheckersOnBoard()-1);
	}
	
	
	
}
