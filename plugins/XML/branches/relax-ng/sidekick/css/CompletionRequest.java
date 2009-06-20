/**
 * CompletionRequest.java
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2006 Jakub Roztocil
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package sidekick.css;


//{{{ Imports
import java.util.*;
import java.util.regex.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.util.Log;
import sidekick.*;
//}}}

public class CompletionRequest {

	//{{{ CompletionRequest constructor
	public CompletionRequest(EditPane editPane, int caret) {
		if (!CssSideKickCompletion.initialized()) {
			CssSideKickCompletion.initialize();
		}
		completionList = new ArrayList<String>();
		buffer = editPane.getBuffer();
		textBeforeCaret = buffer.getText(0, caret);
		word = getWord(textBeforeCaret);
		// Log.log(Log.DEBUG, CompletionRequest.class, "[" + textBeforeCaret + "]");
	}
	//}}}

	//{{{ getSideKickCompletion() method
	public SideKickCompletion getSideKickCompletion() {
		boolean selectedProperty = false;

		if (!canComplete(textBeforeCaret)) {
			return null;
		}

		if (canAddAllUnits(textBeforeCaret)) {

			// user is typing number
			word = "";
			addMatchedToCompletionList(CssSideKickCompletion.getCssUnits());

		} else if (canCompleteUnit(word)) {

			// user is typing unit for number
			word = getUnitStart(word);
			addMatchedToCompletionList(CssSideKickCompletion.getCssUnits(), word);

		} else if (canCompleteCssValues(textBeforeCaret)) {

			// user has typed property
			String currProperty = getCurrentCssProperty(textBeforeCaret);
			ArrayList values = getPropertyValues(currProperty);

			if (word.length() == 0) {

				// and has not yet typed start of value, show him all possible values
				addMatchedToCompletionList(values);

			} else {

				// started type value, show matched
				addMatchedToCompletionList(values, word);

			}
		} else {
			selectedProperty = true;
			addMatchedToCompletionList(findMatchedProperties(word));

		}

		if (completionList.size() > 0) {
			return new CssSideKickCompletion(completionList, word, selectedProperty);
		}
		return null;
		
		
	} //}}}


	//{{{ Private members
	
	private List<String> completionList;
	private JEditBuffer buffer;
	private String textBeforeCaret;
	private String word;

	// {{{ Static fields
	private static ArrayList emptyArrayList = new ArrayList(0);
                                          
	// patterns
	private static Pattern CURR_PROP               = Pattern.compile("([\\w-]+):[^:]*$");
	private static Pattern GET_WORD                = Pattern.compile("[^\\s:;{]*$");
	private static Pattern CAN_COMPLETE            = Pattern.compile("[{;][^}]*([\\w-]|:\\s+)$");
	private static Pattern CAN_COMPLETE_VALUES     = Pattern.compile(":[^;}]*$");
	private static Pattern CAN_COMPLETE_UNITS      = Pattern.compile("^\\d+[a-z]+$");
	private static Pattern CAN_COMPLETE_ALL_UNITS  = Pattern.compile("[;:{\\s.]\\d+$");
	private static Pattern UNIT_START              = Pattern.compile("[a-z]+$");
	// }}}


	//{{{ findMatchedProperties() method
	private ArrayList findMatchedProperties(String startsWith) {
		ArrayList<String> found = new ArrayList<String>();
		Iterator it = CssSideKickCompletion.getCssProperties().keySet().iterator();
		while (it.hasNext()) {
			String n = (String) it.next();
			if (!startsWith.equals(n) && n.startsWith(startsWith)) {
				found.add(n);
			}
		}
		Collections.sort(found);
		return found;
	} //}}}

	//{{{ addMatchedToCompletionList() method
	private void addMatchedToCompletionList(ArrayList cantidateCompletions, String startsWith) {
		Iterator it = cantidateCompletions.iterator();
		String value;
		while (it.hasNext()) {
			value = (String)it.next();
			if (value.length() > startsWith.length() && value.startsWith(startsWith)) {
				completionList.add(value);
			}
		}
	}
	//}}}

	//{{{ addMatchedToCompletionList() method
	private void addMatchedToCompletionList(ArrayList completions) {
		Iterator it = completions.iterator();
		while (it.hasNext()) {
			completionList.add((String)it.next());
		}
	}
	//}}}

	//{{{ getPropertyValues() method
	private ArrayList getPropertyValues(String property) {
		if (CssSideKickCompletion.getCssProperties().containsKey(property)) {
			return (ArrayList)CssSideKickCompletion.getCssProperties().get(property);
		}
		return emptyArrayList;
	}
	//}}}

	//{{{ getWord() method
	private String getWord(String text) {
		Matcher wordM = GET_WORD.matcher(text);
		wordM.find();
		// Log.log(Log.DEBUG, CompletionRequest.class, "Word: '" + wordM.group(0) + "'");
		return wordM.group(0);
	}
	//}}}

	//{{{ getUnitStart() method
	private String getUnitStart(String word) {
		Matcher m = UNIT_START.matcher(word);
		m.find();
		return m.group(0);
	}
	//}}}

	//{{{ getCurrentCssProperty() method
	private String getCurrentCssProperty(String text) {
		Matcher m = CURR_PROP.matcher(text);
		m.find();
		// Log.log(Log.DEBUG, CompletionRequest.class, "getCurrentCssProperty() = '" + m.group(1) + "'");
		return m.group(1);
	}
	//}}}

	//{{{ canComplete() method
	private boolean canComplete(String text) {
		return CAN_COMPLETE.matcher(text).find();
	}
	//}}}

	//{{{ canCompleteCssValues() method
	private boolean canCompleteCssValues(String text) {
		return CAN_COMPLETE_VALUES.matcher(text).find();
	}
	//}}}

	//{{{ canAddAllUnits() method
	private boolean canAddAllUnits(String text) {
		return CAN_COMPLETE_ALL_UNITS.matcher(text).find();
	}
	//}}}

	//{{{ canCompleteUnit() method
	private boolean canCompleteUnit(String word) {
		return CAN_COMPLETE_UNITS.matcher(word).find();
	}
	//}}}
	//}}}

}
