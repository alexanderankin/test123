
package beauty.options.java;

import beauty.parsers.java.JavaParser;

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
* An option pane to configure settings for the built-in Java beautifier.
*/
public class JavaOptionPane extends JPanel {

    private JRadioButton attachedBrackets;
    private JRadioButton brokenBrackets;
    private int bracketStyle;
    private JCheckBox breakElse;
    private JCheckBox padParens;
    private JCheckBox padOperators;

    public JavaOptionPane() {
        super();
        bracketStyle = jEdit.getIntegerProperty( "beauty.java.bracketStyle", JavaParser.ATTACHED );
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
        JLabel description = new JLabel( "<html><b>" + jEdit.getProperty( "beauty.java8.Brackets_and_Padding", "Brackets and Padding" ) );
        attachedBrackets = new JRadioButton( "<html>" + jEdit.getProperty( "beauty.msg.Use_attached_brackets,_e.g.", "Use attached brackets, e.g." ) + "<br> try {" );
        brokenBrackets = new JRadioButton( "<html>" + jEdit.getProperty( "beauty.msg.Use_broken_brackets,_e.g.", "Use broken brackets, e.g." ) + "<br>try<br>{" );

        ButtonGroup bg = new ButtonGroup();
        bg.add( attachedBrackets );
        bg.add( brokenBrackets );
        breakElse = new JCheckBox( "<html>" + jEdit.getProperty( "beauty.msg.Break", "Break" ) + " 'else', 'catch', 'while', e.g.<br>}<br>else" );
        breakElse.setSelected( jEdit.getBooleanProperty( "beauty.java.breakElse", false ) );
        breakElse.setEnabled( bracketStyle == JavaParser.ATTACHED );

        switch ( bracketStyle ) {
            case JavaParser.ATTACHED:
                attachedBrackets.setSelected( true );
                brokenBrackets.setSelected( false );
                break;
            case JavaParser.BROKEN:
                attachedBrackets.setSelected( false );
                brokenBrackets.setSelected( true );
                breakElse.setSelected( true );
                break;
        }

        // default both of these padding parameters to true, since no one likes scrunchy, unreadable code.
        padParens = new JCheckBox( jEdit.getProperty( "beauty.msg.Pad_parenthesis,_e.g._", "Pad parenthesis, e.g. " ) + "if ( i == 1 ) versus if (i == 1)" );
        padParens.setSelected( jEdit.getBooleanProperty( "beauty.java.padParens", true ) );

        padOperators = new JCheckBox( jEdit.getProperty( "beauty.msg.Pad_operators,_e.g._", "Pad operators, e.g. " ) + "if (i==1) versus if (i == 1)" );
        padOperators.setSelected( jEdit.getBooleanProperty( "beauty.java.padOperators", true ) );
        
        ActionListener al = new ActionListener(){

            public void actionPerformed( ActionEvent ae ) {
                if ( attachedBrackets.equals( ae.getSource() ) ) {
                    bracketStyle = JavaParser.ATTACHED;
                }
                else if ( brokenBrackets.equals( ae.getSource() ) ) {
                    bracketStyle = JavaParser.BROKEN;
                    breakElse.setSelected( true );
                }


                breakElse.setEnabled( bracketStyle == JavaParser.ATTACHED );
            }
        };
        attachedBrackets.addActionListener( al );
        brokenBrackets.addActionListener( al );
        add( "0, 0, 1, 1, W, w, 3", description );
        add( "0, 1, 1, 1, W, w, 3", attachedBrackets );
        add( "0, 2, 1, 1, W, w, 3", brokenBrackets );
        add( "0, 3, 1, 1, W, w, 3", breakElse );
        add( "0, 4, 1, 1, W, w, 3", padParens );
        add( "0, 5, 1, 1, W, w, 3", padOperators );
    }

    public void _save() {
        jEdit.setIntegerProperty( "beauty.java.bracketStyle", bracketStyle );
        jEdit.setBooleanProperty( "beauty.java.breakElse", breakElse.isSelected() );
        jEdit.setBooleanProperty( "beauty.java.padParens", padParens.isSelected() );
        jEdit.setBooleanProperty( "beauty.java.padOperators", padOperators.isSelected() );
    }
}
