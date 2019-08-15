/*
* FindAndReplace.java - for jEdit's text component
* Copyright (C) 2002 Dale Anson
*
* You may use and modify this package for any purpose. Redistribution is
* permitted, in both source and binary form, provided that this notice
* remains intact in all source distributions of this package.
*/
package bigdoc;

import java.awt.*;
import java.awt.event.*;
import java.util.regex.*;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.event.*;
import ise.java.awt.*;


/**
 * A panel/dialog for doing find and replace on a JEditTextArea.
 * Repurposed for BigDoc, there is find, but no replace.
 *
 * @author    Dale Anson, danson@grafidog.com
 * @version   $Revision: 98 $
 */
public class FindAndReplace extends JDialog {

    public final static int FIND = 0;
    private JTextArea textarea = null;
    private JTextField to_find = null;


    /**
     * Constructor for FindAndReplace
     *
     * @param textarea
     * @param parent    Description of the Parameter
     */
    public FindAndReplace( JFrame parent, JTextArea textarea ) {
        this( parent, FIND, textarea );
    }


    /**
     * Constructor for FindAndReplace
     *
     * @param type
     * @param parent  Description of the Parameter
     * @param ta      Description of the Parameter
     */
    public FindAndReplace( JFrame parent, int type, JTextArea ta ) {
        super( parent, "Find", true );
        this.textarea = ta;
        setContentPane(getFindPanel());
        pack();
        to_find.requestFocus();
    }

    private JPanel getFindPanel() {
        JPanel panel = new JPanel();
        KappaLayout layout = new KappaLayout();
        panel.setLayout( layout );
        panel.setBorder( new javax.swing.border.EmptyBorder( 11, 11, 11, 11 ) );

        JLabel find_label = new JLabel( "Find:" );
        to_find = new JTextField( 20 );
        JButton find_btn = new JButton( "Find" );
        JButton find_next_btn = new JButton( "Find Next" );
        JButton cancel_btn = new JButton( "Close" );
        final JCheckBox wrap_cb = new JCheckBox( "Wrap search" );
        wrap_cb.setSelected(true);

        panel.add( find_label, "0, 0, 1, 1, SW, w, 3" );
        panel.add( to_find, "0, 1, 1, 1, N, w, 3" );
        panel.add( wrap_cb, "0, 2, 1, 1, 0, w, 3" );

        JPanel btn_panel = new JPanel( new KappaLayout() );
        btn_panel.add( find_btn, "0, 0, 1, 1, 0, w, 3" );
        btn_panel.add( find_next_btn, "0, 1, 1, 1, 0, w, 3" );
        btn_panel.add( cancel_btn, "0, 2, 1, 1, 0, w, 3" );
        panel.add( btn_panel, "1, 0, 1, 3, 0, h, 5" );

        find_btn.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    String text_to_find = to_find.getText();
                    if ( text_to_find == null || text_to_find.length() == 0 ) {
                        return ;
                    }
                    try {
                        // TODO: need to use the document and pull in part of the 
                        // text at a time
                        String doc = textarea.getText();
                        Pattern pattern = Pattern.compile( text_to_find, Pattern.DOTALL );
                        Matcher matcher = pattern.matcher( doc );
                        if ( matcher.find() ) {
                            int start = matcher.start();
                            int end = matcher.end();
                            //String found = doc.substring( start, end );
                            textarea.setCaretPosition( start );
                            textarea.select( start, end );
                        }
                    }
                    catch ( Exception e ) {
                        e.printStackTrace();
                    }
                }
            }
        );
        find_next_btn.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    String text_to_find = to_find.getText();
                    if ( text_to_find == null || text_to_find.length() == 0 ) {
                        return ;
                    }
                    try {
                        int initial_caret = textarea.getCaretPosition();
                        int caret = initial_caret;
                        String doc = textarea.getText();
                        Pattern pattern = Pattern.compile( text_to_find, Pattern.DOTALL );
                        Matcher matcher = pattern.matcher( doc );
                        if (!matcher.find(caret) && wrap_cb.isSelected())
                            caret = 0;
                        if ( matcher.find(caret) ) {
                            int start = matcher.start();
                            int end = matcher.end();
                            //String found = doc.substring( start, end );
                            textarea.setCaretPosition( start );
                            textarea.select( start, end );
                        }
                    }
                    catch ( Exception e ) {
                        e.printStackTrace();
                    }
                }
            }
        );

        cancel_btn.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    setVisible( false );
                    dispose();
                }
            }
        );

        return panel;
    }

}

