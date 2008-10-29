package sn;

import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.View;

public class SourceElement {
	public String namespace;
	public String name;
	public String kind;
	public String sig;
	public String file;
	public int line;
	public String dir;
	
	public SourceElement(String namespace, String name, String kind,
		String sig, String file, int line, String dir)
	{
		this.namespace = namespace;
		this.name = name;
		this.kind = kind;
		this.sig = sig;
		this.file = file;
		this.line = line;
		this.dir = dir;
	}
	public String toString() {
		return getRepresentativeName() + " [" + kind + "] (" + file + ":" + line + ")";
	}
	public String getQualifiedName() {
		StringBuffer sb = new StringBuffer();
		if (namespace != null && namespace.length() > 0)
			sb.append(namespace + "::");
		sb.append(name);
		return sb.toString();
	}
	public String getRepresentativeName() {
		StringBuffer sb = new StringBuffer(getQualifiedName());
		if (sig != null && sig.length() > 0)
			sb.append("(" + sig + ")");
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
		SourceNavigatorPlugin.getEditorInterface().jumpTo(view, path, line);
	}
}
