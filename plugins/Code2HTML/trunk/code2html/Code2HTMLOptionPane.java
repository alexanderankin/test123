/*
 *  Code2HTMLOptionPane.java
 *  Copyright (c) 2000, 2001, 2002 Andre Kaplan
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package code2html;

/* not used yet, see non-complete code below
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
*/

import javax.swing.JCheckBox;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;


/**
 *  This class displays properties to be set by the user in the jEdit -> Plugin
 *  Properties pane
 *
 *  TODO: complete the feature to be able to set custom styles and custom
 *  pre and body tags.
 *
 *@author     Andre Kaplan
 *@version    0.5
 */
public class Code2HTMLOptionPane extends AbstractOptionPane {
    private JCheckBox ckShowGutter;
    private JCheckBox ckShowNumbers;
    private JCheckBox ckUseCSS;
    private NumberTextField tfWrap;
    //private JTextField tfDivider;
    
    /* this is not complete
    private JPanel customBODY;
    private JPanel customBODYCss;
    private JTextArea customBODYCssValue;
    private JPanel customBODYHtml;
    private JTextField customBODYHtmlValue;
    private JPanel customPRE;
    private JPanel customPRECss;
    private JTextArea customPRECssValue;
    private JPanel customPREHtml;
    private JTextField customPREHtmlValue;
    private JPanel customStylePanel;
    */
    
    /**
     *  Code2HTMLOptionPane Constructor
     */
    public Code2HTMLOptionPane() {
        super("code2html");
    }


