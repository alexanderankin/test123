/*
 * (c) 2012 Marcelo Vanzin
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	 See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package common.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;

import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;

import org.gjt.sp.util.Log;

import common.threads.WorkerThreadPool;
import common.threads.WorkRequest;

/** A class that encapsulates running a process and processing its streams.
 *
 * <p>This class uses the CommonControls thread pool to monitor the process's
 * input and output streams, and allows the caller to easily parse the outputs
 * by using a visitor pattern.</p>
 *
 * @author	   Marcelo Vanzin
 * @version	   $Id$
 * @since	   CC 1.4.3
 */
public class ProcessExecutor
{

	private String[]		cmd;
	private String			cwd;
	private Map<String, String> envp;

	private Process			child;
	private StreamReader	stderr;
	private StreamReader	stdout;

	private List<VisitorEntry> visitors;

	private WorkRequest		stderrReq;
	private WorkRequest		stdoutReq;


	/**
	 * Creates a new executor for the given process with the given arguments.
	 *
	 * @param	cmd		The command to execute. The first argument is the
	 *					executable, which should be an absolute path, or be in
	 *					the user's PATH. The remaining are arguments to the
	 *					executable.
	 */
	public ProcessExecutor(String... cmd)
	{
		assert (cmd.length > 0) : "cmd cannot be empty";
		this.cmd = cmd;
		this.visitors = new LinkedList<VisitorEntry>();
	}


	/**
	 * Sets the working directory for the child process.
	 *
	 * @param	dir		Path to the working directory.
	 *
	 * @return this.
	 */
	public ProcessExecutor setDirectory(String cwd)
	{
		assert (child == null) : "can't set stream after child is executing.";
		this.cwd = cwd;
		return this;
	}

	/** Returns the command and arguments it is supposed to execute */
	public String[] getCmd() {
		return cmd;
	}
	
	/**
	 * Adds the current process's environment to the child.
	 * <p>
	 * This allows the current environment to be inherited by the child
	 * process, while still allowing customization using
	 * {@link #addEnv(String, String)}.
	 *
	 * @return this
	 */
	public ProcessExecutor addCurrentEnv()
	{
		assert (child == null) : "can't set stream after child is executing.";
		if (envp == null) {
			envp = new HashMap<String, String>();
		}
		envp.putAll(System.getenv());
		return this;
	}


	/**
	 * Adds a specific environment to use with the child process.
	 * <p>
	 * Calling this method makes the child ignore the current process's
	 * environment. See {@link #addCurrentEnv()} for how to inherit the
	 * current environment.
	 *
	 * @param	name	Name of variable to set.
	 * @param	value	Value of variable.
	 *
	 * @return this.
	 */
	public ProcessExecutor addEnv(String name,
								  String value)
	{
		assert (child == null) : "can't set stream after child is executing.";
		if (envp == null) {
			envp = new HashMap<String, String>();
		}
		envp.put(name, value);
		return this;
	}


	/**
	 * Adds a visitor to monitor the child process's output.
	 *
	 * @param	v	A visitor.
	 *
	 * @return this.
	 */
	public ProcessExecutor addVisitor(Visitor v)
	{
		assert (child == null) : "can't set stream after child is executing.";
		this.visitors.add(new VisitorEntry(v));
		return this;
	}

	/**
	 * Adds a visitor instance to use with the child process.
	 *
	 * @param	v	The visitor to use.
	 *
	 * @return this.
	 */
	public ProcessExecutor addVisitor(LineVisitor v)
	{
		assert (child == null) : "can't set stream after child is executing.";
		return addVisitor(new LineVisitorHelper(v));
	}

	/**
	 * Executes the child commmand, returning a process on success.
	 *
	 * @return A process on success, null otherwise.
	 */
	public Process start()
		throws IOException
	{
		assert (child == null) : "can't reuse ProcessExecutor instances";

		File fcwd = null;
		if (cwd != null) {
			fcwd = new File(cwd);
		}

		String[] env = null;
		if (envp != null) {
			int i = 0;
			env = new String[envp.size()];
			for (Map.Entry<String, String> e : envp.entrySet()) {
				env[i++] = String.format("%s=%s", e.getKey(), e.getValue());
			}
		}

		child = Runtime.getRuntime().exec(cmd, env, fcwd);

		stderr = new StreamReader(child.getErrorStream(), true);
		stdout = new StreamReader(child.getInputStream(), false);

		WorkRequest[] reqs =
			WorkerThreadPool.getSharedInstance().runRequests(
				new Runnable[] { stdout, stderr });
		stdoutReq = reqs[0];
		stderrReq = reqs[1];
		return child;
	}


	/**
	 * Waits for execution of the child process to finish.
	 *
	 * @return Exit code of the process.
	 */
	public int waitFor()
		throws InterruptedException
	{
		assert (child != null) : "command not yet executed";
		child.waitFor();
		stdoutReq.waitFor();
		stderrReq.waitFor();
		return child.exitValue();
	}


	/**
	 * Returns the exit value of the child process.
	 *
	 * @return Child's exit code.
	 */
	public int exitValue()
	{
		assert (child != null) : "command not yet executed";
		return child.exitValue();
	}


	private class StreamReader implements Runnable
	{

		private boolean			iserr;
		private InputStream		input;

		public StreamReader(InputStream in,
							boolean iserr)
		{
			this.input	= in;
			this.iserr	= iserr;
		}


		public void run()
		{
			try {
				byte[] buf = new byte[128];
				while (true) {
					int read = input.read(buf);
					byte[] param = buf;
					if (read == -1) {
						param = null;
						read = 0;
					}

					for (VisitorEntry entry : visitors) {
						entry.process(param, read, iserr);
					}

					if (param == null) {
						break;
					}
				}
			} catch (IOException ioe) {
				Log.log(Log.DEBUG, this, ioe);
			}
		}

	}

	/**
	 * Interface defining a visitor that will be called with data read
	 * from the child process's output.
	 */
	public static interface Visitor {

		/**
		 * Called when data is read from the process output.
		 * <p>
		 * This method is called with <i>buf</i> set to <i>null</i> on EOF.
		 *
		 * @param buf Data from the output.
		 * @param len How much data in the buffer is valid.
		 * @param isError Whether data is from error output.
		 * @return Whether to continue reading the output.
		 */
		public boolean process(byte[] buf, int len, boolean isError);

	}

	/**
	 * Interface defining a visitor that will be called with lines of text
	 * read from the process output.
	 */
	public static interface LineVisitor {

		/**
		 * Called when a line of text is read from the process output.
		 *
		 * @param line The line from the output.
		 * @param isError Whether line is from error output.
		 * @return Whether to continue reading the output.
		 */
		public boolean process(String line, boolean isError);

	}

	/**
	 * A visitor that just buffers the output of the process and makes it
	 * available for retrieval.
	 */
	public static class BufferingVisitor implements Visitor {

		private final ByteArrayOutputStream output;
		private final ByteArrayOutputStream error;

		public static enum Streams {
			BOTH,
			STDOUT,
			STDERR,
			MERGE,
		}

		public BufferingVisitor() {
			this(Streams.BOTH);
		}

		public BufferingVisitor(Streams stream) {
			switch (stream) {
			case BOTH:
				output = new ByteArrayOutputStream();
				error = new ByteArrayOutputStream();
				break;

			case STDOUT:
				output = new ByteArrayOutputStream();
				error = null;
				break;

			case STDERR:
				error = new ByteArrayOutputStream();
				output = null;
				break;

			case MERGE:
				error = new ByteArrayOutputStream();
				output = error;
				break;

			default:
				throw new RuntimeException("W.T.F.");
			}
		}

		public boolean process(byte[] buf, int len, boolean isError) {
			ByteArrayOutputStream target = isError ? error : output;
			if (target != null && buf != null) {
				target.write(buf, 0, len);
			}
			return true;
		}

		public byte[] getOutput() {
			return output.toByteArray();
		}

		public String getOutputString() {
			byte[] data = getOutput();
			return new String(data, 0, data.length);
		}

		public byte[] getError() {
			return error.toByteArray();
		}

		public String getErrorString() {
			byte[] data = getError();
			return new String(data, 0, data.length);
		}

	}

	/**
	 * Visitor implementation that buffers lines of text to feed to
	 * an underlying LineVisitor.
	 */
	private static class LineVisitorHelper implements Visitor {

		private final LineVisitor visitor;
		private final StringBuilder error;
		private final StringBuilder output;

		LineVisitorHelper(LineVisitor visitor) {
			this.visitor = visitor;
			this.error = new StringBuilder();
			this.output = new StringBuilder();
		}

		public boolean process(byte[] buf, int len, boolean isError) {
			StringBuilder target = isError ? error : output;

			if (buf != null) {
				for (int i = 0; i < len; i++) {
					target.append((char)buf[i]);
				}
			} else if (target.length() > 0) {
				target.append("\n");
			}

			int idx;
			while ((idx = target.indexOf("\n")) >= 0) {
				int cut = idx;
				if (idx > 0 && target.charAt(idx - 1) == '\r') {
					// ah, windows.
					cut--;
				}
				String line = target.substring(0, cut);
				target.delete(0, idx + 1);
				if (!visitor.process(line, isError)) {
					return false;
				}
			}

			return true;
		}

	}


	private static class VisitorEntry {

		public final Visitor visitor;
		private boolean enabled;

		VisitorEntry(Visitor v) {
			this.visitor = v;
			this.enabled = true;
		}

		public void process(byte[] buf, int len, boolean isError) {
			if (enabled) {
				enabled = visitor.process(buf, len, isError);
			}
		}

	}
}

