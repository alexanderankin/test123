/*
 * FailureRunView.java 
 * Copyright (c) Tue Aug 01 23:38:52 MSD 2006 Denis Koryavov
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
import javax.swing.border.*;
import javax.swing.event.*;

import junit.framework.*;

import junit.runner.BaseTestRunner;

/**
* A view presenting the test failures as a list.
*/
class FailureRunView implements TestRunView {
        JList fFailureList;
        TestRunContext fRunContext;
        
        //{{{ constructor.
        public FailureRunView(TestRunContext context) {
                fRunContext = context;
                fFailureList = new JList(fRunContext.getFailures());
                fFailureList.setPrototypeCellValue(
                        new TestFailure(new TestCase("dummy") {
                                        protected void runTest() {
                                        }
                                        
                        },
                        new AssertionFailedError("message"))
                        );
                
                fFailureList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                fFailureList.setCellRenderer(new FailureListCellRenderer());
                fFailureList.setToolTipText("Failure - grey X; Error - red X");
                fFailureList.setVisibleRowCount(5);
                fFailureList.addMouseListener(new TestRunViewHandler(this, context));
                fFailureList.addListSelectionListener(
                        new ListSelectionListener() {
                                public void valueChanged(ListSelectionEvent e) {
                                        testSelected();
                                }
                        });
        } 
        //}}}
        
        //{{{ getSelectedTest method.
        public Test getSelectedTest() {
                int index = fFailureList.getSelectedIndex();
                if (index == -1)
                        return null;
                ListModel model = fFailureList.getModel();
                TestFailure failure = (TestFailure) model.getElementAt(index);
                return failure.failedTest();
        } 
        //}}}
        
        //{{{ activate method.
        public void activate() {
                testSelected();
        } 
        //}}}
        
        //{{{ refresh method.
        public void refresh(Test test, TestResult result) {} //}}}
        
        //{{{ getComponent method.
        public Component getComponent() {
                JScrollPane scroll = new JScrollPane(fFailureList,
                        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                scroll.setName("junit.test.failures");
                return scroll;
        } 
        //}}}
        
        //{{{ revealFailure method.
        public void revealFailure(Test failure) {
                fFailureList.setSelectedIndex(0);
        } 
        //}}}
        
        //{{{ aboutToStart and runFinished methods.
        public void aboutToStart(Test suite, TestResult result) {}
        
        public void runFinished(Test suite, TestResult result) {} 
        //}}}
        
        //{{{ testSelected method.
        protected void testSelected() {
                fRunContext.handleTestSelected(getSelectedTest());
        } 
        //}}}
        
        //{{{ nextFailure method.
        public void nextFailure() {
                int index = fFailureList.getSelectedIndex();
                int nextIndex = (index == -1) ? 0 : index + 1;
                int size = fFailureList.getModel().getSize();
                if (size > 0 && nextIndex < size) {
                        fFailureList.setSelectedValue(
                                fFailureList.getModel().getElementAt(nextIndex), 
                                true);
                }
        } 
        //}}}
        
        //{{{ prevFailure method.
        public void prevFailure() {
                int index = fFailureList.getSelectedIndex();
                int nextIndex = (index == -1) ? 0 : index - 1;
                int size = fFailureList.getModel().getSize();
                if (0 <= nextIndex && size > 0) {
                        fFailureList.setSelectedValue(
                                fFailureList.getModel().getElementAt(nextIndex), 
                                true);
                }
        } 
        //}}}
        
        //{{{ FailureListCellRenderer class.
        /**
         * Renders TestFailures in a JList
         */
         private static class FailureListCellRenderer extends JLabel 
         implements ListCellRenderer 
         {
                 private final Icon ERROR_ICON = TestRunner.getIconResource(
                         getClass(), "icons/error.gif");
                 private final Icon FAILURE_ICON = TestRunner.getIconResource(
                         getClass(), "icons/failure.gif");
                 private Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
                 
                 
                 FailureListCellRenderer() {
                         super();
                         this.setOpaque(true);
                 }
                 
                 public Component getListCellRendererComponent(
                         JList list, Object value, int modelIndex,
                         boolean isSelected, boolean cellHasFocus) 
                 {
                         
                         if (isSelected) {
                                 setBackground(list.getSelectionBackground());
                                 setForeground(list.getSelectionForeground());
                         } else {
                                 setBackground(list.getBackground());
                                 setForeground(list.getForeground());
                         }
                         
                         TestFailure failure = (TestFailure) value;
                         String text = failure.failedTest().toString();
                         String msg = failure.thrownException().getMessage();
                         
                         if (msg != null)
                                 text += ":" + BaseTestRunner.truncate(msg);
                         
                                 if (failure.thrownException() 
                                         instanceof AssertionFailedError) 
                                 {
                                         setIcon(FAILURE_ICON);
                                 } else {
                                         setIcon(ERROR_ICON);
                                 }
                                 
                                 setText(text);
                                 setToolTipText(text);
                                 return this;
                 }
                 
         }
        //}}}
        
        // :collapseFolds=1:tabSize=8:indentSize=8:folding=explicit:
}
