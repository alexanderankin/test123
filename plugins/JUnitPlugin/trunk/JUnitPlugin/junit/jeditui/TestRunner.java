/*
 * TestRunner.java
 * :tabSize=4:indentSize=4:noTabs=true:foldLevel=1:
 * Copyright (c) 2001, 2002 Andre Kaplan
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

import common.gui.pathbuilder.PathBuilderDialog;

import junit.PluginTestCollector;
import junit.framework.*;
import junit.runner.*;

import java.util.*;
import java.lang.reflect.*;
import java.text.NumberFormat;
import java.net.URL;
import java.io.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.View;

import junit.JEditReloadingTestSuiteLoader;
import junit.JEditTestSuiteLoader;
import junit.JUnitPlugin;

/**
 * A Swing based user interface to run tests.
 * Enter the name of a class which either provides a static
 * suite method or is a subclass of TestCase.
 * <pre>
 * Synopsis: java junit.swingui.TestRunner [-noloading] [TestCase]
 * </pre>
 * TestRunner takes as an optional argument the name of the testcase class to be run.
 */
public class TestRunner extends BaseTestRunner implements TestRunContext
{
    private View fView;

    private Thread fRunner;
    private TestResult fTestResult;

    private String classPath;

    private TestView testView;
    private JComboBox fSuiteCombo;
    private ProgressBar fProgressIndicator;
    private DefaultListModel fFailures;
    private CounterPanel fCounterPanel;
    private StatusLine fStatusLine;
    private FailureDetailView fFailureView;
    private JTabbedPane fTestViewTab;
    private Vector fTestRunViews= new Vector(); // view associated with tab in tabbed pane
    private static final int GAP= 4;
    private static final int HISTORY_LENGTH= 5;

    private static final String FAILUREDETAILVIEW_KEY= "FailureViewClass";

    public TestRunner(View view) 
    {
        fView = view;
    }

    /**
     * Run the current test.
     */
    public void runSelectedTest(Test test)
    {
        rerunTest(test);
    }

    // {{{ TestRunListener methods
    public void testFailed(final int status, final Test test, final Throwable t)
    {
        SwingUtilities.invokeLater(
            new Runnable() {
                public void run() {
                    switch (status) {
                        case TestRunListener.STATUS_ERROR:
                            fCounterPanel.setErrorValue(fTestResult.errorCount());
                            appendFailure("Error", test, t);
                            break;
                        case TestRunListener.STATUS_FAILURE:
                            fCounterPanel.setFailureValue(fTestResult.failureCount());
                            appendFailure("Failure", test, t);
                            break;
                    }
                }
            }
        );
    }
    
    public void testStarted(String testName)
    {
        postInfo("Running: "+testName);
    }
    
    public void testEnded(String testName)
    {
        synchUI();
        SwingUtilities.invokeLater(
            new Runnable() {
                public void run() {
                    if (fTestResult != null) {
                        fCounterPanel.setRunValue(fTestResult.runCount());
                        fProgressIndicator.step(fTestResult.wasSuccessful()); // TODO update
                    }
                }
            }
        );
    }
    // }}}

    public TestRunView getCurrentTestRunView()
    {
        return (TestRunView) fTestRunViews.elementAt(fTestViewTab.getSelectedIndex());
    }

    public void setSuite(String suiteName)
    {
        fSuiteCombo.getEditor().setItem(suiteName);
    }

    private void addToHistory(final String suite)
    {
        for (int i= 0; i < fSuiteCombo.getItemCount(); i++) {
            if (suite.equals(fSuiteCombo.getItemAt(i))) {
                fSuiteCombo.removeItemAt(i);
                fSuiteCombo.insertItemAt(suite, 0);
                fSuiteCombo.setSelectedIndex(0);
                return;
            }
        }
        fSuiteCombo.insertItemAt(suite, 0);
        fSuiteCombo.setSelectedIndex(0);
        pruneHistory();
    }

    private void pruneHistory()
    {
        int historyLength= getPreference("maxhistory", HISTORY_LENGTH);
        if (historyLength < 1)
            historyLength= 1;
        for (int i= fSuiteCombo.getItemCount()-1; i > historyLength-1; i--)
            fSuiteCombo.removeItemAt(i);
    }

