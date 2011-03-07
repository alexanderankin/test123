package beauty.options;

import javax.swing.table.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.*;

import ise.java.awt.*;

/**
 * An option pane to configure settings for the built-in CSS beautifier.
 */
public class CSSOptionPane extends AbstractOptionPane {

    private JCheckBox padCombinators;    // +, >, ~
    private JCheckBox padSelectors;
    private JCheckBox padAttribs;
    private JCheckBox padOperators;    // / and ,
    private JCheckBox padPrio;    // !
    private JCheckBox padTerm;
    private JCheckBox padParams;    // in functions

    public CSSOptionPane() {
        super( "beauty.css" );
    }

    // called when this class is first accessed
    public void _init() {
        installComponents();
    }

    // create the user interface components and do the layout
    private void installComponents() {
        setLayout( new KappaLayout() );
        setBorder( BorderFactory.createEmptyBorder(6, 6, 6, 6 ) );

        // create the components
        JLabel description = new JLabel( "<html><b>" + jEdit.getProperty("beauty.msg.CSS_Options", "CSS Options") );

        padCombinators = new JCheckBox( jEdit.getProperty("beauty.msg.Pad_combinators_(+,_>,_~)", "Pad combinators (+, >, ~)") );
        padSelectors = new JCheckBox( jEdit.getProperty("beauty.msg.Pad_selectors", "Pad selectors") );
        padAttribs = new JCheckBox( jEdit.getProperty("beauty.msg.Pad_attributes", "Pad attributes") );
        padOperators = new JCheckBox( jEdit.getProperty("beauty.msg.Pad_operators_(/_and_,)", "Pad operators (/ and ,)") );
        padPrio = new JCheckBox( jEdit.getProperty("beauty.msg.Pad_important_(!)", "Pad important (!)") );
        padTerm = new JCheckBox( jEdit.getProperty("beauty.msg.Pad_terms", "Pad terms") );
        padParams = new JCheckBox( jEdit.getProperty("beauty.msg.Pad_function_parameters", "Pad function parameters") );

        padCombinators.setSelected( jEdit.getBooleanProperty( "beauty.css.padCombinators", true ) );
        padSelectors.setSelected( jEdit.getBooleanProperty( "beauty.css.padSelectors", true ) );
        padAttribs.setSelected( jEdit.getBooleanProperty( "beauty.css.padAttribs", true ) );
        padOperators.setSelected( jEdit.getBooleanProperty( "beauty.css.padOperators", true ) );
        padPrio.setSelected( jEdit.getBooleanProperty( "beauty.css.padPrio", true ) );
        padTerm.setSelected( jEdit.getBooleanProperty( "beauty.css.padTerm", true ) );
        padParams.setSelected( jEdit.getBooleanProperty( "beauty.css.padParams", true ) );

        add( "0, 0, 1, 1, W, w, 3", description );
        add( "0, 1, 1, 1, W, w, 3", padCombinators );
        add( "0, 2, 1, 1, W, w, 3", padSelectors );
        add( "0, 3, 1, 1, W, w, 3", padAttribs );
        add( "0, 4, 1, 1, W, w, 3", padOperators );
        add( "0, 5, 1, 1, W, w, 3", padPrio );
        add( "0, 6, 1, 1, W, w, 3", padTerm );
        add( "0, 7, 1, 1, W, w, 3", padParams );
    }

    public void _save() {
        jEdit.setBooleanProperty( "beauty.css.padCombinators", padCombinators.isSelected() );
        jEdit.setBooleanProperty( "beauty.css.padSelectors", padSelectors.isSelected() );
        jEdit.setBooleanProperty( "beauty.css.padAttribs", padAttribs.isSelected() );
        jEdit.setBooleanProperty( "beauty.css.padOperators", padOperators.isSelected() );
        jEdit.setBooleanProperty( "beauty.css.padPrio", padPrio.isSelected() );
        jEdit.setBooleanProperty( "beauty.css.padTerm", padTerm.isSelected() );
        jEdit.setBooleanProperty( "beauty.css.padParams", padParams.isSelected() );
        jEdit.setBooleanProperty( "beauty.css.padSelectors", padSelectors.isSelected() );
    }

}