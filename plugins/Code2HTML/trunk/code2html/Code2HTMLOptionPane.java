/*
 * Code2HTMLOptionPane.java
 * Copyright (c) 2000, 2001, 2002 Andre Kaplan
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


package code2html;

import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Dimension;

import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;

import javax.swing.JCheckBox;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;


public class Code2HTMLOptionPane
    extends AbstractOptionPane
{
    private JCheckBox ckUseCSS;
    private JCheckBox ckShowGutter;
    private JTextField tfWrap;

    // <pre> and <body> custom code elements
    private JPanel customStylePanel;
    private JPanel customBODY;
    private JPanel customBODYHtml;
    private JPanel customBODYCss;
    
    private JTextField customBODYHtmlValue;
    private JTextArea  customBODYCssValue;
    private JTextField customPREHtmlValue;
    private JTextArea  customPRECssValue; 
    
    private JPanel customPRE;
    private JPanel customPREHtml;
    private JPanel customPRECss;

    public Code2HTMLOptionPane() {
        super("code2html");
    }


    public void _init() {
        this.ckUseCSS = new JCheckBox(
            jEdit.getProperty("options.code2html.use-css"),
            jEdit.getBooleanProperty("code2html.use-css", false)
        );
        addComponent(this.ckUseCSS);

        this.ckShowGutter = new JCheckBox(
            jEdit.getProperty("options.code2html.show-gutter"),
            jEdit.getBooleanProperty("code2html.show-gutter", false)
        );
        addComponent(this.ckShowGutter);

        this.tfWrap = new JTextField(4);
        int wrap = jEdit.getIntegerProperty("code2html.wrap", 0);
        if (wrap < 0) { wrap = 0; }
        this.tfWrap.setText("" + wrap);
        addComponent(jEdit.getProperty("options.code2html.wrap"), this.tfWrap);
        
        // Custom <pre> and <body>
        this.customStylePanel = new JPanel(new GridLayout(2,1));
        this.customStylePanel.setBorder(
            new TitledBorder(
                new EtchedBorder(EtchedBorder.LOWERED),
                jEdit.getProperty("options.code2html.custom.styles.1")));
        this.customStylePanel.setPreferredSize(new Dimension(500, 500));
        this.customStylePanel.setToolTipText(
            jEdit.getProperty("options.code2html.custom.styles.2"));
        
        this.customBODY = new JPanel(new BorderLayout());
        this.customStylePanel.add(this.customBODY);
        
        this.customPRE = new JPanel(new BorderLayout());
        this.customStylePanel.add(this.customPRE);
        
        this.customBODYHtmlValue = new JTextField(
            jEdit.getProperty("options.code2html.body.html.value"), 80);
        this.customBODYCssValue = new JTextArea(
            jEdit.getProperty("options.code2html.body.style.value"), 10, 80);
        this.customBODYCssValue.addKeyListener(new KeyAdapter(){
                public void keyPressed(KeyEvent e){
                    if(e.getKeyChar() == KeyEvent.VK_ENTER){
                        customBODYCssValue.append("\n");
                        e.consume();
                    }
                }
            });
        
        this.customPREHtmlValue = new JTextField(
            jEdit.getProperty("options.code2html.pre.html.value"), 80);
        this.customPRECssValue = new JTextArea(
            jEdit.getProperty("options.code2html.pre.style.value"), 10, 80);
        this.customPRECssValue.addKeyListener(new KeyAdapter(){
                public void keyPressed(KeyEvent e){
                    if(e.getKeyChar() == KeyEvent.VK_ENTER){
                        customPRECssValue.append("\n");
                        e.consume();
                    }
                }
            });
        
        this.customBODYHtml = new JPanel(new BorderLayout());
        this.customBODYHtml.add(
            new JLabel(jEdit.getProperty("options.code2html.body.html.open")),
            BorderLayout.WEST);
        this.customBODYHtml.add(this.customBODYHtmlValue, BorderLayout.CENTER);
        this.customBODYHtml.add(
            new JLabel(jEdit.getProperty("options.code2html.body.html.close")),
            BorderLayout.EAST);
        
        this.customBODYCss = new JPanel(new BorderLayout());
        this.customBODYCss.add(
            new JLabel(jEdit.getProperty("options.code2html.body.style.open")),
            BorderLayout.NORTH);
        this.customBODYCss.add(new JScrollPane(this.customBODYCssValue));
        this.customBODYCss.add(
            new JLabel(jEdit.getProperty("options.code2html.body.style.close")),
            BorderLayout.SOUTH);
        
        this.customBODY.add(this.customBODYHtml, BorderLayout.NORTH);
        this.customBODY.add(this.customBODYCss, BorderLayout.CENTER);
        
        this.customPREHtml = new JPanel(new BorderLayout());
        this.customPREHtml.add(
            new JLabel(jEdit.getProperty("options.code2html.pre.html.open")),
            BorderLayout.WEST);
        this.customPREHtml.add(this.customPREHtmlValue, BorderLayout.CENTER);
        this.customPREHtml.add(
            new JLabel(jEdit.getProperty("options.code2html.pre.html.close")),
            BorderLayout.EAST);
        
        this.customPRECss = new JPanel(new BorderLayout());
        this.customPRECss.add(
            new JLabel(jEdit.getProperty("options.code2html.pre.style.open")),
            BorderLayout.NORTH);
        this.customPRECss.add(new JScrollPane(this.customPRECssValue));
        this.customPRECss.add(
            new JLabel(jEdit.getProperty("options.code2html.pre.style.close")),
            BorderLayout.SOUTH);
        
        this.customPRE.add(this.customPREHtml, BorderLayout.NORTH);
        this.customPRE.add(this.customPRECss, BorderLayout.CENTER);
        
        addComponent(this.customStylePanel);
    }


    public void _save() {
        jEdit.setBooleanProperty("code2html.use-css",
            this.ckUseCSS.isSelected());

        jEdit.setBooleanProperty("code2html.show-gutter",
            this.ckShowGutter.isSelected());

        int wrap = Code2HTMLOptionPane.getInteger(this.tfWrap.getText(), 0);
        if (wrap < 0) { wrap = 0; }
        jEdit.setProperty("code2html.wrap", "" + wrap);
        
        // save custom style tags values
        jEdit.setProperty("options.code2html.body.html.value",
            customBODYHtmlValue.getText());
        jEdit.setProperty("options.code2html.body.style.value",
            customBODYCssValue.getText());
        jEdit.setProperty("options.code2html.pre.html.value",
            customPREHtmlValue.getText());
        jEdit.setProperty("options.code2html.pre.style.value",
            customPRECssValue.getText()); 
    }


    public static int getInteger(String value, int defaultVal) {
        int res = defaultVal;
        if (value != null) {
            try {
                res = Integer.parseInt(value);
            } catch (NumberFormatException nfe) {
                Log.log(Log.WARNING, Code2HTMLOptionPane.class,
                    "NumberFormatException caught: [" + value + "]");
            }
        }
        return res;
    }
}

