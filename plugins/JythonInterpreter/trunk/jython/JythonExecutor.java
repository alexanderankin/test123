/*
 *  JythonExecutor.java - JythonInterpreter Shell
 *  Copyright (C) 10 June 2001 Carlos Quiroz
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package jython;

import java.io.*;
import java.util.*;
import javax.swing.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.util.*;
import org.python.core.*;
import org.python.util.*;

import projectviewer.PVActions;
import projectviewer.vpt.VPTProject;

/**
 *  The JythonExecutor encapsulates the Jython Interpreter. It sets a thread
 *  where requests are pushed. The output and error stream are taken and
 *  displayed on a Jython Console
 *
 * @author     Carlos Quiroz
 * @version    $Id: JythonExecutor.java,v 1.46 2004/05/05 19:57:46 orutherfurd Exp $
 */
public class JythonExecutor implements Runnable {

	/**
	 *  Points to the single instance. JythonExecutor is a singleton. This may not
	 *  be very good, maybe there should be a per view Executor
	 */
	private static JythonExecutor executor;

	/**
	 *  The actual Jython Interpreter
	 */
	private JeditConsole interpreter;

	/**
	 *  Thread which takes commands and executes them so no to block the GUI
	 */
	private Thread interpreterThread;

	/**
	 *  Locks for the thread
	 */
	private Object lock;

	/**
	 *  Queue of commands
	 */
	private Vector queue;

	/**
	 *  Signals whether the thread has been interrupted
	 */
	private boolean interrupted;

	/**
	 *  Indicates whether the Interpreter has been initialised. This cna be done
	 *  only once
	 */
	private boolean initialised;

	/**
	 *  Signals whether the jython distribution in the plugin is being used. This
	 *  is determined by examining the classpath
	 */
	private boolean useInternalLib            = false;

	private PySystemState sys = null;

	private PyStringMap originalLocals = null;

	/**
	 *  Private constructo to enforce the singleton
	 */
	private void JythonExecutor() {
	}


	/**
	 *  Gets the JythonExecutor singleton
	 *
	 * @return    The single instance of JythonExecutor
	 */
	public static synchronized JythonExecutor getExecutor() {
		if (executor == null) {
			executor = new JythonExecutor();
		}
		return executor;
	}


	/**
	 *  Executes a macro. It looks for a file with the name name and executes
	 * it. It assumes the file lives under the jython directory
	 *
	 * @param  name  Macro's filename
	 * @deprecated as of JythonInterpreter 0.8
	 */
	public static void execMacro(String name) {
		getExecutor();
		if (executor != null) {
			executor.internalExecMacro(null, name);
		}
	}

	/**
	 * Executes a macro.
	 *
	 * @param view Target view
	 * @param name  Macro's filename
	 */
	public static void execMacro(View view, String name) {
		getExecutor();
		if (executor != null) {
			executor.internalExecMacro(view, name);
		}
	}

	/**
	 *  Executes a python file in a plugin
	 *
	 * @param  plugin    Plugin's name
	 * @param  dir       Directory containing py files
	 * @param  module    Module to be imported
	 * @param  function  Function to be called
	 * @since  JythonInterpreter 0.5
	 */
	public static PyObject execPlugin(String plugin, String dir, String module, String function) {
		getExecutor();
		if (executor != null) {
			return executor.execPlugin(plugin, dir, module, function, true, null);
		} else {
			return null;
		}
	}


	/**
	 *  Executes a python file in a plugin with certain parameters
	 *
	 * @param  plugin    Plugin's name
	 * @param  dir       Directory containing py files
	 * @param  module    Module to be imported
	 * @param  function  Function to be called
	 * @param  args      Arguments passed to the function
	 */
	public static PyObject execPlugin(String plugin, String dir, String module, String function, Object[] args) {
		getExecutor();
		if (executor != null) {
			return executor.execPlugin(plugin, dir, module, function, true, args);
		} else {
			return null;
		}
	}


	/**
	 *  Adds a plugin. It adds a plugin jars to the path
	 *
	 * @param  plugin    Plugin's name
	 * @since JythonInterpreter 0.9
	 */
	public static void addPlugin(String plugin) {

	}


