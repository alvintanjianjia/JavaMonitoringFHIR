package GUI;

public class CheckList {
	
	//Class to support checkbox implementation to hide and show monitoring elements
	//For new monitoring elements, just add getters and setters here.
	private boolean cholestrol = false;
	private boolean diastolicBP = false;
	private boolean systolicBP = false;
	private boolean tobbaco = false;
	
	public CheckList() {
	}
	
	public boolean getModelListCholestrol() {
		return this.cholestrol;
	}
	
	public boolean getModelListDiastolicBP() {
		return this.diastolicBP;
	}
	
	public boolean getModelListSystolicBP() {
		return this.systolicBP;
	}
	
	public boolean getModelListTobacco() {
		return this.tobbaco;
	}
	
	public void setModelListCholestrol(boolean type) {
		this.cholestrol = type;
	}
	
	public void setModelListDiastolicBP(boolean type) {
		this.diastolicBP = type;
	}
	
	public void setModelListSystolicBP(boolean type) {
		this.systolicBP = type;
	}
	
	public void setModelListTobacco(boolean type) {
		this.tobbaco = type;
	}
	
	
}
