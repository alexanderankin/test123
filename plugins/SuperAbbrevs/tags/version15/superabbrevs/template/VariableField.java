package superabbrevs.template;

public class VariableField extends SelectableField {

	private String value;
		
	public VariableField(String value) {
		this.value = value;
	}

	public String toString() {
		return value;
	}

	public int getLength() {
		return value.length();
	}

	public void insert(int at, String s) throws WriteOutsideTemplateException {
		int length = getLength();
		int offset = getOffset();
		if (offset <= at && at <= offset + length) {
			StringBuffer v = new StringBuffer(value);
			v.insert(at - offset,s);
			value = v.toString();
		} else {
			throw new WriteOutsideTemplateException("Insert \""+s+"\" out Side template: "+at);
		}
	}

	public void delete(int at, int deletionLength) throws WriteOutsideTemplateException {
		int length = getLength();
		int offset = getOffset();
		if (offset <= at && at+deletionLength <= offset + length) {
			StringBuffer v = new StringBuffer(value);
			int start = at - offset;
			int end = start + deletionLength;
			v.delete(start,end);
			value = v.toString();
		} else {
			throw new WriteOutsideTemplateException("Delete out Side template: "+at+" length: "+deletionLength);
		}
		
	}
}
