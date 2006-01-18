package superabbrevs;

public class StaticField implements Field {
	
	private String staticText;
	public StaticField(String string) {
		staticText = string;
	}

	public int getLength() {
		return staticText.length();
	}

	public String toString() {
		return staticText;
	}
}
