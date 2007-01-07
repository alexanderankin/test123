package browser;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

import tags.TagLine;
import tags.TagsPlugin;

public class TagDB {

	// Ctags constants
	static private HashSet<String> ScopeNames = new HashSet<String>();
	static private HashSet<String> ShortScopeNames = new HashSet<String>();
	static private String SCOPE_NAME_LIST =
		"class|struct|union|enum|interface|namespace";
	// Names of main table columns
	static public String TAG_COL = "tag";
	static public String DEFINED_COL = "defined";
	static public String REGEXP_COL = "regexp";
	static public String KIND_COL = "kind";
	static public String FILE_COL = "file";
	static public String ACCESS_COL = "access";
	static public String INHERITS_COL = "inherits";
	static public String LANGUAGE_COL = "language";
	static public String IMPLEMENTATION_COL = "implementation";
	static public String LINE_COL = "line";
	static public String SCOPE_COL = "scope";
	static public String SIGNATURE_COL = "signature";

	// TagDB's own added columns
	private static final String TAG_INDEX_COL = "tic";
	
	static private String ICONS = "options.ClassBrowser.icons.";
	static Hashtable<String, ImageIcon> icons =
		new Hashtable<String, ImageIcon>();
	
	private Vector<String> tagFiles = new Vector<String>();
	
	{
		String [] scopes = SCOPE_NAME_LIST.split("\\|");
		for (int i = 0; i < scopes.length; i++)
		{
			ScopeNames.add(scopes[i]);
			ShortScopeNames.add(scopes[i].substring(0, 1));
		}
	}
	
	TagDB()
	{
	}

	public static void main(String args[])
	{
		TagDB db = new TagDB();
		db.addTagFile("/sag/lpool/shlomy/yx.tags");
		System.out.println("*** Searching yxPoint tags");
		db.printRecordSet(db.getTag("yxPoint"));
		System.out.println("*** Searching yxPoint members");
		db.printRecordSet(db.getMembers("yxPoint"));
		System.out.println("*** Searching Scanline subclasses");
		db.printRecordSet(db.getInherits("Scanline"));
	}

	public boolean hasTagFile(String tagFile)
	{
		return tagFiles.contains(tagFile);
	}
	
	public boolean addTagFile(String tagFile)
	{
		tagFiles.add(tagFile);
		return true;
	}
	
	public ImageIcon getIcon(Record tag)
	{
		String kind = tag.get(KIND_COL);
		if (kind.length() == 1)
			return null;	// Kind cannot be uniquely identified
		String iconName =
			jEdit.getProperty(ICONS + kind);
		if (iconName == null || iconName.length() == 0)
			iconName = "unknown.png";
		ImageIcon icon = (ImageIcon) icons.get(kind);
		if (icon == null)
		{
			URL url = TagDB.class.getClassLoader().getResource(
				"icons/" + iconName);
	        try {
	            icon = new ImageIcon(url);
	        }
	        catch (Exception e) {
	        	e.printStackTrace();
	        }
			if (icon != null)
				icons.put(kind, icon);
		}
		return icon;
	}
	
	public boolean isScopeTag(Record tag)
	{
		String kind = tag.get(KIND_COL);
		return (ScopeNames.contains(kind) || ShortScopeNames.contains(kind)); 
	}
	
