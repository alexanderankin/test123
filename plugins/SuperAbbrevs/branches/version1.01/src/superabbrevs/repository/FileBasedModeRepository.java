package superabbrevs.repository;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.gjt.sp.util.IOUtilities;

import superabbrevs.io.PluginDirectory;
import superabbrevs.model.Mode;
import superabbrevs.serialization.ModeSerializer;

import com.google.inject.Inject;

public class FileBasedModeRepository implements ModeRepository {

	private final ModeSerializer modeSerializer;
	private final PluginDirectory directory;

	private List<ModeSavedListener> modeSavedListeners = 
		new ArrayList<ModeSavedListener>();
	
	@Inject 
	public FileBasedModeRepository(PluginDirectory directory, ModeSerializer modeSerializer) {
		this.directory = directory;
		this.modeSerializer = modeSerializer;
	}

	public void save(Mode mode) {
		OutputStream output = null;
		try {
			output = directory.openModeFileForWriting(mode.getName());
			modeSerializer.serialize(output, mode);
			fireModeSavedEvent(mode);
		} finally {
			IOUtilities.closeQuietly(output);
		}
	}

	public Mode load(String modeName) {
		InputStream input = null;
		try {
			input = directory.openModeFileForReading(modeName); 
			return modeSerializer.deserialize(input);
		} catch (Exception e) {
			return new Mode(modeName);
		} finally {
			IOUtilities.closeQuietly(input);
		}
	}

	public void addModeSavedListener(ModeSavedListener listener) {
		assert listener != null;
		modeSavedListeners.add(listener);
	}

	public void removeModeSavedListener(ModeSavedListener listener) {
		modeSavedListeners.remove(listener);
	}
	
	private void fireModeSavedEvent(Mode mode) {
		for (ModeSavedListener listener : modeSavedListeners) {
			listener.modeWasSaved(mode);
		}
	}
}
