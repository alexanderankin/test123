package superabbrevs.model;

public enum SelectionReplacementTypes {
	NOTHING("Nothing"), SELECTION("Selection"), SELECTED_LINES(
			"Selected lines");

	private String label;

	SelectionReplacementTypes(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return label;
	}
}
