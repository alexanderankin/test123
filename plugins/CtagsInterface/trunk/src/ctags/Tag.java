package ctags;

import java.util.Hashtable;
import java.util.Set;

public class Tag {

	private String name;
	private String file;
	private int line;
	private String pattern;
	private String kind;
	private Hashtable<String, String> extensions;
	
	public Tag(String name, String file, String pattern) {
		this.name = name;
		this.file = file;
		this.pattern = pattern;
	}
	public void setExtensions(Hashtable<String, String> extensions) {
		this.extensions = extensions;
		kind = extensions.contains("kind") ? extensions.get("kind") : null; 
		line = extensions.contains("line") ? Integer.valueOf(extensions.get("line")) : -1;
		
	}
	public String getName() {
		return name;
	}
	public String getFile() {
		return file;
	}
	public String getPattern() {
		return pattern;
	}
	public int getLine() {
		return line;
	}
	public String getKind() {
		return kind;
	}
	public String getExtension(String name) {
		return extensions.get(name);
	}
	public Set<String> getExtensions() {
		return extensions.keySet();
	}
}
