package make.provider;

import make.BuildfileProvider;
import make.buildfile.Ant;

public class AntProvider implements BuildfileProvider {
	public boolean accept(String filename) {
		return "build.xml".equals(filename);
	}
	
	public Ant createFor(String dir, String filename) {
		return new Ant(dir, filename);
	}
}
