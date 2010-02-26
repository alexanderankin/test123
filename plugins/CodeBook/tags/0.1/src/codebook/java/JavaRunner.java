package codebook.java;
// imports {{{
import codebook.CodeBookPlugin;
import codebook.gui.ChooserDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.TextUtilities;
// }}} imports
/**
 * @author Damien Radtke
 * class JavaRunner
 * This class is called when the keystroke is activated on a java (or similar) file
 */
public class JavaRunner {
	public static final String dir = CodeBookPlugin.HOME+"java"+File.separator;
	public static enum Trigger { DOT, PARENTHESE, NONE };
	// run() {{{
	/**
	 * The main run method
	 * @param textArea the text area instance
	 */
	public static void run(JEditTextArea textArea) {
		if (!new File(dir).exists()) {
			GUIUtilities.message(textArea.getView(), "codebook.msg.no-java-home", null);
			return;
		}
		Log.log(Log.DEBUG,JavaRunner.class,"Running Java completion");
		int line = textArea.getCaretLine();
		int pos = textArea.getCaretPosition()-textArea.getLineStartOffset(line);
		String text = textArea.getLineText(line).substring(0, pos);
		// See if it's a class or nested class first
		int beg = TextUtilities.findWordStart(text, pos-1, "._(");
		String cls = text.substring(beg, pos);
		// Make sure that completion in scenario SOMETHING(CLASS.[complete] works
		while (cls.startsWith("(")) cls = cls.substring(1);
		// Determine if there's a "." or "(" trigger
		Trigger trigger = Trigger.NONE;
		if (cls.endsWith(".")) {
			trigger = Trigger.DOT;
			cls = cls.substring(0, cls.length()-1);
			pos--;
		} else if (cls.endsWith("(")) {
			trigger = Trigger.PARENTHESE;
			cls = cls.substring(0, cls.length()-1);
			pos--;
		}
		String[] info = getClassAndPackage(cls);
		String[] pkgs = null;
		if (info == null) pkgs = getPackages(cls);
		else  {
			pkgs = new String[] { info[0] };
			cls = info[1];
			if (cls.length() == 0) {
				// The text at the cursor is simply a package name, so display available classes
				try {
					File dat = new File(dir+"api-pkg"+File.separator+pkgs[0]);
					ObjectInputStream in = new ObjectInputStream(new FileInputStream(dat));
					ArrayList<String> list = (ArrayList<String>) in.readObject();
					ArrayList<String[]> complete = new ArrayList<String[]>();
					for (int i = 0; i < list.size(); i++) {
						complete.add(new String[] { list.get(i), "Classes in "+pkgs[0] });
					}
					new codebook.gui.Popup(textArea, complete);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					return;
				}
			}
		}
		if (pkgs == null) {
			// Variable?
			beg = TextUtilities.findWordStart(text, pos-1, "_");
			String var = text.substring(beg, pos);
			Log.log(Log.DEBUG,JavaRunner.class,"Search for var: "+var);
			HashMap<String, String> scopeVars = BufferParser.getScopeVars(textArea);
			if ((cls = scopeVars.get(var)) != null) {
				// Found it within the scope
				Log.log(Log.DEBUG,JavaRunner.class,"Scope var class = "+cls);
				info = getClassAndPackage(cls);
				Log.log(Log.DEBUG,JavaRunner.class,"Info = "+info);
				if (info == null) pkgs = getPackages(cls);
				else {
					pkgs = new String[] { info[0] };
					cls = info[1];
					Log.log(Log.DEBUG,JavaRunner.class,"Package = "+pkgs[0]+", Class = "+cls);
				}
				if (pkgs == null) return;
				if (trigger == Trigger.DOT) {
					ApiParser.JavaClass ob = getClassOb(textArea, cls, pkgs);
					if (ob == null) return;
					if (trigger == Trigger.DOT) {
						// Display instance methods/fields
						ArrayList<String[]> methods = ob.getMethods();
						ArrayList<String[]> fields = ob.getFields();
						ArrayList<String[]> complete = new ArrayList<String[]>();
						for (String[] method : methods) {
							if (method[4].equals("instance") && method[3].equals("public")) {
								String[] m = new String[2];
								m[0] = method[0]+" : "+method[1];
								m[1] = method[2];
								complete.add(m);
							}
						}
						for (String[] field : fields) {
							if (field[4].equals("instance") && field[3].equals("public")) {
								String[] f = new String[2];
								f[0] = field[0]+" : "+field[1];
								f[1] = field[2];
								complete.add(f);
							}
						}
						new codebook.gui.Popup(textArea, complete);
					}
				}
			} else {
				// TODO: Search vars parsed from sidekick on save
			}
		} else {
			// It was a class
			ApiParser.JavaClass ob = (ApiParser.JavaClass) getClassOb(textArea, cls, pkgs);
			if (trigger == Trigger.DOT) {
				// Display static methods/fields
				ArrayList<String[]> methods = ob.getMethods();
				ArrayList<String[]> fields = ob.getFields();
				ArrayList<String[]> complete = new ArrayList<String[]>();
				for (String[] method : methods) {
					if (method[4].equals("static") && method[3].equals("public")) {
						String[] m = new String[2];
						m[0] = method[0]+" : "+method[1];
						m[1] = method[2];
						complete.add(m);
					}
				}
				for (String[] field : fields) {
					if (field[4].equals("static") && field[3].equals("public")) {
						String[] f = new String[2];
						f[0] = field[0]+" : "+field[1];
						f[1] = field[2];
						complete.add(f);
					}
				}
				new codebook.gui.Popup(textArea, complete);
			} else if (trigger == Trigger.PARENTHESE) {
				// Display constructors
				new codebook.gui.Popup(textArea, ob.getConstructors());
			} else {
				// Insert package
				textArea.getBuffer().insert(textArea.getLineStartOffset(line)+beg, ob.getPackage()+".");
			}
		}
	}
	// }}} run()
	// getClassOb() {{{
	/**
	 * Takes a class name and list of potential packages and returns the appropriate data object.
	 * @param cls the name of the class
	 * @param pkgs a list of packages associated with the class
	 */
	private static ApiParser.JavaClass getClassOb(JEditTextArea textArea, String cls, String[] pkgs) {
		String pkg = (pkgs.length <= 1) ? pkgs[0] : (new ChooserDialog(textArea, pkgs)).getChosen();
		String s = File.separator;
		try {
			String path = getClassObjectDir(cls)+s+pkg;
			File dat = new File(path);
			Log.log(Log.DEBUG,JavaRunner.class,"Loading Java class ob at "+path);
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(dat));
			ApiParser.JavaClass ob = (ApiParser.JavaClass) in.readObject();
			return ob;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	// }}} getClassOb()
	// getClassAndPackage() {{{
	/**
	 * If the string is a full class name, return [pkg, class]. Otherwise, return null
	  @param cls the class name to test
	 */
	public static String[] getClassAndPackage(String cls) {
		String[] tokens = cls.split("\\.");
		if (tokens.length == 1) return null;
		if (new File(dir+"api-pkg"+File.separator+cls).exists()) {
			return new String[] { cls, "" };
		}
		String pkg = cls.substring(0, cls.lastIndexOf("."));
		for (int i = tokens.length-1; i >= 0; i--) {
			File pkgFile = new File(dir+"api-pkg"+File.separator+pkg);
			if (pkgFile.exists()) {
				return new String[] { pkg, cls.substring(pkg.length()+1) };
			}
			pkg = pkg.substring(0, pkg.length()-(tokens[i].length()+1));
		}
		return null;
	}
	// }}} getClassAndPackage()
	// getPackages() {{{
	/**
	 * Returns an arraylist of valid package names for the given class, or null if not a valid class
	 * @param cls the classname to test
	 * @return an arraylist of valid packages, or null if the class was not found
	 */
	public static String[] getPackages(String cls) {
		try {
			ArrayList<String> pkgs = new ArrayList<String>();
			File classObDir = new File(getClassObjectDir(cls));
			if (classObDir == null) return null;
			String[] pkgArray = classObDir.list();
			for (String s : pkgArray) {
				pkgs.add(s);
			}
			return pkgs.toArray(pkgArray);
		} catch (Exception e) {
			return null;
		}
	}
	// }}} getPackages()
	// getClassObjectDir() {{{
	/**
	 * Convenience method to locate the directory of a given class
	 * @param cls the classname to locate
	 * @return the directory of this class
	 */
	private static String getClassObjectDir(String cls) {
		String s = File.separator;
		try {
			File fdir = null;
			String path = null;
			if (jEdit.getPlugin("projectviewer.ProjectPlugin") != null) {
				// If projectviewer is installed, look in the active project
				try {
					String proj = projectviewer.ProjectViewer.getActiveProject(
						jEdit.getActiveView()).getName();
					path = dir+s+"project"+s+proj+s+cls;
					fdir = new File(path);
				} catch (Exception e) {}
			}
			if (fdir == null || !fdir.exists()) {
				path = dir+"api-cls"+s+cls;
				fdir = new File(path);
			}
			if (fdir == null || !fdir.exists()) return null;
			return path;
		} catch (Exception e) {
			return null;
		}
	}
	// }}} getClassObjectDir()
	// complete() {{{
	/**
	 * Insert text into the textarea
	 */
	public static void complete(JEditTextArea textArea, String complete) {
		// If complete has parameters, modify string for use with superabbrevs
		int p1 = complete.indexOf("(");
		int col = complete.lastIndexOf(":");
		if (p1 != -1) {
			boolean constructor = (col == -1);
			int p2 = complete.indexOf(")", p1);
			if (p2 != -1) {
				String params = complete.substring(p1+1, p2);
				String new_params = "";
				ArrayList<Integer> seps = new ArrayList<Integer>();
				int index = 0;
				int comma = -1;
				while ((comma = params.indexOf(",", index)) != -1) {
					int b1 = 0, b2 = 0;
					for (int i = 0; i<comma; i++) {
						char c = params.charAt(i);
						if (c == '<') b1++;
						else if (c == '>') b2++;
					}
					if (b1 == b2) seps.add(comma);
					index = comma+1;
				}
				for (int j=0; j<seps.size(); j++) {
					int beg = (j == 0) ? 0 : seps.get(j-1)+1;
					String param = params.substring(beg, seps.get(j)).trim();
					new_params += "${"+(j+1)+":"+param+"}, ";
				}
				// Add the last parameter, after all commas
				if (seps.size()>0) {
					String param = params.substring(seps.get(seps.size()-1)+1, params.length()).trim();
					new_params += "${"+(seps.size()+1)+":"+param+"}";
				} else {
					if (params.length()>0) new_params = "${1:"+params+"}";
				}
				if (constructor) complete = new_params+")";
				else complete = complete.substring(0, p1+1)+new_params+")";
			}
		} else if (col != -1) {
			complete = complete.substring(0, col).trim();
		}
		// If it's a constructor and has no return type, don't insert the class' name
		superabbrevs.SuperAbbrevs.expandAbbrev(textArea.getView(), complete, null);
	}
	// }}} complete()
}
