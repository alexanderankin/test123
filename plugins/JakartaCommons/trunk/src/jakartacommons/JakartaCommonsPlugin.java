/*
 * JakartaCommonsPlugin.java
 * Copyright (c) 2002 Konstantin Pribluda (kpribluda@j-tec-team.de)
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


package jakartacommons;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPlugin;
import org.apache.commons.lang.StringEscapeUtils;
import org.gjt.sp.jedit.textarea.Selection;
import org.gjt.sp.jedit.textarea.TextArea;

/**
* 
*/
public class JakartaCommonsPlugin extends EditPlugin {
	public static void unescapeUnicodeSelection(TextArea textArea, Buffer buffer) {
		if (textArea.getSelectionCount() != 1) return;
		Selection[] selections = textArea.getSelection();
		int len = selections[0].getEnd() - selections[0].getStart();
		String text = buffer.getText(selections[0].getStart(), len);
		String esctext = StringEscapeUtils.unescapeJava(text);	
		buffer.remove(selections[0].getStart(), len);
		buffer.insert(selections[0].getStart(), esctext);	
	}
}
