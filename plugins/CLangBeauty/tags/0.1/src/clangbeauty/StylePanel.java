
package clangbeauty;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

// The dockable panel may display several of these, one per language as found in 
// the .clang-foramt file.
public class StylePanel extends JPanel {

    private HashMap<String, JComponent> components = new HashMap<String, JComponent>();
    private StyleOptions styleOptions = null;

    public StylePanel( String language, String title, StyleOptions styleOptions ) {
        this.styleOptions = styleOptions;
        init( language, title );
    }

    // create and load the style option values into the components
    private void init( final String language, String title ) {

        // need a label and component per option name
        String[] optionNames = styleOptions.getOptionNames();

        // panel settings
        setLayout( new GridLayout( optionNames.length + 1, 2 ) );
        setBorder( BorderFactory.createEtchedBorder() );

        // panel title
        JLabel titleLabel = new JLabel( title );
        add( titleLabel );
        components.put( "titleLabel", titleLabel );
        add( new JLabel( "" ) );    // empty placehold for grid layout

        for ( final String name : optionNames ) {
            add( new JLabel( name ) );    // component label

            String[] optionChoices = styleOptions.getOptionChoices( name );
            if ( optionChoices.length > 0 ) {
                if ( optionChoices.length == 1 && "-1".equals( optionChoices[0] ) ) {

                    // use a NumberTextField
                    NumberTextField numberField = new NumberTextField();
                    String value = styleOptions.getOption( language, name );
                    int number = value == null || "".equals( value ) ? 0 : Integer.parseInt( value );
                    numberField.setValue( number );
                    numberField.getDocument().addDocumentListener( new DocumentListener(){

                        public void changedUpdate( DocumentEvent de ) {
                            saveValue( de );
                        }

                        public void insertUpdate( DocumentEvent de ) {
                            saveValue( de );
                        }

                        public void removeUpdate( DocumentEvent de ) {
                            saveValue( de );
                        }

                        private void saveValue( DocumentEvent de ) {
                            try {
                                styleOptions.setOption( language, name, de.getDocument().getText( 0, de.getDocument().getLength() ) );
                            }
                            catch ( Exception e ) {     // NOPMD
                                // ignored, there won't be an index out of bound exception here
                            }
                        }
                    } );
                    add( numberField );
                    components.put( name, numberField );
                }
                else {

                    // otherwise, use a combo box
                    JComboBox choices = new JComboBox( optionChoices );
                    choices.addActionListener(
                    new ActionListener(){

                        public void actionPerformed( ActionEvent ae ) {
                            JComboBox source = ( JComboBox )ae.getSource();
                            styleOptions.setOption( language, name, ( String )source.getSelectedItem() );
                        }
                    }
                    );
                    String selected = styleOptions.getOption( language, name );
                    if ( selected != null ) {
                        choices.setSelectedItem( selected );
                    }

                    add( choices );
                    components.put( name, choices );
                }
            }
            else {

                // use a plain text field
                JTextField textField = new JTextField( styleOptions.getOption( language, name ) );
                add( textField );
                components.put( name, textField );
            }
        }
    }
    
    /**
     * Load the style option values into the components.
     */
    protected void load( String language, String title, StyleOptions styleOptions ) {
        JLabel titleLabel = ( JLabel )components.get( "titleLabel" );
        titleLabel.setText( title );

        String[] optionNames = styleOptions.getOptionNames();

        for ( String name : optionNames ) {
            JComponent component = components.get( name );
            if ( component instanceof NumberTextField ) {
                NumberTextField numberField = ( NumberTextField )component;
                String value = styleOptions.getOption( language, name );
                int number = value == null || "".equals( value ) ? 0 : Integer.parseInt( value );
                numberField.setValue( number );
            }
            else if ( component instanceof JComboBox ) {
                JComboBox choices = ( JComboBox )component;
                String[] optionChoices = styleOptions.getOptionChoices( name );
                choices.setModel( new DefaultComboBoxModel<String>( optionChoices ) );
                String selected = styleOptions.getOption( language, name );
                if ( selected != null ) {
                    choices.setSelectedItem( selected );
                }
            }
            else {
                JTextField textField = ( JTextField )component;
                textField.setText( styleOptions.getOption( language, name ) );
            }
        }
    }
}
