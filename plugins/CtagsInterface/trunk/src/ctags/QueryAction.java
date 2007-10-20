package ctags;

import options.ActionsOptionPane;

import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

public class QueryAction extends EditAction {

	private String query;
	private String desc;
	
	public QueryAction(String name, String query) {
		super(name);
		this.query = query;
		desc = name + " (" + query + ")";
	}

	public QueryAction(int index) {
		super(jEdit.getProperty(ActionsOptionPane.ACTIONS + index + ".name"));
		query = jEdit.getProperty(ActionsOptionPane.ACTIONS + index + ".query");
	}
	
	@Override
	public void invoke(View view) {
		CtagsInterfacePlugin.jumpToQueryResults(view, query);
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
