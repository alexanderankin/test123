package superabbrevs.io;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

public interface PluginDirectory {

	OutputStream openModeFileForWriting(String modeName) throws FileNotFoundException;
	InputStream openModeFileForReading(String modeName) throws FileNotFoundException;

}