	public Vector<String> findMatches(String pattern)
	{
		Pattern pat = Pattern.compile(pattern);
		Matcher mat = pat.matcher("");
		Vector<String> matches = new Vector<String>();
		for (int i = 0; i < tagFiles.size(); i++)
		{
			BufferedReader in;
			try {
				String tagFilePath = tagFiles.get(i);
				if (tagFilePath == null)	// In case tag file was removed
					continue;
				in = new BufferedReader(new FileReader(tagFilePath));
				String line;
				while ((line = in.readLine()) != null)
				{
					mat.reset(line);
					if (mat.matches())
						matches.add(line + "\t" + TAG_INDEX_COL + ":" + i);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return matches;
	}
	
	public RecordSet getMatchingTags(String pattern)
	{
		Vector<String> lines = findMatches(pattern);
		Vector<Record> info = linesToInfo(lines);
		return new RecordSet(info);
	}
	
	public RecordSet getMatchingTags(String pattern, Vector<String> lines)
	{
		Pattern pat = Pattern.compile(pattern);
		Matcher mat = pat.matcher("");
		Vector<String> matches = new Vector<String>();
		for (int i = 0; i < lines.size(); i++)
		{
			String line = lines.get(i);
			mat.reset(line);
			if (mat.matches())
				matches.add(line);
		}
		Vector<Record> info = linesToInfo(matches);
		return new RecordSet(info);
	}
	
	private Record parseLine(String line)
	{
		// Skip special metadata-tags at the beginning of the file
		if (line.startsWith("!")) 
			return null;
		Hashtable<String, String> info = new Hashtable<String, String>();
		// Get rid of cr/lf
		while (line.endsWith("\n") || line.endsWith("\r"))
			line = line.substring(0, line.length() - 1);
		// Split off extensions
		int idx = line.indexOf(";\"\t");
		if (idx < 0)
			return null;
		String extensions[] = line.substring(idx + 3).split("\t");
		// Get standard fields
		String fields[] = line.substring(0, idx).split("\t", 3);
		if (fields.length < 3)
			return null;
		info.put(TAG_COL, fields[0]);
		info.put(DEFINED_COL, fields[1]);
		info.put(REGEXP_COL, fields[2]);
		// Add the extensions
		for (int i = 0; i < extensions.length; i++)
		{
			String pair[] = extensions[i].split(":", 2);
			if (pair.length != 2)
			{
				// Possibly a tag type
				info.put(KIND_COL, pair[0]);
				continue;
			}
			if (ScopeNames.contains(pair[0]))
				pair[0] = SCOPE_COL;
			else if (pair[0].equals(FILE_COL))
				pair[1] = "Yes";
			info.put(pair[0], pair[1]);
		}
		return new Record(info);
	}
	
	public void shutdown()
	{
	}

	public void printRecordSet(RecordSet r)
	{
		while (r.next())
		{
			System.out.println(r.getField(TAG_COL));
		}
	}
	
	private Vector<Record> linesToInfo(Vector<String> lines)
	{
		Vector<Record> info = new Vector<Record>();
		for (int i = 0; i < lines.size(); i++)
			info.add(parseLine(lines.get(i)));
		return info;
	}
	
	/*
	 *  RegExps for popular Ctags queries
	 */
	public String getInheritsRegExp(String scope)
	{
		return ".*\tinherits:" + scope + "\\b.*"; 
	}
	public String getClassMembersRegExp(String scope)
	{
		return ".*\tclass:" + scope + "\\b.*";
	}
	public String getScopeMembersRegExp(String scope)
	{
		return ".*\t(" + SCOPE_NAME_LIST + "):" + scope + "\\b.*";
	}
	public String getTagDefinitionRegExp(String tag)
	{
		return "^" + tag + "\t.*";
	}
	
	/*
	 * Ctags-query methods
	 */
	public RecordSet getInherits(String scope)
	{
		return getMatchingTags(getInheritsRegExp(scope));
	}
	
	public RecordSet getInherits(String scope, Vector<String> lines)
	{
		return getMatchingTags(getInheritsRegExp(scope), lines);
	}
	
	public RecordSet getMembers(String scope)
	{
		return getMatchingTags(getScopeMembersRegExp(scope));
	}
	
	public RecordSet getTag(String tag)
	{
		return getMatchingTags(getTagDefinitionRegExp(tag));
	}
	
	public RecordSet getTag(String tag, Vector<String> lines)
	{
		return getMatchingTags(getTagDefinitionRegExp(tag), lines);
	}
	
	public class Record
	{
		protected Hashtable<String, String> info;
		
		protected Record()
		{
		}
		public Record(Hashtable<String, String> info)
		{
			this.info = info;
		}
		public String get(String field)
		{
			return info.get(field);
		}
		public boolean hasField(String field)
		{
			return info.containsKey(field);
		}
		public String getName()
		{
			return get(TAG_COL);
		}
		public String getMiddle()
		{
			return "";
		}
		public String toString()
		{
			StringBuffer s = new StringBuffer(getName());
			String signature = get(SIGNATURE_COL);
			if (signature != null)
				s.append(signature + getMiddle());
			else
			{
				s.append(getMiddle());
				String regExp = get(REGEXP_COL);
				if (regExp != null)
					s.append("     [" + unescapeSearch(regExp) + "]");
			}
			return s.toString();
		}
		public void jump(View view)
		{
			String name = getName();
			int tagFileIndex = Integer.parseInt(get(TAG_INDEX_COL));
			String tagFile = tagFiles.get(tagFileIndex);
			String dir = MiscUtilities.getParentOfPath(tagFile);
			String definedIn = get(DEFINED_COL);
			definedIn = MiscUtilities.constructPath(dir, definedIn);
			String regExp = get(REGEXP_COL);
			String lineStr = get(LINE_COL);
			int line = 0;
			if (lineStr != null)
			{
				try {
					line = Integer.parseInt(lineStr);
					regExp = null;
				}
				catch (NumberFormatException e) 
				{
					line = 0;
				}
			}
			if (regExp != null)
				regExp = unescapeSearch(regExp);
			TagLine tagLine = new TagLine(name, definedIn, regExp, line, tagFile);
			TagsPlugin.goToTagLine(view, tagLine, false, name);
		}
		private String unescapeSearch(String regExp)
		{
			// Remove the prefix "/^" and the suffix "$/"
			regExp = regExp.substring(2, regExp.length() - 2);
			// Trim padding spaces, and unescape "/" characters
			return regExp.trim().replaceAll("\\/", "/");
		}
		public String getKind()
		{
			return get(TagDB.KIND_COL);
		}
		public boolean isVariable()
		{
			String kind = getKind();
			if (kind == null)
				return false;
			return (kind.equals("variable") || kind.equals("member"));
		}
		public boolean isStatic()
		{
			return info.containsKey(TagDB.FILE_COL);
		}
		public boolean isPublic()
		{
			String access = get(TagDB.ACCESS_COL);
			if (access == null)
				return true;
			return (access.equals("public"));
		}
	};
	
	public class RecordSet
	{
		Vector<Record> data = new Vector<Record>();
		int index = -1;
		
		public RecordSet(Vector<Record> info)
		{
			set(info);
		}
		public void set(Vector<Record> info)
		{
			data = info;
		}
		public int getSize()
		{
			return data.size();
		}
		public boolean next()
		{
			index++;
			return (index < getSize());
		}
		public Record getRecord()
		{
			return data.get(index);
		}
		public String getField(String name)
		{
			return getRecord().get(name);
		}
		public boolean isEmpty()
		{
			return data.isEmpty();
		}
	}
}
