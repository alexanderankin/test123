/*
 * JCompiler.java - a wrapper around sun.tools.javac.Main
 * (c) 1999, 2000 - Kevin A. Burton and Aziz Sharif
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

import java.lang.reflect.*;
import java.util.StringTokenizer;
import java.util.Vector;
import java.io.*;
import javax.swing.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.*;
import buildtools.*;


/**
 * The class that performs the javac compile run.
 */
public class JCompiler {

	/** true, if JDK version is less than 1.2. */
	private final static boolean isOldJDK = (MiscUtilities.compareVersions(System.getProperty("java.version"), "1.2") < 0);

	/** holds the javac compiler method instance. */
	private static Method compilerMethod = null;

	// on first initialization, set a new SecurityManager. Note, that
	// on JDK 1.1.x a SecurityManager can only be set _once_.
	private static NoExitSecurityManager sm = NoExitSecurityManager.getInstance();

	static {
		try {
			System.setSecurityManager(sm);
		}
		catch (SecurityException secex) {
			Log.log(Log.ERROR, JCompiler.class,
				"Could not set new SecurityManager. Sorry.");
		}
	}


	private PipedOutputStream pipe = null;


	public JCompiler() {
		pipe = new PipedOutputStream();
	}


	/**
	 * compile a file with sun.tools.javac.Main.
	 *
	 * @param view        the view, where error dialogs should go
	 * @param buf         the buffer containing the file to be compiled
	 * @param pkgCompile  if true, JCompiler tries to locate the base directory
	 *                    of the package of the current file and compiles
	 *                    every outdated file.
	 * @param rebuild     if true, JCompiler compiles <i>every</i> file in the
	 *                    package hierarchy.
	 */
	public void compile(View view, Buffer buf, boolean pkgCompile, boolean rebuild) {
		// Search for the compiler method
		compilerMethod = getCompilerMethod();
		if (compilerMethod == null)
			return;

		// Check output directory:
		String outDirPath = null;
		if (jEdit.getBooleanProperty( "jcompiler.specifyoutputdirectory")) {
			outDirPath = jEdit.getProperty("jcompiler.outputdirectory");
			outDirPath = expandPath(outDirPath);
			File outDir = new File(outDirPath);
			try {
				// canonize outDirPath:
				outDirPath = outDir.getCanonicalPath();
			}
			catch (IOException ioex) {
				sendMessage("jcompiler.msg.errorOutputDir", new Object[] { outDirPath, ioex });
				return;
			}
			if (outDir.exists()) {
				if (!outDir.isDirectory()) {
					sendMessage("jcompiler.msg.noOutputDir", new Object[] {outDirPath });
					return;
				}
			} else {
				int reply = JOptionPane.showConfirmDialog(view,
					jEdit.getProperty("jcompiler.msg.createOutputDir.message", new Object[] {outDirPath }),
					jEdit.getProperty("jcompiler.msg.createOutputDir.title"),
					JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE);
				if (reply != JOptionPane.YES_OPTION) {
					return;
				}
				if (!outDir.mkdirs()) {
					GUIUtilities.message(view, "jcompiler.msg.errorCreateOutputDir", null);
					return;
				}
			}
		}

		// Check for auto save / auto save all
		String prop = pkgCompile ? "jcompiler.javapkgcompile.autosave" : "jcompiler.javacompile.autosave";
		if (jEdit.getProperty(prop).equals("current")) {
			// Save current buffer, if dirty, but nothing else:
			if (buf.isDirty())
				buf.save(view, null);
		}
		else if (jEdit.getProperty(prop).equals("all")) {
			// Save all buffers:
			Buffer[] buffers = jEdit.getBuffers();
			for(int i = 0; i < buffers.length; i++)
				if (buffers[i].isDirty())
					buffers[i].save(view, null);
		}
		else if (jEdit.getProperty(prop).equals("ask")) {
			// Ask for unsaved changes:
			if (pkgCompile) {
				// Check if there are any unsaved buffers:
				Buffer[] buffers = jEdit.getBuffers();
				boolean dirty = false;
				for(int i = 0; i < buffers.length; i++)
					if (buffers[i].isDirty())
						dirty = true;
				if (dirty) {
					int result = JOptionPane.showConfirmDialog(view,
						jEdit.getProperty("jcompiler.msg.saveAllChanges.message"),
						jEdit.getProperty("jcompiler.msg.saveAllChanges.title"),
						JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.WARNING_MESSAGE);
					if (result == JOptionPane.CANCEL_OPTION)
						return;
					if (result == JOptionPane.YES_OPTION)
						for(int i = 0; i < buffers.length; i++)
							if (buffers[i].isDirty())
								buffers[i].save(view, null);
				}
			} else {
				// Check if current buffer is unsaved:
				if (buf.isDirty()) {
					int result = JOptionPane.showConfirmDialog(view,
						jEdit.getProperty("jcompiler.msg.saveChanges.message", new Object[] { buf.getName() }),
						jEdit.getProperty("jcompiler.msg.saveChanges.title"),
						JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.WARNING_MESSAGE);
					if (result == JOptionPane.CANCEL_OPTION)
						return;
					if (result == JOptionPane.YES_OPTION)
						buf.save(view, null);
				}
			}
		}

		// Get files to compile:
		String filename = buf.getPath();
		File ff = new File(filename);
		String parent = ff.getParent();
		String[] files = null;
		if (parent != null && pkgCompile == true) {
			// compile/rebuild package: try to get base directory of file
			String sourcedir;
			try {
				sourcedir = JavaUtils.getBaseDirectory(ff.getAbsolutePath());
			}
			catch (IOException ioex) {
				Log.log(Log.ERROR, "JCompiler",
					"couldn't get base directory of file " + filename + ": " + ioex.toString());
				sourcedir = parent;
			}
			FileChangeMonitor monitor = new FileChangeMonitor(
				sourcedir, "java", outDirPath, "class");
			if (rebuild) {
				files = monitor.getAllFiles();
			} else {
				files = monitor.getChangedFiles();
			}
			if (files.length == 0) {
				sendMessage("jcompiler.msg.nofiles", new Object[] { sourcedir });
				return;
			}
			sendMessage("jcompiler.msg.compilefiles", new Object[] {
				new Integer(files.length),
				sourcedir,
				new Integer(outDirPath == null ? 0 : 1),
				outDirPath
			});
		} else {
			sendMessage("jcompiler.msg.compilefile", new Object[] {
				filename,
				new Integer(outDirPath == null ? 0 : 1),
				outDirPath
			});
			files = new String[] { filename };
		}

		String srcPath;
		srcPath = expandPath(jEdit.getProperty("jcompiler.sourcepath"));

		// CLASSPATH setting:
		String cp = jEdit.getProperty("jcompiler.classpath");
		cp = expandPath(cp);

		// Check if package dir should be added to classpath:
		if (jEdit.getBooleanProperty("jcompiler.addpkg2cp")) {
			try {
				String pkgName = JavaUtils.getPackageName(filename);
				Log.log(Log.DEBUG, this, "parent=" + parent + " pkgName=" + pkgName);
				// If no package stmt found then pkgName would be null
				if (parent != null) {
					if (pkgName == null) {
						cp = cp + File.pathSeparator + parent;
					} else {
						String pkgPath = pkgName.replace('.', File.separatorChar);
						Log.log(Log.DEBUG, this, "pkgPath=" + pkgPath);
						if (parent.endsWith(pkgPath)) {
							parent = parent.substring(0, parent.length() - pkgPath.length() - 1);
							cp = cp + File.pathSeparator + parent;
						}
					}
				}
			}
			catch (Exception exp) {
				exp.printStackTrace();
			}
		}

		String libPath = expandPath(jEdit.getProperty("jcompiler.libpath"));
		cp = cp + File.pathSeparator + expandLibPath(libPath);

		// Construct arguments for javac:
		String[] arguments = constructArguments(cp, srcPath, outDirPath, files);

		// Show command line:
		if (jEdit.getBooleanProperty("jcompiler.showcommandline", false)) {
			StringBuffer msg = new StringBuffer();
			for (int i = 0; i < arguments.length; ++i) {
				msg.append(' ');
				msg.append(arguments[i]);
			}
			sendMessage("jcompiler.msg.showcommandline", new Object[] {
				jEdit.getProperty("jcompiler.compiler.class"),
				jEdit.getProperty("jcompiler.compiler.method"),
				msg.toString()
			});
		}

		// Start the javac compiler...
		PrintStream origOut = System.out;
		PrintStream origErr = System.err;
		try {
			PrintStream ps = new PrintStream(pipe);
			System.setOut(ps);
			System.setErr(ps);
			// set "no exit" security manager to prevent javac from exiting:
			if (sm != null) sm.setAllowExit(false);
			// now invoke the compiler method:
			Object methodParams[] = new Object[] { arguments };
			compilerMethod.invoke(null, methodParams);
		}
		catch (InvocationTargetException invex) {
			// the invoked method has thrown an exception
			if (invex.getTargetException() instanceof SecurityException) {
				// don't do anything here because sun.tools.javac.Main.main will
				// always try and exit.
			} else {
				// oh my god, the method has thrown an exception, sheesh!
				Log.log(Log.ERROR, this,
					"The compiler method just threw a runtime exception. " +
					"Please report this to the current JCompiler maintainer."
				);
				invex.getTargetException().printStackTrace();
			}
		}
		catch (IllegalArgumentException illargex) {
			illargex.printStackTrace();
		}
		catch (IllegalAccessException illaccex) {
			illaccex.printStackTrace();
		}
		finally {
			// exit is allowed again
			if (sm != null) sm.setAllowExit(true);
			System.setOut(origOut);
			System.setErr(origErr);
		}

		sendMessage("jcompiler.msg.done");

		try { pipe.flush(); }
		catch (IOException ioex) { /* ignore */ }

		return;
	}


