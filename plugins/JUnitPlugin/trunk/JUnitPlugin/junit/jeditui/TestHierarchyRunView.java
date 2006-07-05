/*
 * TestHierarchyRunView.java
 * Copyright (c) 2002 Calvin Yu
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
 
 import java.awt.Component;
 import javax.swing.*;
 import javax.swing.event.*;
 import javax.swing.tree.TreePath;
 import java.util.Vector;
 import junit.framework.*;
 
 /**
  * A hierarchical view of a test run. The contents of a test suite is shown as a
  * tree.
  */
 class TestHierarchyRunView implements TestRunView {
         TestSuitePanel fTreeBrowser;
         TestRunContext fTestContext;
         
         public TestHierarchyRunView(TestRunContext context) {
                 fTestContext = context;
                 fTreeBrowser = new TestSuitePanel();
                 fTreeBrowser.setName("junit.test.hierarchy");
                 
                 fTreeBrowser.getTree()
                 .addMouseListener(new TestRunViewHandler(this, fTestContext));
                 fTreeBrowser.getTree().addTreeSelectionListener(
                         new TreeSelectionListener() {
                                 public void valueChanged(TreeSelectionEvent e) {
                                         testSelected();
                                 }
                         });
         }
         
         public Component getComponent() {
                 return fTreeBrowser;
         }
         
         public Test getSelectedTest() {
                 return fTreeBrowser.getSelectedTest();
         }
         
         public void activate() {
                 testSelected();
         }
         
         public void revealFailure(Test failure) {
                 JTree tree = fTreeBrowser.getTree();
                 TestTreeModel model = (TestTreeModel) tree.getModel();
                 Vector vpath = new Vector();
                 int index = model.findTest(failure, (Test) model.getRoot(), vpath);
                 
                 if (index >= 0) {
                         Object[] path = new Object[vpath.size() + 1];
                         vpath.copyInto(path);
                         Object last = path[vpath.size() - 1];
                         path[vpath.size()] = model.getChild(last, index);
                         TreePath selectionPath = new TreePath(path);
                         tree.setSelectionPath(selectionPath);
                         tree.makeVisible(selectionPath);
                 }
         }
         
         public void aboutToStart(Test suite, TestResult result) {
                 fTreeBrowser.showTestTree(suite);
                 result.addListener(fTreeBrowser);
         }
         
         public void runFinished(Test suite, TestResult result) {
                 result.removeListener(fTreeBrowser);
         }
         
         protected void testSelected() {
                 fTestContext.handleTestSelected(getSelectedTest());
         }
 }
