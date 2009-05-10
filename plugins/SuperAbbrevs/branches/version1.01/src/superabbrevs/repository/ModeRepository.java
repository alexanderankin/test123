package superabbrevs.repository;

import superabbrevs.model.Mode;

public interface ModeRepository {

	void save(Mode mode);
	Mode Load(String modeName);

}
