package superabbrevs.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PluginDirectoryImpl implements PluginDirectory {

	private Pattern illegalCharectorPattern = Pattern.compile("[/\\\\\\:\\?\"<>\\|]");

	public InputStream openModeFileForReading(String modeName) throws FileNotFoundException {
		File file = modeNameToFile(modeName);
		return new FileInputStream(file);
	}

	public OutputStream openModeFileForWriting(String modeName) throws FileNotFoundException {
		File file = modeNameToFile(modeName);
		return new FileOutputStream(file);
	}
	
	private File modeNameToFile(String modeName) {
		Matcher matcher = illegalCharectorPattern.matcher(modeName);
		String fileName = matcher.replaceAll("_");
		return new File(fileName);
	}
}
