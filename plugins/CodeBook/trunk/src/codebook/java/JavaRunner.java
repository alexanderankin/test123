package codebook.java;
// imports {{{
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
	// TODO: Replace this
	public static final String dir = "/home/damien/.jedit/plugins/codebook.CodeBookPlugin/java/";
	public static enum Trigger { DOT, PARENTHESE, NONE };
	// run() {{{
	/**
	 * The main run method
	 * @param textArea the text area instance
	 */
	public static void run(JEditTextArea textArea) {
		int line = textArea.getCaretLine();
		int pos = textArea.getCaretPosition()-textArea.getLineStartOffset(line);
		String text = textArea.getLineText(line).substring(0, pos);
		// See if it's a class or nested class first
		int beg = TextUtilities.findWordStart(text, pos-1, "._(");
		String cls = text.substring(beg, pos);
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
		String[] pkgs = getPackages(cls);
		if (pkgs == null) {
			// Variable?
			beg = TextUtilities.findWordStart(text, pos-1, "_");
			String var = text.substring(beg, pos);
			HashMap<String, String> scopeVars = BufferParser.getScopeVars(textArea);
			if ((cls = scopeVars.get(var)) != null) {
				// Found it within the scope
				pkgs = getPackages(cls);
				if (pkgs == null) return;
				if (trigger == Trigger.DOT) {
					// TODO: Display instance methods/fields
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
						Log.log(Log.DEBUG,JavaRunner.class,"Complete list: "+complete+", size: "+complete.size());
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
				Log.log(Log.DEBUG,JavaRunner.class,"Complete list: "+complete+", size: "+complete.size());
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
		// TODO: Show a dialog to choose which package if there are multiples
		Log.log(Log.DEBUG,JavaRunner.class,"Packages: "+java.util.Arrays.toString(pkgs));
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
			Log.log(Log.ERROR,JavaRunner.class,
				"Error loading class object "+cls+": "+e+" ("+e.getMessage()+")");
			return null;
		}
	}
	// }}} getClassOb()
	// getPackages() {{{
	/**
	 * Returns an arraylist of valid package names for the given class, or null if not a valid class
	 * @param cls the classname to test
	 * @return an arraylist of valid packages, or null if the class was not found
	 */
	public static String[] getPackages(String cls) {
		Log.log(Log.DEBUG,JavaRunner.class,"Fetching packages for class "+cls);
		try {
			ArrayList<String> pkgs = new ArrayList<String>();
			String[] pkgArray = new File(getClassObjectDir(cls)).list();
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
				String proj = projectviewer.ProjectViewer.getActiveProject(
					jEdit.getActiveView()).getName();
				path = dir+s+"project"+s+proj+s+cls;
				fdir = new File(path);
			}
			if (fdir == null || !fdir.exists()) {
				path = dir+"api"+s+cls;
				Log.log(Log.DEBUG,JavaRunner.class,"Class ob dir: "+path);
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
		if (p1 != -1) {
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
				complete = complete.substring(0, p1+1)+new_params+")";
			}
		} else {
			complete = complete.substring(0, complete.lastIndexOf(":")).trim();
		}
		superabbrevs.SuperAbbrevs.expandAbbrev(textArea.getView(), complete, null);
	}
	// }}} complete()
}
