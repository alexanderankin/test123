/*
 * JCompiler.java - a wrapper around sun.tools.javac.Main
 * (c) 1999, 2000 - Kevin A. Burton and Aziz Sharif
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


import java.lang.reflect.*;
import java.util.StringTokenizer;
import java.util.Vector;
import java.io.*;
import javax.swing.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.util.*;
import buildtools.*;


/**
 * The class that performs the javac compile run.
 */
public class JCompiler
{

	/** true, if JDK version is older than 1.2 */
	private final static boolean isOldJDK = (MiscUtilities.compareStrings(System.getProperty("java.version"), "1.2", true) < 0);

	/** true, if JDK version is 1.3 or higher */
	private final static boolean isModernJDK = (MiscUtilities.compareStrings(System.getProperty("java.version"), "1.3", true) >= 0);

	private Class compilerClass;
	private Constructor compilerConstructor;
	private Method compilerMethod;
	private JCompilerOutput output;
	private View view;
	private Buffer buffer;
	private int threadDoneCount;


	public JCompiler(JCompilerOutput output, View view, Buffer buffer)
	{
		this.output = output;
		this.view = view;
		this.buffer = buffer;

		initCompiler();
	}


	/**
	 * Compile a file with VM builtin compiler, using either
	 * <code>public boolean sun.tools.javac.Main.compile(String[])</code> or
	 * <code>public int com.sun.tools.javac.Main.compile(String[])</code>.
	 *
	 * @param pkgCompile  if true, JCompiler tries to locate the base directory
	 *                    of the package of the current file and compiles
	 *                    every outdated file.
	 * @param rebuild     if true, JCompiler compiles <i>every</i> file in the
	 *                    package hierarchy.
	 * @return            true if the compiler was actually started,
	 *                    false if the compiler wasn't started due to some
	 *                    error.
	 */
	public boolean compile(boolean pkgCompile, boolean rebuild)
	{
		if (compilerMethod == null)
		{
			// compiler method not found.
			// initCompiler() should have already printed an error,
			// so we can silently return here.
			return false;
		}

		// Check output directory:
		String outDir = getOutputDirectory();

		if (outDir != null)
		{
			File fOutDir = new File(outDir);
			try
			{
				// Canonize outDir:
				outDir = fOutDir.getCanonicalPath();
			}
			catch (IOException ioex)
			{
				printError("jcompiler.msg.errorOutputDir", new Object[] { outDir, ioex });
				return false;
			}

			if (fOutDir.exists())
			{
				if (!fOutDir.isDirectory())
				{
					printError("jcompiler.msg.noOutputDir", new Object[] {outDir });
					return false;
				}
			}
			else
			{
				// Ask whether output dir should be created:
				int reply = JOptionPane.showConfirmDialog(view,
					jEdit.getProperty("jcompiler.msg.createOutputDir.message", new Object[] { outDir }),
					jEdit.getProperty("jcompiler.msg.createOutputDir.title"),
					JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE);

				if (reply != JOptionPane.YES_OPTION)
					return false;

				if (!fOutDir.mkdirs())
				{
					GUIUtilities.message(view, "jcompiler.msg.errorCreateOutputDir", null);
					return false;
				}
			}
		}

		// Check for auto save / auto save all
		saveBuffers(pkgCompile);

		// Get files to compile:
		String filename = buffer.getPath();
		String sourceBaseDir;
		String[] files;

		if (pkgCompile)
		{
			sourceBaseDir = getSourceBaseDir(filename);
			files = getFiles(sourceBaseDir, outDir, rebuild);
		}
		else
		{
			sourceBaseDir = new File(filename).getParent();
			files = new String[] { filename };
		}

		if (files.length == 0)
		{
			// No files to compile:
			printInfo("jcompiler.msg.nofiles", new Object[] { sourceBaseDir });
			return false;
		}

		// Show files to compile:
		printInfo("jcompiler.msg.compilefiles",
			new Object[] {
				new Integer(files.length),
				filename,
				sourceBaseDir,
				new Integer(outDir == null ? 0 : 1),
				outDir
			}
		);

		// Construct arguments for javac:
		String[] arguments = constructArguments(getClassPath(filename), getSourcePath(), outDir, files);

		// Show command line:
		if (jEdit.getBooleanProperty("jcompiler.showcommandline", false))
		{
			StringBuffer msg = new StringBuffer("javac");
			for (int i = 0; i < arguments.length; ++i)
			{
				msg.append(' ');
				boolean argNeedsQuote = arguments[i].indexOf(' ') >= 0;
				if (argNeedsQuote && arguments[i].charAt(0) != '"')
					msg.append('"');
				msg.append(arguments[i]);
				if (argNeedsQuote && arguments[i].charAt(arguments[i].length() - 1) != '"')
					msg.append('"');
			}
			printInfo("jcompiler.msg.commandline", new Object[] { msg.toString() });
		}

		// Start the compiler:
		invokeCompiler(arguments);
		return true;
	}


