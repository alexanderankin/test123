package superabbrevs.gui.controls.abbreviationlist;

public class SelectionChangedEvent<T> {
	public SelectionChangedEvent(T oldSelection, T newSelection) {
		setOldSelection(oldSelection);
		setNewSelection(newSelection);
	}
	
	private void setOldSelection(T oldSelection) {
		this.oldSelection = oldSelection;
	}
	
	public T getOldSelection() {
		return oldSelection;
	}
	
	private void setNewSelection(T newSelection) {
		this.newSelection = newSelection;
	}
	
	public T getNewSelection() {
		return newSelection;
	}
	
	private T oldSelection;
	private T newSelection;
}