    private void appendFailure(String kind, Test test, Throwable t)
    {
        fFailures.addElement(new TestFailure(test, t));
        if (fFailures.size() == 1)
            revealFailure(test);
    }

    private void revealFailure(Test test)
    {
        for (Enumeration e= fTestRunViews.elements(); e.hasMoreElements(); ) {
            TestRunView v= (TestRunView) e.nextElement();
            v.revealFailure(test);
        }
    }

    protected void aboutToStart(final Test testSuite) 
    {
        for (Enumeration e= fTestRunViews.elements(); e.hasMoreElements(); ) {
            TestRunView v= (TestRunView) e.nextElement();
            v.aboutToStart(testSuite, fTestResult);
        }
    }

    protected void runFinished(final Test testSuite) 
    {
        SwingUtilities.invokeLater(
            new Runnable() {
                public void run() {
                    for (Enumeration e= fTestRunViews.elements(); e.hasMoreElements(); ) {
                        TestRunView v= (TestRunView) e.nextElement();
                        v.runFinished(testSuite, fTestResult);
                    }
                }
            }
        );
    }

    // {{{ Control creation methods
    protected CounterPanel createCounterPanel()
    {
        return new CounterPanel();
    }

    protected FailureDetailView createFailureDetailView()
    {
        String className= BaseTestRunner.getPreference(FAILUREDETAILVIEW_KEY);
        if (className != null) {
            Class viewClass= null;
            try {
                viewClass= Class.forName(className);
                return (FailureDetailView)viewClass.newInstance();
            } catch(Exception e) {
                JOptionPane.showMessageDialog(fView, "Could not create Failure DetailView - using default view");
            }
        }
        return new DefaultFailureDetailView();
    }

    protected TestView createTestView()
    {
        return new TestView();
    }

