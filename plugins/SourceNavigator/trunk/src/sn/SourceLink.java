/**
 * 
 */
package sn;

import org.gjt.sp.jedit.View;

public class SourceLink {
	String path;
	int line;
	int offset;
	public SourceLink(String path, int line, int offset) {
		this.path = path;
		this.line = line;
		this.offset = offset;
	}
	public void jumpTo(View view) {
		SourceNavigatorPlugin.getEditorInterface().jumpTo(view, path, line, offset);
	}
	public String toString() {
		return path + ":" + line + "." + offset;
	}
}