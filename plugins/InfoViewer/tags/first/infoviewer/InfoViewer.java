/*
 * InfoViewer.java - Info viewer for HTML, txt
 * Copyright (C) 1999 Slava Pestov
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
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.text.html.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.HistoryTextField;
import org.gjt.sp.util.Log;

/**
 * an info viewer for jEdit's. It uses a Swing JEditorPane to display the 
 * HTML, and implements a URL history.
 * @author Slava Pestov
 * @author Dirk Moebius
 */
public class InfoViewer extends JFrame 
        implements HyperlinkListener, PropertyChangeListener {

    // greet string
    private final static String GREET = props("infoviewer.title") + " v" +
                                  props("plugin.InfoViewerPlugin.version");
                   
    // status numbers for showStatus()
    private final static int OPENING = 1; 
    private final static int LOADING = 2;
    private final static int READY = 3;
    private final static int ERROR = 4;
        
    // private members
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
    
    private JLabel status;
    private JEditorPane viewer;
    private HistoryTextField urlField;
    private JButton bStartStop;
    private JMenu mBmarks;
    private JMenu mHelp;
    private URL[] history;
    private URL currentURL;
    private String currentTitle;
    private int currentStatus;
    private int historyPos;
    private Bookmarks bookmarks;
    
    private BookmarkHandler bookmarkhandler = new BookmarkHandler();
    
    private ImageIcon anim_icon = new ImageIcon(
        getClass().getResource("images/fish_anim.gif"));
    private ImageIcon no_anim_icon = new ImageIcon(
        getClass().getResource("images/fish.gif"));
        
        
    
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

        history = new URL[25];
        
        // initialize actions
        createActions();
        
        // the menu
        createMenu();
        
        // the url textfield
        urlField = new HistoryTextField("infoviewer");
        urlField.addKeyListener(new KeyHandler());

        // url textfield and label        
        JPanel urlPanel = new JPanel(new BorderLayout());
        urlPanel.add(new JLabel(props("infoviewer.label.gotoURL")),
                     BorderLayout.WEST);
        urlPanel.add(urlField, BorderLayout.CENTER);

        // the viewer
        viewer = new JEditorPane();
        viewer.setEditable(false);
        viewer.addHyperlinkListener(this);
        viewer.addPropertyChangeListener(this);
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

        setSize(600,400);
        GUIUtilities.loadGeometry(this, "infoviewer");

        setVisible(true);
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
    }

        
    private void createMenu() {
        // File menu        
        JMenu mFile = new JMenu(props("infoviewer.menu.file"));
        mFile.setMnemonic(props("infoviewer.menu.file.mnemonic").charAt(0));
        addMenuItem(mFile, aOpenFile);
        addMenuItem(mFile, aEditURL);
        addMenuItem(mFile, aReload);
        mFile.add(new JSeparator());
        addMenuItem(mFile, aClose);

        // Edit menu        
        JMenu mEdit = new JMenu(props("infoviewer.menu.edit"));
        mEdit.setMnemonic(props("infoviewer.menu.edit.mnemonic").charAt(0));
        addMenuItem(mEdit, aCopy);
        addMenuItem(mEdit, aSelectAll);
        
        // Goto menu
        JMenu mGoto = new JMenu(props("infoviewer.menu.goto"));
        mGoto.setMnemonic(props("infoviewer.menu.goto.mnemonic").charAt(0));
        addMenuItem(mGoto, aBack);
        addMenuItem(mGoto, aForward);
        addMenuItem(mGoto, aHome);
        
        // Bookmarks menu
        mBmarks = new JMenu(props("infoviewer.menu.bmarks"));
        mBmarks.setMnemonic(props("infoviewer.menu.bmarks.mnemonic").charAt(0));
        updateBookmarksMenu();
        
        // Help menu
        mHelp = new JMenu(props("infoviewer.menu.help"));
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
    

    private void addMenuItem(JMenu m, InfoViewerAction a) {
        JMenuItem mi = m.add(a);
        mi.setMnemonic(a.getValue(InfoViewerAction.MNEMONIC).toString().charAt(0));
        mi.setAccelerator((KeyStroke) a.getValue(InfoViewerAction.ACCELERATOR));
    } 

    
    public void updateBookmarksMenu() {
        mBmarks.removeAll();
        addMenuItem(mBmarks, aBookmarksAdd);
        addMenuItem(mBmarks, aBookmarksEdit);
        mBmarks.add(new JSeparator());
        
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
        addMenuItem(mHelp, aAbout);        
        EditPlugin plugin = jEdit.getPlugin("InfoViewerPlugin");
        if (plugin == null) return;
        String docs = props("plugin.InfoViewerPlugin.docs");
        if (docs == null) return;
        URL docsURL = plugin.getClass().getResource(docs);
        if (docsURL == null) return;
        JMenuItem mi = new JMenuItem(props("infoviewer.menu.help.readme"));
        mi.setActionCommand(docsURL.toString());
        mi.addActionListener(bookmarkhandler);
        mi.setMnemonic(props("infoviewer.menu.help.readme.mnemonic").charAt(0));
        mHelp.add(mi);
    }
    
    
    private JToolBar createToolbar() {
        Dimension space = new Dimension(10,10);
        JToolBar tb = new JToolBar(JToolBar.HORIZONTAL);
        tb.add(aOpenFile).setText(null);
        tb.add(aEditURL).setText(null);
        tb.add(Box.createRigidArea(space));
        tb.add(aBack).setText(null);
        tb.add(aForward).setText(null);
        tb.add(Box.createRigidArea(space));
        tb.add(aReload).setText(null);
        tb.add(aHome).setText(null);
        tb.add(Box.createGlue());
        bStartStop = new JButton(anim_icon);
        bStartStop.setDisabledIcon(no_anim_icon);
        bStartStop.setBorderPainted(false);
        bStartStop.setEnabled(false);
        tb.add(bStartStop);
        return tb;
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
     * @param url The URL as String
     * @param addToHistory Should the URL be added to the back/forward
     * history?
     */
    public void gotoURL(String url, boolean addToHistory) {
        if (url == null) return;
        if (url.trim().length() == 0) return;
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
     * @param url The URL
     * @param addToHistory Should the URL be added to the back/forward
     * history?
     */
    public void gotoURL(URL url, boolean addToHistory) {
        if (url == null) return;
        
        String urlText = url.toString().trim();
        if (urlText.length() == 0) return;
        
        // reset default cursor so that the hand cursor doesn't
        // stick around
        viewer.setCursor(Cursor.getDefaultCursor());
        currentStatus = OPENING;
        showStatus();
        bStartStop.setEnabled(true);
                
        try {
            urlField.setText(urlText);
            viewer.setPage(url);
            currentURL = url;
            if (addToHistory) {
                history[historyPos++] = url;
                if(history.length == historyPos)
                    System.arraycopy(history, 1, history,
                                              0, history.length);
                history[historyPos] = null;
            }
            currentStatus = LOADING;
            showStatus();          
        }
        catch(FileNotFoundException fnf) {
            Log.log(Log.ERROR, this, fnf);
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
            Object newtitle = viewer.getDocument().getProperty(
                Document.TitleProperty);
            if (newtitle == null) {
                currentTitle = currentURL.toString();
            } else {
                currentTitle = newtitle.toString();
            }
            setTitle("InfoViewer: " + currentTitle);
            bStartStop.setEnabled(false);
            currentStatus = READY;
            showStatus();
        }
    }


    /**
     * return the URL, that currently is being viewed.
     */
    public URL getURL() {
        return currentURL;
    }
    
    
    public void setStatusText(final String text) {
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
                                    new Object[] { currentURL.toString() } ));
                break;
            case OPENING:
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
    }
                

    private void showError(String errortext) {
        viewer.setContentType("text/html");
        viewer.setText("<html><head></head><body>\n" + 
                       "<h1>Error</h1>\n" + 
                       errortext + 
                       "\n</body></html>");
        currentURL = null;
        bStartStop.setEnabled(false);
        currentStatus = ERROR;
        showStatus();
    }
   
   
    private void showError(Exception e) {
        Log.log(Log.ERROR, this, e);
        showError(e.getLocalizedMessage());
    }
    
    
    public void setVisible(boolean visible) {
        if (!visible) {
            GUIUtilities.saveGeometry(this, "infoviewer");
        }
        super.setVisible(visible);
    }

    
    private static String props(String key) {
        return jEdit.getProperty(key);
    }

    
    private static String props(String key, Object[] args) {
        return jEdit.getProperty(key, args);
    }


    public JEditorPane getViewer() {
        return viewer;
    }

        
    public void forward() {
        if (history.length - historyPos <= 1)
            getToolkit().beep();
        else {
            URL url = history[historyPos];
            if (url == null)
                getToolkit().beep();
            else {
                historyPos++;
                gotoURL(url, false);
            }
        }
    }

    
    public void back() {
        if (historyPos <= 1) {
            getToolkit().beep();
        } else {
            URL url = history[--historyPos - 1];
            gotoURL(url, false);
        }
    }
    

    /**
     * reload the current URL
     */
    public void reload() {
        if (currentURL == null) return;
        viewer.setText("");
        viewer.setEditorKit(null);
        gotoURL(currentURL, false);
        /*
        Document doc = viewer.getDocument();
	try {
            doc.remove(0, doc.getLength());
        } catch (BadLocationException e) { }
        try {
            viewer.read(viewer.getPage().openConnection().getInputStream(), doc);
        }
        catch (IOException e) {
            showError(e);
        }
        */
    }
    
    
    public void addToBookmarks() {
        if (currentURL == null) {
            GUIUtilities.error(null, "infoviewer.error.nourl", null);
            return;
        }
        String title = getPageTitle();
        jEdit.setProperty("infoviewer.bookmarks.title." 
                          + bookmarks.getSize(), title);
        jEdit.setProperty("infoviewer.bookmarks.url." 
                          + bookmarks.getSize(), currentURL.toString());
        bookmarks.add(title, currentURL.toString());
        
        jEdit.unsetProperty("infoviewer.bookmarks.title." 
                            + bookmarks.getSize());
        jEdit.unsetProperty("infoviewer.bookmarks.url."
                            + bookmarks.getSize());
                            
        // add menu item                            
        JMenuItem mi = new JMenuItem(title);
        mBmarks.add(mi);
        mi.setActionCommand(currentURL.toString());
        mi.addActionListener(bookmarkhandler);
    }


    private String getPageTitle() {
        if (currentTitle == null)
            if (currentURL == null)
                return "(no title)";
            else
                return currentURL.toString();
        else
            return currentTitle;
    }

    
    /**********************************************************************/
    
    private class BookmarkHandler implements ActionListener {
        /**
         * a bookmark was selected in the Bookmarks menu. Open the
         * corresponding URL in the InfoViewer.
         */
        public void actionPerformed(ActionEvent evt) {
            gotoURL(evt.getActionCommand(), true);
        }
    }
    
    
    /**********************************************************************/
    
    private class KeyHandler extends KeyAdapter {
        public void keyPressed(KeyEvent evt) {
            if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                gotoURL(urlField.getText(), true);
            }
        }
    }

 
}
