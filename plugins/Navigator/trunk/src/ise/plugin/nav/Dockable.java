package ise.plugin.nav;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import ise.java.awt.KappaLayout;
import ise.java.awt.LambdaLayout;

// TODO: if the line is too long, the current label will cover the buttons.
// Need to limit the width of the current label.
public class Dockable extends JPanel implements ChangeListener {

    private Navigator client = null;

    private JCheckBox groupByFile = null;
    private JCheckBox groupByLine = null;
    private JCheckBox showLineText = null;
    private JCheckBox showLineTextSyntax = null;
    private JCheckBox showStripes = null;
    private JRadioButton viewScope = null;
    private JCheckBox showPath = null;
    private JCheckBox showLineNumber = null;
    private JCheckBox showCaretOffset = null;
    private JPanel controlPanel = null;
    private JPanel currentPanel = null;
    private JLabel currentLabel = null;
    private JPanel flipPanel = null;

    private JButton options = null;
    private JButton clear = null;
    private JButton back;
    private JButton forward;

    private NavHistoryList backList = null;
    private NavHistoryList forwardList = null;

    public Dockable(Navigator navigator) {
        this.client = navigator;
        client.addChangeListener(this);
        installComponents();
        installListeners();
    }

    private void installComponents() {
        setLayout(new BorderLayout());
        //setBorder( BorderFactory.createEmptyBorder( 11, 11, 11, 11 ) );

        // create top panel components...

        // group by file
        JLabel groupByLabel = new JLabel("Group by:");
        groupByFile = new JCheckBox(jEdit.getProperty("navigator.options.groupByFile.label"));
        groupByFile.setName("groupByFile");
        groupByFile.setSelected(NavigatorPlugin.groupByFile());

        // group by line
        groupByLine = new JCheckBox(jEdit.getProperty("navigator.options.groupByLine.label"));
        groupByLine.setName("groupByLine");
        groupByLine.setSelected(NavigatorPlugin.groupByLine());

        // navigator scope
        JLabel scopeLabel = new JLabel("Scope:");
        viewScope = new JRadioButton(jEdit.getProperty("navigator.viewScope.label", "View scope"));
        viewScope.setName("viewScope");
        JRadioButton editPaneScope = new JRadioButton(jEdit.getProperty("navigator.editPaneScope.label", "EditPane scope"));
        editPaneScope.setName("editPaneScope");
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(viewScope);
        buttonGroup.add(editPaneScope);
        int scope = NavigatorPlugin.getScope();
        viewScope.setSelected(scope == NavigatorPlugin.VIEW_SCOPE);
        editPaneScope.setSelected(scope == NavigatorPlugin.EDITPANE_SCOPE);

        // show line text in back and forward lists
        showLineText = new JCheckBox(jEdit.getProperty("navigator.options.showLineText.label", "Show line text in history list"));
        showLineText.setName("showLineText");
        showLineText.setSelected(jEdit.getBooleanProperty("navigator.showLineText", true));

        // show syntax highlighting in back and forward lists
        showLineTextSyntax = new JCheckBox(jEdit.getProperty("navigator.options.showLineTextSyntax.label", "Show syntax highlighting in history list"));
        showLineTextSyntax.setName("showLineTextSyntax");
        showLineTextSyntax.setSelected(jEdit.getBooleanProperty("navigator.showLineText", true) && jEdit.getBooleanProperty("navigator.showLineTextSyntax", true));
        JPanel syntaxPanel = new JPanel();
        syntaxPanel.add(Box.createHorizontalStrut(11));
        syntaxPanel.add(showLineTextSyntax);

        // show stripes in back and forward lists
        showStripes = new JCheckBox(jEdit.getProperty("navigator.options.showStripes.label", "Show stripes in history list"));
        showStripes.setName("showStripes");
        showStripes.setSelected(jEdit.getBooleanProperty("navigator.showStripes", true));

        showPath = new JCheckBox("Show path");
        showPath.setSelected(jEdit.getBooleanProperty("navigator.showPath", true));
        showLineNumber = new JCheckBox("Show line number");
        showLineNumber.setSelected(jEdit.getBooleanProperty("navigator.showLineNumber", true));
        showCaretOffset = new JCheckBox("Show caret offset");
        showCaretOffset.setSelected(jEdit.getBooleanProperty("navigator.showCaretOffset", true));

        options = new SquareButton(GUIUtilities.loadIcon("22x22/actions/document-properties.png"));
        options.setToolTipText(jEdit.getProperty("navigator.options.label", "Options"));
        back = new SquareButton(GUIUtilities.loadIcon("ArrowL.png"));
        back.setModel(client.getBackModel());
        back.setToolTipText(jEdit.getProperty("navigator.back.label", "Back"));
        forward = new SquareButton(GUIUtilities.loadIcon("ArrowR.png"));
        forward.setModel(client.getForwardModel());
        forward.setToolTipText(jEdit.getProperty("navigator.forward.label", "Forward"));
        clear = new SquareButton(GUIUtilities.loadIcon("22x22/actions/edit-clear.png"));
        clear.setToolTipText(jEdit.getProperty("navigator.clearHistory.label", "Clear history"));

        backList = new NavHistoryList(client.getView(), client, client.getBackListModel(), null);
        backList.setToolTipText(jEdit.getProperty("navigator.backlist.tooltip", "Back history, click an item to jump to it."));
        forwardList = new NavHistoryList(client.getView(), client, client.getForwardListModel(), null);
        forwardList.setToolTipText(jEdit.getProperty("navigator.forwardlist.tooltip", "Forward history, click an item to jump to it."));

        controlPanel = new JPanel(new LambdaLayout());
        controlPanel.setVisible(false);
        controlPanel.add("0, 0, 1, 1, W, w, 0", groupByLabel);
        controlPanel.add("0, 1, 1, 1, W, w, 0", groupByFile);
        controlPanel.add("0, 2, 1, 1, W, w, 0", groupByLine);

        controlPanel.add("1, 0, 1, 1, 0, w, 0", KappaLayout.createHorizontalStrut(11, false));

        controlPanel.add("2, 0, 1, 1, W, w, 0", scopeLabel);
        controlPanel.add("2, 1, 1, 1, W, w, 0", viewScope);
        controlPanel.add("2, 2, 1, 1, W, w, 0", editPaneScope);

        controlPanel.add("3, 0, 1, 1, 0, w, 0", KappaLayout.createHorizontalStrut(11, false));

        controlPanel.add("4, 0, 1, 1, W, w, 0", showLineText);
        controlPanel.add("4, 1, 1, 1, W, w, 0", showLineTextSyntax);
        controlPanel.add("4, 2, 1, 1, W, w, 0", showStripes);

        controlPanel.add("5, 0, 1, 1, 0, w, 0", KappaLayout.createHorizontalStrut(11, false));

        controlPanel.add("6, 0, 1, 1, W, w, 0", showPath);
        controlPanel.add("6, 1, 1, 1, W, w, 0", showLineNumber);
        controlPanel.add("6, 2, 1, 1, W, w, 0", showCaretOffset);

        currentPanel = new JPanel();
        currentLabel = new JLabel();
        currentLabel.setText(client.getCurrentPosition() == null ? "" : client.getCurrentPosition().htmlText());
        currentLabel.setToolTipText(jEdit.getProperty("navigator.current.tooltip", "Current position"));
        currentPanel.add(currentLabel);

        flipPanel = new JPanel(new BorderLayout());
        flipPanel.add(currentPanel, BorderLayout.NORTH);
        flipPanel.add(controlPanel, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel(new KappaLayout());
        buttonPanel.add("0, 0, 1, 3, 0,, 3", back);
        buttonPanel.add("1, 0, 1, 3, 0,, 3", forward);
        buttonPanel.add("2, 0, 1, 3, 0,, 3", clear);
        buttonPanel.add("3, 0, 1, 3, 0,, 3", options);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(flipPanel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        JPanel middlePanel = new JPanel(new GridLayout(1, 2, 3, 3));
        middlePanel.add(backList);
        middlePanel.add(forwardList);

        add(topPanel, BorderLayout.NORTH);
        add(middlePanel, BorderLayout.CENTER);

    }

    private void installListeners() {
        final String name = OptionPanel.NAME;

        groupByFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                jEdit.setBooleanProperty(name + ".groupByFile", groupByFile.isSelected());
            }
        } );