	public PipedOutputStream getOutputPipe() {
		return pipe;
	}


	private void sendMessage(String property) {
		sendString(jEdit.getProperty(property));
	}


	private void sendMessage(String property, Object[] args) {
		sendString(jEdit.getProperty(property, args));
	}


	private void sendString(String msg) {
		Log.log(Log.DEBUG, this, msg);
		byte[] bytes = msg.getBytes();

		if (pipe != null && bytes != null) {
			try {
				pipe.write(bytes, 0, bytes.length);
			}
			catch (IOException ioex) {
				// ignored
			}
			catch (NullPointerException ioex) {
				// this exception occurs sometimes on crappy VM implementations
				// like IBM JDK 1.1.8: pipe.write() throws it, if there's
				// no sink, maybe because the connection to the sink has not
				// yet been established or the the thread that creates the
				// sink has stopped.
				Log.log(Log.ERROR, this, "lost the output sink!");
			}
		}
	}


	private Method getCompilerMethod() {
		if (compilerMethod != null)
			return compilerMethod;

		String className = jEdit.getProperty("jcompiler.compiler.class");
		String methodName = jEdit.getProperty("jcompiler.compiler.method");
		Class compilerClass = null;

		try {
			try {
				compilerClass = Class.forName(className);
			}
			catch (ClassNotFoundException cnf) {
				if (!isOldJDK) {
					String home = System.getProperty("java.home");
					Log.log(Log.DEBUG, this, "java.home=" + home);
					if (home.toLowerCase().endsWith(File.separator + "jre"))
						home = home.substring(0, home.length() - 4);
					File toolsJar = new File(MiscUtilities.constructPath(home, "lib", "tools.jar"));
					if (toolsJar.exists()) {
						Log.log(Log.DEBUG, this, "loading class " + className + " from " + toolsJar.getCanonicalPath());
						ClassLoader cl = ZipClassLoader.getInstance(toolsJar);
						compilerClass = cl.loadClass(className);
					} else {
						Log.log(Log.ERROR, this, cnf);
						sendMessage("jcompiler.msg.nocompilermethod3", new Object[] { className, methodName, toolsJar });
						return null;
					}
				} else {
					Log.log(Log.ERROR, this, cnf);
					sendMessage("jcompiler.msg.nocompilermethod1", new Object[] { className, methodName });
					return null;
				}
			}

			String[] stringarray = new String[] {};
			Class stringarrayclass = stringarray.getClass();
			Class[] formalparams = new Class[] { stringarrayclass };
			compilerMethod = compilerClass.getMethod(methodName, formalparams);
		}
		catch (Exception e) {
			Log.log(Log.ERROR, this, e);
			Object[] args = new Object[] { className, methodName };
			if (isOldJDK) {
				sendMessage("jcompiler.msg.nocompilermethod1", args);
			} else {
				sendMessage("jcompiler.msg.nocompilermethod2", args);
			}
		}

		return compilerMethod;
	}


