/*
 * InfoViewer.java - Info viewer for HTML, txt
 * Copyright (C) 2000 Dirk Moebius
 * Based on HTMLViewer.java Copyright (C) 1999 Slava Pestov
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

package infoviewer;

import infoviewer.actions.*;
import infoviewer.workaround.*;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
import java.net.*;

import javax.accessibility.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.text.html.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.HistoryTextField;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.util.Log;


/**
 * an info viewer for jEdit. It uses a Swing JEditorPane to display the
 * HTML, and implements a URL history, bookmarks and some other
 * web browsing functions.
 *
 * @author Dirk Moebius
 * @author Slava Pestov
 */
public class InfoViewer
    extends JFrame
    implements HyperlinkListener, PropertyChangeListener, EBComponent
{

    /**
     * Creates a new info viewer for the specified URL.
     * @param url The URL
     */
    public InfoViewer(URL url) {
        this();
        gotoURL(url, true);
    }


    /**
     * Creates a new info viewer for the specified URL.
     * @param url The URL as String
     */
    public InfoViewer(String url) {
        this();
        gotoURL(url, true);
    }


    /**
     * Creates a new info viewer with an empty page
     */
    public InfoViewer() {
        super(jEdit.getProperty("infoviewer.title"));

        setIconImage(GUIUtilities.getEditorIcon());

        history = new History();
        historyhandler = new URLButtonHandler(false);
        bookmarkhandler = new URLButtonHandler(true);

        // initialize actions
        createActions();

        // the menu
        createMenu();

        // the url textfield
        urlField = new HistoryTextField("infoviewer");
        urlField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                gotoURL(urlField.getText(), true);
            }
        });

        // url textfield and label
        JPanel urlPanel = new JPanel(new BorderLayout());
        urlPanel.add(new JLabel(props("infoviewer.label.gotoURL")),
                     BorderLayout.WEST);
        urlPanel.add(urlField, BorderLayout.CENTER);

        // the viewer
        viewer = new EnhancedJEditorPane();
        viewer.setEditable(false);
        viewer.setFont(new Font("Monospaced", Font.PLAIN, 12));
        viewer.addHyperlinkListener(this);
        viewer.addPropertyChangeListener(this);
        viewer.addMouseListener(new MouseHandler());
        JScrollPane scrViewer = new JScrollPane(viewer);

        // the status bar
        status = new JLabel(GREET);
        status.setBorder(new BevelBorder(BevelBorder.LOWERED));
        status.setFont(new Font("Dialog", Font.PLAIN, 10));
        status.setMinimumSize(
            new Dimension(100, status.getPreferredSize().height));

        // the inner content: url textfield, viewer, status bar
        JPanel innerPanel = new JPanel(new BorderLayout());
        innerPanel.add(urlPanel, BorderLayout.NORTH);
        innerPanel.add(scrViewer, BorderLayout.CENTER);
        innerPanel.add(status, BorderLayout.SOUTH);

        // the toolbar
        JToolBar tb = createToolbar();

        // the outer content: toolbar, inner content
        getContentPane().add(tb, BorderLayout.NORTH);
        getContentPane().add(innerPanel, BorderLayout.CENTER);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        showStatus();
        setSize(600,400);
        GUIUtilities.loadGeometry(this, "infoviewer");
        setVisible(true);
        EditBus.addToBus(this);
    }


    /**
     * Displays the specified URL in the HTML component. The URL will be
     * added to the back/forward history. This is the same as invoking
     * <code>gotoURL(url, true)</code>.
     * @param url The URL as String
     */
    public void gotoURL(String url) {
        gotoURL(url, true);
    }


    /**
     * Displays the specified URL in the HTML component.
     * @param url          The URL as String
     * @param addToHistory Should the URL be added to the back/forward
     *                     history?
     */
    public void gotoURL(String url, boolean addToHistory) {
        if (url == null) return;
        url = url.trim();
        if (url.length() == 0) return;
        try {
            URL u = new URL(url);
            gotoURL(u, addToHistory);
        }
        catch (MalformedURLException mu) {
            Log.log(Log.ERROR, this, mu);
            showError(props("infoviewer.error.badurl.message",
                            new Object[] { mu } ));
        }
    }


    /**
     * Displays the specified URL in the HTML component.
     * @param url          The URL
     * @param addToHistory Should the URL be added to the back/forward
     *                     history?
     */
    public void gotoURL(URL url, boolean addToHistory) {
        if (url == null) return;
        String urlText = url.toString().trim();
        if (urlText.length() == 0) return;

        // reset default cursor so that the hand cursor doesn't stick around
        viewer.setCursor(Cursor.getDefaultCursor());
        bStartStop.setEnabled(true);
        urlField.setText(urlText);
        currentURL = new TitledURLEntry(urlText, urlText);
        currentStatus = LOADING;
        if (addToHistory) {
            history.add(currentURL);
        }
        showStatus();
        updateGoMenu();

        try {
            URL oldURL = viewer.getPage();
            viewer.setPage(url);
            // workaround for JEditorPane: if url is the same as oldURL,
            // then a "page" property change is never fired. This happens
            // in some versions of Swing. So, in this case, we need to
            // invoke pageComplete() manually.
            if (oldURL != null && oldURL.sameFile(url)) {
                pageComplete();
            }
        }
        catch(FileNotFoundException fnf) {
            String[] args = { urlText };
            showError(props("infoviewer.error.filenotfound.message", args));
        }
        catch(IOException io) {
            Log.log(Log.ERROR, this, io);
            String[] args = { urlText, io.getMessage() };
            showError(props("infoviewer.error.ioerror.message", args));
        }
    }


    /**
     * go forward in history. Beep if that's not possible.
     */
    public void forward() {
        String nextURL = history.getNext();
        if (nextURL == null) {
            getToolkit().beep();
        } else {
            gotoURL(nextURL, false);
        }
    }


    /**
     * go back in history. Beep, if that's not possible.
     */
    public void back() {
        String prevURL = history.getPrevious();
        if (prevURL == null) {
            getToolkit().beep();
        } else {
            gotoURL(prevURL, false);
        }
    }


    /**
     * reload the current URL.
     */
    public void reload() {
        if (currentURL == null) return;
        // Clear the viewer and flush viewers' memorized URL:
        viewer.getDocument().putProperty(Document.StreamDescriptionProperty,
                                         DUMMY_URL);
        gotoURL(currentURL.getURL(), false);
    }


    /**
     * add the current page to the bookmark list.
     */
    public void addToBookmarks() {
        if (currentURL == null) {
            GUIUtilities.error(null, "infoviewer.error.nourl", null);
            return;
        }
        jEdit.setProperty("infoviewer.bookmarks.title."
                          + bookmarks.getSize(), currentURL.getTitle());
        jEdit.setProperty("infoviewer.bookmarks.url."
                          + bookmarks.getSize(), currentURL.getURL());
        bookmarks.add(currentURL);

        jEdit.unsetProperty("infoviewer.bookmarks.title."
                            + bookmarks.getSize());
        jEdit.unsetProperty("infoviewer.bookmarks.url."
                            + bookmarks.getSize());

        // add menu item
        JMenuItem mi = new JMenuItem(currentURL.getTitle());
        mBmarks.add(mi);
        mi.setActionCommand(currentURL.getURL());
        mi.addActionListener(bookmarkhandler);
    }


    /**
     * return the URL, that currently is being viewed, as String.
     * @return the current URL as String, or null, if no URL is currently
     *         being viewed.
     */
    public String getCurrentURL() {
        return currentURL == null ? null : currentURL.getURL();
    }


    /**
     * return the JEditorPane instance that is used to view HTML and text
     * URLs
     */
    public JEditorPane getViewer() {
        return viewer;
    }


    /**
     * from interface HyperlinkListener: called when a hyperlink is
     * clicked, entered or leaved.
     */
    public void hyperlinkUpdate(HyperlinkEvent evt) {
        URL url = evt.getURL();
        if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            if (evt instanceof HTMLFrameHyperlinkEvent) {
                ((HTMLDocument)viewer.getDocument())
                    .processHTMLFrameHyperlinkEvent(
                    (HTMLFrameHyperlinkEvent)evt);
            } else {
                if (url != null)
                    gotoURL(url, true);
            }
        }
        else if (evt.getEventType() == HyperlinkEvent.EventType.ENTERED) {
            viewer.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            if (url != null)
                setStatusText(url.toString());
        }
        else if (evt.getEventType() == HyperlinkEvent.EventType.EXITED) {
            viewer.setCursor(Cursor.getDefaultCursor());
            showStatus();
        }
    }


    /**
     * from interface PropertyChangeListener: called, when a property
     * is changed. This is used to listen for "page" property change
     * events, which occur, when the page is loaded completely.
     */
    public void propertyChange(PropertyChangeEvent e) {
        if ("page".equals(e.getPropertyName())) {
            pageComplete();
        }
    }


    /**
     * listen for messages on the EditBus. Currently it listens for
     * PropertiesChanged messages.
     */
    public void handleMessage(EBMessage msg) {
        if (msg instanceof PropertiesChanged) {
            updateBookmarksMenu();
            updateGoMenu();
        }
    }


    /**
     * overridden to save geometry when the frame is made invisible.
     */
    public void setVisible(boolean visible) {
        if (!visible)
            GUIUtilities.saveGeometry(this, "infoviewer");
        super.setVisible(visible);
    }


    private void createActions() {
        aOpenFile      = new infoviewer.actions.open_file();
        aEditURL       = new infoviewer.actions.edit_url();
        aReload        = new infoviewer.actions.reload();
        aClose         = new infoviewer.actions.close();
        aCopy          = new infoviewer.actions.copy();
        aSelectAll     = new infoviewer.actions.select_all();
        aBack          = new infoviewer.actions.back();
        aForward       = new infoviewer.actions.forward();
        aHome          = new infoviewer.actions.home();
        aBookmarksAdd  = new infoviewer.actions.bookmarks_add();
        aBookmarksEdit = new infoviewer.actions.bookmarks_edit();
        aAbout         = new infoviewer.actions.about();
        aFollowLink    = new infoviewer.actions.follow_link();
    }


    private void createMenu() {
        // File menu
        EnhancedJMenu mFile = new EnhancedJMenu(props("infoviewer.menu.file"));
        mFile.setMnemonic(props("infoviewer.menu.file.mnemonic").charAt(0));
        mFile.add(aOpenFile);
        mFile.add(aEditURL);
        mFile.add(aReload);
        mFile.add(new JSeparator());
        mFile.add(aClose);

        // Edit menu
        EnhancedJMenu mEdit = new EnhancedJMenu(props("infoviewer.menu.edit"));
        mEdit.setMnemonic(props("infoviewer.menu.edit.mnemonic").charAt(0));
        mEdit.add(aCopy);
        mEdit.add(aSelectAll);

        // Goto menu
        mGoto = new EnhancedJMenu(props("infoviewer.menu.goto"));
        mGoto.setMnemonic(props("infoviewer.menu.goto.mnemonic").charAt(0));
        updateGoMenu();

        // Bookmarks menu
        mBmarks = new EnhancedJMenu(props("infoviewer.menu.bmarks"));
        mBmarks.setMnemonic(props("infoviewer.menu.bmarks.mnemonic").charAt(0));
        updateBookmarksMenu();

        // Help menu
        mHelp = new EnhancedJMenu(props("infoviewer.menu.help"));
        mHelp.setMnemonic(props("infoviewer.menu.help.mnemonic").charAt(0));
        updateHelpMenu();

        // Menubar
        JMenuBar mb = new JMenuBar();
        mb.add(mFile);
        mb.add(mEdit);
        mb.add(mGoto);
        mb.add(mBmarks);
        mb.add(mHelp);
        setJMenuBar(mb);
    }


    private JToolBar createToolbar() {
        EnhancedJToolBar tb = new EnhancedJToolBar(JToolBar.HORIZONTAL);

        tb.add(aBack);
        tb.add(aForward);
        tb.add(aReload);
        tb.add(aHome);
        tb.add(aOpenFile);
        tb.add(aEditURL);

        tb.add(Box.createGlue());

        bStartStop = new JButton(ICON_ANIM);
        bStartStop.setDisabledIcon(ICON_NOANIM);
        bStartStop.setBorderPainted(false);
        bStartStop.setEnabled(false);
        tb.add(bStartStop);

        return tb;
    }


    /**
     * update the bookmarks menu according to the bookmarks stored
     * in the properties.
     */
    private synchronized void updateBookmarksMenu() {
        mBmarks.removeAll();
        mBmarks.add(aBookmarksAdd);
        mBmarks.add(aBookmarksEdit);
        mBmarks.add(new JSeparator());
        // add bookmarks
        bookmarks = new Bookmarks();
        for (int i=0; i<bookmarks.getSize(); i++) {
            String title = bookmarks.getTitle(i);
            if (title.length() > 0 && title.charAt(0) == '-') {
                mBmarks.add(new JSeparator());
            } else {
                JMenuItem mi = new JMenuItem(title);
                mBmarks.add(mi);
                mi.setActionCommand(bookmarks.getURL(i));
                mi.addActionListener(bookmarkhandler);
            }
        }
    }


    private void updateHelpMenu() {
        mHelp.removeAll();
        mHelp.add(aAbout);
        // find InfoViewer docs
        EditPlugin[] plugins = jEdit.getPlugins();
        EditPlugin plugin = null;
        for (int i=0; i<plugins.length; i++) {
            if (plugins[i].getClassName().equals("InfoViewerPlugin")) {
                plugin = plugins[i];
                break;
            }
        }
        if (plugin == null) return;
        String docs = props("plugin.InfoViewerPlugin.docs");
        if (docs == null) return;
        URL docsURL = plugin.getClass().getResource(docs);
        if (docsURL == null) return;
        // add a menu item for the docs
        JMenuItem mi = new JMenuItem(props("infoviewer.menu.help.readme"));
        mi.setActionCommand(docsURL.toString());
        mi.addActionListener(bookmarkhandler);
        mi.setMnemonic(props("infoviewer.menu.help.readme.mnemonic").charAt(0));
        mHelp.add(mi);
    }


    private synchronized void updateGoMenu() {
        mGoto.removeAll();
        mGoto.add(aBack);
        mGoto.add(aForward);
        mGoto.add(aHome);
        mGoto.add(new JSeparator());
        // add history
        TitledURLEntry[] entr = history.getGoMenuEntries();
        int pos = history.getHistoryPos();
        for (int i = 0; i < entr.length; i++) {
            JMenuItem mi = new JMenuItem(entr[i].getTitle(),
                pos == entr[i].getHistoryPos() ? ICON_CHECK : ICON_NOCHECK);
            mi.setActionCommand("history:" + entr[i].getHistoryPos());
            mi.addActionListener(historyhandler);
            mGoto.add(mi);
        }
    }

    private synchronized void updateGoMenuTitles() {
        TitledURLEntry[] entr = history.getGoMenuEntries();
        for (int i = 0; i < entr.length; i++) {
            JMenuItem mi = mGoto.getItem(i + 4);
            mi.setText(entr[i].getTitle());
        }
    }


    private void updateActions() {
        aForward.setEnabled(history.hasNext());
        aBack.setEnabled(history.hasPrevious());
        aEditURL.setEnabled(currentURL != null);
    }


    private void pageComplete() {
        Document doc = viewer.getDocument();
        if (doc != null) {
            // try to get the title of the document
            String newTitle = getTitleFromDocument(doc);
            if (currentURL != null) {
                currentURL.setTitle(newTitle);
            }
            // set the new window title
            setTitle(props("infoviewer.titlewithurl", new Object[] {newTitle}));
            // update title in the "Go" menu history
            updateGoMenuTitles();
        }
        bStartStop.setEnabled(false);
        currentStatus = READY;
        showStatus();
    }


    /** try to get the title of the document */
    private String getTitleFromDocument(Document doc) {
        Object obj = doc.getProperty(Document.TitleProperty);
        if (obj == null) {
            return currentURL != null ? currentURL.getURL()
                                      : props("infoviewer.notitle");
        } else {
            return obj.toString();
        }
    }


    private void setStatusText(final String text) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                status.setText(text);
            }
        });
    }


    private void showStatus() {
        switch (currentStatus) {
            case LOADING:
                setStatusText(props("infoviewer.status.loading",
                                    new Object[] { currentURL.getURL() } ));
                break;
            case READY:
                int size = viewer.getDocument().getLength();
                setStatusText(props("infoviewer.status.ready",
                                new Integer[] { new Integer(size) }));
                break;
            case ERROR:
                setStatusText(props("infoviewer.status.error"));
                break;
            default:
                setStatusText(GREET);
                break;
        }
        updateActions();
    }


    private void showError(String errortext) {
        // Clear the viewer and flush viewers' memorized URL:
        viewer.getDocument().putProperty(Document.StreamDescriptionProperty,
                                         DUMMY_URL);
        viewer.setContentType("text/html");
        viewer.setText("<html><head></head><body>\n" +
                       "<h1>Error</h1><p>\n" +
                       errortext +
                       "\n</body></html>");
        setTitle(props("infoviewer.titlewithurl", new Object[] { "Error" } ));
        bStartStop.setEnabled(false);
        currentURL = null;
        currentStatus = ERROR;
        showStatus();
    }


    private void showError(Exception e) {
        Log.log(Log.ERROR, this, e);
        showError(e.getLocalizedMessage());
    }


    /** convenience method for jEdit.getProperty(String). */
    private static String props(String key) {
        return jEdit.getProperty(key);
    }


    /** convenience method for jEdit.getProperty(String,Object[]). */
    private static String props(String key, Object[] args) {
        return jEdit.getProperty(key, args);
    }


    /**
     * this is a dummy URL that is needed to flush the URL cache of
     * the JEditorKit. This URL will not be navigated to, so it is totally
     * meaningless, where it points to, as long as it is valid.
     */
    private static URL DUMMY_URL;

    static {
        try {
            DUMMY_URL = new URL("file:/");
        }
        catch (MalformedURLException e) {
            Log.log(Log.ERROR, InfoViewer.class,
                    "error creating DUMMY_URL: " + e);
        }
    }


    // greet string
    private final static String GREET
        = props("infoviewer.greetstring",
                new Object[] { props("infoviewer.title"),
                               props("plugin.InfoViewerPlugin.version") });

    // status numbers for showStatus()
    private final static int LOADING = 1;
    private final static int READY = 2;
    private final static int ERROR = 3;

    // icons
    private final static ImageIcon ICON_ANIM = new ImageIcon(
        InfoViewer.class.getResource("images/fish_anim.gif"));
    private final static ImageIcon ICON_NOANIM = new ImageIcon(
        InfoViewer.class.getResource("images/fish.gif"));
    private final static ImageIcon ICON_CHECK = new ImageIcon(
        InfoViewer.class.getResource("images/checkmenu_check.gif"));
    private final static ImageIcon ICON_NOCHECK = new ImageIcon(
        InfoViewer.class.getResource("images/checkmenu_nocheck.gif"));

    // infoviewer actions
    private InfoViewerAction aOpenFile;
    private InfoViewerAction aEditURL;
    private InfoViewerAction aReload;
    private InfoViewerAction aClose;
    private InfoViewerAction aCopy;
    private InfoViewerAction aSelectAll;
    private InfoViewerAction aBack;
    private InfoViewerAction aForward;
    private InfoViewerAction aHome;
    private InfoViewerAction aBookmarksAdd;
    private InfoViewerAction aBookmarksEdit;
    private InfoViewerAction aAbout;
    private infoviewer.actions.follow_link aFollowLink;

    // gui elements
    private JLabel status;
    private EnhancedJEditorPane viewer;
    private HistoryTextField urlField;
    private JButton bStartStop;
    private EnhancedJMenu mGoto;
    private EnhancedJMenu mBmarks;
    private EnhancedJMenu mHelp;

    // misc
    private TitledURLEntry currentURL;
    private int currentStatus;
    private Bookmarks bookmarks;
    private History history;
    private URLButtonHandler bookmarkhandler;
    private URLButtonHandler historyhandler;


    private class URLButtonHandler implements ActionListener {
        private boolean addToHistory = true;

        public URLButtonHandler(boolean addToHistory) {
            this.addToHistory = addToHistory;
        }

        /**
         * a bookmark was selected in the Bookmarks menu.
         * Open the corresponding URL in the InfoViewer.
         * The URL will be added to the history, if this URLButtonHandler
         * was initialized with <code>addToHistory = true</code>.
         */
        public void actionPerformed(ActionEvent evt) {
            String cmd = evt.getActionCommand();
            if (cmd.startsWith("history:")) {
                try {
                    int newhispos = Integer.parseInt(cmd.substring(8));
                    history.setHistoryPos(newhispos);
                    cmd = history.getCurrent();
                }
                catch (NumberFormatException ex) {
                    Log.log(Log.DEBUG, this, ex);
                }
            }
            gotoURL(cmd, addToHistory);
        }
    }


    private class MouseHandler extends MouseAdapter {

        JPopupMenu popup = null;

        public void mousePressed(MouseEvent evt) {
            if ((evt.getModifiers() & InputEvent.BUTTON3_MASK) != 0) {
                evt.consume();

                AccessibleText txt = viewer.getAccessibleContext()
                    .getAccessibleText();
                if (txt != null && txt instanceof AccessibleHypertext) {
                    AccessibleHypertext hyp = (AccessibleHypertext) txt;
                    int charIndex = hyp.getIndexAtPoint(evt.getPoint());
                    int linkIndex = hyp.getLinkIndex(charIndex);
                    if (linkIndex >= 0) {
                        // user clicked on a link
                        aFollowLink.setEnabled(true);
                        aFollowLink.setClickPoint(evt.getPoint());
                    } else {
                        aFollowLink.setEnabled(false);
                    }
                }

                JPopupMenu popup = getPopup();
                popup.show(viewer, evt.getX() - 1, evt.getY() - 1);
            }
        }

        private JPopupMenu getPopup() {
            if (popup == null) {
                popup = new JPopupMenu();
                popup.add(aBack);
                popup.add(aForward);
                popup.addSeparator();
                popup.add(aEditURL);
                popup.addSeparator();
                popup.add(aFollowLink);
            }
            return popup;
        }
    }

}
