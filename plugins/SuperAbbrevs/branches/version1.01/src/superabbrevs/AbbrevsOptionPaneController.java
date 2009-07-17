package superabbrevs;

import java.io.IOException;
import java.util.Set;

import superabbrevs.model.Abbreviation;
import superabbrevs.model.Mode;

public interface AbbrevsOptionPaneController {

	public abstract ModeService getModeService();

	public abstract Set<Abbreviation> loadsAbbrevs(String modeName);

	public abstract Mode loadMode(String modeName);

	public abstract void saveAbbrevs() throws IOException;

}