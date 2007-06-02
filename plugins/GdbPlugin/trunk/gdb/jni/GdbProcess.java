package gdb.jni;

import gdb.launch.LaunchConfiguration;
import gdb.options.GeneralOptionPane;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.gjt.sp.jedit.jEdit;

public class GdbProcess {
	
	private static final String GDB_PROCESS_LIBRARY = "gdbProcess";
	
	public interface Reader {
		boolean ready()  throws IOException;
		String readLine() throws IOException;
	}
	public interface Writer {
		void write(String s) throws IOException;
	}
	
	private boolean jni = false;
	
	// Java communication
	private Process p = null;

	// JNI communication
	
	// Common communication
	Reader gdbOutputReader = null;
	Reader gdbErrorReader = null;
	Writer gdbInputWriter = null;
	Reader programOutputReader = null;
	Reader programErrorReader = null;
	Writer programInputWriter = null;
	
    public GdbProcess(LaunchConfiguration config) throws IOException {
    	jni = jEdit.getBooleanProperty(GeneralOptionPane.USE_JNI_PROP);
    	if (jni)
    		startJNI(config);
    	else
    		startJava(config);
    }
	public void destroy() {
		if (jni)
			destroyJNI();
		else
			destroyJava();
	}
	public void pause() {
		if (jni)
			pauseJNI();
		else
			pauseJava();
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
    
    // Java communication
	private void startJava(LaunchConfiguration config) throws IOException {
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
	private void destroyJava() {
    	if (p != null) {
    		p.destroy();
    		p = null;
    	}
	}
	private void pauseJava() {
		// No can do... limitation of Java - cannot send process signals.
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

	// JNI communication
	// Native methods
	private native void start(String gdb, String prog, String args,
			String wdir, String env);
	private native void end();
	private native void pauseProgram();
	private native int getGdbErrorStream();
	private native int getGdbOutputStream();
	private native int getGdbInputStream();
	private native int getProgramErrorStream();
	private native int getProgramOutputStream();
	private native int getProgramInputStream();
	private native boolean isReady(int stream);
	private native String readLine(int stream);
	private native void write(int stream, String s);
	
	private void startJNI(LaunchConfiguration config) throws IOException {
        System.loadLibrary(GDB_PROCESS_LIBRARY);
        start(jEdit.getProperty(GeneralOptionPane.GDB_PATH_PROP),
        	config.getProgram(), config.getArguments(), config.getDirectory(),
        	config.getEnvironment());
		gdbOutputReader = new NativeProcessReader(getGdbOutputStream()); 
		programOutputReader = new NativeProcessReader(getProgramOutputStream());
		gdbErrorReader = new NativeProcessReader(getGdbErrorStream());
		programErrorReader = new NativeProcessReader(getProgramErrorStream());
		gdbInputWriter = new NativeProcessWriter(getGdbInputStream());
		programInputWriter = new NativeProcessWriter(getProgramInputStream());
	}
	private void destroyJNI() {
		end();
	}
	private void pauseJNI() {
		pauseProgram();
	}
	public class NativeProcessWriter implements Writer {
		int stream;
		public NativeProcessWriter(int inputStream) {
			stream = inputStream;
		}
		public void write(String s) throws IOException {
			GdbProcess.this.write(stream, s);
		}
	}
	public class NativeProcessReader implements Reader {
		int stream;
		public NativeProcessReader(int outputStream) {
			stream = outputStream;
		}
		public String readLine() throws IOException {
			return GdbProcess.this.readLine(stream);
		}
		public boolean ready() throws IOException {
			return GdbProcess.this.isReady(stream);
		}
	}


}
