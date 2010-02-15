package codebook.java;
// imports {{{
import org.gjt.sp.jedit.View;
import java.net.URL;
import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
// }}} imports
/**
 * @author Damien Radtke
 * class ApiParser
 * A class that extracts data from Java Api pages
 */
public class ApiParser {
	// NOTE: This modifier list may not be complete
	public static final String[] MODIFIERS = new String[] { "final", "synchronized" };
	public static final String[] VISIBILITY = new String[] { "public", "protected", "private" };
	// parseAPI() {{{
	/**
	 * Parses an entire API by reading through the class list and calling parse() on each class
	 * @param path the location of the api to parse (root folder)
	 * @param remote true if the api is online, false if local
	 */
	 public static void parseAPI(final View view, String _path, final boolean remote) {
	 	 final String sep = (remote) ? "/" : File.separator;
	 	 final String path = (_path.endsWith(sep)) ? _path : _path+sep;
	 	 view.getStatus().setMessage("Preparing to parse...");
	 	 new Thread(new Runnable() {
	 	 		 public void run() {
	 	 		 	 LinkedList<String> clsList = new LinkedList<String>();
	 	 		 	 try {
	 	 		 	 	 String classlist = readPage(path+"allclasses-frame.html", remote);
	 	 		 	 	 Pattern p = Pattern.compile("<A HREF=\".*?>.*?</A>");
	 	 		 	 	 Matcher m = p.matcher(classlist);
	 	 		 	 	 while (m.find()) {
	 	 		 	 	 	 String page = classlist.substring(m.start()+9, classlist.indexOf("\"", m.start()+9));
	 	 		 	 	 	 clsList.add(path+page);
	 	 		 	 	 }
	 	 		 	 	 int total = clsList.size();
	 	 		 	 	 int i = 1;
	 	 		 	 	 for (final String cls : clsList) {
	 	 		 	 	 	 view.getStatus().setMessage("("+i+" / "+total+") Parsing "+cls+"...");
							 parse(cls, remote);
	 	 		 	 	 	 i++;
	 	 		 	 	 }
	 	 		 	 } catch (Exception e) {
	 	 		 	 	 // Unable to parse API
	 	 		 	 	 view.getStatus().setMessage("Ran into an error parsing api.");
	 	 		 	 	 e.printStackTrace();
	 	 		 	 	 System.exit(0);
	 	 		 	 }
	 	 		 	 view.getStatus().setMessageAndClear("Api parsing complete");
	 	 		 }
	 	 }).start();
	}
	public static void parseAPI(View view, String path) {
		parseAPI(view, path, path.startsWith("http://"));
	}
	// }}} parseAPI()
	// parse() {{{
	/**
	 * Parses an api page
	 * @param path the location of the page to parse
	 * @param remote true if the page is online, false if local
	 */
	public static void parse(String path, boolean remote) {
		JavaClass jcl = new JavaClass();
		String text = null;
		try {
			text = readPage(path, remote);
		} catch (Exception e) {
			// Couldn't read page
			//view.getStatus().setMessage("Couldn't read page: "+e);
			e.printStackTrace();
			System.exit(0);
			//return;
		}
		// Parse class data
		String classData = pullSection(text, "<!-- ======== START OF CLASS DATA ======== -->", false);
		if (!classData.isEmpty()) {
			int br = classData.indexOf("<BR>");
			String pkg = clearTags(classData.substring(0, br)).trim();
			int pre = classData.indexOf("</H2>", br);
			String title = clearTags(classData.substring(br, pre)).trim();
			String name = title.substring(title.indexOf(" ")+1);
			if (name.indexOf("<") != -1) name = name.substring(0, name.indexOf("<"));
			//view.getStatus().setMessage("Name: "+name);
			jcl.setPackage(pkg);
			jcl.setName(name);
			//view.getStatus().setMessage(name);
		}
		// Parse field data
		String fieldData = pullSection(text, "<!-- =========== FIELD SUMMARY =========== -->", true);
		if (!fieldData.isEmpty()) {
			Pattern p = Pattern.compile("<TR .*?>.*?</TR>");
			Matcher m = p.matcher(fieldData);
			while (m.find()) {
				String field = fieldData.substring(m.start(), m.end());
				if (field.indexOf("Deprecated.") != -1 || field.indexOf("Field Summary") != -1) continue;
				int br = field.indexOf("<BR>");
				String field_value = clearTags(field.substring(0, br)).trim();
				String field_desc = clearTags(field.substring(br, field.length())).trim();
				boolean is_static = false;
				if (field_value.startsWith("static ")) {
					is_static = true;
					field_value = field_value.substring(7);
				}
				String vis = "public";
				for (int i=0; i<VISIBILITY.length; i++) {
					if (field_value.startsWith(VISIBILITY[i]+" ")) {
						vis = VISIBILITY[i];
						field_value = field_value.substring(VISIBILITY[i].length()+1);
					}
				}
				int space = field_value.indexOf(" ");
				String field_type = field_value.substring(0, space);
				field_value = field_value.substring(space+1);
				
				//view.getStatus().setMessage(field_value+" : "+field_type+" ["+field_desc+"] ("+is_static+")");
				jcl.addField(field_value, field_type, field_desc, vis, is_static);
			}
		}
		// Parse constructor data
		String constructorData = pullSection(text, "<!-- ======== CONSTRUCTOR SUMMARY ======== -->", true);
		if (!constructorData.isEmpty()) {
			Pattern p = Pattern.compile("<TR .*?>.*?</TR>");
			Matcher m = p.matcher(constructorData);
			while (m.find()) {
				String con = constructorData.substring(m.start(), m.end());
				if (con.indexOf("Deprecated.") != -1 || con.indexOf("Constructor Summary") != -1) continue;
				int br = con.indexOf("<BR>");
				String con_value = clearTags(con.substring(0, br)).trim();
				String con_desc = clearTags(con.substring(br, con.length())).trim();
				//view.getStatus().setMessage(con_value+" ["+con_desc+"]");
				jcl.addConstructor(con_value, con_desc);
			}
		}
		// Parse method data
		String methodData = pullSection(text, "<!-- ========== METHOD SUMMARY =========== -->", true);
		if (!methodData.isEmpty()) {
			Pattern p = Pattern.compile("<TR .*?>.*?</TR>");
			Matcher m = p.matcher(methodData);
			while (m.find()) {
				String method = methodData.substring(m.start(), m.end());
				if (method.indexOf("Deprecated.") != -1 || method.indexOf("Method Summary") != -1) continue;
				int br = method.indexOf("<BR>", method.indexOf("</FONT>"));
				if (br == -1) {
					int end = methodData.indexOf("</TR>", m.end()+1);
					method = methodData.substring(m.start(), end);
					br = method.indexOf("<BR>", method.indexOf("</FONT>"));
				}
				String method_value = clearTags(method.substring(0, br)).trim();
				String method_desc = clearTags(method.substring(br, method.length())).trim();
				boolean is_static = false;
				if (method_value.startsWith("static ")) {
					method_value = method_value.substring(7);
					is_static = true;
				}
				String vis = "public";
				for (int i=0; i<VISIBILITY.length; i++) {
					if (method_value.startsWith(VISIBILITY[i]+" ")) {
						vis = VISIBILITY[i];
						method_value = method_value.substring(VISIBILITY[i].length()+1);
					}
				}
				int space = method_value.lastIndexOf(" ", method_value.indexOf("("));
				String method_type = method_value.substring(0, space);
				method_value = method_value.substring(space+1);
				jcl.addMethod(method_value, method_type, method_desc, vis, is_static);
				//view.getStatus().setMessage(method_value+" : "+method_type+" ["+method_desc+"] ("+is_static+")");
			}
		}
		jcl.save();
	}
	public static void parse(String path) {
		parse(path, path.startsWith("http://"));
	}
	// }}} parse
	// pullSection() {{{
	/**
	 * Reads the section with the given comment header and returns it
	 * @param text the text to pull a section from
	 * @param header the comment header (such as <!-- ======== START OF CLASS DATA ======== -->)
	 * @param isTable true if the information under the header is stored in a table.
	 * This is necessary in order to be able to correctly determine the end of the section
	 * @return the text contained in this section
	 */
	private static String pullSection(String text, String header, boolean isTable) {
		// This method determines the end of the section by searching for the next comment
		// if "isTable" is true, the search must begin after the first <TABLE> tag
		int i = text.indexOf(header);
		if (i == -1)
			return "";
		i += header.length();
		int j = (isTable) ? text.indexOf("<!--", text.indexOf("<TABLE ", i)) : text.indexOf("<!--", i);
		return text.substring(i, j);
	}
	// }}} pullSection()
	// clearTags() {{{
	/**
	 * Removes all HTML tags from the supplied text and converts from XML code (e.g. &lt;) to ASCII characters
	 * @param text the text to remove HTML tags from
	 * @return the cleared text
	 */
	private static String clearTags(String text) {
		text = text.replace("</CODE>", " ");
		text = text.replace("&nbsp;", " ");
		text = text.replaceAll("<.*?>", "");
		text = text.replace("&gt;", ">");
		text = text.replace("&lt;", "<");
		text = text.replaceAll("\\s{2,}", " "); // Remove large spaces
		// Remove any potential modifiers we don't need
		text = text.replace("abstract ", "");
		text = text.replace("synchronized ", "");
		text = text.replace("native ", "");
		text = text.replace("final ", "");
		text = text.replace("strictfp ", "");
		return text;
	}
	// }}} clearTags()
	// readPage() {{{
	/**
	 * Reads a single Api page and returns its text
	 * @param path the location of the page to read
	 * @param remote true if the page is online, false if local
	 */
	private static String readPage(String path, boolean remote) throws Exception {
		StringBuffer text = new StringBuffer("");
		InputStream in = null;
		if (remote)
			in = new URL(path).openConnection().getInputStream();
		else
			in = new FileInputStream(new File(path));
		Scanner read = new Scanner(new InputStreamReader(in));
		while (read.hasNext()) {
			text.append(read.next()+" ");
		}
		// Convert all HTML tags to uppercase, but ignore quoted literals inside those tags
		final String tagRegex = "<.*?>";
		Pattern tagPattern = Pattern.compile(tagRegex);
		Matcher tagMatcher = tagPattern.matcher(text);
		while (tagMatcher.find()) {
			StringBuffer str = new StringBuffer(text.substring(tagMatcher.start(),
			                                    tagMatcher.end()));
			final String quoteRegex = "\".*?\"|'.*?'";
			Pattern quotePattern = Pattern.compile(quoteRegex);
			Matcher quoteMatcher = quotePattern.matcher(str);
			int i = 0;
			while (quoteMatcher.find()) {
				String toCaps = str.substring(i, quoteMatcher.start());
				str.replace(i, quoteMatcher.start(), toCaps.toUpperCase());
				i = quoteMatcher.end();
			}
			text.replace(tagMatcher.start(), tagMatcher.end(), str.toString());
		}
		return text.toString();
	}
	// }}} readPage()
	// class JavaClass {{{
	/**
	 * @author Damien Radtke
	 * class JavaClass
	 * This class stores parsed data, which is written out to .dat files
	 */
	public static class JavaClass implements Serializable {
		private String pkg;
		private String name;
		private ArrayList<String[]> fields; // {val, type, desc, vis, static | instance}
		private ArrayList<String[]> constructors; // {val, desc}
		private ArrayList<String[]> methods; // {val, type, desc, vis, static | instance}

