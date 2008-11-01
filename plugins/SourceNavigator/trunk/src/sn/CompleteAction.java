/**
 * 
 */
package sn;

import java.util.Vector;

import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

public class CompleteAction extends EditAction {
	private static final String COMPLETE_ACTION_PREFIX = "source-navigator-complete-";
	private DbDescriptor desc;
	public CompleteAction(DbDescriptor desc) {
		super(COMPLETE_ACTION_PREFIX + desc.name);
		jEdit.setTemporaryProperty(
			COMPLETE_ACTION_PREFIX + desc.name + ".label", "Complete " + desc.label);
		this.desc = desc;
	}
	@Override
	public void invoke(View view) {
		String text = SourceNavigatorPlugin.getEditorInterface().getTextToComplete(
				view, "\\w+");
		Vector<DbRecord> tags = DbAccess.lookup(desc, text, true);
		if (tags == null || tags.isEmpty())
			return;
		new DbRecordCompletionPopup(view, text, tags);
	}
}