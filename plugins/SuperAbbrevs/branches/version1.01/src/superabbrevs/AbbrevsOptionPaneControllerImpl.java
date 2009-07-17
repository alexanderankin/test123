package superabbrevs;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Set;

import com.google.inject.Inject;

import superabbrevs.model.Abbreviation;
import superabbrevs.model.Mode;
import superabbrevs.repository.ModeRepository;


public class AbbrevsOptionPaneControllerImpl implements AbbrevsOptionPaneController {
    Hashtable<String,Mode> modes = new Hashtable<String, Mode>();
    
	private final ModeService modeService;
	private final ModeRepository modeRepository;

	/* (non-Javadoc)
	 * @see superabbrevs.AbbrevsOptionPaneController#getModeService()
	 */
	public ModeService getModeService() {
		return modeService;
	}

	@Inject
	public AbbrevsOptionPaneControllerImpl(ModeService modeService, ModeRepository modeRepository) {
        this.modeService = modeService;
		this.modeRepository = modeRepository;
    }
    
    /* (non-Javadoc)
	 * @see superabbrevs.AbbrevsOptionPaneController#loadsAbbrevs(java.lang.String)
	 */
    public Set<Abbreviation> loadsAbbrevs(String modeName) {
        return loadMode(modeName).getAbbreviations();
    }
    
    /* (non-Javadoc)
	 * @see superabbrevs.AbbrevsOptionPaneController#loadMode(java.lang.String)
	 */
    public Mode loadMode(String modeName) {
    	if (modes.containsKey(modeName)) {
            return modes.get(modeName);
        } else {
            Mode mode = modeRepository.load(modeName);
            modes.put(modeName, mode);
            return mode;
        }
    }

    /* (non-Javadoc)
	 * @see superabbrevs.AbbrevsOptionPaneController#saveAbbrevs()
	 */
    public void saveAbbrevs() throws IOException {
        for(Mode mode : modes.values()) {
            modeRepository.save(mode);  
        }
    }
}
