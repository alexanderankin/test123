package launcher.extapp;


import java.io.File;
import java.util.regex.Pattern;

import launcher.LauncherUtils;

import org.gjt.sp.jedit.View;

public class PatternExternalApplicationLauncher extends ExternalApplicationLauncher {

	protected static final String PROP_PREFIX = PatternExternalApplicationLauncher.class.getName();

	protected final String patternString;
	protected final Pattern pattern;
	
	public PatternExternalApplicationLauncher(Object[] args) {
		this(PROP_PREFIX, args, false, false);
	}

	protected PatternExternalApplicationLauncher(String prop, Object[] args) {
		this(prop, args, false, false);
	}

	public PatternExternalApplicationLauncher(Object[] args, boolean stateful, boolean userDefined) {
		this(PROP_PREFIX, args, stateful, userDefined);
	}

	public PatternExternalApplicationLauncher(String prop, Object[] args, boolean stateful, boolean userDefined) {
		super(prop, args, stateful, userDefined);
		this.patternString = args[1].toString();
		this.pattern = compileToPattern(patternString);
	}
	
	public Pattern compileToPattern(String pattern) {
		return pattern == null || pattern.isEmpty() ?
				null :
				Pattern.compile(pattern);
	}

	@Override
	public boolean isFirstLevelFor(Object resolvedResource) {
		return canLaunch(resolvedResource);
	}

	@Override
	public boolean canLaunch(Object resolvedResource) {
		if (resolvedResource instanceof File) {
			File file = (File) resolvedResource;
			if (!file.exists())
				return false;
			if (pattern == null)
				return true;
			// We can launch any File that matches the pattern
			return pattern.matcher(file.toString()).matches();
		}
		return false;
	}

	@Override
	public boolean launch(View view, Object resource) {
        try {
        	File file = LauncherUtils.resolveToFile(resource);
        	if (file == null) return false;
        	if (!file.exists())
        			return false;
        	LauncherUtils.runCmd(new String[] {
        			getApplicationPath(), file.toString()});
        } catch (Exception exp) {
        	logFailedLaunch(this, getApplicationPath() + " " + resource.toString(), exp);
            return false;
        }
		return true;
	}

}
