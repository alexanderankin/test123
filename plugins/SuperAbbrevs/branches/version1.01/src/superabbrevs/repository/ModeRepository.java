package superabbrevs.repository;

import superabbrevs.model.Mode;

public interface ModeRepository {

	void save(Mode mode);
	Mode load(String modeName);

	void addModeSavedListener(ModeSavedListener listener);
	void removeModeSavedListener(ModeSavedListener listener);
}