        groupByLine.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                jEdit.setBooleanProperty(name + ".groupByLine", groupByLine.isSelected());
            }
        } );

        viewScope.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                int scope;
                if (viewScope.isSelected()) {
                    scope = NavigatorPlugin.VIEW_SCOPE;
                } else {
                    scope = NavigatorPlugin.EDITPANE_SCOPE;
                }
                jEdit.setIntegerProperty(name + ".scope", scope);
            }
        } );

        showLineText.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                jEdit.setBooleanProperty("navigator.showLineText", showLineText.isSelected());
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        backList.updateUI();
                        forwardList.updateUI();
                    }
                } );
            }
        } );

        showLineTextSyntax.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                jEdit.setBooleanProperty("navigator.showLineTextSyntax", showLineTextSyntax.isSelected());
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        backList.updateUI();
                        forwardList.updateUI();
                    }
                } );
            }
        } );

        showStripes.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                jEdit.setBooleanProperty("navigator.showStripes", showStripes.isSelected());
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        backList.updateUI();
                        forwardList.updateUI();
                    }
                } );
            }
        } );

        options.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        controlPanel.setVisible(! controlPanel.isVisible());
                        currentPanel.setVisible(! currentPanel.isVisible());
                        flipPanel.invalidate();
                        Dockable.this.repaint();
                    }
                } );
            }
        }
       );
        clear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                NavigatorPlugin.clearHistory(client.getView());
            }
        } );

        back.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                if (me.getButton() == MouseEvent.BUTTON3) {
                    NavHistoryPopup popup = NavigatorPlugin.backList(client.getView());
                    GUIUtilities.showPopupMenu(popup, back, me.getX(), me.getY());
                }
            }
        } );

        forward.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                if (me.getButton() == MouseEvent.BUTTON3) {
                    NavHistoryPopup popup = NavigatorPlugin.forwardList(client.getView());
                    GUIUtilities.showPopupMenu(popup, forward, me.getX(), me.getY());
                }
            }
        } );

        backList.addMouseListener(new MouseHandler(backList));
        forwardList.addMouseListener(new MouseHandler(forwardList));

        showPath.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                jEdit.setBooleanProperty("navigator.showPath", showPath.isSelected());
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        backList.updateUI();
                        forwardList.updateUI();
                    }
                } );
            }
        } );

        showLineNumber.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                jEdit.setBooleanProperty("navigator.showLineNumber", showLineNumber.isSelected());
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        backList.updateUI();
                        forwardList.updateUI();
                    }
                } );
            }
        } );

        showCaretOffset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                jEdit.setBooleanProperty("navigator.showCaretOffset", showCaretOffset.isSelected());
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        backList.updateUI();
                        forwardList.updateUI();
                    }
                } );
            }
        } );
    }

    public void stateChanged(ChangeEvent ce) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                clear.setEnabled(client.getBackModel().isEnabled() || client.getForwardModel().isEnabled());
                backList.setModel(client.getBackListModel());
                forwardList.setModel(client.getForwardListModel());
                currentLabel.setText(client.getCurrentPosition() == null ? "" : client.getCurrentPosition().htmlText());
            }
        } );
    }

    class MouseHandler extends MouseAdapter {
        NavHistoryList list;
        public MouseHandler(NavHistoryList list) {
            this.list = list;
        }
        public void mouseClicked(MouseEvent evt) {
            list.jump();
        }
    }
}