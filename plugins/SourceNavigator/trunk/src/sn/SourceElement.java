package sn;

import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.View;

public class SourceElement {
	public String namespace;
	public String name;
	public String kind;
	public String file;
	public int line;
	public String dir;
	
	public SourceElement(String namespace, String name, String kind,
		String file, int line, String dir)
	{
		this.namespace = namespace;
		this.name = name;
		this.kind = kind;
		this.file = file;
		this.line = line;
		this.dir = dir;
	}
	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (namespace != null && namespace.length() > 0)
			sb.append(namespace + "::");
		sb.append(name);
		sb.append(" [" + kind + "] (" + file + ":" + line + ")");
		return sb.toString();
	}
	public String getLocation() {
		return file + ":" + line;
	}
	public void jumpTo(View view) {
		String path;
		if (! MiscUtilities.isAbsolutePath(file))
			path = dir + "/" + file;
		else
			path = file;
		SourceNavigatorPlugin.jumpTo(view, path, line);
	}
}
