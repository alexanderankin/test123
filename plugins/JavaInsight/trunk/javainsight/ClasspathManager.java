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


// buildtools support
import javainsight.buildtools.JavaUtils;
import javainsight.buildtools.ToolTipJTree;
import javainsight.buildtools.packagebrowser.*;

// GUI stuff
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

// quicksort support
import org.gjt.sp.jedit.MiscUtilities;

// jEdit properties support
import org.gjt.sp.jedit.jEdit;

// traversal support
import java.util.Enumeration;
import java.util.StringTokenizer;

// debugging
import org.gjt.sp.util.Log;


/**
 * A GUI widget for viewing and managing your classpath.
 *
 * @author Kevin A. Burton
 * @author Dirk Moebius
 * @version $Id$
 */
public class ClasspathManager extends JPanel {

    public ClasspathManager(JavaInsight javaInsightInstance) {
        super(new BorderLayout());
        this.javaInsight = javaInsightInstance;

        tree = new ToolTipJTree();
        populateTree();
        tree.setEditable(false);
        tree.putClientProperty("JTree.lineStyle", "Angled");
        tree.addTreeSelectionListener(new TreeSelectionHandler());
        tree.addMouseListener(new TreeMouseHandler());
        tree.expandPath(new TreePath(root.getPath()));

        add(new JScrollPane(tree), BorderLayout.CENTER);
    }


    /**
     * Changes the model of the tree to a new view structure.
     * The view of the tree is determined by the following jEdit properties:
     * <ul>
     *   <li><code>javainsight.tree.view</code>, one of:
     *     <ul>
     *       <li><code>view-packages</code>
     *       <li><code>view-classpath</code>
     *     </ul>
     *   <li><code>javainsight.tree.viewIsFlat</code>
     * </ul>
     */
    private void populateTree() {
        if (root != null) {
            root.removeAllChildren();
            root = null;
        }

        String view = jEdit.getProperty("javainsight.tree.view", "view-packages");
        boolean isFlat = jEdit.getBooleanProperty("javainsight.tree.viewIsFlat", false);

        if (view.equals("view-packages")) {
            root = new DefaultMutableTreeNode("All Packages");
            JavaPackage[] packages = PackageBrowser.getPackages();
            addPackages(root, packages, isFlat);
        }
        else if (view.equals("view-classpath")) {
            root = new DefaultMutableTreeNode("Current Classpath");
            ClasspathEntry[] classpath = PackageBrowser.getPackagesAsClasspath();

            for (int i = 0; i < classpath.length; ++i) {
                DefaultMutableTreeNode classpathNode = new DefaultMutableTreeNode(classpath[i]);
                root.add(classpathNode);
                JavaPackage[] packages = classpath[i].getPackages();
                addPackages(classpathNode, packages, isFlat);
            }
        }
        else
            throw new IllegalArgumentException("invalid tree view: " + view);

        tree.setModel(new DefaultTreeModel(root));
    }


    private void addPackages(DefaultMutableTreeNode parent, JavaPackage[] packages, boolean isFlat) {
        MiscUtilities.quicksort(packages, new JavaPackageComparator());
        for (int i = 0; i < packages.length; ++i) {
            JavaPackage pkg = packages[i];
            DefaultMutableTreeNode packageNode;
            if (isFlat) {
                // add a new package node directly under the parent:
                packageNode = new DefaultMutableTreeNode(pkg);
                parent.add(packageNode);
            } else {
                // search for the package node; create it, if it is not there yet:
                packageNode = getPackageNode(parent, pkg);
            }
            // add classes to the package node:
            JavaClass[] classes = pkg.getClasses();
            MiscUtilities.quicksort(classes, new JavaClassComparator());
            for (int j = 0; j < classes.length; ++j)
                packageNode.add(new DefaultMutableTreeNode(classes[j], false));
        }
    }


    private DefaultMutableTreeNode getPackageNode(DefaultMutableTreeNode parent, JavaPackage pkg) {
        Log.log(Log.DEBUG, this, "getPackageNode() pkg=" + pkg);
        StringTokenizer st = new StringTokenizer(pkg.toString(), ".");
        DefaultMutableTreeNode node = parent;
        while (st.hasMoreTokens()) {
            String part = st.nextToken();
            Log.log(Log.DEBUG, this, "part=" + part);
            // search the current node's children for the package part:
            Enumeration e = node.children();
            boolean foundChild = false;
            while (e.hasMoreElements()) {
                DefaultMutableTreeNode child = (DefaultMutableTreeNode) e.nextElement();
                Object obj = child.getUserObject();
                if (obj instanceof String && obj.toString().equals(part))
                    if (st.hasMoreTokens()) {
                        node = child;
                        foundChild = true;
                        break;
                    } else
                        return child; // found
            }
            if (!foundChild) {
                DefaultMutableTreeNode child = new DefaultMutableTreeNode(part);
                node.add(child);
                node = child;
            }
        }
        return node;
    }


    /** Change the model of the tree and save the current state as jEdit property. */
    void setView(String view) {
        jEdit.setProperty("javainsight.tree.view", view);
        populateTree();
    }


    /** Toggle between flat and structured package view. */
    void toggleFlatView() {
        boolean state = jEdit.getBooleanProperty("javainsight.tree.viewIsFlat", false);
        jEdit.setBooleanProperty("javainsight.tree.viewIsFlat", !state);
        populateTree();
    }


    /** Decompile currently selected class node. */
    void decompile() {
        if (currentNode != null) {
            Object userObject = currentNode.getUserObject();
            if (userObject != null && userObject instanceof JavaClass)
                javaInsight.decompile(((JavaClass)userObject).getName());
        }
    }


    private JavaInsight javaInsight;
    private ToolTipJTree tree;
    private DefaultMutableTreeNode root;
    private DefaultMutableTreeNode currentNode;
    private TreePopup popup;


    private class TreeSelectionHandler implements TreeSelectionListener {
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
                javaInsight.setStatus(classnode.getName());
            }
        }
    }


    private class TreeMouseHandler extends MouseAdapter {
        public void mouseClicked(MouseEvent evt) {
            if ((evt.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
                if (evt.getClickCount() == 2)
                    decompile();
            }
            else if ((evt.getModifiers() & MouseEvent.BUTTON3_MASK) != 0) {
                if (popup != null && popup.isVisible())
                    popup.setVisible(false);
                else {
                    Object userObject = null;
                    TreePath path = tree.getPathForLocation(evt.getX(), evt.getY());
                    if (path != null) {
                        tree.setSelectionPath(path);
                        userObject = ((DefaultMutableTreeNode)path.getLastPathComponent()).getUserObject();
                    }
                    popup = new TreePopup(ClasspathManager.this, userObject);
                    popup.show(tree, evt.getX() + 1, evt.getY() + 1);
                }
            }
        }
    }

}

