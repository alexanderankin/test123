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
import org.gjt.sp.jedit.ServiceManager;
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
	private static final JCompilerShell shell =
		(JCompilerShell) ServiceManager.getService("console.Shell", "JCompiler");


	class ToolsJarNotFoundException extends RuntimeException
	{
		public ToolsJarNotFoundException(String message)
		{
			super(message);
		}
	}


	public void start()
	{
		if(!MiscUtilities.isToolsJarAvailable())
		{
			// I can't live without my tools.jar:
			throw new ToolsJarNotFoundException(
				"Could not find library tools.jar!\n"
				+ "\n"
				+ "This library is essential for the JCompiler plugin.\n"
				+ "It is provided with a full Java Development Kit (JDK),\n"
				+ "not a simple Java Runtime Environment (JRE).\n"
				+ "You are probably running a JRE only.\n"
				+ "\n"
				+ "Further information:\n"
				+ "tools.jar usually resides in {java.home}/../lib/.\n"
				+ "Your java.home is " + System.getProperty("java.home") + "\n"
				+ "\n"
				+ "Please make sure that tools.jar is either in your\n"
				+ "CLASSPATH or in the location mentioned above.\n"
				+ "If that doesn't help, try adding it to jEdit's jars/ folder.");
		}
	}


	/**
	 * Returns a shared JCompilerShell singleton instance.
	 */
	public static JCompilerShell getShell()
	{
		return shell;
	}


	/**
	 * Executes an arbitrary command in the JCompiler shell.
	 * @see  JCompilerShell  for a list of supported commands.
	 */
	public static void executeCommand(View view, String command)
	{
		// ensure Console window is visible:
		DockableWindowManager wm = view.getDockableWindowManager();
		wm.showDockableWindow("console");
		// set current Console shell to JCompilerShell:
		Console console = (Console) wm.getDockable("console");
		console.setShell(shell);
		// run the command:
		console.run(shell, command);
		// bugfix: textarea looses focus after compile:
		view.getTextArea().requestFocus();
	}


	/**
	 * Invokes the command "compile" on the Shell instance,
	 * compiling the current buffer.
	 */
	public static void compileFile(View view)
	{
		executeCommand(view, "compile");
	}


	/**
	 * Invokes the command "compilepkg" on the Shell instance,
	 * compiling outdated files of the source tree containing current buffer.
	 */
	public static void compilePackage(View view)
	{
		executeCommand(view, "compilepkg");
	}


	/**
	 * Invokes the command "rebuildpkg" on the Shell instance,
	 * recompiling all files of the source tree containing current buffer.
	 */
	public static void rebuildPackage(View view)
	{
		executeCommand(view, "rebuildpkg");
	}

}

