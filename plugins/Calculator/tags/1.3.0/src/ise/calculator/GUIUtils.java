/*
This file, unless otherwise noted, is wholly the work of Dale Anson,
danson@grafidog.com. The complete contents of this file is hereby 
released into the public domain, with no rights reserved. For questions, 
concerns, or comments, please email the author.
*/

package ise.calculator;

import java.util.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Some GUI utility methods that I seem to use over and over, so I put them
 * here in one place. Could be easily modified to be AWT-only utilities.
 *
 * @author    Dale Anson
 * @version   $Revision$
 */
public class GUIUtils {

    /**
     * Centers <code>you</code> on <code>me</code>. Useful for centering
     * dialogs on their parent frames.
     *
     * @param me   Component to use as basis for centering.
     * @param you  Component to center on <code>me</code>.
     */
    public static void center(Component me, Component you) {
        Rectangle my = me.getBounds();
        Dimension your = you.getSize();
        int x = my.x + (my.width - your.width) / 2;
        if (x < 0) {
            x = 0;
        }
        int y = my.y + (my.height - your.height) / 2;
        if (y < 0) {
            y = 0;
        }
        you.setLocation(x, y);
    }

    /**
     * Centers a component on the screen.
     *
     * @param me  Component to center.
     */
    public static void centerOnScreen(Component me) {
        Dimension screen_size = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension window_size = me.getSize();
        me.setBounds((screen_size.width - window_size.width) / 2, (screen_size.height - window_size.height) / 2, window_size.width, window_size.height);
    }

    /**
     * Expands a component to fill the screen, just like a 'maximize window'.
     *
     * @param frame the component to expand
     */
    public static void fillScreen(Component frame) {
        Dimension screen_size = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize(screen_size);
        centerOnScreen(frame);
    }

    /**
     * @param c  a Component
     * @return   the Frame containing the component or null if the component
     * doesn't have a containing Frame.
     */
    public static Frame getRootFrame(Component c) {
        Object parent = c.getParent();
        while (parent != null) {
            if (parent instanceof Frame) {
                return (Frame) parent;
            }
            parent = ((Component) parent).getParent();
        }
        return null;
    }

    /**
     * This is the only method that relies on Swing.  Comment this one out and
     * this class is AWT-only capable.
     *
     * @param c  a Component
     * @return   the JFrame containing the component or null if the component
     * doesn't have a containing JFrame.
     */
    public static javax.swing.JFrame getRootJFrame(Component c) {
        Object parent = c.getParent();
        while (parent != null) {
            if (parent instanceof javax.swing.JFrame) {
                return (javax.swing.JFrame) parent;
            }
            parent = ((Component) parent).getParent();
        }
        return null;
    }

    /**
     * @param c  a Component
     * @return   the Window containing the component or null if the component
     * doesn't have a containing Window.
     */
    public static Window getRootWindow(Component c) {
        Object parent = c.getParent();
        while (parent != null) {
            if (parent instanceof Window) {
                return (Window) parent;
            }
            parent = ((Component) parent).getParent();
        }
        return null;
    }

    public static boolean isEmptyOrBlank(String s) {
        if (s == null || s.length() == 0) {
            return true;
        }
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

}

