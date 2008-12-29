
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


package ise.plugin.svn.gui.component;

import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.DocumentFilter.FilterBypass;

import org.tmatesoft.svn.core.wc.SVNRevision;


public class RevisionTextField extends JTextField {


    RevisionSelectionPanelModel model = null;

    public RevisionTextField() {
        super();
        addFilter();
    }

    public RevisionTextField( String text, int columns ) {
        super( text, columns );
        addFilter();
    }

    public void setRevisionModel( RevisionSelectionPanelModel model ) {
        this.model = model;
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
                if ( model != null ) {
                    Number number = Integer.parseInt( RevisionTextField.this.getText() );
                    model.setRevision( SVNRevision.create( number.longValue() ) );
                }
            }
        }

        public void remove( DocumentFilter.FilterBypass fb,
                int offset,
                int length )
        throws BadLocationException {
            super.remove( fb, offset, length );
            if ( model != null ) {
                Number number = Integer.parseInt( RevisionTextField.this.getText() );
                model.setRevision( SVNRevision.create( number.longValue() ) );
            }
        }

        public void replace( FilterBypass fb, int offset,
                int length, String text, AttributeSet attrs )
        throws BadLocationException {
            if ( text == null )
                return ;
            if ( isNumeric( text ) ) {
                super.replace( fb, offset, length, text, attrs );
                if ( model != null ) {
                    Number number = Integer.parseInt( RevisionTextField.this.getText() );
                    model.setRevision( SVNRevision.create( number.longValue() ) );
                }
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
}