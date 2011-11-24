package superabbrevs.template;

public class VariableFieldPointer implements Field {

	private VariableField field;
	public VariableFieldPointer(VariableField field) {
		this.field = field;
	}

	public String toString() {
		return field.toString();
	}

	public int getLength() {
		return field.getLength();
	}
}