    /**
     *  layout the components and initialize the GUI
     */
    public void _init() {
        
        // use a style sheet rather than in-line style
        ckUseCSS = new JCheckBox(
            jEdit.getProperty("options.code2html.use-css"),
            jEdit.getBooleanProperty("code2html.use-css", false));
        addComponent(ckUseCSS);

        // show the gutter as part of the output
        ckShowGutter = new JCheckBox(
            jEdit.getProperty("options.code2html.show-gutter"),
            jEdit.getBooleanProperty("code2html.show-gutter", false));
        addComponent(ckShowGutter);

        // show the line numbers as part of the output
        // TODO: doesn't showing the gutter take care of this?
        // TODO: this isn't used anywhere
        /*
        ckShowNumbers = new JCheckBox(
            jEdit.getProperty("options.code2html.show-numbers"),
            jEdit.getBooleanProperty("code2html.show-numbers", true));
        addComponent(ckShowNumbers);
        */
        
        // set a line wrap width, this might be necessary for printing
        int wrap = jEdit.getIntegerProperty("code2html.wrap", 0);
        if (wrap < 0) {
            wrap = 0;
        }
        tfWrap = new NumberTextField(String.valueOf(wrap), 4);
        tfWrap.setMinValue(0);
        addComponent(jEdit.getProperty("options.code2html.wrap"), tfWrap);

        // set the character to use as the gutter divider
        // TODO: this isn't used anywhere
        /*
        tfDivider = new JTextField(4);
        String divider = jEdit.getProperty("code2html.gutter-divider", ":");
        tfDivider.setText(divider);
        addComponent(
            jEdit.getProperty("options.code2html.gutter-divider"),
            tfDivider);
        */
        
        // Custom <pre> and <body>
        // TODO: complete this, should be able to set custom style, pre, and body
        // definitions
        /* 
        customStylePanel = new JPanel(new GridLayout(2, 1));
        customStylePanel.setBorder(
            new TitledBorder(
            new EtchedBorder(EtchedBorder.LOWERED),
            jEdit.getProperty("options.code2html.custom.styles.1")));
        customStylePanel.setPreferredSize(new Dimension(500, 500));
        customStylePanel.setMinimumSize(new Dimension(300, 300));
        customStylePanel.setToolTipText(
            jEdit.getProperty("options.code2html.custom.styles.2"));

        customBODY = new JPanel(new BorderLayout());
        customStylePanel.add(customBODY);

        customPRE = new JPanel(new BorderLayout());
        customStylePanel.add(customPRE);

        customBODYHtmlValue = new JTextField(
            jEdit.getProperty("options.code2html.body.html.value"), 80);
        customBODYCssValue = new JTextArea(
            jEdit.getProperty("options.code2html.body.style.value"), 10, 80);
        customBODYCssValue.addKeyListener(
            new KeyAdapter() {// prevent jEdit stealing the ENTER strokes
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                        customBODYCssValue.append(
                            System.getProperty("line.separator"));
                        e.consume();
                    }
                }
            });

        customPREHtmlValue = new JTextField(
            jEdit.getProperty("options.code2html.pre.html.value"), 80);
        customPRECssValue = new JTextArea(
            jEdit.getProperty("options.code2html.pre.style.value"), 10, 80);
        customPRECssValue.addKeyListener(
            new KeyAdapter() {// prevent jEdit stealing the ENTER strokes
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                        customPRECssValue.append(
                            System.getProperty("line.separator"));
                        e.consume();
                    }
                }
            });

        customBODYHtml = new JPanel(new BorderLayout());
        customBODYHtml.add(
            new JLabel(jEdit.getProperty("options.code2html.body.html.open")),
            BorderLayout.WEST);
        customBODYHtml.add(customBODYHtmlValue, BorderLayout.CENTER);
        customBODYHtml.add(
            new JLabel(jEdit.getProperty("options.code2html.body.html.close")),
            BorderLayout.EAST);

        customBODYCss = new JPanel(new BorderLayout());
        customBODYCss.add(
            new JLabel(jEdit.getProperty("options.code2html.body.style.open")),
            BorderLayout.NORTH);
        customBODYCss.add(new JScrollPane(customBODYCssValue));
        customBODYCss.add(
            new JLabel(jEdit.getProperty("options.code2html.body.style.close")),
            BorderLayout.SOUTH);

        customBODY.add(customBODYHtml, BorderLayout.NORTH);
        customBODY.add(customBODYCss, BorderLayout.CENTER);

        customPREHtml = new JPanel(new BorderLayout());
        customPREHtml.add(
            new JLabel(jEdit.getProperty("options.code2html.pre.html.open")),
            BorderLayout.WEST);
        customPREHtml.add(customPREHtmlValue, BorderLayout.CENTER);
        customPREHtml.add(
            new JLabel(jEdit.getProperty("options.code2html.pre.html.close")),
            BorderLayout.EAST);

        customPRECss = new JPanel(new BorderLayout());
        customPRECss.add(
            new JLabel(jEdit.getProperty("options.code2html.pre.style.open")),
            BorderLayout.NORTH);
        customPRECss.add(new JScrollPane(customPRECssValue));
        customPRECss.add(
            new JLabel(jEdit.getProperty("options.code2html.pre.style.close")),
            BorderLayout.SOUTH);

        customPRE.add(customPREHtml, BorderLayout.NORTH);
        customPRE.add(customPRECss, BorderLayout.CENTER);

        addComponent(customStylePanel);
        */
        //Component c = get

        //getFrame().pack();
        revalidate();
    }


    /**
     *  Save he properties that have been set in the GUI
     */
    public void _save() {
        jEdit.setBooleanProperty("code2html.use-css",
            ckUseCSS.isSelected());

        jEdit.setBooleanProperty("code2html.show-gutter",
            ckShowGutter.isSelected());

        //jEdit.setBooleanProperty("code2html.show-numbers",
        //    ckShowNumbers.isSelected());

        int wrap = tfWrap.getValue();
        jEdit.setProperty("code2html.wrap", "" + wrap);

        //jEdit.setProperty("code2html.gutter-divider", tfDivider.getText());

        // save custom style tags values
        /* this is not complete
        jEdit.setProperty("options.code2html.body.html.value",
            customBODYHtmlValue.getText());
        jEdit.setProperty("options.code2html.body.style.value",
            customBODYCssValue.getText());
        jEdit.setProperty("options.code2html.pre.html.value",
            customPREHtmlValue.getText());
        jEdit.setProperty("options.code2html.pre.style.value",
            customPRECssValue.getText());
        */
    }
}

