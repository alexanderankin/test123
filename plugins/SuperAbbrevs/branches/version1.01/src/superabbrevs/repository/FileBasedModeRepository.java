package superabbrevs.repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

import superabbrevs.io.PluginDirectory;
import superabbrevs.model.Mode;
import superabbrevs.serialization.ModeSerializer;

import com.google.inject.Inject;

public class FileBasedModeRepository implements ModeRepository {

	private final ModeSerializer modeSerializer;
	private final PluginDirectory directory;

	@Inject 
	public FileBasedModeRepository(PluginDirectory directory, ModeSerializer modeSerializer) {
		this.directory = directory;
		this.modeSerializer = modeSerializer;
	}

	public void save(Mode mode) throws FileNotFoundException {
		OutputStream output = null;
		try {
			output = directory.openModeFileForWriting(mode.getName());
			modeSerializer.serialize(output, mode);
		} finally {
			IOUtils.closeQuietly(output);
		}
	}

	public Mode Load(String modeName) throws FileNotFoundException {
		InputStream input = null;
		try {
			if (new File(modeName).exists()) {
				input = directory.openModeFileForReading(modeName); 
				return modeSerializer.deserialize(input);
			} else {
				return new Mode(modeName);
			}
		} finally {
			IOUtils.closeQuietly(input);
		}
	}
}
