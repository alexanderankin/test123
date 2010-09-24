
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

import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.DocumentFilter.FilterBypass;


public class LineNumberTextField extends JTextField implements ComboBoxEditor {


    public LineNumberTextField() {
        super();
        addFilter();
    }

    public LineNumberTextField( String text, int columns ) {
        super( text, columns );
        addFilter();
    }

    // add an instance of NumericDocumentFilter as a
    // document filter to the current text field
    private void addFilter() {
        ( ( AbstractDocument ) this.getDocument() ).setDocumentFilter( new NumericDocumentFilter() );
    }

    class NumericDocumentFilter extends DocumentFilter {
        public void insertString( FilterBypass fb,
                int offset, String string, AttributeSet attr )
        throws BadLocationException {
            if ( string == null )
                return ;
            if ( isNumeric( string ) ) {
                super.insertString( fb, offset, string, attr );
            }
        }

        public void remove( DocumentFilter.FilterBypass fb,
                int offset,
                int length )
        throws BadLocationException {
            super.remove( fb, offset, length );
        }

        public void replace( FilterBypass fb, int offset,
                int length, String text, AttributeSet attrs )
        throws BadLocationException {
            if ( text == null )
                return ;
            if ( isNumeric( text ) ) {
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
    }
    
    public Component getEditorComponent() {
        return this;   
    }
    
    public Object getItem() {
        return getText();   
    }
 
    public void setItem(Object item) {
        setText(item == null ? "" : item.toString());
    }
}