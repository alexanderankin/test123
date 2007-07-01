package ise.plugin.svn.library;


import javax.swing.*;
import java.util.Vector;
import org.gjt.sp.jedit.jEdit;

/**
 * ComboBox that fills itself from some properties stored in the jEdit property
 * file.  Loads at most 10 items plus a "-- Select --" item that is not actually
 * selectable.
 */
public class PropertyComboBox extends JComboBox {

    public static final String SELECT = "-- Select --";
    private String propertyPrefix = null;
    private Vector<String> values = null;

    public PropertyComboBox( String propertyPrefix ) {
        if (propertyPrefix == null || propertyPrefix.length() == 0) {
            throw new IllegalArgumentException("invalid property prefix");
        }
        this.propertyPrefix = propertyPrefix;

        values = new Vector<String>();

        for ( int i = 1; i < 10; i++ ) {
            String name = jEdit.getProperty( propertyPrefix + i );
            if ( name == null ) {
                break;
            }
            values.insertElementAt( name, 0 );
        }
        if ( values.size() > 0 && !values.contains( SELECT ) ) {
            values.insertElementAt( SELECT, 0 );
        }
        setModel(new DefaultComboBoxModel(values));
    }

    /**
     * Adds a value to the combo box.  The item will be added immediately after
     * the "-- Select --".
     * @param value a value to add to the list
     */
    public void addValue(String value) {
        if (value != null && value.length() > 0) {
            values.insertElementAt(value, 1);
        }
    }

    /**
     * Saves the current list in the combo box to the jEdit property file.  Only the
     * top 10 items will be saved.
     */
    public void save() {
        if ( values != null ) {
            for (int i = 1; i < Math.min(values.size(), 10); i++) {
                String value = values.get( i );
                if ( SELECT.equals( value ) ) {
                    continue;
                }
                if ( value != null && value.length() > 0 ) {
                    jEdit.setProperty( propertyPrefix + i, value );
                }
            }
        }
    }

}
