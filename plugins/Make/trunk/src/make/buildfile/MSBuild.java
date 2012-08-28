package make.buildfile;

import errorlist.ErrorSource;
import make.*;
import java.io.*;
import java.util.regex.*;
import org.gjt.sp.jedit.MiscUtilities;

public class MSBuild extends Buildfile {
	private String toolsPath;
	private Pattern error;
	
	public MSBuild(String dir, String name) {
		super(dir, name);
		// TODO: make the .NET version configurable
		this.toolsPath = getToolsPath("4.0");
		this.error = Pattern.compile("^(.+)\\((\\d+),(\\d+)\\): error \\w+: (.+)\\s\\[(.*)\\]$");
		this.parseTargets();
	}
	
	private String getToolsPath(String dotNetVersion) {
		try {
			Process p = Runtime.getRuntime().exec(new String[] { "reg", "query", 
				"HKLM\\Software\\Microsoft\\MSBuild\\ToolsVersions\\" + dotNetVersion, "/v", "MSBuildToolsPath" });
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			
			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
				String[] res = line.trim().split("\\s+");
				if (res[0].equals("MSBuildToolsPath")) {
					return res[2];
				}
			}
			return "";
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public String getName() {
		return "MSBuild";
	}
	
	protected void _parseTargets() {
		// Just stick to the three standard targets
		// TODO: consider adding configuration support
		this.targets.add(new BuildTarget("Build", null));
		this.targets.add(new BuildTarget("Rebuild", null));
		this.targets.add(new BuildTarget("Clean", null));
	}
	
	protected Process _runTarget(BuildTarget target) throws IOException {
		System.out.println("running, toolsPath = " + this.toolsPath);
		String msbuild = MiscUtilities.constructPath(this.toolsPath, "msbuild.exe");
		// "Rebuild" should be passed to msbuild as "Clean;Build"
		String targetName = (target.name.equals("Rebuild") ? "Clean;Build" : target.name);
		return Runtime.getRuntime().exec(new String[] { msbuild, "/t:"+target.name, this.name }, null, this.dir);
	}
	
	protected void _processErrors(String line) {
		Matcher mat = this.error.matcher(line);
		if (mat.find()) {
			String errorPath = mat.group(1);
			if (!MiscUtilities.isAbsolutePath(errorPath)) {
				String projFile = mat.group(5);
				errorPath = MiscUtilities.constructPath(MiscUtilities.getParentOfPath(projFile), errorPath);
			}
			
			int errorLine = new Integer(mat.group(2));
			// String errorCol = new Integer(mat.group(3));
			String errorMsg = mat.group(4);
			MakePlugin.addError(ErrorSource.ERROR, errorPath, errorLine-1, 0, 0, errorMsg, null);
		}
	}
}