	/**
	 *  Checks the jython version
	 *
	 * @param  view  Description of Parameter
	 */
	public static void checkVersion(View view) {
		if (PySystemState.hexversion < 33620131) {
			GUIUtilities.error(view, "jython.check.wrong", new Object[]{PySystemState.version});
		} else {
			GUIUtilities.message(view, "jython.check.right", new Object[]{PySystemState.version});
		}
	}

	/**
	 * Interrupts the current execution in the interpreter
	 *
	 * @since JythonInterpreter 0.9
	 */
	public void interruptCurrent(Console console) {
		interpreter.interrupt(Py.getThreadState());//new ThreadState(interpreterThread, Py.getSystemState()));
		PySystemState sys = Py.getSystemState();
		PyObject locals = interpreter.getLocals();
		interrupt();
	}

	/**
	 *  Stops the current execution in the interpreter. It also
	 */
	public void interrupt() {
		originalLocals = null;
		interpreter = null;

		PySystemState sys = Py.getSystemState();
		sys.path = new PyList();

		interpreter = getInterpreter();
	}


	/**
	 *  Executes a buffer. The buffer is not checked as being valid or a python
	 *  file. This should be done by the client
	 *
	 * @param  view     Target view
	 * @param  buffer   Buffer to be executed
	 * @param  console  Target console
	 */
	public void execBuffer(View view, Buffer buffer, Console console) {
		if (buffer != null && buffer.getPath() != null) {
			JythonCommand command = new ExecuteCommand(buffer, view, console);
			try {
				queue.addElement(command);
				synchronized (lock) {
					lock.notify();
				}
			} catch (Exception e) {
				interpreter.doException(console, e);
			}
		}
	}

	/**
	 *  Imports a buffer
	 *
	 * @param  view     Target view
	 * @param  buffer   Buffer to be executed
	 * @param  console  Target console
	 */
	public void importBuffer(View view, Buffer buffer, Console console) {
		JythonCommand command = new ImportCommand(buffer, view, console);
		try {
			queue.addElement(command);
			synchronized (lock) {
				lock.notify();
			}
		} catch (Exception e) {
			interpreter.doException(console, e);
		}
	}

	public PyObject eval(String what) {
		return interpreter.eval(what);
	}

	/**
	 *  Executes one line of code
	 *
	 * @param  view     Target view
	 * @param  command  command
	 * @param  console  Target console
	 */
	public void exec(View view, String command, Console console) {
		Log.log(Log.DEBUG, this, command);
		try {
			JythonCommand entry = new LineCommand(command, view, console);
			queue.addElement(entry);
			synchronized (lock) {
				lock.notify();
			}
		} catch (Exception e) {
			interpreter.doException(console, e);
		}
	}


	/**
	 *  Main processing method for the JythonExecutor object
	 */
	public void run() {
		JeditConsole interp  = getInterpreter();
		// keep the loop forever
		while (true) {
			try {
				synchronized (lock) {
					if (queue.size() == 0) {
						try {
							lock.wait(100);
						} catch (InterruptedException e) {

						}
					}
					if (queue.size() == 0) {
						continue;
					}
					if (interrupted) {
						return;
					}
					JythonCommand command  = (JythonCommand)queue.elementAt(0);
					queue.removeElementAt(0);
					command.execute(interpreter);
				}
			} catch (Throwable ex) {
				Log.log(Log.ERROR, this,  ex.getMessage());
			}
		}

	}

