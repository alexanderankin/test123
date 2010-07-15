package projectbuilder.config;
//{{{ imports
import org.gjt.sp.jedit.OptionPane;
import org.gjt.sp.jedit.OptionGroup;
import projectviewer.vpt.VPTProject;
import java.util.StringTokenizer;
//}}}
public class ProjectConfigGroup extends OptionGroup {
	private VPTProject project;
	private String name;
	public ProjectConfigGroup(VPTProject proj) {
		super(proj.getProperty("project.type"));
		this.project = proj;
		this.name = proj.getProperty("project.type");
		StringTokenizer tokenizer = new StringTokenizer(project.getProperty("project.options"));
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			addOptionPane(new ProjectConfigPane(project, token));
		}
	}
	public String getLabel() {
		return name.replace("_", " ");
	}
}
