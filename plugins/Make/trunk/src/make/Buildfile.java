package make;

import java.io.*;
import java.util.LinkedList;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.StatusBar;
import org.gjt.sp.util.ThreadUtilities;

public abstract class Buildfile {
	protected File dir;
	protected String name;
	public LinkedList<BuildTarget> targets;
	
	public Buildfile(String dir, String name) {
		this.dir = new File(dir);
		this.name = name;
	}
	
	public void parseTargets() {
		this.targets = new LinkedList<BuildTarget>();
		this._parseTargets();
	}
	
	public void runTarget(final BuildTarget target) {
		MakePlugin.clearErrors();
		ThreadUtilities.runInBackground(new Thread() {
				public void run() {
					try {
						StatusBar status = jEdit.getActiveView().getStatus();
						Process p = _runTarget(target);
						// TODO: set up an error source so that ErrorList can catch them
						BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
						String line;
						while ((line = reader.readLine()) != null) {
							System.out.println(line);
							status.setMessageAndClear(line);
							_processErrors(line);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
		});
	}
	
	public abstract String getName();
	protected abstract void _parseTargets();
	protected abstract Process _runTarget(BuildTarget target) throws IOException;
	protected abstract void _processErrors(String line);
}