	/**
	 * Compile with sun.tools.javac.Main.compile(), using the specified
	 * command line arguments.
	 *
	 * @param arguments  the command line arguments
	 * @return           true if the compiler was actually started,
	 *                   false if the compiler wasn't started due to some
	 *                   error.
	 */
	public boolean compile(String[] arguments)
	{
		// Search for the compiler method:
		initCompiler();

		if (compilerMethod == null)
			return false; // compiler method not found

		// Start the compiler:
		invokeCompiler(arguments);
		return true;
	}


	private void initCompiler()
	{
		boolean isModern = false;
		String compilerClassname = "sun.tools.javac.Main";

		if (isModernJDK && jEdit.getBooleanProperty("jcompiler.modernCompiler", true))
		{
			compilerClassname = "com.sun.tools.javac.Main";
			isModern = true;
		}

		// Find compiler class:
		try
		{
			compilerClass = Class.forName(compilerClassname);

			// Get constructor:
			// The classic compiler constructor has signature Main(OutputStream,String).
			// The modern compiler constructor has signature Main().
			Class[] constructorSignature = isModern ? new Class[] {}
				: new Class[] {OutputStream.class, String.class};
			compilerConstructor = compilerClass.getConstructor(constructorSignature);

			// Get the method "compile(String[] arguments)".
			// The method has the same signature on the classic and modern
			// compiler, but they have different return types (boolean/int).
			// Since the return type is ignored here, it doesn't matter.
			Class[] methodSignature = { String[].class };
			compilerMethod = compilerClass.getMethod("compile", methodSignature);
		}
		catch (ClassNotFoundException cnf)
		{
			Log.log(Log.ERROR, this, cnf);
			printError("jcompiler.msg.class_not_found", new Object[] { compilerClassname });
		}
		catch (Exception e)
		{
			Log.log(Log.ERROR, this, e);
			printError("jcompiler.msg.class_not_found_other_error", new Object[] { compilerClassname, e });
		}
	}


	private void invokeCompiler(String[] arguments)
	{
		threadDoneCount = 0;
		PrintStream origOut = System.out;
		PrintStream origErr = System.err;
		PipedOutputStream pipeComp = new PipedOutputStream();
		PipedOutputStream pipeOut = new PipedOutputStream();
		PipedOutputStream pipeErr = new PipedOutputStream();
		OutputThread threadComp = new OutputThread("compiler output", pipeComp);
		OutputThread threadOut = new OutputThread("stdout", pipeOut);
		OutputThread threadErr = new OutputThread("stderr", pipeErr);

		try
		{
			// redirect stdout/stderr:
			System.setOut(new PrintStream(pipeOut, true));
			System.setErr(new PrintStream(pipeErr, true));

			// instantiate a new compiler class with the constructor arguments:
			Object compiler;
			if (isModernJDK && jEdit.getBooleanProperty("jcompiler.modernCompiler", true))
			{
				// modern compiler constructor needs no arguments:
				compiler = compilerConstructor.newInstance(null);
			}
			else
			{
				// classic compiler constructor needs two arguments:
				// an OutputStream for compiler error output, and a String
				// with the program name, which is always "javac":
				compiler = compilerConstructor.newInstance(new Object[] { pipeComp, "javac" });
			}

			// invoke the method 'compile(String[] arguments)' on the compiler instance.
			// Note: the return value of the compile method is ignored.
			compilerMethod.invoke(compiler, new Object[] { arguments });
		}
		catch (InvocationTargetException invex)
		{
			// the invoked method itself has thrown an exception
			Throwable targetException = invex.getTargetException();
			if (targetException instanceof InterruptedException)
			{
				Log.log(Log.DEBUG, this, "JCompiler interrupted.");
				printError("jcompiler.msg.interrupted");
			}
			else
			{
				Log.log(Log.ERROR, this, "The compiler method itself just threw a runtime exception:");
				Log.log(Log.ERROR, this, targetException);
				Object[] args = new Object[] { compilerClass, targetException };
				printError("jcompiler.msg.compilermethod_exception", args);
			}
		}
		catch (Exception e)
		{
			if (e instanceof InterruptedException)
			{
				Log.log(Log.DEBUG, this, "JCompiler interrupted.");
				printError("jcompiler.msg.interrupted");
			}
			else
			{
				Log.log(Log.ERROR, this, e);
				Object[] args = new Object[] { compilerClass, e };
				printError("jcompiler.msg.compilermethod_exception", args);
			}
		}
		finally
		{
			// close pipes, this causes the OutputThreads to terminate:
			try { pipeComp.close(); } catch (IOException ioex) { Log.log(Log.WARNING, this, ioex); }
			try { pipeOut.close(); } catch (IOException ioex) { Log.log(Log.WARNING, this, ioex); }
			try { pipeErr.close(); } catch (IOException ioex) { Log.log(Log.WARNING, this, ioex); }
			// restore stdout/err:
			System.setOut(origOut);
			System.setErr(origErr);
			// free some memory:
			System.gc();
		}
	}


