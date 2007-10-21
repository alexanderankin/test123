package ctags;

import options.ActionsOptionPane;

import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

public class QueryAction extends EditAction {

	public static final String TAG = "{tag}";
	public static final String PROJECT = "{project}";
	private String query;
	private String desc;
	
	public QueryAction(String name, String query) {
		super(name);
		jEdit.setTemporaryProperty(name + ".label", name);
		this.query = query;
		desc = name + " (" + query + ")";
	}

	public QueryAction(int index) {
		super(jEdit.getProperty(ActionsOptionPane.ACTIONS + index + ".name"));
		jEdit.setTemporaryProperty(getName() + ".label", getName());
		query = jEdit.getProperty(ActionsOptionPane.ACTIONS + index + ".query");
	}
	
	@Override
	public void invoke(View view) {
		String s = query.replace(TAG,
			CtagsInterfacePlugin.getDestinationTag(view));
		projects.ProjectWatcher pvi = CtagsInterfacePlugin.getProjectWatcher();
		String project = (pvi == null) ? "" : pvi.getActiveProject(view);
		s = s.replace(PROJECT, project);
		CtagsInterfacePlugin.jumpToQueryResults(view, s);
	}
	
	public String toString() {
		return desc;
	}

	public String getQuery() {
		return query;
	}

	public void save(int index) {
		jEdit.setProperty(ActionsOptionPane.ACTIONS + index + ".name", getName());
		jEdit.setProperty(ActionsOptionPane.ACTIONS + index + ".query", query);
	}
}
