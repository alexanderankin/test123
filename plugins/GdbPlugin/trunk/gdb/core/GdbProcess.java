package gdb.core;

import gdb.launch.LaunchConfiguration;
import gdb.options.GeneralOptionPane;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.gjt.sp.jedit.jEdit;

import debugger.jedit.Plugin;

public class GdbProcess {
	
	public interface Reader {
		boolean ready()  throws IOException;
		String readLine() throws IOException;
	}
	public interface Writer {
		void write(String s) throws IOException;
	}
	
	private Process p = null;

	Reader gdbOutputReader = null;
	Reader gdbErrorReader = null;
	Writer gdbInputWriter = null;
	Reader programOutputReader = null;
	Reader programErrorReader = null;
	Writer programInputWriter = null;
	
    public GdbProcess(LaunchConfiguration config) throws IOException {
		String command = jEdit.getProperty(GeneralOptionPane.GDB_PATH_PROP) +
			" --interpreter=mi " + config.getProgram();
		String cwd = config.getDirectory();
		String [] env = config.getEnvironmentArray();
		if (cwd == null || cwd.length() == 0)
			cwd = ".";
		File dir = new File(cwd);
		p = Runtime.getRuntime().exec(command, env, dir);
		gdbOutputReader = programOutputReader =
			new ProcessReader(p.getInputStream());
		gdbErrorReader = programErrorReader =
			new ProcessReader(p.getErrorStream());
		gdbInputWriter = programInputWriter =
			new ProcessWriter(p.getOutputStream());
    }
	public void destroy() {
    	if (p != null) {
    		p.destroy();
    		p = null;
    	}
	}
	public void pause() {
		// Use an extenral program
	}

	// Common communication
	public Reader getGdbOutputReader() {
		return gdbOutputReader;
    }
    public Reader getGdbErrorReader() {
    	return gdbErrorReader;
    }
    public Writer getGdbInputWriter() {
    	return gdbInputWriter;
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
    
	public class ProcessWriter extends BufferedWriter implements Writer {
		public ProcessWriter(OutputStream outputStream) {
			super(new OutputStreamWriter(outputStream));
		}
		@Override
		public void write(String str) throws IOException {
			super.write(str);
			super.flush();
		}
	}
	public class ProcessReader extends BufferedReader implements Reader {
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
