package codebook.java;
// imports {{{
import java.util.HashMap;

import org.gjt.sp.jedit.textarea.TextArea;
import org.gjt.sp.util.StandardUtilities;
import org.gjt.sp.util.Log;
// }}} imports
/**
 * @author Damien Radtke
 * class BufferParser
 * Handles buffer parsing for java and related modes
 */
public class BufferParser {
	// TODO: Implement buffer parsing on save via sidekick
	// getScopeVars() {{{
	/**
	 * This method starts at the cursor and reads up the buffer as long as indentation remains the same.
	 * @param textArea the currently editing text area
	 * @return a var->class hashmap of variables found in this scope
	 */
	public static HashMap<String, String> getScopeVars(TextArea textArea) {
		HashMap<String, String> map = new HashMap<String, String>();
		int i = textArea.getCaretLine();
		String line = textArea.getLineText(i);
		int space = StandardUtilities.getLeadingWhiteSpace(line);
		final String regex = "^\\w+?(<.*?>)*?\\s+?\\w*?(\\s*?=.*?|\\s*?;)";
		while (StandardUtilities.getLeadingWhiteSpace(line = textArea.getLineText(i)) == space) {
			// Parse this line
			Log.log(Log.DEBUG,BufferParser.class,"Starting line: "+line);
			line = line.trim();
			for (int j=0; j<ApiParser.MODIFIERS.length; j++) {
				if (line.startsWith(ApiParser.MODIFIERS[j]+" ")) {
					line = line.substring(ApiParser.MODIFIERS[j].length()+1);
				}
			}
			if (line.matches(regex)) {
				try {
					// Add the var to map
					int end = line.indexOf("=");
					if (end == -1) end = line.indexOf(";");
					do {
						end--;
					} while (line.charAt(end) == ' ');
					int sp = line.lastIndexOf(" ", end);
					String var = line.substring(sp+1, end+1);
					String cls = line.substring(0, sp).trim();
					if (cls.indexOf("<") != -1) cls = cls.substring(0, cls.indexOf("<"));
					//org.gjt.sp.jedit.Macros.message(org.gjt.sp.jedit.jEdit.getActiveView(), var+" : "+cls);
					map.put(var, cls);
				} catch (Exception e) {}
			}
			Log.log(Log.DEBUG,BufferParser.class,"Ending line: "+line);
			i--;
		}
		return map;
	}
	// }}} getScopeVars()
}
