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


import java.util.Vector;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import jcompiler.*;
import jcompiler.options.*;


public class JCompilerPlugin extends EditPlugin
{

	public void start() {
		shell = new JCompilerShell();
		EditBus.addToNamedList(console.Shell.SHELLS_LIST, shell);
	}


	public void createMenuItems(Vector menuItems) {
		menuItems.addElement(GUIUtilities.loadMenu("jcompiler-menu"));
	}


	public void createOptionPanes(OptionsDialog optionsDialog) {
		OptionGroup group = new OptionGroup(
			jEdit.getProperty("options.jcompiler.label"));
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
		console.Console console = (console.Console) wm.getDockableWindow("console");
		console.setShell(shell);
		// run the command:
		console.run(command);
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


	/** holds a shared instance of the JCompiler shell. */
	private static JCompilerShell shell;

}