	private String[] constructArguments(String cp, String srcPath, String outDirPath, String[] files) {
		Vector vectorArgs = new Vector();

		if (cp != null && !cp.equals("")) {
			vectorArgs.addElement("-classpath");
			vectorArgs.addElement(cp);
		}

		if (srcPath != null && !srcPath.equals("") && !isOldJDK) {
			vectorArgs.addElement("-sourcepath");
			vectorArgs.addElement(srcPath);
		}

		if (jEdit.getBooleanProperty("jcompiler.genDebug"))
			vectorArgs.addElement("-g");

		if (jEdit.getBooleanProperty("jcompiler.genOptimized"))
			vectorArgs.addElement("-O");

		if (jEdit.getBooleanProperty("jcompiler.showdeprecated"))
			vectorArgs.addElement("-deprecation");

		if (jEdit.getBooleanProperty("jcompiler.specifyoutputdirectory")
			&& outDirPath != null && !outDirPath.equals("")) {
			vectorArgs.addElement("-d");
			vectorArgs.addElement(outDirPath);
		}

		String otherOptions = jEdit.getProperty("jcompiler.otheroptions");
		if (otherOptions != null) {
			StringTokenizer st = new StringTokenizer(otherOptions, " ");
			while (st.hasMoreTokens()) {
				vectorArgs.addElement(st.nextToken());
			}
		}

		for (int i = 0; i < files.length; ++i)
			vectorArgs.addElement(files[i]);

		String[] arguments = new String[vectorArgs.size()];
		vectorArgs.copyInto(arguments);
		return arguments;
	}


