/*
 * jEdit edit mode settings:
 * :mode=java:tabSize=4:indentSize=4:noTabs=true:maxLineLen=0:
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

package javainsight;


// GUI stuff.
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

// classpath browser functionality
import javainsight.buildtools.packagebrowser.*;
import javainsight.buildtools.JavaUtils;
import javainsight.buildtools.MiscUtils;

// events
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// jode decompiler
import jode.decompiler.Decompiler;

// io
import java.io.IOException;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

// jedit
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.DockableWindowManager;

// debugging
import org.gjt.sp.util.Log;


/**
 * The Java Insight plugin dockable panel.
 *
 * @author Kevin A. Burton
 * @version $Id$
 */
public class JavaInsight extends JPanel implements TreeSelectionListener
{

    private static final String VERSION = jEdit.getProperty("plugin.javainsight.JavaInsightPlugin.version");


    public JavaInsight(View view) {
        super(new BorderLayout());
        this.view = view;

        // populate the root node...
        root = new DefaultMutableTreeNode("All Packages");
        JavaPackage[] packages = PackageBrowser.getPackages();
        MiscUtilities.quicksort(packages, new JavaPackageComparator());

        for (int i = 0; i < packages.length; ++i) {
            DefaultMutableTreeNode packageNode = new DefaultMutableTreeNode(packages[i].getName());
            JavaClass[] classes = packages[i].getClasses();
            MiscUtilities.quicksort(classes, new JavaClassComparator());

            for (int j = 0; j < classes.length; ++j) {
                packageNode.add(new DefaultMutableTreeNode(classes[j]));
            }

            root.add(packageNode);
        }

        // create tree:
        tree = new JTree(root);

        // create tabs:
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Packages", new JScrollPane(tree));
        tabs.addTab("Classpath", new ClasspathManager(this));
        int tabsPos = Integer.parseInt(jEdit.getProperty("view.docking.tabsPos", "0"));
        tabs.setTabPlacement(tabsPos == 0 ? JTabbedPane.TOP : JTabbedPane.BOTTOM);

        // create log text area:
        log = new JTextArea("");
        log.setEditable(false);
        log.setFont(new Font("SansSerif", Font.PLAIN, 11));
        JScrollPane logScr = new JScrollPane(log);
        logScr.setPreferredSize(new Dimension(100, 50));
        logScr.setColumnHeaderView(new JLabel("Decompiler Messages"));

        // create split pane divider:
        int splitPos = JSplitPane.VERTICAL_SPLIT;
        String dockPosition = jEdit.getProperty(
            JavaInsightPlugin.DOCKABLE_NAME + ".dock-position",
            DockableWindowManager.FLOATING
        );
        if (dockPosition.equals(DockableWindowManager.BOTTOM) ||
            dockPosition.equals(DockableWindowManager.TOP))
            splitPos = JSplitPane.HORIZONTAL_SPLIT;
        split = new JSplitPane(splitPos, true, tabs, logScr);
        split.setOneTouchExpandable(true);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                String dividerLocation = jEdit.getProperty("javainsight.dividerLocation", (String)null);
                if (dividerLocation != null)
                    split.setDividerLocation(Integer.parseInt(dividerLocation));
            }
        });

        // create status bar:
        status = new JLabel("Java Insight " + VERSION);

        // add components to main panel:
        add(split, BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);

        tree.addTreeSelectionListener(this);
        tree.addMouseListener(new TreeMouseListener());
        tree.expandPath(new TreePath(root.getPath()));
    }


    public void setStatus(String status) {
        this.status.setText(status);
        this.status.setToolTipText(status);
    }


    public DefaultMutableTreeNode getCurrentNode() {
        return currentNode;
    }


    /**
     * Return the classpath needed by the Jode Decompiler.
     * Jode expects a classpath where the entries are separated by
     * <code>Decompiler.altPathSeparatorChar</code> (which is a comma,
     * really).
     */
    private static String getJodeClassPath() {
        String classArray[] = JavaUtils.getClasspath();
        StringBuffer classpath = new StringBuffer();

        for (int i = 0; i < classArray.length; ++i) {
            if (i != 0)
                classpath.append(Decompiler.altPathSeparatorChar);
           classpath.append(classArray[i]);
        }

        if (classpath.length() == 0)
            classpath.append('.');

        return classpath.toString();
    }


    /**
     * Return a new Jode decompiler instance initialized with the options
     * from the JavaInsight option pane.
     *
     * @author Dirk Moebius
     */
    private static Decompiler getJodeDecompiler() {
        boolean pretty = jEdit.getBooleanProperty("javainsight.jode.pretty", true);
        boolean onetime = jEdit.getBooleanProperty("javainsight.jode.onetime", false);
        boolean decrypt = jEdit.getBooleanProperty("javainsight.jode.decrypt", true);
        String importPkgLimit = jEdit.getProperty("javainsight.jode.pkglimit", "0");
        String importClassLimit = jEdit.getProperty("javainsight.jode.clslimit", "1");

        Decompiler decompiler = new Decompiler();
        decompiler.setClassPath(getJodeClassPath());
        decompiler.setOption("style", jEdit.getProperty("javainsight.jode.style", "sun"));
        decompiler.setOption("pretty", pretty ? "yes" : "no");
        decompiler.setOption("onetime", onetime ? "yes" : "no");
        decompiler.setOption("decrypt", decrypt ? "yes" : "no");
        decompiler.setOption("import", importPkgLimit + "," + importClassLimit);

        return decompiler;
    }


    /**
     * Given a classname, decompile it and put the results into a new
     * jEdit buffer.
     *
     * @param view  the view in which the new buffer should be created.
     * @param className  the name of the class is to be decompiled.
     *
     * @author Dirk Moebius
     */
    void decompileToBuffer(String className) throws Throwable {
        try {
            StringWriter decompilerOutput = new StringWriter();
            StringWriter errorOutput = new StringWriter();

            Decompiler decompiler = getJodeDecompiler();
            decompiler.setErr(new PrintWriter(errorOutput));
            decompiler.decompile(className, decompilerOutput, null);
            decompilerOutput.flush();
            errorOutput.flush();

            // Insert error output into log text area:
            log.setText(errorOutput.toString());

            // Strip all '\r' out of the result:
            String output = decompilerOutput.toString();
            int len = output.length();
            StringBuffer sbuf = new StringBuffer(len);
            for (int i = 0; i < len; ++i)
                if (output.charAt(i) != '\r')
                    sbuf.append(output.charAt(i));
            String result = sbuf.toString();

            // Create new jEdit buffer
            int lastDot = className.lastIndexOf('.');
            String basename = (lastDot < 0 ? className : className.substring(lastDot + 1) + ".java");
            Buffer buf = jEdit.openFile(view, null, basename, false, true);
            jEdit.closeBuffer(view, buf); // close old buffer with the same name, if one exists
            buf = jEdit.openFile(view, null, basename, false, true);

            // Try to set Java mode (if it exists)
            Mode javaMode = jEdit.getMode("java");
            if (javaMode != null)
                buf.setMode(javaMode);

            // Insert the normal output into the buffer
            buf.beginCompoundEdit();
            buf.insertString(0, result.toString(), null);
            // If the string ends with a newline, the generated
            // buffer adds one extra newline; so we need to remove it:
            if (result.endsWith("\n") && buf.getLength() > 0)
                buf.remove(buf.getLength() - 1, 1);
            buf.endCompoundEdit();
            view.getTextArea().setCaretPosition(0);
            // Clear the dirty flag (if option is on)
            if (jEdit.getBooleanProperty("javainsight.clearDirty", false))
                buf.setDirty(false);
        } catch (Throwable t) {
            // Rethrow the exception
            throw t.fillInStackTrace();
        }
    }


    /**
     * Given a classname, decompile it and store it to a file.
     *
     * @param className  The name of the class is to be decompiled.
     * @param fileName  The directory or the file where the results should
     *        be put. If it is null, JavaInsight chooses a location, as
     *        specified by the results of
     *        <code>buildtools.MiscUtils.getTempDir("JavaInsight")</code>.
     * @return  The name of the filename that was decompiled.
     * @see  buildtools.MiscUtils#getTempDir(java.lang.String)
     */
    String decompileToFile(String className, String fileName) throws Throwable {
        try {
            String outputFile;

            if (fileName == null) {
                // create new file in temp dir:
                outputFile = MiscUtilities.constructPath(
                    MiscUtils.getTempDir("JavaInsight"),
                    JavaUtils.getJavaFile(className)
                );
            } else {
                File f = new File(fileName);
                f.mkdirs();
                if (f.isDirectory()) {
                    int lastDot = className.lastIndexOf('.');
                    String baseName = lastDot < 0 ? className : className.substring(lastDot + 1);
                    outputFile = MiscUtilities.constructPath(fileName, baseName + ".java");
                } else {
                    outputFile = fileName;
                }
            }

            Log.log(Log.DEBUG, this, "output file=" + outputFile);

            File outFile = new File(outputFile);
            boolean overwrite = jEdit.getBooleanProperty("javainsight.overwrite", true);
            if (!overwrite && outFile.exists()) {
                Log.log(Log.DEBUG, this, "already exists, and overwrite flag is false.");
                return outputFile;
            }

            // make sure all its directories exist.
            new File(outFile.getParent()).mkdirs();

            Writer decompilerOutput = new BufferedWriter(new FileWriter(outFile));
            Decompiler decompiler = getJodeDecompiler();
            decompiler.decompile(className, decompilerOutput, null);
            decompilerOutput.flush();
            decompilerOutput.close();

            return outputFile;
        }
        catch (Throwable t) {
            // Rethrow the exception
            throw t.fillInStackTrace();
        }
    }


    /**
     * Decompile a class to a new jEdit buffer.
     * Called by <code>this.mouseClicked(MouseEvent)</code> and
     * <code>ClasspathManager</code>.
     */
    void decompile(String className) {
        setStatus("Decompiling " + className + "...");
        setCursor(new Cursor(Cursor.WAIT_CURSOR));

        try {
            if (jEdit.getBooleanProperty("javainsight.decompileToBuffer", true)) {
                decompileToBuffer(className);
            } else {
                String filename = decompileToFile(className, null);
                jEdit.openFile(view, filename);
            }
            setStatus("Decompiled " + className + ".");
        }
        catch(Throwable t) {
            t.printStackTrace();
            GUIUtilities.error(view, "javainsight.error.decompile", new Object[] {className, t});
            setStatus("Decompiling " + className + ": Error!");
        }
        finally {
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }


    /** implements the TreeSelectionListener interface */
    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (node == null)
            return;

        currentNode = node;

        if (node.getUserObject() instanceof JavaClass) {
            // also output the source of this class
            JavaClass classnode = (JavaClass) node.getUserObject();
            Log.log(Log.DEBUG, this,
                "The source CLASSPATH entry of \""
                + classnode.getName()
                + "\" is \""
                + classnode.getSource()
                + "\""
            );
            setStatus(classnode.getName());
        }
    }


    /** overwritten to save split pane divider position */
    public void removeNotify() {
        super.removeNotify();
        jEdit.setProperty("javainsight.dividerLocation", Integer.toString(split.getDividerLocation()));
    }


    private DefaultMutableTreeNode root;
    private DefaultMutableTreeNode currentNode;
    private JTree tree;
    private JTextArea log;
    private JLabel status;
    private JSplitPane split;
    private View view;


    private class TreeMouseListener extends MouseAdapter {
        public void mouseClicked(MouseEvent evt) {
            if(evt.getClickCount() != 2)
                return;
            if (currentNode == null)
                return;
            Object userObject = currentNode.getUserObject();
            if (!(userObject instanceof JavaClass))
                return;
            decompile(((JavaClass)userObject).getName());
        }
    }

}
