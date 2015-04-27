package it.unibo.ai.didattica.mulino.actions;

public class Phase2Action extends Action {

	private static final long serialVersionUID = 1L;
	
	private String from;
	private String to;
	private String removeOpponentChecker;
	
	

	public String getFrom() { return from; }
	public void setFrom(String from) { this.from = from; }

	public String getTo() { return to; }
	public void setTo(String to) { this.to = to; }

	public String getRemoveOpponentChecker() { return removeOpponentChecker; }
	public void setRemoveOpponentChecker(String removeOpponentChecker) { this.removeOpponentChecker = removeOpponentChecker; }



	@Override
	public String toString() {
		return from+to+((removeOpponentChecker == null) ? "" : removeOpponentChecker);
	}
}