	private synchronized void threadDone()
	{
		++threadDoneCount;
		if (threadDoneCount == 3)
		{
			printInfo("jcompiler.msg.done");
			output.outputDone();
		}
	}


	private void print(String property) { output.outputText(jEdit.getProperty(property)); }
	private void print(String property, Object[] args) { output.outputText(jEdit.getProperty(property, args)); }
	private void printInfo(String property) { output.outputInfo(jEdit.getProperty(property)); }
	private void printInfo(String property, Object[] args) { output.outputInfo(jEdit.getProperty(property, args)); }
	private void printError(String property) { output.outputError(jEdit.getProperty(property)); }
	private void printError(String property, Object[] args) { output.outputError(jEdit.getProperty(property, args)); }


	private String[] constructArguments(String cp, String srcPath, String outDir, String[] files)
	{
		Vector vectorArgs = new Vector();

		if (cp != null && !cp.equals(""))
		{
			vectorArgs.addElement("-classpath");
			vectorArgs.addElement(cp);
		}

		if (srcPath != null && !srcPath.equals("") && !isOldJDK)
		{
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
			&& outDir != null && !outDir.equals(""))
		{
			vectorArgs.addElement("-d");
			vectorArgs.addElement(outDir);
		}

		String otherOptions = jEdit.getProperty("jcompiler.otheroptions");
		if (otherOptions != null)
		{
			StringTokenizer st = new StringTokenizer(otherOptions, " ");
			while (st.hasMoreTokens())
				vectorArgs.addElement(st.nextToken());
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
	private static String expandLibPath(String path)
	{
		StringTokenizer st;
		File f;
		StringBuffer result;
		String token;

		if (path == null || path.length() == 0)
			return "";

		st = new StringTokenizer(path, File.pathSeparator);
		result = new StringBuffer(path.length());

		while (st.hasMoreTokens())
		{
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
	 * build a path containing the jar and zip files in the directory
	 * represented by <code>f</code>.
	 *
	 * @param  f  a directory
	 * @return a classpath containg all the jar and zip files from the given directory.
	 */
	private static String buildPathForDirectory(File f)
	{
		String[] archiveFiles = f.list(new FilenameFilter()
		{
			public boolean accept(File dir, String filename)
			{
				return filename.toLowerCase().endsWith(".jar") || filename.toLowerCase().endsWith(".zip");
			}
		});

		if (archiveFiles.length == 0)
			return "";

		StringBuffer result = new StringBuffer();
		for (int i = 0; i < archiveFiles.length; i++)
		{
			if (i > 0)
				result.append(File.pathSeparator);
			result.append(f.getPath());
			result.append(File.separator);
			result.append(archiveFiles[i]);
		}

		return result.toString();
	}


	/**
	 * Expand any variables in the specified string.
	 *
	 * NOTE: The current implementation only looks for variables named
	 * <code>$basePath</code> and replaces them with the contents of the
	 * property "jcompiler.basepath".
	 *
	 * @param  s  the string, possibly containing variables
	 * @return the string with all variables expanded.
	 */
	public static String expandVariables(String s)
	{
		if (s == null || s.length() == 0)
			return "";

		String basePath = jEdit.getProperty("jcompiler.basepath", "").trim();

		if (basePath.length() == 0)
			return s;

		return replaceAll(s, "$basepath", basePath);
	}


	/**
	 * Returns the string with all occurences of the specified variable replaced
	 * by the specified value.
	 * @param  s  the string
	 * @param  variable  the variable to look for; must begin with "$"
	 * @param  value  to value to be set in for the variable
	 * @return the modified string.
	 */
	private static String replaceAll(String s, String variable, String value)
	{
		if (s == null || s.length() == 0)
			return s;

		if (variable == null || variable.length() == 0)
			return s;

		if (value == null)
			value = "";

		String result = s;
		int matchIndex = result.indexOf(variable);
		int vlen = variable.length();

		while (matchIndex != -1)
		{
			result = result.substring(0, matchIndex) + value + result.substring(matchIndex + vlen);
			matchIndex = result.indexOf(variable, matchIndex + vlen);
		}

		return result;
	}


	private void saveBuffers(boolean pkgCompile)
	{
		String prop = pkgCompile ? "jcompiler.javapkgcompile.autosave" : "jcompiler.javacompile.autosave";
		String which = jEdit.getProperty(prop, "ask");

		if (which.equals("current"))
			saveCurrentBuffer();
		else if (which.equals("all"))
			saveAllBuffers();
		else if (which.equals("ask"))
			saveBuffersAsk(pkgCompile);
		// do nothing on which == "no"
	}


	/** Save current buffer, if dirty. */
	private void saveCurrentBuffer()
	{
		if (buffer.isDirty())
		{
			buffer.save(view, null);
			VFSManager.waitForRequests();
		}
	}


	/** Save all buffers without asking. */
	private void saveAllBuffers()
	{
		boolean savedSomething = false;
		Buffer[] buffers = jEdit.getBuffers();

		for(int i = 0; i < buffers.length; i++)
		{
			if (buffers[i].isDirty())
			{
				buffers[i].save(view, null);
				savedSomething = true;
			}
		}

		if (savedSomething)
			VFSManager.waitForRequests();
	}


	/** Ask for unsaved changes and save. */
	private void saveBuffersAsk(boolean pkgCompile)
	{
		boolean savedSomething = false;
		if (pkgCompile)
		{
			// Check if there are any unsaved buffers:
			Buffer[] buffers = jEdit.getBuffers();
			boolean dirty = false;
			for(int i = 0; i < buffers.length; i++)
				if (buffers[i].isDirty())
					dirty = true;

			if (dirty)
			{
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
						{
							buffers[i].save(view, null);
							savedSomething = true;
						}
			}
		}
		else // !pkgCompile
		{
			// Check if current buffer is unsaved:
			if (buffer.isDirty())
			{
				int result = JOptionPane.showConfirmDialog(view,
					jEdit.getProperty("jcompiler.msg.saveChanges.message", new Object[] { buffer.getName() }),
					jEdit.getProperty("jcompiler.msg.saveChanges.title"),
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.WARNING_MESSAGE);
				if (result == JOptionPane.CANCEL_OPTION)
					return;
				if (result == JOptionPane.YES_OPTION)
				{
					buffer.save(view, null);
					savedSomething = true;
				}
			}
		}

		if (savedSomething)
			VFSManager.waitForRequests();
	}


	/**
	 * Returns the classpath, made up of the expanded classpath
	 * property (jcompiler.classpath), the expanded output directory,
	 * and the expanded libpath property (jcompiler.libpath).
	 *
	 * NOTE: this version of the getClassPath() method does not
	 * consider the jcompiler.addpkg2cp option.  If you need this option
	 * use the getClassPath(String) method.
	 *
	 * @see #getClassPath(String)
	 */
	public static String getClassPath()
	{
		String cp = expandVariables(jEdit.getProperty("jcompiler.classpath"));
		String outputDir = getOutputDirectory();

		if (outputDir != null)
			cp = appendClassPath(cp, outputDir);

		cp = appendClassPath(cp, getRequiredLibraryPath());

		return cp;
	}


	/**
	 * Returns the classpath made up of getClassPath() + the package
	 * directory of the class to be compiled if the
	 * jcompiler.addpkg2cp option is turned on.
	 *
	 * @param filename the filename of the class for determining the package.
	 */
	public static String getClassPath(String filename)
	{
		String cp = getClassPath();

		// Check if package dir should be added to classpath:
		if (jEdit.getBooleanProperty("jcompiler.addpkg2cp"))
		{
			try
			{
				String pkgName = JavaUtils.getPackageName(filename);
				String parent = new File(filename).getParent();

				// If no package stmt found then pkgName would be null
				if (parent != null)
				{
					if (pkgName == null)
						cp = appendClassPath(cp, parent);
					else
					{
						String pkgPath = pkgName.replace('.', File.separatorChar);

						if (parent.endsWith(pkgPath))
						{
							parent = parent.substring(0, parent.length() - pkgPath.length() - 1);
							cp = appendClassPath(cp, parent);
						}
					}
				}
			}
			catch (Exception ex)
			{
				Log.log(Log.WARNING, JCompiler.class, ex);
			}
		}

		return cp;
	}


	/**
	 * @return the sourcepath defined by jcompiler.sourcepath, with
	 * variable expansion.
	 */
	public static String getSourcePath()
	{
		return expandVariables(jEdit.getProperty("jcompiler.sourcepath"));
	}


	/**
	 * Returns the expanded value of the jcompiler.libpath property,
	 * which is the required library path.
	 */
	public static String getRequiredLibraryPath()
	{
		// expand variables first
		String libPath = expandVariables(jEdit.getProperty("jcompiler.libpath"));
		// expand directories to get all jars
		return expandLibPath(libPath);
	}


	/**
	 * Returns the output directory defined by jcompiler.outputdirectory, with
	 * variable expansion.
	 * If the jcompiler.specifyoutputdirectory option is not turned on,
	 * null is returned.
	 *
	 * @return the expanded output directory or null if the option is turned off.
	 */
	public static String getOutputDirectory()
	{
		String outDir = null;
		if (jEdit.getBooleanProperty( "jcompiler.specifyoutputdirectory"))
			outDir = expandVariables(jEdit.getProperty("jcompiler.outputdirectory"));
		return outDir;
	}


	/**
	 * Scans the named file for the <code>package</code> statement and tries
	 * to determine the base directory of the source tree where the file
	 * resides.
	 *
	 * @param filename  the filename of the class for determining the package.
	 * @return a path name denoting the base directory of the java source
	 *    file, or simply the parent directory containing the file, if the
	 *    base directory could not be determined.
	 */
	public static String getSourceBaseDir(String filename)
	{
		File file = new File(filename);
		String parent = file.getParent();
		String sourceDir;

		try
		{
			sourceDir = JavaUtils.getBaseDirectory(file.getAbsolutePath());
		}
		catch (IOException ioex)
		{
			Log.log(Log.ERROR, "JCompiler",
				"couldn't get base directory of file " + filename
				+ ": " + ioex.toString());
			sourceDir = parent;
			Log.log(Log.DEBUG, "JCompiler", "using " + parent);
		}

		return sourceDir;
	}


	/**
	 * Get the files that should be compiled.
	 * Recursively scans the base directory and all of its subdirectories
	 * for Java source files.
	 *
	 * @param baseDir  the base directory, usually determined by
	 *    <code>getSourceBaseDir(String)</code>.
	 * @param destDir  the destination directory, only used if the parameter
	 *    <code>all</code> is true.
	 * @param all  if true, returns <i>all</i> files of the source tree;
	 *    if false, returns only outdated files by comparing the source tree
	 *    with the tree rooted at the destination directory.
	 * @return an array of (all or outdated) file names, denoted by absolute
	 *    paths.
	 * @see #getSourceBaseDir(String)
	 */
	public static String[] getFiles(String baseDir, String destDir, boolean all)
	{
		FileChangeMonitor monitor = new FileChangeMonitor(baseDir, "java", destDir, "class");
		return all ? monitor.getAllFiles() : monitor.getChangedFiles();
	}


	/**
	 * Append <code>additionalPath</code> to <code>classPath</code>, safely
	 * handling classPath or additionalPath being empty.
	 *
	 * @param classPath the classPath to append to.
	 * @param additionalPath the path to be appended to <code>classPath</code>
	 * @return the new classpath, consisting of classPath + pathSseparator
	 *    + additionalPath
	 */
	public static String appendClassPath(String classPath, String additionalPath)
	{
		String result;

		if (additionalPath.length() == 0)
			result = classPath; // nothing to append
		else if (classPath.length() > 0)
			result = classPath + File.pathSeparator + additionalPath;
		else
			result = additionalPath; // nothing to append to

		return result;
	}


	/**
	 * This class monitors output created by a PipedOutputStream.
	 * Note: this thread starts itself on construction.
	 */
	class OutputThread extends Thread
	{
		private BufferedReader buf;

		OutputThread(String name, PipedOutputStream outpipe)
		{
			super("OutputThread for " + name);
			this.setDaemon(true);
			//this.setPriority(NORM_PRIORITY - 1);

			try
			{
				buf = new BufferedReader(new InputStreamReader(new PipedInputStream(outpipe)));
				this.start();
			}
			catch (IOException ioex)
			{
				Log.log(Log.ERROR, this, ioex);
			}
		}

		public void run()
		{
			String line;
			try
			{
				while ((line = buf.readLine()) != null)
					output.outputText(line);
				Log.log(Log.DEBUG, JCompiler.this, this.toString() + " ends.");
				buf.close();
				threadDone();
			}
			catch (IOException ioex)
			{
				Log.log(Log.ERROR, this, ioex);
			}
		}
	}

}

