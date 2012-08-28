package make.buildfile;

import errorlist.ErrorSource;
import make.*;
import java.io.*;
import java.util.regex.*;
import java.util.LinkedList;
import org.gjt.sp.jedit.OperatingSystem;
import org.gjt.sp.jedit.MiscUtilities;

public class Ant extends Buildfile {
	private String ant;
	private Pattern error;
	private Pattern errorExtras;
	
	private String errorPath;
	private int errorLine;
	private String errorMsg;
	private LinkedList<String> errorExtraMsgs;
	
	public Ant(String dir) {
		this(dir, null);
	}
	
	public Ant(String dir, String name) {
		super(dir, name != null ? name : "build.xml");
		
		// find the ant executable
		this.ant = "ant" + (OperatingSystem.isWindows() ? ".bat" : "");
		String home = System.getenv().get("ANT_HOME");
		if (home != null && home.length() > 0) {
			ant = MiscUtilities.constructPath(home, "bin/" + ant);
		}
		
		// set up error/warning regexes
		// TODO: if Console is installed, use the property set in its pane
		this.error = Pattern.compile("\\s*\\[.+\\]\\s+(.+?):(\\d+):(.+)");
		this.errorExtras = Pattern.compile("\\s*\\[.+\\]\\s*((?:symbol|location|found|required)\\s*:.*)");
		
		this.parseTargets();
	}
	
	public String getName() {
		return "Ant";
	}
	
	protected void _parseTargets() {
		try {
			Process p = Runtime.getRuntime().exec(new String[] { this.ant, "-p", "-f", this.name}, null, this.dir);
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			
			Pattern pat = Pattern.compile("^\\s(\\S+)\\s*?(\\S.*)$");
			Pattern def = Pattern.compile("^Default target: (\\S+?)$");
			
			String line;
			boolean targets = false;
			while ((line = reader.readLine()) != null) {
				if (line.endsWith("targets:")) {
					targets = true;
					continue;
				} else if (!targets) {
					continue;
				}
				
				Matcher mat = pat.matcher(line);
				if (mat.find()) {
					this.targets.add(new BuildTarget(mat.group(1), mat.group(2)));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected Process _runTarget(BuildTarget target) throws IOException {
		return Runtime.getRuntime().exec(new String[] { this.ant, "-f", this.name, target.name }, null, this.dir);
	}
	
	/**
	 * Ant error messages show the path, line, and message on one line,
	 * then the error's description on the next.
	 */
	protected void _processErrors(String line) {
		Matcher mat = null;
		if (this.errorPath != null) {
			mat = this.errorExtras.matcher(line);
			if (mat.find()) {
				this.errorExtraMsgs.add(mat.group(1));
			} else {
				MakePlugin.addError(ErrorSource.ERROR, this.errorPath, this.errorLine-1, 0, 0, this.errorMsg, this.errorExtraMsgs);
				this.errorPath = null;
			}
		} else {
			mat = this.error.matcher(line);
			if (mat.find()) {
				this.errorPath = mat.group(1);
				this.errorLine = new Integer(mat.group(2));
				this.errorMsg = mat.group(3);
				this.errorExtraMsgs = new LinkedList<String>();
			}
		}
	}
}
