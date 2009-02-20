package superabbrevs.model;

public enum ReplacementTypes {
	AT_CARET("At caret"), BUFFER("Buffer"), LINE("Line"), WORD("Word"), CHAR(
			"Character");

	private String label;

	ReplacementTypes(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return label;
	}
}
