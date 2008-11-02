/**
 * 
 */
package sn;

import java.util.Vector;

import org.gjt.sp.jedit.View;

public class CompleteAction extends LookupAction {
	private static final String COMPLETE_ACTION_PREFIX = "complete";
	public CompleteAction(DbDescriptor desc) {
		super(COMPLETE_ACTION_PREFIX, "Complete ", desc);
	}
	@Override
	protected String getTextForAction(View view) {
		return SourceNavigatorPlugin.getEditorInterface().getTextToComplete(
			view, "\\w+");
	}
	@Override
	protected boolean isPrefixLookup() {
		return true;
	}
	@Override
	protected void multipleTags(View view, String text, Vector<DbRecord> records) {
		new DbRecordCompletionPopup(view, text, records, this);
	}
	@Override
	protected void singleTag(View view, String text, DbRecord record) {
		String insertion = record.getName().substring(text.length());
		SourceNavigatorPlugin.getEditorInterface().insertAtCaret(view, insertion);
	}
}