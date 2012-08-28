package make.buildfile;

import make.*;
import java.io.*;
import java.util.regex.*;

public class Make extends Buildfile {
	public Make(String dir) {
		this(dir, null);
	}
	
	public Make(String dir, String name) {
		super(dir, name != null ? name : "Makefile");
		this.parseTargets();
	}
	
	public String getName() {
		return "Make";
	}
	
	protected void _parseTargets() {
		try {
			Process p = Runtime.getRuntime().exec(new String[] { "make", "-r", "-p", "-n", "-f", this.name}, null, this.dir);
			// apparently it hangs if you wait for it. Not sure why.
			//int exitCode = p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			
			// Start at the '# Files' line
			String line;
			while (!(line = reader.readLine()).equals("# Files")) {
				//System.out.println(line);
			}
			
			boolean notATarget = false;
			Pattern pat = Pattern.compile("^(\\w+?):");
			while ((line = reader.readLine()) != null) {
				if (notATarget) {
					// the previous line said 'not a target', so skip it
					notATarget = false;
					continue;
				} else if (line.equals("# Not a target:")) {
					// skip this line and the next one
					notATarget = true;
					continue;
				}
				
				Matcher mat = pat.matcher(line);
				if (mat.find()) {
					this.targets.add(new BuildTarget(mat.group(1), null));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected Process _runTarget(BuildTarget target) throws IOException {
		return Runtime.getRuntime().exec(new String[] { "make", "-f", this.name, target.name }, null, this.dir);
	}
	
	protected void _processErrors(String line) {}
}
