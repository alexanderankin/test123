package classpath;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class ClasspathFilter extends FileFilter {

	public boolean accept(File file) {
		if (file.isDirectory())
			return true;

		String filename = file.getName();
		if (".classpath".equals(filename))
			return true;

		return (filename.endsWith(".jar") || filename.endsWith(".zip"));
	}

	public String getDescription() {
		return "Classpath elements (directories, *.jar, *.zip, .classpath)";
	}
}
