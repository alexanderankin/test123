package superabbrevs.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.MiscUtilities;

import superabbrevs.SuperAbbrevsPlugin;

public class PluginDirectoryImpl implements PluginDirectory {

	private Pattern illegalCharectorPattern = Pattern.compile("[/\\\\\\:\\?\"<>\\|]");
	private static final String ABBREVS_DIR =
        EditPlugin.getPluginHome(SuperAbbrevsPlugin.class).getPath();
	
	public InputStream openModeFileForReading(String modeName) {
		try {
			return tryOpenModeFileForReading(modeName);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private InputStream tryOpenModeFileForReading(String modeName) throws FileNotFoundException {
		File file = modeNameToFile(modeName);
		return new FileInputStream(file);
	}

	public OutputStream openModeFileForWriting(String modeName) {
		try {
			return tryOpenModeFileForWriting(modeName);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	private OutputStream tryOpenModeFileForWriting(String modeName) throws FileNotFoundException {
		File file = modeNameToFile(modeName);
		return new FileOutputStream(file);
	}

	private File modeNameToFile(String modeName) {
		Matcher matcher = illegalCharectorPattern.matcher(modeName);
		String fileName = matcher.replaceAll("_") + ".xml";
		String path = MiscUtilities.constructPath(ABBREVS_DIR, fileName);
		return new File(path);
	}
}
