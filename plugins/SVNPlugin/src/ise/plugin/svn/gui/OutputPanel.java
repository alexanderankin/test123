package ise.plugin.svn.gui;

import java.awt.*;
import java.util.logging.*;
import javax.swing.*;
import ise.plugin.svn.*;

public class OutputPanel extends JPanel {

    public static final int RESULTS = 0;
    public static final int CONSOLE = 1;
    private JTabbedPane tabs;

    // used to generate unique name for this panel
    public static int COUNT = 0;
    private int myIndex;

    // the logger for this panel, this logger should only log to this panel
    // via the handler
    private Logger logger = null;
    private SubversionGUILogHandler handler = new SubversionGUILogHandler();

    public OutputPanel() {
        super(new BorderLayout());

        myIndex = ++COUNT;
        logger = Logger.getLogger( "ise.plugin.svn.Subversion" + myIndex );
        logger.setLevel(Level.ALL);
        logger.removeHandler(handler);
        logger.addHandler(handler);

        tabs = new JTabbedPane();
        tabs.addTab("SVN Results", new JPanel());
        tabs.addTab("SVN Console", getConsolePanel());
        add(tabs);
    }

    private JPanel getConsolePanel() {
        return handler.getPanel();
    }

    public Logger getLogger() {
        return logger;
    }

    public void showTab(int tab) {
        switch(tab) {
        case RESULTS:
            tabs.setSelectedIndex(RESULTS);
            break;
        default:
            tabs.setSelectedIndex(CONSOLE);
        }
    }

    public void setResultsPanel(JPanel panel) {
        JScrollPane scroller = new JScrollPane(panel);
        tabs.setComponentAt(RESULTS, scroller);
    }

}
