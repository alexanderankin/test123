package projectbuilder.options;
// imports {{{
import javax.swing.BorderFactory;
import javax.swing.Box;
import java.awt.Dimension;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import projectviewer.vpt.VPTProject;
import projectbuilder.ProjectToolbar;
// }}} imports
public class BuildRunOptionsPane extends AbstractOptionPane {
	private VPTProject proj;
	private CommandList buildList;
	private CommandList runList;
	public BuildRunOptionsPane(VPTProject proj) {
		super("projectBuilder.pv.build-run-options");
		this.proj = proj;
	}
	protected void _init() {
		setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		buildList = new CommandList(proj, "build");
		runList = new CommandList(proj, "run");
		addComponent(buildList);
		addComponent(Box.createRigidArea(new Dimension(0, 12)));
		addSeparator();
		addComponent(Box.createRigidArea(new Dimension(0, 12)));
		addComponent(runList);
	}
	protected void _save() {
		buildList.save();
		runList.save();
		View[] views = jEdit.getViews();
		for (int i = 0; i<views.length; i++) {
			ProjectToolbar toolbar = ProjectToolbar.viewMap.get(views[i]);
			if (toolbar != null) {
				toolbar.updateBoxes(proj);
			}
		}
	}
}
