package launcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.gjt.sp.util.Log;

import common.swingworker.SwingWorker;

public class LauncherSwingWorker extends SwingWorker<Integer, Object> {

	String cmd = null;
	String[] cmds = null;

	public LauncherSwingWorker(String[] cmds) {
		super();
		this.cmds = cmds;
	}

	public LauncherSwingWorker(String cmd) {
		super();
		this.cmd = cmd;
	}

	public int handleProcess(Process p) throws IOException,
			InterruptedException {
		InputStream stderr = p.getErrorStream();
		InputStreamReader isr = new InputStreamReader(stderr);
		BufferedReader br = new BufferedReader(isr);
		String line = null;
		while ((line = br.readLine()) != null)
			Log.log(Log.DEBUG, LauncherUtils.class, line);
		int exitVal = p.waitFor();
		Log.log(Log.DEBUG, LauncherUtils.class, "Process exitValue: "
						+ exitVal);
		p.waitFor();
		return exitVal;
	}

	@Override
	public Integer doInBackground() throws Exception {
		StringBuffer cmdLine = new StringBuffer();
		if (cmd == null) {
			for (String arg : cmds)
				cmdLine.append(arg).append(' ');
		} else {
			cmdLine.append(cmd);
		}
		Log.log(Log.DEBUG, LauncherUtils.class, "Executing: " + cmdLine);
		Process p;
		if (cmd == null)
			p = Runtime.getRuntime().exec(cmds);
		else
			p = Runtime.getRuntime().exec(cmd);
		return new Integer(handleProcess(p));
	}

	@Override
	protected void done() {
		//
	}

}
