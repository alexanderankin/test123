/*
* JUnitPlugin.java
* Copyright (c) 2001, 2002 Andre Kaplan
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

package junit;

import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.JPanel;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;

import junit.jeditui.*;

import projectviewer.*;
import projectviewer.vpt.*;
import projectviewer.config.*;
import projectviewer.event.*;

/**
* The plugin for jUnit.
*/
public class JUnitPlugin extends EditPlugin {
        static private Hashtable testRunners = new Hashtable();
        
        //{{{ start method.
        public void start() {
        }
        //}}}
        
        //{{{ stop method. 
        public void stop() {
                testRunners.clear();
        }
        //}}}
        
        //{{{ createJUnitPanelFor method.
        public static JPanel createJUnitPanelFor(View view) {
                TestRunner testRunner = getTestRunner(view);
                return testRunner._createUI();
        }
        //}}}
        
        //{{{ createPathBuilder
        public static JPanel createPathBuilder() {
                VPTProject project = getActiveProject();
                ProjectOptionsPanel panel = new ProjectOptionsPanel(
                        jEdit.getProperty("options.junit.pconfig.label"), 
                        project);
                
                panel.setPath(getClassPath());
                if (project != null) {
                        panel.setStartDirectory(project.getRootPath());
                }
                return panel;
        }
        //}}}
        
        //{{{ getClassPath method.
        public static String getClassPath() {
                String classPath = "";
                VPTProject project = getActiveProject();
                if (project != null) {
                        classPath = project.getProperty("junit.class-path");
                        if (classPath == null) {
                                project.setProperty("junit.class-path", "");
                                classPath = "";
                        }
                }
                return classPath;
        } 
        //}}}
        
        //{{{ refresh method.
        public static void refresh(VPTProject project, View view) {
                String classPath = project.getProperty("junit.class-path");
                getTestRunner(view).setClassPath(classPath);        
        } 
        //}}}
        
        //{{{ configureClassPath method.
        public static void configureClassPath(String classPath) {
                getActiveProject().setProperty("junit.class-path", classPath);
                getTestRunner(jEdit.getActiveView()).setClassPath(classPath);  
        } 
        //}}}
        
        //{{{  getTestRunner method.
        private static TestRunner getTestRunner(View view) {
                TestRunner testRunner = (TestRunner) testRunners.get(view);
                if (testRunner == null) {
                        testRunner = new TestRunner(view);
                        testRunners.put(view, testRunner);
                }
                return testRunner;
        } 
        //}}}
        
        //{{{ getActiveProject method.
        private static VPTProject getActiveProject() {
                return ProjectViewer.getActiveProject(jEdit.getActiveView());
        } 
        //}}}
        
        // :collapseFolds=1:tabSize=8:indentSize=8:folding=explicit:
}
