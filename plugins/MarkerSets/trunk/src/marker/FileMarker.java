package marker;

public class FileMarker {
	String file;
	int line;
	char shortcut;
	
	public FileMarker(String file, int line, char shortcut) {
		this.file = file;
		this.line = line;
		this.shortcut = shortcut;
	}
	
	public String toString() {
		return file + ":" + line + ":" + shortcut;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (! (obj instanceof FileMarker))
			return false;
		FileMarker other = (FileMarker) obj;
		return (file.equals(other.file) && line == other.line);
	}

	@Override
	public int hashCode() {
		return file.hashCode() + line;
	}

	
}
