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

	public ModeService getModeService() {
		return modeService;
	}

	@Inject
	public AbbrevsOptionPaneControllerImpl(ModeService modeService, ModeRepository modeRepository) {
        this.modeService = modeService;
		this.modeRepository = modeRepository;
    }
    
    public Set<Abbreviation> loadsAbbrevs(String modeName) {
        return loadMode(modeName).getAbbreviations();
    }
    
    public Mode loadMode(String modeName) {
    	if (modes.containsKey(modeName)) {
            return modes.get(modeName);
        } else {
            return forceLoadMode(modeName);
        }
    }
    
    public Mode forceLoadMode(String modeName) {
    	Mode mode = modeRepository.load(modeName);
        modes.put(modeName, mode);
        return mode;
    }

    public void saveAbbrevs() throws IOException {
        for(Mode mode : modes.values()) {
            modeRepository.save(mode);  
        }
    }
}
