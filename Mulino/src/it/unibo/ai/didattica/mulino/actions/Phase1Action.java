package it.unibo.ai.didattica.mulino.actions;

public class Phase1Action extends Action {

	private static final long serialVersionUID = 1L;
	private String putPosition;
	private String removeOpponentChecker;
	
	public String getPutPosition() { return putPosition; }
	public void setPutPosition(String putChecker) { this.putPosition = putChecker; }
	public String getRemoveOpponentChecker() { return removeOpponentChecker; }
	public void setRemoveOpponentChecker(String removeOpponentChecker) { this.removeOpponentChecker = removeOpponentChecker; }
	

	@Override
	public String toString() {
		return putPosition+((removeOpponentChecker == null) ? "" : removeOpponentChecker);
	}
}
