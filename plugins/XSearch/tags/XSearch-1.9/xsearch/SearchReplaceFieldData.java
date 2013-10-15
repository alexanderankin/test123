package xsearch;

import javax.swing.JTextField;

import org.gjt.sp.util.Log;
import org.gjt.sp.util.StandardUtilities;

/**
 *  Description of the Class
 *
 *@author     widmann
 *@created    4. Januar 2005
 */
public class SearchReplaceFieldData {

	private int        caret;
	private boolean    isReplaceField;
	private int        selectionEnd;
	private int        selectionStart;

	private JTextField textField;

	/**
	 *Constructor for the SearchReplaceFieldData object
	 *
	 * stores status data of a textfield
	 * Is created when mouse enters re-support button area
	 * Note: Changes via keyboard are ignored
	 *
	 *@param  textField       Description of the Parameter
	 *@param  isReplaceField  Description of the Parameter
	 */
	SearchReplaceFieldData(JTextField textField, boolean isReplaceField) {
		this.textField = textField;
		this.isReplaceField = isReplaceField;
		// save textfield data immediately, because selection gets lost later
		selectionStart = textField.getSelectionStart();
		selectionEnd = textField.getSelectionEnd();
		caret = textField.getCaretPosition();
		Log.log(Log.DEBUG, ReSupportPopup.class, "+++ ReSupportPopup.115: selectionStart = " + selectionStart + ", selectionEnd = " + selectionEnd + ", caret = " + caret);
	}

	/**
	 *  Gets the newRegexpString attribute of the SearchReplaceFieldData object
	 *
	 * action depends on selection state of the target textfield:
	 * no selection: insert regexString at caret
	 * selection, regexString standard: replace selection
	 * selection, regexString bracket: put brackets around selection
	 * selection, regexString "make regexp": replace selection by matching regexp
	 *
	 *@param  regexpString  Description of the Parameter
	 */
	void setNewRegexpString(String regexpString) {
		String currentText = textField.getText();
		String newText;
		int newCaretPosition;
		// check data consistency (maybe status changed via keyboard)
		if (currentText.length() < selectionStart
			 || currentText.length() < selectionEnd
			 || currentText.length() < caret) {
			selectionStart = 0;
			selectionEnd = 0;
			caret = currentText.length();
		}
		if (selectionStart == selectionEnd) {
			// no selection
			if (regexpString.equals("make regexp")) {
				newText = currentText;
				newCaretPosition = caret;
			}
			else if (regexpString.equals("esc")) {
				// TODO: replace selection by matching regexp:
				newText = StandardUtilities.charsToEscapes(currentText, "\r\t\n\\()[]{}$^*+?|.");
				newCaretPosition = caret;
			}
			else {
				newText = currentText.substring(0, caret) + regexpString +
					currentText.substring(caret, currentText.length());
					newCaretPosition = caret + regexpString.length();
			}
		}
		else {
			// something selected
			if (regexpString.equals("make regexp")) {
				// TODO: replace selection by matching regexp:
				newText = currentText;
				newCaretPosition = caret;
			}
			else if (regexpString.equals("esc")) {
				// TODO: replace selection by matching regexp:
				newText = StandardUtilities.charsToEscapes(currentText, "\r\t\n\\()[]{}$^*+?|.");
				newCaretPosition = caret;
			}
			else if (regexpString.equals("()") || regexpString.equals("[]") 
			|| regexpString.equals("[^]")) {
				// put brackets around selection
				newText = currentText.substring(0, selectionStart) + 
					regexpString.substring(0,regexpString.length()-1) +
					currentText.substring(selectionStart, selectionEnd) + 
					regexpString.substring(regexpString.length()-1) +
					currentText.substring(selectionEnd, currentText.length());
					newCaretPosition = selectionEnd + 1;
			}
			else {
				newText = currentText.substring(0, selectionStart) + regexpString +
					currentText.substring(selectionEnd, currentText.length());
					newCaretPosition = selectionStart + regexpString.length();
			}
		}
		Log.log(Log.DEBUG, ReSupportPopup.class,"+++ ReSupportPopup.208: regexpString = "+regexpString+", currentText = "+currentText+", newText = "+newText);
		textField.setText(newText);
		textField.requestFocusInWindow();
		textField.setCaretPosition(newCaretPosition);
	}

	/**
	 *  Gets the replaceField attribute of the SearchReplaceFieldData object
	 *
	 *@return    The replaceField value
	 */
	boolean isReplaceField() {
		return isReplaceField;
	}
}

