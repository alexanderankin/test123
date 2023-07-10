
package beauty.options;

import beauty.parsers.json.JSONBeautyListener;

import ise.java.awt.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.*;


/**
* An option pane to configure settings for the built-in Json beautifier.
*/
public class JsonOptionPane extends AbstractOptionPane {

    private JRadioButton attachedBrackets;
    private JRadioButton brokenBrackets;
    private int bracketStyle;

    public JsonOptionPane() {
        super( "beauty.json" );
        bracketStyle = jEdit.getIntegerProperty( "beauty.json.bracketStyle", JSONBeautyListener.ATTACHED );
    }

    // called when this class is first accessed
    public void _init() {
        installComponents();
    }

    // create the user interface components and do the layout
    private void installComponents() {
        setLayout( new KappaLayout() );
        setBorder( BorderFactory.createEmptyBorder( 6, 6, 6, 6 ) );

        // create the components
        attachedBrackets = new JRadioButton( "<html>" + jEdit.getProperty( "beauty.msg.Use_attached_brackets,_e.g.", "Use attached brackets, e.g." ) + "<br> object: {" );
        brokenBrackets = new JRadioButton( "<html>" + jEdit.getProperty( "beauty.msg.Use_broken_brackets,_e.g.", "Use broken brackets, e.g." ) + "<br>object:<br>{" );

        ButtonGroup bg = new ButtonGroup();
        bg.add( attachedBrackets );
        bg.add( brokenBrackets );

        switch ( bracketStyle ) {
            case JSONBeautyListener.ATTACHED:
                attachedBrackets.setSelected( true );
                brokenBrackets.setSelected( false );
                break;
            case JSONBeautyListener.BROKEN:
                attachedBrackets.setSelected( false );
                brokenBrackets.setSelected( true );
                break;
        }

        ActionListener al = new ActionListener(){

            public void actionPerformed( ActionEvent ae ) {
                if ( attachedBrackets.equals( ae.getSource() ) ) {
                    bracketStyle = JSONBeautyListener.ATTACHED;
                }
                else if ( brokenBrackets.equals( ae.getSource() ) ) {
                    bracketStyle = JSONBeautyListener.BROKEN;
                }
            }
        };
        attachedBrackets.addActionListener( al );
        brokenBrackets.addActionListener( al );
        add( "0, 1, 1, 1, W, w, 3", attachedBrackets );
        add( "0, 2, 1, 1, W, w, 3", brokenBrackets );
    }

    public void _save() {
        jEdit.setIntegerProperty( "beauty.json.bracketStyle", bracketStyle );
    }
}
