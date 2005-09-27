/*
 * AStyleThread.java - a thread for beautifying an jEdit buffer using AStyle
 * Copyright (c) 2001 Dirk Moebius (dmoebius@gmx.net)
 * Artistic Style (c) 1998-2001 Tal Davidson (davidsont@bigfoot.com)
 *
 * :tabSize=4:indentSize=4:noTabs=false:maxLineLen=0:
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


package astyleplugin;


import java.util.Enumeration;
import java.util.Vector;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import astyle.ASSourceIterator;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.Marker;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.options.BeanHelper;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.util.Log;


public class AStyleThread implements Runnable {

	public AStyleThread(Buffer buffer, View view, boolean showErrorDialogs) {
		this.buffer = buffer;
		this.view = view;
		this.showErrorDialogs = showErrorDialogs;
	}


	public void run() {
		Log.log(Log.DEBUG, this, "beautifying the buffer...");

		int caretPos = 0;
		JEditTextArea textarea = null;

		try {
			if (view != null) {
				view.showWaitCursor();
				textarea = view.getTextArea();
				caretPos = textarea.getCaretPosition();
			}

			initFormatter();

			// format the buffer by traversing all lines:
			StringBuffer result = new StringBuffer(buffer.getLength() + 1000);
			while (formatter.hasMoreLines()) {
				result.append(formatter.nextLine());
				if (formatter.hasMoreLines())
					result.append('\n');
			}

			// store the string back:
			String contents = result.toString();

			if (contents == null || contents.length() == 0) {
				// result string is empty!
				Log.log(Log.ERROR, this, jEdit.getProperty("astyleplugin.error.empty.message"));
				if (showErrorDialogs)
					GUIUtilities.error(view, "astyleplugin.error.empty", null);
				return;
			}

			// remember and remove all markers:
			Vector markers = (Vector) buffer.getMarkers().clone();
			buffer.removeAllMarkers();

			// set new buffer contents:
			buffer.beginCompoundEdit();
			buffer.remove(0, buffer.getLength());
			buffer.insert(0, contents);
			buffer.endCompoundEdit();

			// restore markers:
			Enumeration itr = markers.elements();
			while (itr.hasMoreElements()) {
				Marker marker = (Marker) itr.nextElement();
				buffer.addMarker(marker.getShortcut(), marker.getPosition());
			}

			// restore remembered caret position:
			if (textarea != null) {
				textarea.setCaretPosition(Math.min(caretPos, textarea.getBufferLength()));
				textarea.scrollToCaret(true);
			}

			Log.log(Log.DEBUG, this, "completed with success.");
		}
		catch(Exception ex) {
			Log.log(Log.ERROR, this, ex);
			if (showErrorDialogs)
				GUIUtilities.error(view, "astyleplugin.error.other", new Object[] { ex });
		}
		finally {
			if (view != null)
				view.hideWaitCursor();
			view = null;
		}
	}


	private void initFormatter() {
		if (beanHelper == null) {
			beanHelper = new BeanHelper("astyleplugin", "astyleplugin.Formatter", this.getClass().getClassLoader());
			formatter = (Formatter) beanHelper.createBean();
		} else {
			beanHelper.initBean(formatter);
		}

		// The following properties are set automatically according to the
		// current buffer settings:
		//   - cStyle (boolean)
		//   - useTabs (boolean)
		//   - tabIndentation (int)
		//   - spaceIndentation (int)
		//   - tabSpaceConversionMode (boolean)
		// (These properties are excluded from the BeanOptionPane.)

		String mode = buffer.getMode().getName();
		int tabSize = ((Integer) buffer.getProperty("tabSize")).intValue();
		int indentSize = ((Integer) buffer.getProperty("indentSize")).intValue();
		boolean noTabs = buffer.getBooleanProperty("noTabs");
		boolean assumeCStyle = mode.equals("c") || mode.equals("c++") || mode.equals("cplusplus");

		if (assumeCStyle) {
			Log.log(Log.DEBUG, this, "assuming C/C++ style, because mode name is 'c', 'c++' or 'cplusplus'");
			formatter.setCStyle(true);
		} else {
			Log.log(Log.DEBUG, this, "assuming Java style, because mode name is not 'c', 'c++' or 'cplusplus'");
			formatter.setCStyle(false);
		}

		formatter.setTabIndentation(tabSize);
		formatter.setSpaceIndentation(indentSize);
		formatter.setUseTabs(!noTabs);
		formatter.setTabSpaceConversionMode(noTabs);
		formatter.init(new JEditTextIterator());
	}


	private View view = null;
	private Buffer buffer = null;
	private boolean showErrorDialogs = false;

	private static BeanHelper beanHelper = null;
	private static Formatter formatter = null;


	class JEditTextIterator implements ASSourceIterator {

		public JEditTextIterator() {
			lineCount = buffer.getLineCount();
			currentLine = 0;
		}

		public boolean hasMoreLines() {
			return currentLine < lineCount;
		}

		public String nextLine() {
			return getLineText(currentLine++);
		}

		private final String getLineText(int line) {
			int start = buffer.getLineStartOffset(line);
			int end = buffer.getLineEndOffset(line);
			return buffer.getText(start, end - start - 1);
		}

		private int lineCount;
		private int currentLine;

	}

}
