package gdb.jni;

import gdb.launch.LaunchConfiguration;
import gdb.options.GeneralOptionPane;

import java.io.File;
import java.io.IOException;

import org.gjt.sp.jedit.jEdit;

public class GdbProcess {
	
	public interface Reader {
		boolean ready()  throws IOException;
		String readLine() throws IOException;
	}
	public interface Writer {
		void write(String s) throws IOException;
	}
	
	private boolean jni = false;
	
	// Java communication
	// Gdb process
	private Process p = null;
	Reader gdbOutputReader = null;
	Reader gdbErrorReader = null;
	Writer gdbInputWriter = null;
	Reader programOutputReader = null;
	Reader programErrorReader = null;
	Writer programInputWriter = null;
	

	// JNI communication
	
    static {
          //System.loadLibrary ( "gdbProcess" ) ;
    }

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
    private void destroyJava() {
    	if (p != null) {
    		p.destroy();
    		p = null;
    	}
	}
	private void destroyJNI() {
		// TODO Auto-generated method stub
		
	}
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
	private void startJNI(LaunchConfiguration config) {
		// TODO Auto-generated method stub
		
	}
}