	/**
	 *  Creates the interpreter used by the JythonExecutor
	 *
	 * @return    The interpreter single instance
	 */
	protected synchronized JeditConsole getInterpreter() {
		if (interpreter == null) {
			// verify if it has been ever initialised
			if (!initialised) {
				// loads custom user properties
				Properties props          = new Properties();
				if (jEdit.getBooleanProperty("options.jython.registry.override")) {
					int propsSize  = Integer.parseInt(jEdit.getProperty("options.jython.registry.count"));
					for (int i = 0; i < propsSize; i++) {
						String key    = null;
						String value  = null;
						key = jEdit.getProperty("options.jython.registry.name." + i);
						value = jEdit.getProperty("options.jython.registry.value." + i);
						if (value != null && value.length() > 0) {
							props.setProperty(key, value);
						}
					}
				}
				// try to detect if jython is available in the classpath, if not use
				// bundled
				Properties sysProperties  = System.getProperties();
				String classpath          = sysProperties.getProperty("java.class.path");
				if (classpath != null) {
					// check if jython is in the classpath
					int jpy  = classpath.toLowerCase().indexOf("jython.jar");
					if (jpy == -1) {
						useInternalLib = true;
						}
				}
				// Modify cachedir property if has not been set before
				if (useInternalLib && !props.containsKey("python.cachedir")) {
					props.setProperty("python.cachedir", MiscUtilities.constructPath(MiscUtilities.constructPath(jEdit.getSettingsDirectory(), "jython"), "cachedir"));
				}
				try {
					JeditConsole.initialize(sysProperties, props, new String[]{""});
				} catch (Exception e) {
					Log.log(Log.ERROR, this, e);
				} finally {
					initialised = true;
				}
			}
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ByteArrayOutputStream err = new ByteArrayOutputStream();
			// adds __main__ module
			PyModule mod = imp.addModule("__main__");
			// if using internal lib add the appropriate jars to sys.path
			PySystemState sys   = Py.getSystemState();

			if (originalLocals == null) {
				originalLocals = ((PyStringMap)mod.__dict__);
				originalLocals = originalLocals.copy();
			}

			try {
				if (originalLocals != null && sys != null) {
					interpreter = new JeditConsole(originalLocals, sys, out, err);
				} else {
					interpreter = new JeditConsole(out, err);
				}
			} catch (Exception e) {
				Log.log(Log.ERROR, this, "Not possible to build interpreter");
				Log.log(Log.ERROR, this, e);
				return null;
			}

			if (useInternalLib) {
				PluginJAR jar  = jEdit.getPlugin("jython.JythonPlugin").getPluginJAR();
				Log.log(Log.DEBUG, this, "Using internal library");
				String path = new File(new File(jar.getPath()).getParent()
						, "jythonlib.jar").toString();
				sys.packageManager.addJar(path, true);
				sys.path.insert(0, new PyString(path + "/Lib"));
				path = new File(new File(jar.getPath()).getParent()
						, "jython.jar").toString();
				sys.packageManager.addJar(path, true);

			}

			// ProjectViewer integration:
			// Add the projectviewer's path to the jython interpreter's classpath
			// Do nothing if the projectviewer plugin is not installed
			if (jEdit.getPlugin("projectviewer.ProjectPlugin") != null) {
				VPTProject currentProject = PVActions.getCurrentProject(jEdit.getActiveView());
				if (currentProject != null) {
					String path = currentProject.getRootPath();
					if (path != null) sys.path.insert(0, new PyString(path));
				}
			}

			if (jEdit.getBooleanProperty("options.jython.autoloadPlugins")) {
				PluginJAR[] plugins = jEdit.getPluginJARs();
				for (int i=0;i<plugins.length;i++) {
					sys.packageManager.addJar(plugins[i].getPath(), true);
				}
			} else {
				/* we always need SideKick.jar for PyParser.py and ErrorList*/
				PluginJAR[] plugins = jEdit.getPluginJARs();
				for (int i=0;i<plugins.length;i++) {
					if (plugins[i].getPath().toUpperCase().endsWith("SIDEKICK.JAR")) {
						sys.packageManager.addJar(plugins[i].getPath(), true);
					}
					if (plugins[i].getPath().toUpperCase().endsWith("ERRORLIST.JAR")) {
						sys.packageManager.addJar(plugins[i].getPath(), true);
					}
				}
			}
			execPlugin("jython.JythonPlugin", "jython", "init", "_start", false, null);
			//interpreter.runsource("from init import _execMacro");
			//interpreter.runsource("from init import _getJEditJythonDir");
			interpreter.runsource("from init import _getUsersJythonDir");
			interpreter.runsource("from org.gjt.sp.jedit import Buffer");
			interpreter.runsource("from org.gjt.sp.jedit import EditPane");
			interpreter.runsource("from org.gjt.sp.jedit import jEdit");
			interpreter.runsource("from org.gjt.sp.jedit import View");
			lock = new Object();
			queue = new Vector();
			interrupted = false;
			interpreterThread = new Thread(this);
			interpreterThread.start();
		}
		return interpreter;
	}


