package superabbrevs.repository;

import java.io.FileNotFoundException;

import superabbrevs.model.Mode;

public interface ModeRepository {

	void save(Mode mode) throws FileNotFoundException;
	Mode Load(String modeName) throws FileNotFoundException;

}