		JavaClass() {
			fields = new ArrayList<String[]>();
			constructors = new ArrayList<String[]>();
			methods = new ArrayList<String[]>();
		}
		
		public void setPackage(String pkg) { this.pkg = pkg; }
		public void setName(String name) { this.name = name; }
		
		public void addField(String val, String type, String desc, String vis, boolean is_static) {
			String[] field = new String[] { val, type, desc, vis, is_static ? "static" : "instance" };
			fields.add(field);
		}
		
		public void addConstructor(String val, String desc) {
			String[] con = new String[] { val, desc };
			constructors.add(con);
		}
		
		public void addMethod(String val, String type, String desc, String vis, boolean is_static) {
			String[] method = new String[] { val, type, desc, vis, is_static ? "static" : "instance" };
			methods.add(method);
		}
		
		public String getPackage() { return this.pkg; }
		public String getName() { return this.name; }
		public ArrayList<String[]> getFields() { return this.fields; }
		public ArrayList<String[]> getConstructors() { return this.constructors; }
		public ArrayList<String[]> getMethods() { return this.methods; }
		
		public void save() {
			String _name = new String(name);
			int lt = -1;
			if ((lt = _name.indexOf("<")) != -1) _name = _name.substring(0, lt);
			String s = File.separator;
			String classDir = codebook.CodeBookPlugin.HOME+"java"+s+"api"+s+_name+s;
			try {
				File classDirFile = new File(classDir);
				if (!classDirFile.exists()) classDirFile.mkdirs();
				File dat = new File(classDir+pkg);
				ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(dat));
				out.writeObject(this);
				out.close();
				//view.getStatus().setMessage("Class data saved to "+dat.getPath());
			} catch (Exception e) {
				// Couldn't save data
				e.printStackTrace();
				System.exit(0);
			}
		}
	}
	// }}} JavaClass
}
