/*
 * BibTeXOptionPane.java - BibTeX options panel
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2002 Anthony Roy
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
package uk.co.antroy.latextools;
//{{{ Imports
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.text.*;
import java.util.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.*;
import javax.swing.text.*;
//}}}

public class BibTeXOptionPane extends AbstractOptionPane
{
	//{{{ BibTexOptionPane constructor
	public BibTeXOptionPane()
	{
		super("bibtex");
	} //}}}

	//{{{ _init() method
	protected void _init()
	{
		wordlength = new WholeNumberField(jEdit.getIntegerProperty("bibtex.bibtitle.wordlength",0),4);
		wordcount = new WholeNumberField(jEdit.getIntegerProperty("bibtex.bibtitle.wordcount",0),4);

		wordlengthPan = new JPanel();
		wordlengthPan.add(new JLabel(jEdit.getProperty("options.bibtex.wordlength")));
		wordlengthPan.add(wordlength);

		wordcountPan  = new JPanel();
		wordcountPan.add(new JLabel(jEdit.getProperty("options.bibtex.wordcount")));
		wordcountPan.add(wordcount);

		addComponent(inserttags = new JCheckBox(jEdit.getProperty(
			"options.bibtex.inserttags")));
		inserttags.getModel().setSelected(jEdit.getBooleanProperty(
			"bibtex.inserttags"));
		addComponent(wordlengthPan);
		addComponent(wordcountPan);

	} //}}}

	//{{{ _save() method
	protected void _save()
	{
		jEdit.setBooleanProperty("bibtex.inserttags",inserttags
			.getModel().isSelected());
		jEdit.setIntegerProperty("bibtex.bibtitle.wordlength",wordlength.getValue());
		jEdit.setIntegerProperty("bibtex.bibtitle.wordcount",wordcount.getValue());

	} //}}}

	//{{{ Private members
	private JCheckBox inserttags;
	private JPanel wordlengthPan,
	               wordcountPan;
	private WholeNumberField wordlength,
	                         wordcount;
//	}}}

  protected class WholeNumberField extends JTextField {
    private Toolkit toolkit;
    private NumberFormat integerFormatter;

    public WholeNumberField(int value, int columns) {
        super(columns);
        toolkit = Toolkit.getDefaultToolkit();
        integerFormatter = NumberFormat.getNumberInstance(Locale.US);
        integerFormatter.setParseIntegerOnly(true);
        setValue(value);
    }

    public int getValue() {
        int retVal = 0;
        try {
            retVal = integerFormatter.parse(getText()).intValue();
        } catch (ParseException e) {
            // This should never happen because insertString allows
            // only properly formatted data to get in the field.
            toolkit.beep();
        }
        return retVal;
    }

    public void setValue(int value) {
        setText(integerFormatter.format(value));
    }

    protected Document createDefaultModel() {
        return new WholeNumberDocument();
    }

    protected class WholeNumberDocument extends PlainDocument {
        public void insertString(int offs,
                                 String str,
                                 AttributeSet a)
                throws BadLocationException {
            char[] source = str.toCharArray();
            char[] result = new char[source.length];
            int j = 0;

            for (int i = 0; i < result.length; i++) {
                if (Character.isDigit(source[i]))
                    result[j++] = source[i];
                else {
                    toolkit.beep();
                    System.err.println("insertString: " + source[i]);
                }
            }
            super.insertString(offs, new String(result, 0, j), a);
        }
    }
}

}
