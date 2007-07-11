package browser;

import java.util.regex.Pattern;

public class GlobalRecord {
	private String [] fields;
	static private Pattern spaces = Pattern.compile("\\s+");
	
	public GlobalRecord(String line) {
    	fields = spaces.split(line, 4);
	}
	public String getName() {
		return fields[0];
	}
	public int getLine() {
		return Integer.parseInt(fields[1]);
	}
	public String getFile() {
		return fields[2];
	}
	public String getText() {
		return fields[3];
	}
}
