/**
 * 
 */
package browser;

import org.gjt.sp.jedit.jEdit;

import tags.TagLine;
import tags.TagsPlugin;

@SuppressWarnings("unchecked")
public class FunctionTag implements Comparable
{
	String name;
	String file;
	int line;
	
	FunctionTag(String name, String file, int line)
	{
		this.name = name;
		this.file = file;
		this.line = line;
	}
	public int getLine() {
		return line;
	}
	public String getName() {
		return name;
	}
	public String toString()
	{
		StringBuffer s = new StringBuffer(name);
		if (file != null)
			s.append(" [" + file + ":" + line + "]");
		return s.toString();
	}
	public void jump()
	{
		if (file == null)
			return;
		TagLine tagLine = new TagLine(name, file, "", line, "");
		TagsPlugin.goToTagLine(jEdit.getActiveView(), tagLine, false, name);
	}
	public int compareTo(Object o) {
		if (! (o instanceof FunctionTag))
			return 1;
		FunctionTag other = (FunctionTag)o;
		int res = name.compareTo(other.name);
		if (res != 0)
			return res;
		if (file == null)
		{
			if (other.file == null)
				return 0;
			return -1;
		}
		if (other.file == null)
			return 1;
		res = file.compareTo(other.file);
		if (res != 0)
			return res;
		if (line < other.line)
			return -1;
		else if (line > other.line)
			return 1;
		return 0;
	}
}