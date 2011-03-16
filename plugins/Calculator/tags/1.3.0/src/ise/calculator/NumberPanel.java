/*
This file, unless otherwise noted, is wholly the work of Dale Anson,
danson@grafidog.com. The complete contents of this file is hereby 
released into the public domain, with no rights reserved. For questions, 
concerns, or comments, please email the author.
*/

package ise.calculator;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import static ise.calculator.Base.*;

/**
 * The number panel for the calculator.
 * @author Dale Anson, July 2003
 */
public class NumberPanel extends JPanel {

    private int base = BASE_10;

    private RectangleButton b0 = new RectangleButton("0");
    private RectangleButton b1 = new RectangleButton("1");
    private RectangleButton b2 = new RectangleButton("2");
    private RectangleButton b3 = new RectangleButton("3");
    private RectangleButton b4 = new RectangleButton("4");
    private RectangleButton b5 = new RectangleButton("5");
    private RectangleButton b6 = new RectangleButton("6");
    private RectangleButton b7 = new RectangleButton("7");
    private RectangleButton b8 = new RectangleButton("8");
    private RectangleButton b9 = new RectangleButton("9");
    private RectangleButton ba = new RectangleButton("A");
    private RectangleButton bb = new RectangleButton("B");
    private RectangleButton bc = new RectangleButton("C");
    private RectangleButton bd = new RectangleButton("D");
    private RectangleButton be = new RectangleButton("E");
    private RectangleButton bf = new RectangleButton("F");
    private RectangleButton dot = new RectangleButton(".");

    // action command to button map
    private HashMap buttons = new HashMap();

    private JTextField x_register = null;

    private int current_base;
    private int current_mode;

    // variables for macro recording --
    // is recording happening?
    private boolean recording = false;

    // storage of the recorded steps
    private ArrayList macro = null;

