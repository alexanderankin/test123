/*
 * JUnitPlugin.java
 * :tabSize=4:indentSize=4:noTabs=true:
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

package junit;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.JPanel;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;

import junit.jeditui.TestRunner;

/**
 * The plugin for jUnit.
 */
public class JUnitPlugin extends EditPlugin
{

    static private Hashtable testRunners = new Hashtable();

    public void start() {}

    public void stop() {
        Enumeration e = testRunners.elements();
        while (e.hasMoreElements()) {
            TestRunner testRunner = (TestRunner) e.nextElement();
            testRunner.terminate();
        }
        testRunners.clear();
    }

    public void createMenuItems(Vector menuItems) {
        menuItems.addElement(GUIUtilities.loadMenu("junit-menu"));
    }

    /**
     * Returns the last used class path.
     */
    static public String getLastUsedClassPath() {
        return jEdit.getProperty("junit.last-used-class-path", "");
    }

    /**
     * Sets the last used class path.
     */
    static public void setLastUsedClassPath(String aPath) {
        jEdit.setProperty("junit.last-used-class-path", aPath);
    }

    static public JPanel createJUnitPanelFor(View view) {
        TestRunner testRunner = (TestRunner) testRunners.get(view);
        if (testRunner == null) {
            testRunner = new TestRunner(view);
            testRunners.put(view, testRunner);
        }
        return testRunner._createUI();
    }

}

