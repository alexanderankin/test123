/*
 * CodeAidPopup.java
 * Copyright (c) 1999, 2000, 2001, 2002 CodeAid team
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


package org.jymc.jpydebug.jedit.popup;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.util.Log;
import org.jymc.jpydebug.jedit.*;


public abstract class CodeAidPopup
{
    protected String typedText;
    protected Hint hint;
    protected JEditTextArea textArea;
    private PopupWindow window;

    /**
     * This value allows only one instance of a popup to be shown at a time.
     */
    private static CodeAidPopup currentPopup;


    /**
     * Create a new <code>CodeAidPopup</code>.
     */
    public CodeAidPopup() {
        typedText = "";
    }


    /**
     * Sets the StartingText attribute of the CodeAidPopup object
     */
    public void setStartingText(String text) {
        typedText = text;
        updateTypedText();
    }


    /**
     * Sets the Hint attribute of the CodeAidPopup object
     */
    public void setHint(Hint aHint) {
        hint = aHint;
    }


    /**
     * Returns the size of the popup.
     */
    public Dimension getSize() {
        return window.getSize();
    }


    /** Returns the location of the popup. */
    public Point getLocation() {
        return window.getLocation();
    }


    /**
     * Show this popup centered on the given text area.
     */
    public void show(JEditTextArea mytextArea) {
        show(mytextArea, -1, -1);
    }


    /**
     * Show this popup at the given location.
     */
    public void show(JEditTextArea aTextArea, int x, int y) {
        if (!aTextArea.isShowing()) {
            throw new IllegalStateException("Text area is not showing");
        }
        if (currentPopup != null) {
            if ( PythonJeditPanel.isDebugEnabled() ) Log.log(Log.DEBUG, this,
                "Not displaying popup because another popup or this popup is already displayed");
            return;
        }

        textArea = aTextArea;
        PopupWindow mywindow = getPopupWindow(getView());
        calculateBounds(textArea, mywindow, x, y);
        if (PythonJeditPanel.isDebugEnabled() ) Log.log(Log.DEBUG, this, "Showing popup at " + mywindow.getLocation());
        mywindow.setVisible(true);
        currentPopup = this;
        

        if (hint != null) {
            showHint();
        }
        SwingUtilities.invokeLater(
            new Runnable()
            {
                public void run() {
                    textArea.requestFocus();
                }
            });
    }


    /**
     * Dispose of this popup.
     */
    public void dispose() {
        if (hint != null) {
            hint.hide();
            hint = null;
        }
        window.dispose();
        window = null;
        currentPopup = null;
        typedText = "";
        if (PythonJeditPanel.isDebugEnabled() ) Log.log(Log.DEBUG, this, "Disposing popup");
        getTextArea().requestFocus();
    }


    /**
     * Returns the text area that this popup belongs to.
     */
    protected JEditTextArea getTextArea() {
        return textArea;
    }


    /**
     * Returns the view that of the text area that invoked this popup.
     */
    protected View getView() {
        return GUIUtilities.getView(textArea);
    }


    /**
     * Returns a popup window.
     */
    protected PopupWindow getPopupWindow(View view) {
        if (window == null) {
            window = new PopupWindow(view);
            try {
                Class clazz = Class.forName(getClass().getName() + "$Java14Initializer");
                PopupInitializer initializer = (PopupInitializer) clazz.newInstance();
                initializer.init(window);
            } catch (Exception e) {
            }
            window.setContentPane(createPopupComponent());
            window.pack();
        }
        return window;
    }


    /**
     * Update the text typed from in the text area.
     */
    protected abstract void updateTypedText();


    /**
     * Create the component that the popup will display.
     */
    protected abstract JComponent createPopupComponent();


    /**
     * Create a key listener to intercept key events.
     */
    protected KeyListener createKeyEventInterceptor() {
        return new KeyHandler();
    }


    /**
     * Returns the height of a line in the text area.
     */
    private int getLineHeight() {
        return textArea.getPainter().getFontMetrics(textArea.getPainter().getFont()).getHeight();
    }


    /**
     * Show the hint popup.
     */
    private void showHint() {
        JLayeredPane layeredPane = textArea.getRootPane().getLayeredPane();
        Point pt = window.getLocation();
        SwingUtilities.convertPointFromScreen(pt, layeredPane);

        Dimension space = layeredPane.getSize();
        int heightAbovePopup = pt.y - (getLineHeight() * 2);
        int heightBelowPopup = (int) (space.getHeight() - (pt.y + window.getHeight() + 11));

        if (heightAbovePopup > heightBelowPopup) {
            pt.y -= hint.getSize().height + (getLineHeight() * 2);
        } else {
            pt.y += window.getHeight() + 11;
        }
        hint.show(pt);
    }


    /**
     * Returns <code>true</code> if a popup is currently visible.
     */
    public static boolean isPopupVisible() {
        return currentPopup != null;
    }


    /**
     * Calculate the bounds for this popup.
     */
    private static void calculateBounds(JEditTextArea textArea,
        Window popup, int x, int y) {
        Dimension size = popup.getSize();
        if (x < 0) {
            x = (textArea.getWidth() - size.width) / 2;
        }
        if (y < 0) {
            y = (textArea.getHeight() - size.height) / 2;
        }
        if (size.width > textArea.getWidth() - x) {
            size.width = textArea.getWidth() - x;
        }
        size.width = Math.max(size.width, 250);
        Point pt = new Point(x, y);
        if (PythonJeditPanel.isDebugEnabled() ) Log.log(Log.DEBUG, CodeAidPopup.class, "pt before translation: " + pt);
        Point textAreaPointOnScreen = textArea.getLocationOnScreen();
        pt.translate(textAreaPointOnScreen.x, textAreaPointOnScreen.y);
        if (PythonJeditPanel.isDebugEnabled() ) Log.log(Log.DEBUG, CodeAidPopup.class, "pt after translation: " + pt);
        popup.setLocation(pt);
        popup.setSize(size);
    }


    protected class KeyHandler extends KeyAdapter
    {
        public void keyTyped(KeyEvent evt) {
            char c = evt.getKeyChar();
            if (c != KeyEvent.CHAR_UNDEFINED && c != '\b') {
                typedText += c;
                textArea.userInput(c);
            }
        }


        public void keyPressed(KeyEvent evt) {
            switch (evt.getKeyCode()) {
                case KeyEvent.VK_BACK_SPACE:
                    if (typedText.length() != 0) {
                        typedText = typedText.substring(0, typedText.length() - 1);
                    }
                    textArea.backspace();
                    if (typedText.length() == 0) {
                        dispose();
                    }
                    break;

                case KeyEvent.VK_ESCAPE:
                    dispose();
                    break;

                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_HOME:
                case KeyEvent.VK_END:
                case KeyEvent.VK_DELETE:
                    dispose();

                default:
                    if (evt.isActionKey()) {
                        dispose();
                        getView().processKeyEvent(evt);
                    }
                    break;
            }
        }
    }


    /**
     * The popup window.
     */
    private class PopupWindow extends JWindow
    {
        private KeyListener keyListener;


        /**
         * Create a new <code>PopupWindow</code>.
         */
        public PopupWindow(View aView) {
            super(aView);
        }


        /**
         * Set whether this window is visible.
         */
        public void setVisible(boolean visible) {
            if (visible) {
                installKeyHandler();
            } else {
                uninstallKeyHandler();
            }
            super.setVisible(visible);
        }


        /**
         * Install a handler for handling key events.
         */
        private void installKeyHandler() {
            keyListener = createKeyEventInterceptor();
            addKeyListener(keyListener);
            getView().setKeyEventInterceptor(keyListener);
        }


        /**
         * Uninstall a handler from handling key events.
         */
        private void uninstallKeyHandler() {
            View view = getView();
            if (view == null) {
                return;
            }
            if (view.getKeyEventInterceptor() != keyListener) {
                if (PythonJeditPanel.isDebugEnabled() ) Log.log(Log.WARNING, this, "Key event interceptor does not belong to popup");
            } else {
                view.setKeyEventInterceptor(null);
            }
            removeKeyListener(keyListener);
        }
    }


    /**
     * Initializes this popup for Java 1.4.
     */
    /* REPORTED AS NEVE USED LOCALLY
    private class Java14Initializer 
    implements PopupInitializer
    {
        public void init(PopupWindow mywindow) 
        {
            if (PythonJeditPanel.isDebugEnabled() ) Log.log(Log.DEBUG, this, "Setting up popup for JDK1.4");
            mywindow.setFocusable(false);
            mywindow.setFocusTraversalKeysEnabled(false);
        }
    }
    */

    /**
     * An interface to support component initialization of a popup.
     */
    private interface PopupInitializer
    {
        void init(PopupWindow popup);
    }
}

