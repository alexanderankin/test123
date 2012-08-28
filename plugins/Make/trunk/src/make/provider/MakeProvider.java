package make.provider;

import make.BuildfileProvider;
import make.buildfile.Make;

public class MakeProvider implements BuildfileProvider {
	public boolean accept(String filename) {
		return "Makefile".equals(filename) || filename.endsWith(".mk");
	}
	
	public Make createFor(String dir, String filename) {
		return new Make(dir, filename);
	}
}
