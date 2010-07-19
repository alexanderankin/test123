package projectbuilder.config;
//{{{ imports
import org.gjt.sp.jedit.OptionPane;
import org.gjt.sp.jedit.OptionGroup;
import org.gjt.sp.jedit.jEdit;
import projectviewer.config.OptionsService;
import projectviewer.vpt.VPTProject;
import java.util.StringTokenizer;
//}}}
public class ProjectConfigOptionsService implements OptionsService {
	public OptionPane getOptionPane(VPTProject proj) {
		return null;
	}
	public OptionGroup getOptionGroup(VPTProject proj) {
		if (proj.getProperty("project.options") !=  null && proj.getProperty("project.type") != null) {
			return new ProjectConfigGroup(proj);
		}
		return null;
	}
}
