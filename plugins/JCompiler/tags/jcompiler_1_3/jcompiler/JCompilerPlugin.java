/*
 * JCompilerPlugin.java - JCompiler plugin
 * Copyright (c) 1999, 2000 Kevin A. Burton and Aziz Sharif
 *
 * :tabSize=4:indentSize=4:noTabs=false:maxLineLen=0:
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


package jcompiler;


import java.io.File;
import java.io.IOException;
import java.util.Vector;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.JARClassLoader;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.OptionGroup;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.jedit.gui.OptionsDialog;
import org.gjt.sp.util.Log;
import jcompiler.options.*;
import console.Console;
import console.Shell;


public class JCompilerPlugin extends EditPlugin
{

	/** holds a shared instance of the JCompiler shell. */
	private static final JCompilerShell shell = new JCompilerShell();


	public void start() {
		String javaHome = System.getProperty("java.home");
		if (javaHome.toLowerCase().endsWith(File.separator + "jre"))
			javaHome = javaHome.substring(0, javaHome.length() - 4);
		String toolsPath = MiscUtilities.constructPath(javaHome,"lib","tools.jar");
		String javaVersion = System.getProperty("java.version");
		boolean isJDK12 = MiscUtilities.compareVersions(javaVersion, "1.2") >= 0;

		// find and add tools.jar to the list of jEdit plugins on JDK 1.2 or higher:
		// (tools.jar contains the compiler class)
		if (isJDK12 && new File(toolsPath).exists()) {
			EditPlugin.JAR jar = jEdit.getPluginJAR(toolsPath);
			if (jar == null) {
				Log.log(Log.DEBUG, this, "JDK 1.2 or higher detected, adding " + toolsPath + " to jEdit plugins");
				try {
					jEdit.addPluginJAR(new EditPlugin.JAR(toolsPath, new JARClassLoader(toolsPath)));
				}
				catch (IOException ioex) {
					Log.log(Log.ERROR, this, "Could not add tools.jar to jEdit plugins, reason follows...");
					Log.log(Log.ERROR, this, ioex);
				}
			}
		}

		// register the JCompiler shell:
		Shell.registerShell(shell);
	}


	public void stop() {
		Shell.unregisterShell(shell);
	}


	public void createMenuItems(Vector menuItems) {
		menuItems.addElement(GUIUtilities.loadMenu("jcompiler-menu"));
	}


	public void createOptionPanes(OptionsDialog optionsDialog) {
		OptionGroup group = new OptionGroup(jEdit.getProperty("options.jcompiler.label"));
		group.addOptionPane(new JCompilerOptionPaneGeneral());
		group.addOptionPane(new JCompilerOptionPaneCompiler());
		optionsDialog.addOptionGroup(group);
	}


	/**
	 * Returns a shared JCompilerShell singleton instance.
	 */
	public static JCompilerShell getShell() {
		return shell;
	}


	/**
	 * Executes an arbitrary command in the JCompiler shell.
	 * @see  JCompilerShell  for a list of supported commands.
	 */
	public static void executeCommand(View view, String command) {
		// ensure Console window is visible:
		DockableWindowManager wm = view.getDockableWindowManager();
		wm.addDockableWindow("console");
		// set current Console shell to JCompilerShell:
		Console console = (Console) wm.getDockableWindow("console");
		console.setShell(shell);
		// run the command:
		console.run(shell, console, command);
		// bugfix: textarea looses focus after compile:
		view.getTextArea().requestFocus();
	}


	/**
	 * Invokes the command "compile" on the Shell instance,
	 * compiling the current buffer.
	 */
	public static void compileFile(View view) {
		executeCommand(view, "compile");
	}


	/**
	 * Invokes the command "compilepkg" on the Shell instance,
	 * compiling outdated files of the source tree containing current buffer.
	 */
	public static void compilePackage(View view) {
		executeCommand(view, "compilepkg");
	}


	/**
	 * Invokes the command "rebuildpkg" on the Shell instance,
	 * recompiling all files of the source tree containing current buffer.
	 */
	public static void rebuildPackage(View view) {
		executeCommand(view, "rebuildpkg");
	}

}
