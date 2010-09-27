
/*
 * Copyright (c) 2003, Dale Anson
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


package ise.plugin.nav;

import java.awt.Component;
import javax.swing.ComboBoxEditor;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.DocumentFilter.FilterBypass;


/**
 * A text field that only accepts integer numbers.  The minimum and maximum
 * values that are acceptable can be set, otherwise, Integer.MIN_VALUE and
 * Integer.MAX_VALUE are used.
 */
public class NumberTextField extends JTextField implements ComboBoxEditor {

    private int minValue = Integer.MIN_VALUE;
    private int maxValue = Integer.MAX_VALUE;
    
    // for ComboBoxEditor, keep track of the object being displayed.  This object
    // should have a <code>toString</code> method that returns a string that parses
    // into an integer.
    private Object item = null;

    public NumberTextField() {
        super();
        addFilter();
    }

    public NumberTextField( String text, int columns ) {
        super( text, columns );
        addFilter();
    }

    public void setMaxValue(int value) {
        maxValue = value;
    }

    public int getMaxValue() {
        return maxValue;
    }

    // if text field is empty, fill it with min value
    public void setMinValue(int value) {
        minValue = value;
        String text = getText();
        if (text == null || text.length() == 0) {
            text = String.valueOf(minValue);
        }
        setText(text);
    }

    public int getMinValue() {
        return minValue;
    }

    // if text field is empty, return min value
    public int getValue() {
        String text = getText();
        if (text == null || text.length() == 0) {
            text = String.valueOf(getMinValue());   
        }
        return Integer.parseInt(text);
    }

    public void setValue(int value) {
        setText(String.valueOf(value));
    }

    // add an instance of NumericDocumentFilter as a
    // document filter to the current text field
    private void addFilter() {
        ( ( AbstractDocument ) this.getDocument() ).setDocumentFilter( new NumericDocumentFilter() );
    }

    class NumericDocumentFilter extends DocumentFilter {
        public void insertString( FilterBypass fb, int offset, String string, AttributeSet attr )
        throws BadLocationException {
            if ( string == null ) {
                return ;
            }
            if ( isNumeric( string ) && inRange( new StringBuilder(getText()).insert(offset, string) ) ) {
                super.insertString( fb, offset, string, attr );
            }
        }

        public void remove( FilterBypass fb, int offset, int length )
        throws BadLocationException {
            super.remove( fb, offset, length );
        }

        public void replace( FilterBypass fb, int offset, int length, String text, AttributeSet attrs )
        throws BadLocationException {
            if ( text == null ) {
                return ;
            }
            if ( isNumeric( text ) && inRange( new StringBuilder(getText()).replace(offset, offset + length, text) ) ) {
                super.replace( fb, offset, length, text, attrs );
            }
        }

        private boolean isNumeric( String string ) {
            for ( char c : string.toCharArray() ) {
                if ( ! Character.isDigit( c ) ) {
                    return false;
                }
            }
            return true;
        }

        private boolean inRange( CharSequence string ) {
            int value = Integer.parseInt( string.toString() );
            return value <= maxValue && value >= minValue;
        }
    }
    
    // ComboBoxEditor methods...
    
    public Component getEditorComponent() {
        return this;   
    }
    
    public Object getItem() {
        return item;   
    }
    
    public void setItem(Object item) {
        this.item = item;
        setText(item == null ? "0" : item.toString());   
    }
}