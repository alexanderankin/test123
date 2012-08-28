package make.provider;

import make.BuildfileProvider;
import make.buildfile.MSBuild;

public class MSBuildProvider implements BuildfileProvider {
	public boolean accept(String filename) {
		return filename.endsWith(".sln") || filename.endsWith(".csproj");
	}
	
	public MSBuild createFor(String dir, String filename) {
		return new MSBuild(dir, filename);
	}
}
