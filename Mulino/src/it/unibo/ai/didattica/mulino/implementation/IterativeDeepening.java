package it.unibo.ai.didattica.mulino.implementation;

import it.unibo.ai.didattica.mulino.domain.MillMinimax;
import it.unibo.ai.didattica.mulino.domain.MillMove;

public class IterativeDeepening extends Thread {

	private MillMove move;
	private MillMinimax state;
	private int minDepth;
	private boolean terminate = false;
	
	public IterativeDeepening(MillMinimax state) 
	{	
		this.state = state;
		this.minDepth = 1;
	}
	
	public IterativeDeepening(MillMinimax state, int minDepth) {
		super();
		this.state = state;
		this.minDepth = minDepth;
	}

	public MillMove getBestMove() {
		return move;
	}
	
	public void run()
	{
		int depth =  minDepth;
		while( !terminate )
		{
			move = (MillMove)state.getBestMove(depth);
			depth++;
		}
	}
	
	public void terminate()
	{
		terminate = true;
	}

	




}
