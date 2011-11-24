package superabbrevs.template;

public class TextField implements Field {
	
	private String text;
	
	public TextField(String text) {
		this.text = text;
	}

	public int getLength() {
		return text.length();
	}

	public String toString() {
		return text;
	}
}