	/**
	 * Expand any directory in the path to include all jar or zip files in that directory;
	 *
	 * @param  path  the path to be expanded.
	 * @return the path with directories expanded.
	 */
	protected String expandLibPath(String path) {
		StringTokenizer st;
		File f;
		StringBuffer result;
		String token;

		st = new StringTokenizer(path, File.pathSeparator);
		result = new StringBuffer(path.length());

		while (st.hasMoreTokens()) {
			token = st.nextToken();
			f = new File(token);

			if (f.isDirectory())
				result.append(buildPathForDirectory(f));
			else
				result.append(token);

			if (st.hasMoreTokens())
				result.append(File.pathSeparator);
		}

		return result.toString();
	}


	/**
	 * build a path containing the jar and zip files in the directory represented by <code>f</code>.
	 *
	 * @param  f  a directory
	 * @return a classpath containg all the jar and zip files from the given directory.
	 */
	private String buildPathForDirectory(File f) {
		String[] archiveFiles = f.list(new FilenameFilter() {
			public boolean accept(File dir, String filename) {
				return filename.toLowerCase().endsWith(".jar") || filename.toLowerCase().endsWith(".zip");
			}
		});

		if (archiveFiles.length == 0)
			return "";

		StringBuffer result = new StringBuffer();
		for (int i = 0; i < archiveFiles.length; i++) {
			if (i > 0)
				result.append(File.pathSeparator);
			result.append(f.getPath());
			result.append(File.separator);
			result.append(archiveFiles[i]);
		}

		return result.toString();
	}


	private String expandPath(String path) {
		String basePath = jEdit.getProperty("jcompiler.basepath");

		if (basePath == null) {
			basePath = "";
		}

		return expandPath(path, basePath);
	}


	/**
	 * Expand any variables in path.
	 *
	 * NOTE: only looks for $basePath right now.
	 *
	 * @param  path  the path, possibly containing variables
	 * @return the path with all variables expanded.
	 */
	private String expandPath(String path, String basePath) {
		final String varName = "$basepath";
		String result = path;
		int matchIndex = result.indexOf(varName);

		if (basePath == null) {
			basePath = "";
		}

		while (matchIndex != -1) {
			result = result.substring(0, matchIndex) + basePath + result.substring(matchIndex + varName.length());
			matchIndex = result.indexOf(varName, matchIndex + varName.length());
		}

		return result;
	}

}

