/*
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

 
package buildtools.java.classpathmanager; 


//buildtools support
import buildtools.JavaUtils;
import buildtools.java.packagebrowser.ClasspathEntry;
import buildtools.java.packagebrowser.JavaClass;
import buildtools.java.packagebrowser.JavaPackage;
import buildtools.java.packagebrowser.PackageBrowser;

//GUI stuff
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.BorderLayout;
import java.awt.Dimension;

/*
A GUI widget for viewing... and managing your classpath.

@author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
@version $Id$
*/
public class ClasspathManager extends JPanel {

    private String[] classpath = null;
    private DefaultMutableTreeNode root = new DefaultMutableTreeNode( "Current CLASSPATH");
    private JTree  entries = new JTree( root );
    
    public ClasspathManager() {
        super();
        

        this.setLayout( new BorderLayout() );

        this.refresh();
        
        this.add( new JScrollPane( this.entries ), BorderLayout.CENTER );
        this.setEnabled(true);
        this.setVisible(true);
        
    }
    
    private void refresh() {

        //init the JList
        ClasspathEntry[] classpath = PackageBrowser.getPackagesAsClasspath();;
        
        for (int i = 0; i < classpath.length; ++i) {

            DefaultMutableTreeNode classpathNode = new DefaultMutableTreeNode( classpath[i] );

            root.add( classpathNode );
            
            JavaPackage[] packages = classpath[i].getPackages();
            for (int j = 0; j < packages.length; ++j) {
                
                DefaultMutableTreeNode packageNode = new DefaultMutableTreeNode( packages[j] );

                classpathNode.add( packageNode );

                JavaClass[] classes = packages[j].getClasses();
                for (int k = 0; k < classes.length; ++k) { 
                    
                    packageNode.add( new DefaultMutableTreeNode( classes[k] ) );

                }
                
                
            }


        }

        this.entries.expandPath( new TreePath( root.getPath() ) );
        
    }


    public String[] getClasspath() {
        if (this.classpath == null) {
            this.classpath = JavaUtils.getClasspath();
        }
            
        return this.classpath;
    }


    
    public static void main(String[] args) {

        JFrame frame = new JFrame();

        frame.getContentPane().add( new ClasspathManager() );
        frame.setSize(new Dimension(350, 600) );
        //frame.addWindowListener( this );
        frame.setEnabled(true);
        frame.setVisible(true);
        frame.show();
        frame.toFront();

        
    }
    

    
}
