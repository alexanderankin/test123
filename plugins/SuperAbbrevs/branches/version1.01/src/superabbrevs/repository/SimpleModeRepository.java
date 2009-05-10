package superabbrevs.repository;

import java.io.InputStream;
import java.io.OutputStream;

import superabbrevs.io.PluginDirectory;
import superabbrevs.model.Mode;
import superabbrevs.serialization.ModeSerializer;

public class SimpleModeRepository implements ModeRepository {

	private final ModeSerializer modeSerializer;
	private final PluginDirectory directory;

	public SimpleModeRepository(PluginDirectory directory, ModeSerializer modeSerializer) {
		this.directory = directory;
		this.modeSerializer = modeSerializer;
	}

	public void save(Mode mode) {
		OutputStream output = directory.openModeFileForWriting(mode.getName());
		modeSerializer.serialize(output, mode);
	}

	public Mode Load(String modeName) {
		InputStream input = directory.openModeFileForReading(modeName); 
		return modeSerializer.deserialize(input, modeName);
	}
}
