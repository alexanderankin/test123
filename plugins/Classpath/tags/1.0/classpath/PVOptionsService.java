package classpath;

import projectviewer.config.OptionsService;
import projectviewer.vpt.VPTProject;
import org.gjt.sp.jedit.OptionPane;
import org.gjt.sp.jedit.OptionGroup;

public class PVOptionsService implements OptionsService {
	/**
	 * The project classpath option pane
	 */
	public OptionPane getOptionPane(VPTProject proj) {
		return new PVClasspathOptionPane(proj);
	}

	public OptionGroup getOptionGroup(VPTProject proj) {
		return null;
	}
}
