package utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.jedit.textarea.JEditTextArea;

public class EditorInterface {
	
	/* Returns the tag to jump to: The selected tag or the specified
	 * regexp at the caret. (null if not found)
	 */
	public String getTagForJump(View view, String tagRegExp) {
		String tag = view.getTextArea().getSelectedText();
		if (tag == null || tag.length() == 0)
			tag = getTagAtCaret(view, tagRegExp);
		return tag;
	}
	
	// Returns the specified regexp at the caret. (null if not found)
	private String getTagAtCaret(View view, String tagRegExp) {
		JEditTextArea ta = view.getTextArea();
		int line = ta.getCaretLine();
		int index = ta.getCaretPosition() - ta.getLineStartOffset(line);
		String text = ta.getLineText(line);
		Pattern pat = Pattern.compile(tagRegExp);
		Matcher m = pat.matcher(text);
		int end = -1;
		int start = -1;
		String selected = "";
		while (end <= index) {
			if (! m.find())
				return null;
			end = m.end();
			start = m.start();
			selected = m.group();
		}
		if (start > index || selected.length() == 0)
			return null;
		return selected;
	}

	// Jump to the specified file:line in the specified view
	public void jumpTo(final View view, String file, final int line) {
		jumpTo(view, file, line, 0);
	}
	
	// Jump to the specified file:line:offset in the specified view
	public void jumpTo(final View view, String file, final int line,
			final int offset)
	{
		Buffer buffer = jEdit.openFile(view, file);
		if (buffer == null) {
			System.err.println("Unable to open: " + file);
			return;
		}
		if (line <= 0)
			return;
		VFSManager.runInAWTThread(new Runnable() {
			public void run() {
				try {
					view.getTextArea().setCaretPosition(
						view.getTextArea().getLineStartOffset(line - 1) + offset);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
