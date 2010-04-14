/*
Copyright (c) 2007, Dale Anson
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.
* Neither the name of the author nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package ise.plugin.svn.library;

import javax.swing.*;
import java.util.*;
import org.gjt.sp.jedit.jEdit;

/**
 * ComboBox that fills itself from some properties stored in the jEdit property
 * file.  Loads at most 10 items plus a "-- Select --" item that is not actually
 * selectable.
 */
public class PropertyComboBox extends JComboBox {

    public static final String SELECT = "-- Select --";
    private String propertyPrefix = null;
    DefaultComboBoxModel model = null;

    public PropertyComboBox( String propertyPrefix ) {
        if ( propertyPrefix == null || propertyPrefix.length() == 0 ) {
            throw new IllegalArgumentException( "invalid property prefix" );
        }
        this.propertyPrefix = propertyPrefix;

        // load the previous values
        Stack<String> values = new Stack<String>();
        for ( int i = 1; i < 10; i++ ) {
            try {
                String name = jEdit.getProperty( propertyPrefix + i );
                if ( name == null ) {
                    break;
                }
                if ( values.search( name ) == -1 ) {
                    values.push( name );
                }
            }
            catch ( Exception e ) {
                // the try/catch is really only for testing when jEdit isn't
                // available.  This exception should never happen during
                // regular runtime.
                values.push( SELECT );
            }
        }
        model = new DefaultComboBoxModel( ( Vector ) values );

        if ( model.getSize() > 0 && model.getIndexOf( SELECT ) < 0 ) {
            model.insertElementAt( SELECT, 0 );
        }
        setModel( model );
        if ( model.getSize() > 0 ) {
            setSelectedIndex( 0 );
        }
    }

    @Override
    public void setEditable( boolean editable ) {
        // never editable
        super.setEditable( false );
    }

    /**
     * Adds a value to the combo box.  The item will be added immediately after
     * the "-- Select --".
     * @param value a value to add to the list
     */
    public void addValue( String value ) {
        if ( value != null && value.length() > 0 ) {
            int index = model.getIndexOf( SELECT ) >= 0 ? 1 : 0;
            model.removeElement( value );
            model.insertElementAt( value, index );
            model.setSelectedItem( value );
        }
        save();
    }

    /**
     * Saves the current list in the combo box to the jEdit property file.  Only the
     * top 10 items will be saved.
     */
    public void save() {
        for ( int i = 1; i < Math.min( model.getSize(), 10 ); i++ ) {
            String value = ( String ) model.getElementAt( i );
            if ( SELECT.equals( value ) ) {
                // there's a bug here, if SELECT is hit, the loop counter
                // increments, so only 9 items will be saved
                continue;
            }
            if ( value != null && value.length() > 0 ) {
                jEdit.setProperty( propertyPrefix + i, value );
            }
        }
    }
}
