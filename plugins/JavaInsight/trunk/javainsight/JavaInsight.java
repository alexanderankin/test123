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
import buildtools.java.packagebrowser.JavaClass;
import buildtools.java.packagebrowser.JavaClassComparator;
import buildtools.java.packagebrowser.JavaPackage;
import buildtools.java.packagebrowser.JavaPackageComparator;
import buildtools.java.packagebrowser.PackageBrowser;
import buildtools.JavaUtils;
import buildtools.MiscUtils;

// quicksort functionality
import org.gjt.sp.jedit.MiscUtilities;

// events
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

// io
import java.io.IOException;
import java.io.File;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;

// jedit
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.DockableWindowManager;

// debugging
import org.gjt.sp.util.Log;

// misc
import java.util.Vector;


/**
 * The Java Insight plugin dockable panel.
 *
 * @author Kevin A. Burton
 * @version $Id$
 */
public class JavaInsight extends JPanel implements TreeSelectionListener, MouseListener
{

    private static final String VERSION = jEdit.getProperty("plugin.javainsight.JavaInsightPlugin.version");


    private DefaultMutableTreeNode root = new DefaultMutableTreeNode("All Packages");
    private DefaultMutableTreeNode currentNode = null;
    private JTree tree = new JTree(root);
    private JTextArea log = new JTextArea("");
    private JLabel status = new JLabel("Java Insight " + VERSION);
    private JSplitPane split = null;
    private View view = null;


    public JavaInsight(View view) {
        super(new BorderLayout());
        this.init(view, false);
    }


    private void init(View view, boolean bottomOrTop) {
        this.view = view;

        // populate the root node...
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

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Packages", new JScrollPane(tree));
        tabs.addTab("Classpath", new ClasspathManager(this));
        int tabsPos = Integer.parseInt(jEdit.getProperty("view.docking.tabsPos", "0"));
        tabs.setTabPlacement(tabsPos == 0 ? JTabbedPane.TOP : JTabbedPane.BOTTOM);

        log.setEditable(false);
        log.setFont(new Font("SansSerif", Font.PLAIN, 11));

        JScrollPane logScr = new JScrollPane(log);
        logScr.setPreferredSize(new Dimension(100, 50));
        logScr.setColumnHeaderView(new JLabel("Decompiler Output"));

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
        String dividerLocation = jEdit.getProperty("javainsight.dividerLocation", (String)null);
        if (dividerLocation != null)
            split.setDividerLocation(Integer.parseInt(dividerLocation));

        add(split, BorderLayout.CENTER);
        add(status, tabsPos == 0 ? BorderLayout.SOUTH : BorderLayout.NORTH);

        tree.addTreeSelectionListener(this);
        tree.addMouseListener(this);
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
     * Jode expects a comma delimited classpath.
     */
    private static String getJodeClassPath() {
        String classArray[] = JavaUtils.getClasspath();
        String classpath = "";

        for (int i = 0; i < classArray.length; ++i)
           classpath += classArray[i]+",";

        if (!classpath.equals(""))
          classpath = classpath.substring(0, classpath.length()-1);
        else
          classpath = ".";

        return classpath;
    }


    /**
     * Return the command line arguments needed by the Jode Decompiler.
     *
     * @author Dirk Moebius
     */
    private static String[] getJodeArguments(String className) {
        Vector args = new Vector();

        String style = jEdit.getProperty("javainsight.jode.style", "sun");
        args.addElement("--style");
        args.addElement(style);

        boolean pretty = jEdit.getBooleanProperty("javainsight.jode.pretty", true);
        if (pretty)
            args.addElement("--pretty");

        boolean onetime = jEdit.getBooleanProperty("javainsight.jode.onetime", false);
        if (onetime)
            args.addElement("--onetime");

        boolean decrypt = jEdit.getBooleanProperty("javainsight.jode.decrypt", true);
        if (decrypt)
            args.addElement("--decrypt");

        String importPkgLimit = jEdit.getProperty("javainsight.jode.pkglimit", "0");
        String importClassLimit = jEdit.getProperty("javainsight.jode.clslimit", "1");
        args.addElement("--import");
        args.addElement(importPkgLimit + "," + importClassLimit);

        args.addElement("--classpath");
        args.addElement(getJodeClassPath());

        args.addElement(className);

        String[] array = new String[args.size()];
        args.copyInto(array);

        // debug
        StringBuffer debug = new StringBuffer();
        for (int i = 0; i < array.length; ++i) {
            if (i > 0) debug.append(" ");
            debug.append(array[i]);
        }
        Log.log(Log.DEBUG, JavaInsight.class, "jode " + debug.toString());

        return array;
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
        String[] params = getJodeArguments(className);
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;
        ByteArrayOutputStream newOut = new ByteArrayOutputStream();
        ByteArrayOutputStream newErr = new ByteArrayOutputStream();

        try {
            System.setOut(new PrintStream(new BufferedOutputStream(newOut)));
            System.setErr(new PrintStream(new BufferedOutputStream(newErr)));
            jode.decompiler.Main.main(params);
        } catch (Throwable t) {
            // Rethrow the exception, but execute the finally clause
            throw t.fillInStackTrace();
        } finally {
            System.setOut(originalOut);
            System.setErr(originalErr);
        }

        // Insert text from stderr into log text area:
        log.setText(newErr.toString());

        // Strip all '\r' out of the result:
        byte[] bytes = newOut.toByteArray();
        StringBuffer sbuf = new StringBuffer(bytes.length);
        for (int i = 0; i < bytes.length; ++i)
            if (((char)bytes[i]) != '\r')
                sbuf.append((char) bytes[i]);
        String result = sbuf.toString();

        // Create new jEdit buffer
        int lastDot = className.lastIndexOf('.');
        String basename = (lastDot < 0 ? className : className.substring(lastDot + 1) + ".java");
        Buffer buf = jEdit.openFile(view, null, basename, false, true);
        jEdit.closeBuffer(view, buf);
        buf = jEdit.openFile(view, null, basename, false, true);

        // Try to set Java mode (if it exists)
        Mode javaMode = jEdit.getMode("java");
        if (javaMode != null)
            buf.setMode(javaMode);

        // Insert the normal output into the buffer
        buf.beginCompoundEdit();
        buf.insertString(0, result.toString(), null);
        // When the string ends with a newline, the generated
        // buffer adds one extra newline so we remove it:
        if (result.endsWith("\n") && buf.getLength() > 0)
            buf.remove(buf.getLength() - 1, 1);
        buf.endCompoundEdit();
        view.getTextArea().setCaretPosition(0);

        if (jEdit.getBooleanProperty("javainsight.clearDirty", false))
            buf.setDirty(false);
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

        String[] params = getJodeArguments(className);
        PrintStream original = System.out;

        try {
            System.setOut(new PrintStream(new FileOutputStream(outputFile)));
            jode.decompiler.Main.main(params);
        } catch (Throwable t) {
            // rethrow the exception, but execute the finally clause
            throw t.fillInStackTrace();
        } finally {
            System.setOut(original);
        }

        return outputFile;
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


    // MouseListener interface
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


    public void mousePressed(MouseEvent evt)  { }
    public void mouseReleased(MouseEvent evt) { }
    public void mouseEntered(MouseEvent evt)  { }
    public void mouseExited(MouseEvent evt)   { }


    // TreeSelectionListener interface
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


    public void removeNotify() {
        super.removeNotify();
        jEdit.setProperty("javainsight.dividerLocation", Integer.toString(split.getDividerLocation()));
    }

}
