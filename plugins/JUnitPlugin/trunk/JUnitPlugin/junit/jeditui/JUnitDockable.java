/*
* jUnitDockable.java
* Copyright (c) 2003 Calvin Yu
* Copyright (c) 2006 Denis Koryavov
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
package junit.jeditui;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import junit.JUnitPlugin;
import junit.framework.Test;
import junit.framework.TestFailure;
import junit.runner.BaseTestRunner;
import junit.runner.FailureDetailView;
import junit.runner.TestCollector;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.gui.HistoryTextField;
import org.gjt.sp.util.Log;
import projectviewer.*;
import projectviewer.vpt.*;
import projectviewer.config.*;
// TODO: [if ProjectViewer.getActiveProject(jEdit.getActiveView()) = null {classPathButton.setEnabled(false)}]
class JUnitDockable extends JPanel {
    private static final int GAP = 4;
    private static final String FAILUREDETAILVIEW_KEY = "FailureViewClass";
    private TestRunner runner;
    private HistoryTextField currentTest;
    private ProgressBar progressBar;
    private CounterPanel counter;
    private JTabbedPane testRunViewTab;
    private FailureDetailView failureView;
    private StatusLine statusLine;
    private JButton stop;
    
    /**
    * Views associated with tab in tabbed pane
    */
    private Vector testRunViews = new Vector();
    
    //{{{ constructor.
    public JUnitDockable(TestRunner aRunner) {
        runner = aRunner;
        currentTest = createCurrentTestField();
        Component browseButton = createBrowseButton();
        progressBar = new ProgressBar();
        stop = createStopButton();
        counter = createCounterPanel();
        JButton classPathButton = createSetClassPathButton();
        testRunViewTab = createTestRunViews();
        failureView = createFailureDetailView();
        JScrollPane tracePane = new JScrollPane(failureView.getComponent(),
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        JSplitPane splitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
            testRunViewTab, tracePane);
        
        statusLine = createStatusLine();
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = -1;
        // comp gbc w fill wx anchor
        startRow(currentTest, gbc, 2, GridBagConstraints.HORIZONTAL, 1,
            GridBagConstraints.WEST);
        
        nextCol(browseButton, gbc, 1, GridBagConstraints.HORIZONTAL, 0,
            GridBagConstraints.CENTER);
        
        startRow(progressBar, gbc, 2, GridBagConstraints.HORIZONTAL, 1,
            GridBagConstraints.WEST);
        
        nextCol(stop, gbc, 1, GridBagConstraints.HORIZONTAL, 0,
            GridBagConstraints.WEST);
        
        startRow(counter, gbc, 2, GridBagConstraints.NONE, 1,
            GridBagConstraints.CENTER);
        
        nextCol(classPathButton, gbc, 1, GridBagConstraints.HORIZONTAL, 0,
            GridBagConstraints.EAST);
        
        startRow(splitter, gbc, 3, GridBagConstraints.BOTH, 1,
            GridBagConstraints.WEST);
        
        startRow(statusLine, gbc, 3, GridBagConstraints.HORIZONTAL, 1,
            GridBagConstraints.CENTER);
    } 
    //}}}
    
    public void setCurrentTest(String suiteName) {
        currentTest.setText(suiteName);
    }
    
    public void startTesting(final int testCount) {
        SwingUtilities.invokeLater(
            new Runnable() {
                public void run() {
                    progressBar.start(testCount);
                    counter.setTotal(testCount);
                    showInfo("Running...");
                    stop.setEnabled(true);
                }
            }
            );
    }
    
    public void aboutToStart(final Test testSuite) {
        for (Enumeration e = testRunViews.elements(); e.hasMoreElements();) {
            TestRunView v = (TestRunView) e.nextElement();
            v.aboutToStart(testSuite, runner.getTestResult());
        }
    }
    
    public void runFinished(final Test testSuite) {
        SwingUtilities.invokeLater(
            new Runnable() {
                public void run() {
                    stop.setEnabled(false);
                    for (Enumeration e = testRunViews.elements(); e.hasMoreElements();) {
                        TestRunView v = (TestRunView) e.nextElement();
                        v.runFinished(testSuite, runner.getTestResult());
                    }
                }
            });
    }
    
    public TestRunView getCurrentTestRunView() {
        return (TestRunView)
        testRunViews.elementAt(testRunViewTab.getSelectedIndex());
    }
    
    public void showFailureDetail(Test test) {
        if (test != null) {
            ListModel failures = runner.getFailures();
            for (int i = 0; i < failures.getSize(); i++) {
                TestFailure failure = (TestFailure) failures.getElementAt(i);
                if (failure.failedTest() == test) {
                    failureView.showFailure(failure);
                    return;
                }
            }
        }
        failureView.clear();
    }
    
    public void clearStatus() {
        statusLine.clear();
    }
    
    public void reset() {
        counter.reset();
        progressBar.reset();
        failureView.clear();
    }
    
    public boolean requestDefaultFocus() {
        currentTest.requestFocus();
        return true;
    }
    
    public String getCurrentTest() {
        if (currentTest == null)
            return "";
        return currentTest.getText();
    }
    
    public void revealFailure(Test test) { 
        for (Enumeration e = testRunViews.elements(); e.hasMoreElements();) {
            TestRunView v = (TestRunView) e.nextElement();
            v.revealFailure(test);
        }
    }
    
    public void browseTestClasses() {
        TestCollector collector = runner.createTestCollector();
        TestSelector selector = new TestSelector(runner.getView(), collector);
        if (selector.isEmpty()) {
            JOptionPane.showMessageDialog(runner.getView(), 
                "No Test Cases found.");
            return;
        }
        
        selector.setVisible(true);
        String className = selector.getSelectedItem();
        
        if (className != null)
            setCurrentTest(className);
        currentTest.requestFocus();
    }
    
    //{{{ configureClassPath method.
    public void configureClassPath() {
        VPTProject pr = ProjectViewer.getActiveProject(jEdit.getActiveView());
        if (pr == null) {
            JOptionPane.showMessageDialog(runner.getView(),
                jEdit.getProperty("junit.error.no-project-selected.message"),
                jEdit.getProperty("junit.dock.title"),
                JOptionPane.ERROR_MESSAGE
                );
            return;
        }
        ProjectOptions.run(pr, null, null, "junit.pconfig");
        runner.setClassPath(JUnitPlugin.getClassPath());
    } 
    //}}}
    
    public void setErrorCount(int count) {
        counter.setErrorValue(count);
    }
    
    public void setFailureCount(int count) {
        counter.setFailureValue(count);
    }
    
    public void setRunCount(int count) {
        counter.setRunValue(count);
    }
    
    public void showInfo(final String message) {
        if (SwingUtilities.isEventDispatchThread()) {
            statusLine.showInfo(message);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        showInfo(message);
                    }
            });
        }
    }
    
    public void showStatus(final String message) {
        if (SwingUtilities.isEventDispatchThread()) {
            statusLine.showError(message);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        showStatus(message);
                    }
            });
        }
    }
    
    public void updateProgress(boolean success) {
        progressBar.step(success);
    }
    
    private JTabbedPane createTestRunViews() {
        JTabbedPane pane = new JTabbedPane(JTabbedPane.BOTTOM);
        addTab(pane, new FailureRunView(runner));
        addTab(pane, new TestHierarchyRunView(runner));
        pane.addChangeListener(
            new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    testRunViewChanged();
                }
            });
        return pane;
    }
    
    //{{{ createCurrentTestField method.
    private HistoryTextField createCurrentTestField() {
        HistoryTextField field =
        new HistoryTextField("junit.test-suite.history", false, true);
        field.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    try {
                        runner.runSuite();
                    } catch (NoClassDefFoundError e) {
                        int result = GUIUtilities.confirm(runner.getView(),
                            "junit.error.class-not-found",
                            new String[] { e.getMessage() },
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.ERROR_MESSAGE);
                        
                        if (result == JOptionPane.OK_OPTION) {
                            configureClassPath();
                        }
                    }
                }
        });
   
        field.getDocument().addDocumentListener(new DocumentListener() {
                public void changedUpdate(DocumentEvent evt) {
                    textChanged();
                }
                
                public void insertUpdate(DocumentEvent evt) {
                    textChanged();
                }
                
                public void removeUpdate(DocumentEvent evt) {
                    textChanged();
                }
        });
        
        return field;
    }
    //}}}
    
    //{{{ createBrowseButton method.
    private Component createBrowseButton() {
        JButton browse = createImageButton("icons/open.gif",
            jEdit.getProperty("junit.browse-tests.tooltip"));
        browse.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    browseTestClasses();
                }
        });
        
        return browse;
    }
    //}}}
    
    private CounterPanel createCounterPanel() {
        return new CounterPanel();
    }
    
    private FailureDetailView createFailureDetailView() {
        String className = BaseTestRunner.getPreference(FAILUREDETAILVIEW_KEY);
        if (className != null) {
            Class viewClass = null;
            try {
                viewClass = Class.forName(className);
                return (FailureDetailView) viewClass.newInstance();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(runner.getView(),
                    "Could not create Failure DetailView - using default view");
            }
        }
        
        return new DefaultFailureDetailView();
    }
    
    private JButton createSetClassPathButton() {
        JButton button = createImageButton("icons/classpath.gif",
            jEdit.getProperty("junit.set-class-path.tooltip"));
        button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    configureClassPath();
                }
        });
        
        return button;
    }
    
    private JButton createStopButton() {
        JButton button = createImageButton("icons/stop.png",
            jEdit.getProperty("junit.stop.tooltip"));
        button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    runner.runSuite();
                }
        });
        
        button.setEnabled(false);
        return button;
    }
    
    private StatusLine createStatusLine() {
        return new StatusLine(420);
    }
    
    private void textChanged() {
        clearStatus();
    }
    
    private void testRunViewChanged() {
        getCurrentTestRunView().activate();
    }
    
    private void addTab(JTabbedPane tabs, TestRunView testRunView) {
        testRunViews.addElement(testRunView);
        Component component = testRunView.getComponent();
        Icon icon = getIconResource(
            jEdit.getProperty(component.getName() + ".icon"));
        String label = jEdit.getProperty(component.getName() + ".label");
        String tooltip = jEdit.getProperty(component.getName() + ".tooltip");
        tabs.addTab(label, icon, component, tooltip);
    }
    
    private Icon getIconResource(String name) {
        return TestRunner.getIconResource(getClass(), name);
    }
    
    private JButton createImageButton(String image, String tooltip) {
        JButton button = new JButton(getIconResource(image));
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setToolTipText(tooltip);
        return button;
    }
    
    private void startRow(Component co, GridBagConstraints gbc,
        int w, int fill, double wx, int anchor) {
    gbc.gridx = 0;
    gbc.gridy++;
    addGrid(co, gbc, w, fill, wx, anchor);
        }
        
        private void nextCol(Component co, GridBagConstraints gbc,
            int w, int fill, double wx, int anchor) {
        gbc.gridx += gbc.gridwidth;
        addGrid(co, gbc, w, fill, wx, anchor);
            }
            
            private void addGrid(Component co, GridBagConstraints gbc,
                int w, int fill, double wx, int anchor) {
            gbc.gridwidth = w;
            gbc.anchor = anchor;
            gbc.weightx = wx;
            gbc.fill = fill;
            
            if (fill == GridBagConstraints.BOTH || fill == GridBagConstraints.VERTICAL)
                gbc.weighty = 1.0;
            else
                gbc.weighty = 0;
                gbc.insets = new Insets(gbc.gridy == 0 ? GAP : 0, gbc.gridx == 0 ? GAP : 0,
                    GAP, GAP);
                add(co, gbc);
                }
  // :collapseFolds=1:tabSize=8:indentSize=8:folding=explicit:              
}
