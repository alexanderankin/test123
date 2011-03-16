/*
This file, unless otherwise noted, is wholly the work of Dale Anson,
danson@grafidog.com. The complete contents of this file is hereby 
released into the public domain, with no rights reserved. For questions, 
concerns, or comments, please email the author.
*/

package ise.calculator;

import java.awt.Dimension;
import javax.swing.JToggleButton;
import javax.swing.BorderFactory;

/**
 * A 48 x 30 JButton. Note that 48/30 = 1.6, which is very close to the
 * golden ratio, so certainly these buttons look very good.
 */
public class RectangleToggleButton extends JToggleButton {
    private Dimension size = new Dimension(48, 30);

    public RectangleToggleButton(String text) {
        super(text);
        setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    }

    public Dimension getPreferredSize() {
        return size;
    }
    public Dimension getMinimumSize() {
        return size;
    }
    public Dimension getMaximumSize() {
        return size;
    }
}
