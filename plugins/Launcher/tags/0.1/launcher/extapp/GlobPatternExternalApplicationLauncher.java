package launcher.extapp;


import java.util.regex.Pattern;

import org.gjt.sp.util.StandardUtilities;

public class GlobPatternExternalApplicationLauncher extends PatternExternalApplicationLauncher {

	//protected static final String PROP_PREFIX = GlobPatternExternalApplicationLauncher.class.getName();

	public GlobPatternExternalApplicationLauncher(Object[] args) {
		this(PROP_PREFIX, args, false, false);
	}

	protected GlobPatternExternalApplicationLauncher(String prop, Object[] args) {
		this(prop, args, false, false);
	}

	public GlobPatternExternalApplicationLauncher(Object[] args, boolean stateful, boolean userDefined) {
		this(PROP_PREFIX, args, stateful, userDefined);
	}

	public GlobPatternExternalApplicationLauncher(String prop, Object[] args, boolean stateful, boolean userDefined) {
		super(prop, args, stateful, userDefined);
	}
	
	@Override
	public Pattern compileToPattern(String glob) {
		return glob == null || glob.length() == 0 ?
				null :
				Pattern.compile(StandardUtilities.globToRE(glob));
	}
	
}