	/**
	 * Implementation of the execMacro frontend. It will execute the macro with
	 * the given filename
	 *
	 * @param  name  Macro's filename
	 */
	protected void internalExecMacro(View view, String name) {
		if (interpreter == null) {
			interpreter = getInterpreter();
		}
		if (view == null) {
			view = jEdit.getFirstView();
		}

		// If a starup script is being executed a view
		// won't yet exist.  We'll still make the jEdit
		// variables available to macros, but they'll
		// be set to None.
		EditPane editPane = null;
		Buffer buffer = null;
		JEditTextArea textArea = null;
		if(view != null){
			editPane = view.getEditPane();
			textArea = editPane.getTextArea();
			buffer = editPane.getBuffer();
		}

		PySystemState sys =Py.getSystemState();
		PyObject py_view = (view != null ? new PyJavaInstance(view) : Py.None);
		PyObject py_buffer = (buffer != null ? new PyJavaInstance(buffer) : Py.None);
		PyObject py_editPane = (editPane != null ? new PyJavaInstance(editPane) : Py.None);
		PyObject py_textArea = (textArea != null ? new PyJavaInstance(textArea) : Py.None);

		PyObject init = ((PyStringMap)sys.modules).get(new PyString("init"));
		init.__setattr__("view", py_view);
		init.__setattr__("buffer", py_buffer);
		init.__setattr__("editPane", py_editPane);
		init.__setattr__("textArea", py_textArea);

		// make view, buffer, editPane, editPane,
		// and textArea available as locals
		PyObject locals = interpreter.getLocals();
		locals.__setitem__("view", py_view);
		locals.__setitem__("buffer", py_buffer);
		locals.__setitem__("editPane", py_editPane);
		locals.__setitem__("textArea", py_textArea);

		// so __name__ == '__main__' works
		locals.__setitem__("__name__", new PyString("__main__"));

		// Executes the buffer synchronously
		try{
			interpreter.execfile(name);
		}catch(Throwable t){
			new JythonErrorDialog(view, t);
		}
		interpreter.doIO(" ", null);
	}


	/**
	 *  Execute a plugin's code
	 *
	 * @param  plugin    Plugin's name
	 * @param  dir       Directory containing the target module
	 * @param  module    Imported module
	 * @param  function  Function to be invoked
	 * @param  delete    Indicates whether to delete the imported module
	 * @param  args      Function arguments
	 */
	private PyObject execPlugin(String plugin, String dir, String module, String function, boolean delete, Object args[]) {
		EditPlugin ePlugin  = jEdit.getPlugin(plugin);
		getInterpreter();
		PyObject result = null;
		if (ePlugin != null) {
			try {
				PluginJAR jar  = ePlugin.getPluginJAR();

				PySystemState sys   = Py.getSystemState();
				String path         = jar.getPath();
				sys.packageManager.addJar(path, true);
				PyString element    = new PyString(path + "/" + dir);
				try {
					sys.path.index(element);
				} catch (Exception e) {
					sys.path.append(element);
				}

				interpreter.pushLine("import " + module);

				if (sys.modules.__getitem__(new PyString(module)) instanceof PyNone) {
					Log.log(Log.ERROR, this, "Executing plugin failed " + plugin + ", module " + module);
					return null;
				}
				PyModule amodule = (PyModule)sys.modules.__getitem__(new PyString(module));

				if (args == null) {
					result = amodule.invoke(function.intern());
				} else {
					PyObject pyArgs[] = new PyObject[args.length];
					for (int i = 0; i < args.length; i++) {
						pyArgs[i] = Py.java2py(args[i]);
					}
					result = amodule.invoke(function.intern(), pyArgs);
				}
				if (delete) {
					interpreter.pushLine("del " + module);
				}
			} catch (Exception e) {
				Log.log(Log.ERROR, this, e);
			}
		} else {
			Log.log(Log.ERROR, this, "plugin " + plugin + " not found");
		}
		return result;
	}
}

// :indentSize=4:lineSeparator=\n:noTabs=false:tabSize=4:
