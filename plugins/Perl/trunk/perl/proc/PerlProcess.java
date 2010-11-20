package perl.proc;

import perl.launch.LaunchConfiguration;
import perl.options.GeneralOptionPane;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.Map.Entry;

import javax.swing.JOptionPane;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

import debugger.jedit.Plugin;

public class PerlProcess {
	
	public interface Reader {
		boolean ready()  throws IOException;
		String readLine() throws IOException;
	}
	public interface Writer {
		void write(String s) throws IOException;
	}

	private static final String PAUSE_NOT_IMPLEMENTED = Plugin.MESSAGE_PREFIX +
		"pause_not_implemented";
	private static final String PROGRAM_COULD_NOT_BE_DETECTED = Plugin.MESSAGE_PREFIX +
		"program_not_detected";
	
	private Process p = null;
	private String program = null;
	
	Reader outputReader = null;
	Reader errorReader = null;
	Writer inputWriter = null;
	Reader programOutputReader = null;
	Reader programErrorReader = null;
	Writer programInputWriter = null;
	
    public PerlProcess(LaunchConfiguration config) throws IOException {
    	program = config.getProgram();
		String command = jEdit.getProperty(GeneralOptionPane.PERL_PATH_PROP) +
			" --interpreter=mi " + program;
		String cwd = config.getDirectory();
		String [] env = config.getEnvironmentArray();
		if (cwd == null || cwd.length() == 0)
			cwd = ".";
		File dir = new File(cwd);
		p = Runtime.getRuntime().exec(command, env, dir);
		outputReader = programOutputReader =
			new ProcessReader(p.getInputStream());
		errorReader = programErrorReader =
			new ProcessReader(p.getErrorStream());
		inputWriter = programInputWriter =
			new ProcessWriter(p.getOutputStream());
    }
	public void destroy() {
    	if (p != null) {
    		p.destroy();
    		p = null;
    	}
	}
	public void pause() {
		View v = jEdit.getActiveView();
		if (! jEdit.getBooleanProperty(GeneralOptionPane.USE_EXTERNAL_COMMANDS_PROP)) {
			JOptionPane.showMessageDialog(v, jEdit.getProperty(PAUSE_NOT_IMPLEMENTED));
			return;
		}
		Vector<String> procs = getProcesses();
		HashMap<Integer, Integer> progPid = findProcessId(procs, program);
		int pid;
		// if a single program process exists, signal it
		if (progPid.size() == 1) {
			pid = progPid.keySet().iterator().next().intValue();
			//System.err.println("A single program process is running: " + pid);
		}
		else {
			// otherwise, look for the program as a perl child
			//System.err.println("Multiple program processes are running");
			HashMap<Integer, Integer> perlPid = findProcessId(procs, jEdit.getProperty(GeneralOptionPane.PERL_PATH_PROP));
			// if a single perl debugger process exists, look for its child
			if (perlPid.size() == 1) {
				int gdbid = perlPid.keySet().iterator().next().intValue();
				//System.err.println("A single gdb process is running: " + gdbid);
				pid = findProcChild(gdbid, progPid);
				//System.err.println("Program child of gdb is: " + pid);
			} else {
				// otherwise, look for gdb as a jedit child
				//System.err.println("Multiple gdb processes are running");
				HashMap<Integer, Integer> jeditPid = findProcessIdRegExp(procs, ".*java.*jedit\\.jar.*");
				if (jeditPid.size() == 1) {
					int jeditpid = jeditPid.keySet().iterator().next().intValue();
					//System.err.println("A single jedit process is running: " + jeditpid);
					int perlid = findProcChild(jeditpid, perlPid);
					//System.err.println("Gdb child of jedit is: " + gdbid);
					pid = findProcChild(perlid, progPid);
					//System.err.println("Program child of gdb is: " + pid);
				} else {
					// Give up... multiple jedit, gdb and program processes are
					// running, can't identify process to signal
					JOptionPane.showMessageDialog(v,
							jEdit.getProperty(PROGRAM_COULD_NOT_BE_DETECTED));
					return;
				}
			}
		}
		if (pid == 0) {
			// Couldn't find program
			JOptionPane.showMessageDialog(v,
					jEdit.getProperty(PROGRAM_COULD_NOT_BE_DETECTED));
			return;
		}
		//System.err.println("Sending SIGINT to program process: " + pid);
		signal(pid, 2);
	}
	private void signal(int pid, int i) {
		try {
			Runtime.getRuntime().exec("kill -s " + i + " " + pid);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private int findProcChild(int parentid, HashMap<Integer, Integer> children) {
		Iterator<Entry<Integer,Integer>> it = children.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer,Integer> entry = it.next();
			if (entry.getValue().intValue() == parentid)
				return entry.getKey().intValue();
		}
		return 0;
	}
	private HashMap<Integer, Integer> findProcessIdRegExp(Vector<String> procs, String regexp) {
		HashMap<Integer, Integer> pids = new HashMap<Integer, Integer>();
		for (int i = 0; i < procs.size(); i++) {
			String p = procs.get(i);
			if (! p.matches(regexp))
				continue;
			String [] fields = p.split("\\s+");
			pids.put(Integer.parseInt(fields[1]), Integer.parseInt(fields[2]));
		}
		return pids;
	}
	private HashMap<Integer, Integer> findProcessId(Vector<String> procs, String proc) {
		HashMap<Integer, Integer> pids = new HashMap<Integer, Integer>();
		for (int i = 0; i < procs.size(); i++) {
			String p = procs.get(i);
			if (! p.contains(proc))
				continue;
			String [] fields = p.split("\\s+");
			pids.put(Integer.parseInt(fields[1]), Integer.parseInt(fields[2]));
		}
		return pids;
	}

	private Vector<String> getProcesses() {
		Vector<String> procs = new Vector<String>();
		String command = "ps -f";
		BufferedReader br = null;
		try {
			p = Runtime.getRuntime().exec(command);
			br = new BufferedReader(
					new InputStreamReader(p.getInputStream()));
			String line;
			while ((line = br.readLine()) != null)
				procs.add(line);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(jEdit.getActiveView(),
					"Failed to read process list from 'ps'.");
			e.printStackTrace();
		} finally {
			if (br != null)
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return procs;
	}
	// Common communication
	public Reader getGdbOutputReader() {
		return outputReader;
    }
    public Reader getGdbErrorReader() {
    	return errorReader;
    }
    public Writer getGdbInputWriter() {
    	return inputWriter;
    }
    public Reader getProgramOutputReader() {
    	return programOutputReader;
    }
    public Reader getProgramErrorReader() {
    	return programErrorReader;
    }
    public Writer getProgramInputWriter() {
    	return programInputWriter;
    }
    
	static public class ProcessWriter extends BufferedWriter implements Writer {
		public ProcessWriter(OutputStream outputStream) {
			super(new OutputStreamWriter(outputStream));
		}
		@Override
		public void write(String str) throws IOException {
			super.write(str);
			super.flush();
		}
	}
	static public class ProcessReader extends BufferedReader implements Reader {
		public ProcessReader(InputStream inputStream) {
			super(new InputStreamReader(inputStream));
		}
	}

	/*
	 *  Method to extract a file from the jar.
	private void extractFileFromJar(String fileName) {
		String jarPath = jEdit.getPlugin(Plugin.class.getName()).getPluginJAR().getPath();
		File jarFile = new File(jarPath);
		File jarDir = jarFile.getParentFile();
		String jarCopy = new File(jarDir, fileName).getPath(); 
		JarFile jar = null;
		try {
			jar = new JarFile(jarPath);
			JarEntry entry = jar.getJarEntry(fileName);
			if (entry != null) {
				InputStream entryStream = jar.getInputStream(entry);
				try {
					FileOutputStream file = new FileOutputStream(jarCopy);
					try {
						byte[] buffer = new byte[1024];
						int bytesRead;
						while ((bytesRead = entryStream.read(buffer)) != -1) {
							file.write(buffer, 0, bytesRead);
						}
					}
					finally {
						file.close();
					}
				}
				finally {
					entryStream.close();
				}
			} else {
				System.err.println("Could not find " + fileName + " in " + jarPath);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		finally {
			try {
				jar.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.load(jarCopy);
	}
	*/
}