    ActionListener num_listener = new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
            String previous = x_register.getText();
            try {
                JButton b = (JButton) ae.getSource();
                if (x_register.getSelectedText() != null && x_register.getSelectedText().equals(previous)) {
                    x_register.setDocument(new RegisterDocument(current_base, current_mode));
                }
                Document doc = x_register.getDocument();
                doc.insertString(doc.getLength(), b.getActionCommand(), null);
            }
            catch (Exception e) {
                x_register.setText(previous);
            }
            x_register.requestFocus();
        }
    } ;

    public NumberPanel(JTextField field) {
        this(BASE_10, FLOAT, field);
    }

    /**
     * @param base Numeric base, one of 2, 8, 10, or 16, as defined in Base.
     * @param mode As defined in Base.
     * @param field The JTextField for the x-register.
     */
    public NumberPanel(int base, int mode, JTextField field) {

        super();
        this.base = base;
        current_mode = mode;
        x_register = field;

        LambdaLayout layout = new LambdaLayout();
        setLayout(layout);

        add(bd, "0, 0, 1, 1, 0,, 2");
        add(be, "1, 0, 1, 1, 0,, 2");
        add(bf, "2, 0, 1, 1, 0,, 2");
        add(ba, "0, 1, 1, 1, 0,, 2");
        add(bb, "1, 1, 1, 1, 0,, 2");
        add(bc, "2, 1, 1, 1, 0,, 2");
        add(b7, "0, 2, 1, 1, 0,, 2");
        add(b8, "1, 2, 1, 1, 0,, 2");
        add(b9, "2, 2, 1, 1, 0,, 2");
        add(b4, "0, 3, 1, 1, 0,, 2");
        add(b5, "1, 3, 1, 1, 0,, 2");
        add(b6, "2, 3, 1, 1, 0,, 2");
        add(b1, "0, 4, 1, 1, 0,, 2");
        add(b2, "1, 4, 1, 1, 0,, 2");
        add(b3, "2, 4, 1, 1, 0,, 2");
        add(b0, "0, 5, 2, 1, 0, w, 2");
        add(dot, "2, 5, 1, 1, 0,, 2");

        buttons.put(bd.getActionCommand(), bd);
        buttons.put(be.getActionCommand(), be);
        buttons.put(bf.getActionCommand(), bf);
        buttons.put(ba.getActionCommand(), ba);
        buttons.put(bb.getActionCommand(), bb);
        buttons.put(bc.getActionCommand(), bc);
        buttons.put(b7.getActionCommand(), b7);
        buttons.put(b8.getActionCommand(), b8);
        buttons.put(b9.getActionCommand(), b9);
        buttons.put(b4.getActionCommand(), b4);
        buttons.put(b5.getActionCommand(), b5);
        buttons.put(b6.getActionCommand(), b6);
        buttons.put(b1.getActionCommand(), b1);
        buttons.put(b2.getActionCommand(), b2);
        buttons.put(b3.getActionCommand(), b3);
        buttons.put(b0.getActionCommand(), b0);
        buttons.put(dot.getActionCommand(), dot);

        setBase(base, mode);

        addActionListener(num_listener);

    }

    public void setBase(int base, int mode) {
        // turn off all buttons
        b0.setEnabled(false);
        b1.setEnabled(false);
        b2.setEnabled(false);
        b3.setEnabled(false);
        b4.setEnabled(false);
        b5.setEnabled(false);
        b6.setEnabled(false);
        b7.setEnabled(false);
        b8.setEnabled(false);
        b9.setEnabled(false);
        ba.setEnabled(false);
        bb.setEnabled(false);
        bc.setEnabled(false);
        bd.setEnabled(false);
        be.setEnabled(false);
        bf.setEnabled(false);
        dot.setEnabled(false);

        current_base = base;

        switch (mode) {
            // float and big decimal use dot and 0 - 9
            case FLOAT:
                be.setEnabled(true);
            case BIGDECIMAL:
                b0.setEnabled(true);
                b1.setEnabled(true);
                b2.setEnabled(true);
                b3.setEnabled(true);
                b4.setEnabled(true);
                b5.setEnabled(true);
                b6.setEnabled(true);
                b7.setEnabled(true);
                b8.setEnabled(true);
                b9.setEnabled(true);
                bf.setEnabled(false);
                dot.setEnabled(true);
                return ;
        }

        // if not a float mode, adjust by base
        dot.setEnabled(false);
        switch (base) {
            case BASE_16:
                b0.setEnabled(true);
                b1.setEnabled(true);
                b2.setEnabled(true);
                b3.setEnabled(true);
                b4.setEnabled(true);
                b5.setEnabled(true);
                b6.setEnabled(true);
                b7.setEnabled(true);
                b8.setEnabled(true);
                b9.setEnabled(true);
                ba.setEnabled(true);
                bb.setEnabled(true);
                bc.setEnabled(true);
                bd.setEnabled(true);
                be.setEnabled(true);
                bf.setEnabled(true);
                break;
            case BASE_10:
                b0.setEnabled(true);
                b1.setEnabled(true);
                b2.setEnabled(true);
                b3.setEnabled(true);
                b4.setEnabled(true);
                b5.setEnabled(true);
                b6.setEnabled(true);
                b7.setEnabled(true);
                b8.setEnabled(true);
                b9.setEnabled(true);
                break;
            case BASE_8:
                b0.setEnabled(true);
                b1.setEnabled(true);
                b2.setEnabled(true);
                b3.setEnabled(true);
                b4.setEnabled(true);
                b5.setEnabled(true);
                b6.setEnabled(true);
                b7.setEnabled(true);
                break;
            case BASE_2:
                b0.setEnabled(true);
                b1.setEnabled(true);
                break;
        }
    }

    public int getBase() {
        return base;
    }

    public void addActionListener(ActionListener al) {
        b1.addActionListener(al);
        b2.addActionListener(al);
        b3.addActionListener(al);
        b4.addActionListener(al);
        b5.addActionListener(al);
        b6.addActionListener(al);
        b7.addActionListener(al);
        b8.addActionListener(al);
        b9.addActionListener(al);
        b0.addActionListener(al);
        ba.addActionListener(al);
        bb.addActionListener(al);
        bc.addActionListener(al);
        bd.addActionListener(al);
        be.addActionListener(al);
        bf.addActionListener(al);
        dot.addActionListener(al);
    }

    protected void setRecording(boolean recording, ArrayList list) {
        this.recording = recording;
        macro = list;
    }

    protected void doClick(String cmd) {
        JButton btn = (JButton) buttons.get(cmd);
        if (btn != null) {
            num_listener.actionPerformed(new ActionEvent(btn, 0, cmd));
            if (recording && macro != null) {
                macro.add(cmd);
            }
        }
    }
}