package superabbrevs.io;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

public interface PluginDirectory {

	OutputStream openModeFileForWriting(String modeName);
	InputStream openModeFileForReading(String modeName);

}
