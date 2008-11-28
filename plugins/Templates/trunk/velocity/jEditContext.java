/*
 * jEditContext.java
 * Copyright (c) 2002 Calvin Yu
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
package velocity;

import org.apache.velocity.context.AbstractContext;
import org.apache.velocity.context.Context;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.Selection;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.StandardUtilities;

/**
 * An context to interface with jEdit.
 */
public class jEditContext extends AbstractContext
   implements VelocityConstants
{

   public final static String SELECTION   = "_jeditSelection";
   public final static String INDENTATION = "_jeditIndentation";
   public final static String TAB         = "_jeditTab";

   public final static String[] KEYS = new String[] {
      BUFFER, TEXT_AREA, VIEW, INDENTATION, TAB
   };

   private String selectionText;
   private String indentation;
   private String tab;
   private JEditTextArea textArea;
   private View view;

   /**
    * Create a new <code>jEditContext</code>.
    */
   public jEditContext(View aView, JEditTextArea aTextArea,
                       Context innerContext)
   {
      super(innerContext);
      textArea = aTextArea;
      view = aView;
      indentation = getLeadingWhiteSpace();
   }

   /**
    * Capture the current selection and store it in the context.  The selected text is then removed.
    */
   public void captureSelection()
   {
      Selection[] selections = textArea.getSelection();
      if (selections.length == 0) {
         return;
      }
      if (selections.length > 1) {
         Log.log(Log.WARNING, this, "Mulitple selections encountered, will not capture");
         return;
      }
      selectionText = textArea.getSelectedText();
      textArea.getBuffer().remove(selections[0].getStart(),
                                  selections[0].getEnd() - selections[0].getStart());
   }


   /**
    * Returns a jEdit value.
    */
   public Object internalGet(String key)
   {
      if (BUFFER.equals(key)) {
         return textArea.getBuffer();
      } else if (VIEW.equals(key)) {
         return view;
      } else if (TEXT_AREA.equals(key)) {
         return textArea;
      }

      if (!key.startsWith("_jedit")) {
         return null;
      }
      if (INDENTATION.equals(key)) {
         return indentation;
      } else if (TAB.equals(key)) {
         if (tab == null) {
            int tabSize = textArea.getBuffer().getTabSize();
            boolean noTabs = textArea.getBuffer().getBooleanProperty("noTabs");
            tab = StandardUtilities.createWhiteSpace(tabSize, (noTabs ? 0 : tabSize));
         }
         return tab;
      } else if (SELECTION.equals(key)) {
		  return (selectionText == null ? "" : selectionText);
      }
      return null;
   }

   /**
    * Returns all keys.
    */
   public Object[] internalGetKeys()
   {
      return KEYS;
   }

   /**
    * Returns <code>true</code> if this context contains the given key.
    */
   public boolean internalContainsKey(Object key)
   {
      return indexOf(key, KEYS) > -1;
   }

   /**
    * Put the given value.
    */
   public Object internalPut(String key, Object value)
   {
      return getChainedContext().put(key, value);
   }

   /**
    * Removes the given value.
    */
   public Object internalRemove(Object key)
   {
      return getChainedContext().remove(key);
   }

   /**
    * Evaluates the leading whitespace for the current caret line.
    */
   protected String getLeadingWhiteSpace()
   {
      Buffer buffer = (Buffer) textArea.getBuffer();
      String line = textArea.getLineText(textArea.getCaretLine());
      if (line == null) {
         line = "";
      }
      int len = StandardUtilities.getLeadingWhiteSpaceWidth(line, buffer.getTabSize());
      int tabs = buffer.getBooleanProperty("noTabs") ? 0 : buffer.getTabSize();
      return StandardUtilities.createWhiteSpace(len, tabs);
   }

   /**
    * Returns <code>true</code> if the given arrays contains the given object.
    */
   private int indexOf(Object key, Object[] arr)
   {
      if (key == null) {
         return -1;
      }
      for (int i=0; i<arr.length; i++) {
         if (key.equals(arr[i])) {
            return i;
         }
      }
      return -1;
   }

}
