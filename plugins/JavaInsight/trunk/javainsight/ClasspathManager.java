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
import javainsight.buildtools.packagebrowser.*;

// GUI stuff
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.BorderLayout;
import java.awt.Dimension;

// quicksort support
import org.gjt.sp.jedit.MiscUtilities;

// debug
import org.gjt.sp.util.Log;


// FIXME: this class is unneccessary.
// This classpath tree could be shown in the main tree of the JavaInsight
// class panel. It's just a case of switching models, maybe by a right-click
// popup menu. (Dirk Moebius)


/**
 * A GUI widget for viewing and managing your classpath.
 *
 * @author Kevin A. Burton
 * @version $Id$
 */
public class ClasspathManager extends JPanel implements TreeSelectionListener, MouseListener {

    private String[] classpath = null;
    private DefaultMutableTreeNode root = new DefaultMutableTreeNode("Current CLASSPATH");
    private DefaultMutableTreeNode currentNode = null;
    private JTree tree = new JTree(root);
    private JavaInsight javaInsight;


    public ClasspathManager(JavaInsight javaInsightInstance) {
        super(new BorderLayout());
        this.javaInsight = javaInsightInstance;
        init();
    }


    private void init() {
        // init the JList
        ClasspathEntry[] classpath = PackageBrowser.getPackagesAsClasspath();

        for (int i = 0; i < classpath.length; ++i) {
            DefaultMutableTreeNode classpathNode = new DefaultMutableTreeNode(classpath[i]);
            root.add(classpathNode);

            JavaPackage[] packages = classpath[i].getPackages();
            MiscUtilities.quicksort(packages, new JavaPackageComparator());

            for (int j = 0; j < packages.length; ++j) {
                DefaultMutableTreeNode packageNode = new DefaultMutableTreeNode(packages[j]);
                classpathNode.add(packageNode);

                JavaClass[] classes = packages[j].getClasses();
                MiscUtilities.quicksort(classes, new JavaClassComparator());

                for (int k = 0; k < classes.length; ++k) {
                    packageNode.add(new DefaultMutableTreeNode(classes[k]));
                }
            }
        }

        tree.addTreeSelectionListener(this);
        tree.addMouseListener(this);
        tree.expandPath(new TreePath(root.getPath()));

        add(new JScrollPane(tree), BorderLayout.CENTER);
    }


    public String[] getClasspath() {
        if (this.classpath == null) {
            this.classpath = JavaUtils.getClasspath();
        }
        return this.classpath;
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

        javaInsight.decompile(((JavaClass)userObject).getName());
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
            javaInsight.setStatus(classnode.getName());
        }
    }

}

