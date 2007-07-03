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
            String name = jEdit.getProperty( propertyPrefix + i );
            if ( name == null ) {
                break;
            }
            if ( values.search( name ) == -1 ) {
                values.push( name );
            }
        }
        model = new DefaultComboBoxModel( (Vector)values );

        if ( model.getSize() > 0 && model.getIndexOf( SELECT ) < 0 ) {
            model.insertElementAt( SELECT, 0 );
        }
        setModel( model );
    }

    @Override
    public void setEditable( boolean editable ) {
        super.setEditable(false);
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
