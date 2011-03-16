/*
This file, unless otherwise noted, is wholly the work of Dale Anson,
danson@grafidog.com. The complete contents of this file is hereby 
released into the public domain, with no rights reserved. For questions, 
concerns, or comments, please email the author.
*/

package ise.calculator;

import java.awt.*;
import java.awt.event.*;

import java.io.IOException;
import java.net.URL;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.html.*;

/**
 * An About dialog that uses a JEditorPane to display content.  JEditorPane
 * can render html and even make the links work, so this About dialog can
 * actually connect the user to my home page.
 *
 * @version $Revision$
 * @author Dale Anson
 */
public class AboutDialog extends JDialog implements Navable {

    /** Description of the Field */
    private JEditorPane editor;

    /** Description of the Field */
    private JScrollPane scrollpane;

    /** Description of the Field */
    private Nav nav;
    private URL initialURL = null;

    /**
     * Constructor
     *
     * @param owner      parent frame hosting this dialog
     * @param title      title to display on dialog, probably just "About"
     * @param mime_type  JEditorPane allows "text/html", "text/plain", and
     *        "text/rtf"
     * @param contents   the stuff to show, coded in the correct mime type
     * @param nav   should "back" and "forward" buttons be displayed?
     */
    public AboutDialog(Frame owner, String title, String mime_type, String contents, boolean nav) {

        // initialize
        super(owner, title, false);

        // set up JEditorPane
        editor = new JEditorPane(mime_type, contents);
        init(nav);
    }

    /**
     * Constructor for AboutDialog
     *
     * @param owner
     * @param title
     * @param contents
     * @param nav
     * @exception Exception  Description of Exception
     */
    public AboutDialog(Frame owner, String title, URL contents, boolean nav) throws IOException {

        // initialize
        super(owner, title, false);

        // set up JEditorPane
        editor = new JEditorPane(contents);
        initialURL = contents;
        init(nav);
    }

    /**
     * Description of the Method
     *
     * @param use_nav
     */
    private void init(boolean use_nav) {
        editor.setEditable(false);
        editor.addHyperlinkListener(new LinkListener());

        // set up main panel
        BorderLayout layout = new BorderLayout();
        JPanel panel = new JPanel(layout);

        scrollpane = new JScrollPane(editor);
        panel.add(scrollpane, BorderLayout.CENTER);

        JButton ok_btn = new JButton("Ok");
        ok_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                setVisible(false);
                dispose();
            }
        }
       );

        JPanel btn_panel = new JPanel();
        if (use_nav) {
            btn_panel.setLayout(new LambdaLayout());
            nav = new Nav(this);
            btn_panel.add(nav, "0, 0, 1, 1, W,, 5");
            btn_panel.add(ok_btn, "1, 0, 1, 1, E,, 5");

            if (initialURL != null) {
                nav.update(initialURL);
            }
        }
        else {
            btn_panel.add(ok_btn);
        }
        panel.add(btn_panel, BorderLayout.SOUTH);
        setContentPane(panel);
        pack();
        setSize(new Dimension(300, 250));
    }

    /**
     * Overridden to make sure that the top of the document is visible.
     *
     * @param visible  The new visible value
     */
    public void setVisible(boolean visible) {
        editor.getCaret().setDot(0);
        scrollpane.getViewport().scrollRectToVisible(new Rectangle(0, 0, 1, 1));
        super.setVisible(visible);
    }

    /**
     * Sets the position attribute of the AboutDialog object
     *
     * @param ref  The new position value
     */
    public void setPosition(Object ref) {

        if (ref instanceof URL) {

            try {
                editor.setPage((URL) ref);
            }
            catch (Exception e) {   // NOPMD
                // ignored
            }
        }
        else if (ref instanceof String) {
            editor.scrollToReference((String) ref);
        }
    }

    /**
     * Makes the hyperlinks work, swiped from Swing Connection.
     */
    class LinkListener implements HyperlinkListener {

        /** Description of the Field */
        private Cursor hand_cursor = Cursor.getPredefinedCursor (Cursor.HAND_CURSOR);

        /** Description of the Field */
        private Cursor default_cursor = Cursor.getPredefinedCursor (Cursor.DEFAULT_CURSOR);

        /**
         * Description of the Method
         *
         * @param e
         */
        public void hyperlinkUpdate(HyperlinkEvent e) {

            JEditorPane pane = (JEditorPane) e.getSource();

            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {

                if (e instanceof HTMLFrameHyperlinkEvent) {

                    HTMLFrameHyperlinkEvent evt = (HTMLFrameHyperlinkEvent) e;
                    HTMLDocument doc = (HTMLDocument) pane.getDocument();
                    doc.processHTMLFrameHyperlinkEvent(evt);
                }
                else {

                    try {

                        java.net.URL url = e.getURL();

                        if (url != null) {
                            pane.setPage(url);

                            if (nav != null) {
                                nav.update(url);
                            }
                        }
                        else {

                            String desc = e.getDescription();
                            desc = desc.substring(1);
                            pane.scrollToReference(desc);

                            if (nav != null) {
                                nav.update(desc);
                            }
                        }
                    }
                    catch (Throwable t) {       // NOPMD
                        JOptionPane.showMessageDialog(AboutDialog.this, "Unable to open URL.", "Hyperlink Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
            else if (e.getEventType() == HyperlinkEvent.EventType.ENTERED) {
                pane.setCursor(hand_cursor);
            }
            else if (e.getEventType() == HyperlinkEvent.EventType.EXITED) {
                pane.setCursor(default_cursor);
            }
        }
    }

    // for testing

    /**
     * The main program for the AboutDialog class
     *
     * @param args  The command line arguments
     */
    public static void main(String[] args) {

        JFrame frame = new JFrame();
        AboutDialog about = new AboutDialog(frame, "About", "text/html", "<html>Here is an About Dialog.</html>", false);
        about.setVisible(true);
    }
}