    protected JButton createSetClassPathButton()
    {
        JButton button = createImageButton("icons/classpath.gif",
                                           jEdit.getProperty("junit.set-class-path.tooltip"));
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                configureClassPath();
            }
        });
        return button;
    }

    protected StatusLine createStatusLine()
    {
        return new StatusLine(420);
    }

    protected JTabbedPane createTestRunViews()
    {
        JTabbedPane pane= new JTabbedPane(JTabbedPane.BOTTOM);

        addTab(pane, new FailureRunView(this));
        addTab(pane, new TestHierarchyRunView(this));

        pane.addChangeListener(
            new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    testViewChanged();
                }
            }
        );
        return pane;
    }
    
    protected void addTab(JTabbedPane tabs, TestRunView testRunView)
    {
        fTestRunViews.addElement(testRunView);
        Component component = testRunView.getComponent();
        Icon icon = getIconResource(getClass(),
            jEdit.getProperty(component.getName() + ".icon"));
        String label = jEdit.getProperty(component.getName() + ".label");
        String tooltip = jEdit.getProperty(component.getName() + ".tooltip");
        tabs.addTab(label, icon, component, tooltip);
    }
    // }}}

    public void testViewChanged()
    {
        TestRunView view= getCurrentTestRunView();
        view.activate();
    }

    protected TestResult createTestResult()
    {
        return new TestResult();
    }

    public JPanel _createUI()
    {
        fFailures= new DefaultListModel();

        fTestViewTab= createTestRunViews();

        fFailureView= createFailureDetailView();
        JScrollPane tracePane= new JScrollPane(fFailureView.getComponent(), JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        JPanel panel = new JPanel(new GridBagLayout()) {
            public boolean requestDefaultFocus() {
                fSuiteCombo.getEditor().getEditorComponent().requestFocus();
                return true;
            }
        };

        int row = -1;
        testView = createTestView();
        row++;
        addGrid(panel, testView,      0, row, 3, GridBagConstraints.HORIZONTAL       , 1 , GridBagConstraints.CENTER);

        fCounterPanel= createCounterPanel();
        JButton classPathButton = createSetClassPathButton();
        row++;
        addGrid(panel, fCounterPanel,      0, row, 2, GridBagConstraints.NONE       , 1 , GridBagConstraints.CENTER);
        addGrid(panel, classPathButton   , 1, row, 1, GridBagConstraints.NONE       , 0 , GridBagConstraints.EAST);

        row++;
        JSplitPane splitter= new JSplitPane(JSplitPane.VERTICAL_SPLIT, fTestViewTab, tracePane);
        addGrid(panel, splitter,           0, row, 3, GridBagConstraints.BOTH,       1.0, GridBagConstraints.WEST);

        fStatusLine= createStatusLine();
        row++;
        addGrid(panel, fStatusLine,        0, row, 3, GridBagConstraints.HORIZONTAL, 1.0, GridBagConstraints.CENTER);

        return panel;
    }
    
    private void addGrid(JPanel p, Component co, int x, int y, int w, int fill, double wx, int anchor)
    {
        GridBagConstraints c= new GridBagConstraints();
        c.gridx= x; c.gridy= y;
        c.gridwidth= w;
        c.anchor= anchor;
        c.weightx= wx;
        c.fill= fill;
        if (fill == GridBagConstraints.BOTH || fill == GridBagConstraints.VERTICAL)
            c.weighty= 1.0;
        c.insets= new Insets(y == 0 ? GAP : 0, x == 0 ? GAP : 0, GAP, GAP);
        p.add(co, c);
    }

    protected String getSuiteText() 
    {
        if (fSuiteCombo == null)
            return "";
        return (String)fSuiteCombo.getEditor().getItem();
    }

    public ListModel getFailures() 
    {
        return fFailures;
    }

    public void browseTestClasses() 
    {
        TestCollector collector= createTestCollector();
        TestSelector selector= new TestSelector(fView, collector);
        if (selector.isEmpty()) {
            JOptionPane.showMessageDialog(fView, "No Test Cases found.\nCheck that the configured \'TestCollector\' is supported on this platform.");
            return;
        }
        selector.show();
        String className= selector.getSelectedItem();
        if (className != null)
            setSuite(className);
    }

    TestCollector createTestCollector() 
    {
        return new PluginTestCollector(getClassPath());
    }

    private void loadHistory(JComboBox combo) throws IOException 
    {
        BufferedReader br= new BufferedReader(new FileReader(getSettingsFile()));
        int itemCount= 0;
        try {
            String line;
            while ((line= br.readLine()) != null) {
                combo.addItem(line);
                itemCount++;
            }
            if (itemCount > 0)
                combo.setSelectedIndex(0);

        } finally {
            br.close();
        }
    }

    private File getSettingsFile() 
    {
        String home= System.getProperty("user.home");
        return new File(home,".junitsession");
    }

    private void postInfo(final String message) 
    {
        SwingUtilities.invokeLater(
            new Runnable() {
                public void run() {
                    showInfo(message);
                }
            }
        );
    }

    private void postStatus(final String status) 
    {
        SwingUtilities.invokeLater(
            new Runnable() {
                public void run() {
                    showStatus(status);
                }
            }
        );
    }

    private void rerunTest(Test test) 
    {
        if (!(test instanceof TestCase)) {
            showInfo("Could not reload "+ test.toString());
            return;
        }
        Test reloadedTest= null;
        try {
            Class reloadedTestClass= getLoader().reload(test.getClass());
            Class[] classArgs= { String.class };
            Object[] args= new Object[]{((TestCase)test).getName()};
            Constructor constructor= reloadedTestClass.getConstructor(classArgs);
            reloadedTest=(Test)constructor.newInstance(args);
        } catch(Exception e) {
            showInfo("Could not reload "+ test.toString());
            return;
        }
        TestResult result= new TestResult();
        reloadedTest.run(result);

        String message= reloadedTest.toString();
        if(result.wasSuccessful())
            showInfo(message+" was successful");
        else if (result.errorCount() == 1)
            showStatus(message+" had an error");
        else
            showStatus(message+" had a failure");
    }

    protected void reset() 
    {
        fCounterPanel.reset();
        fProgressIndicator.reset();
        fFailureView.clear();
        fFailures.clear();
    }

    /**
     * Configure the class path.
     */
    public void configureClassPath()
    {
        PathBuilderDialog dialog =
            new PathBuilderDialog(fView, jEdit.getProperty("junit.class-path-dialog.title"));
        dialog.getPathBuilder().setPath(getClassPath());
        dialog.pack();
        dialog.setLocationRelativeTo(fView);
        dialog.show();
        if (dialog.getResult()) {
            classPath = dialog.getPathBuilder().getPath();
            JUnitPlugin.setLastUsedClassPath(classPath);
        }
    }

    /**
     * Returns the class path.
     */
    protected String getClassPath()
    {
        if (classPath == null) {
            classPath = JUnitPlugin.getLastUsedClassPath();
        }
        return classPath;
    }

    protected void runFailed(String message)
    {
        showStatus(message);
        fRunner= null;
    }

    synchronized public void runSuite()
    {
        if (fRunner != null) {
            fTestResult.stop();
        } else {
            setLoading(shouldReload());
            reset();
            showInfo("Load Test Case...");
            final String suiteName= getSuiteText();
            final Test testSuite= getTest(suiteName);
            if (testSuite != null) {
                addToHistory(suiteName);
                doRunTest(testSuite);
            }
        }
        testView.showTestEntry();
    }

    private boolean shouldReload()
    {
        return true;
    }

    synchronized protected void runTest(final Test testSuite)
    {
        if (fRunner != null) {
            fTestResult.stop();
        } else {
            reset();
            if (testSuite != null) {
                doRunTest(testSuite);
            }
        }
    }

    private void doRunTest(final Test testSuite)
    {
        fRunner= new Thread("TestRunner-Thread") {
            public void run() {
                testView.showProgressIndicator();
                TestRunner.this.start(testSuite);
                postInfo("Running...");

                long startTime= System.currentTimeMillis();
                testSuite.run(fTestResult);

                if (fTestResult.shouldStop()) {
                    postStatus("Stopped");
                } else {
                    long endTime= System.currentTimeMillis();
                    long runTime= endTime-startTime;
                    postInfo("Finished: " + elapsedTimeAsString(runTime) + " seconds");
                }
                runFinished(testSuite);
                fRunner= null;
                System.gc();
                testView.showTestEntry();
                fSuiteCombo.getEditor().getEditorComponent().requestFocus();
            }
        };
        // make sure that the test result is created before we start the
        // test runner thread so that listeners can register for it.
        fTestResult= createTestResult();
        fTestResult.addListener(TestRunner.this);
        aboutToStart(testSuite);

        fRunner.start();
    }

    private void saveHistory() throws IOException
    {
        BufferedWriter bw= new BufferedWriter(new FileWriter(getSettingsFile()));
        try {
            for (int i= 0; i < fSuiteCombo.getItemCount(); i++) {
                String testsuite= fSuiteCombo.getItemAt(i).toString();
                bw.write(testsuite, 0, testsuite.length());
                bw.newLine();
            }
        } finally {
            bw.close();
        }
    }

    public void handleTestSelected(Test test)
    {
        showFailureDetail(test);
    }

    private void showFailureDetail(Test test)
    {
        if (test != null) {
            ListModel failures= getFailures();
            for (int i= 0; i < failures.getSize(); i++) {
                TestFailure failure= (TestFailure)failures.getElementAt(i);
                if (failure.failedTest() == test) {
                    fFailureView.showFailure(failure);
                    return;
                }
            }
        }
        fFailureView.clear();
    }

    private void showInfo(String message)
    {
        fStatusLine.showInfo(message);
    }

    private void showStatus(String status)
    {
        fStatusLine.showError(status);
    }

    private void start(final Test test)
    {
        SwingUtilities.invokeLater(
            new Runnable() {
                public void run() {
                    int total= test.countTestCases();
                    fProgressIndicator.start(total);
                    fCounterPanel.setTotal(total);
                }
            }
        );
    }

    /**
     * Wait until all the events are processed in the event thread
     */
    private void synchUI()
    {
        try {
            SwingUtilities.invokeAndWait(
                new Runnable() {
                    public void run() {}
                }
            );
        }
        catch (Exception e) {
        }
    }

    /**
     * Terminates the TestRunner
     */
    public void terminate()
    {
        try {
            saveHistory();
        } catch (IOException e) {
            System.out.println("Couldn't save test run history");
        }
    }

    public TestSuiteLoader getLoader()
    {
        if (shouldReload()) {
            if (getClassPath().length() == 0) {
                return new JEditReloadingTestSuiteLoader();
            } else {
                return new JEditReloadingTestSuiteLoader(getClassPath());
            }
        }
        return new JEditTestSuiteLoader();
    }

    public void textChanged()
    {
        clearStatus();
    }
    
    static public Icon getIconResource(Class clazz, String name)
    {
        URL url= clazz.getResource(name);
        if (url == null) {
            System.err.println("Warning: could not load \""+name+"\" icon");
            return null;
        }
        return new ImageIcon(url);
    }
    
    static public JButton createImageButton(String image, String tooltip)
    {
        JButton button = new JButton(getIconResource(TestRunner.class, image));
        button.setMargin(new Insets(0,0,0,0));
        button.setToolTipText(tooltip);
        return button;
    }

    protected void clearStatus()
    {
        fStatusLine.clear();
    }
    
    private class TestView extends JPanel
    {
        private CardLayout cardLayout;

        public TestView()
        {
            cardLayout = new CardLayout();
            setLayout(cardLayout);
            add("testEntry", createTestEntryPanel());
            add("progressIndicator", createProgressIndicator());
        }
        
        public void showTestEntry()
        {
            if (!SwingUtilities.isEventDispatchThread()) {
                try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                        public void run() {
                            showTestEntry();
                        }
                    });
                } catch (Exception e) {}
            } else {
                cardLayout.show(this, "testEntry");
            }
        }

        public void showProgressIndicator()
        {
            if (!SwingUtilities.isEventDispatchThread()) {
                try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                        public void run() {
                            showProgressIndicator();
                        }
                    });
                } catch (Exception e) {}
            } else {
                cardLayout.show(this, "progressIndicator");
            }
        }

        public JPanel createTestEntryPanel()
        {
            JPanel testEntry = new JPanel(new GridBagLayout());
            fSuiteCombo = createSuiteCombo();
            Component browseButton = createBrowseButton();
            addGrid(testEntry, fSuiteCombo  , 0, 0, 1, GridBagConstraints.HORIZONTAL  , 1, GridBagConstraints.WEST);
            addGrid(testEntry, browseButton , 1, 0, 1, GridBagConstraints.NONE        , 0, GridBagConstraints.CENTER);
            return testEntry;
        }

        private Component createProgressIndicator()
        {
            JPanel panel = new JPanel(new GridBagLayout());
            fProgressIndicator = new ProgressBar();
            addGrid(panel, fProgressIndicator, 0, 0, 1, GridBagConstraints.HORIZONTAL, 1, GridBagConstraints.WEST);
            return panel;
        }

        protected Component createBrowseButton()
        {
            JButton browse = createImageButton("icons/open.gif",
                                               jEdit.getProperty("junit.browse-tests.tooltip"));
            browse.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        browseTestClasses();
                    }
                }
            );
            return browse;
        }

        protected JComboBox createSuiteCombo()
        {
            JComboBox combo= new JComboBox();
            combo.setEditable(true);
            combo.setLightWeightPopupEnabled(false);
    
            combo.getEditor().getEditorComponent().addKeyListener(
                new KeyAdapter() {
                    public void keyTyped(KeyEvent e) {
                        textChanged();
                        if (e.getKeyChar() == KeyEvent.VK_ENTER)
                            runSuite();
                    }
                }
            );
            try {
                loadHistory(combo);
            } catch (IOException e) {
                // fails the first time
            }
            combo.addItemListener(
                new ItemListener() {
                    public void itemStateChanged(ItemEvent event) {
                        if (event.getStateChange() == ItemEvent.SELECTED) {
                            textChanged();
                        }
                    }
                }
            );
            return combo;
        }
    }

}
