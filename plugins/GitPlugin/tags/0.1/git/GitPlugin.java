package git;

import org.gjt.sp.jedit.EditPlugin ;
import org.gjt.sp.jedit.jEdit;

public class GitPlugin extends EditPlugin {
	public static String gitPath() {
		return jEdit.getProperty("git.path", "git");
	}
    
    
};
