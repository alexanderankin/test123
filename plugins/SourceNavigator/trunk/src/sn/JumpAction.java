/**
 * 
 */
package sn;

import java.util.Vector;

import org.gjt.sp.jedit.View;

public class JumpAction extends LookupAction {
	private static final String JUMP_ACTION_PREFIX = "jump";
	public JumpAction() { // Jump to any kind of tag
		this(null);
	}
	public JumpAction(DbDescriptor desc) {
		super(JUMP_ACTION_PREFIX, "Jump to ", desc);
	}
	@Override
	protected String getTextForAction(View view) {
		return SourceNavigatorPlugin.getEditorInterface().getTagForJump(
				view, "\\w+");
	}
	@Override
	protected boolean isPrefixLookup() {
		return false;
	}
	@Override
	protected void multipleTags(View view, String text, Vector<DbRecord> records) {
		DbDescriptor descToUse = desc;
		if (desc == null) {
			descToUse = records.get(0).getDbDescriptor();
			for (DbRecord record: records)
				if (record.getDbDescriptor() != descToUse)
					return;	// No implementation yet
		}
		// Show the table with the lookup result
		String dockableName = SourceNavigatorPlugin.getDockableName(descToUse);
		view.getDockableWindowManager().showDockableWindow(dockableName);
		DbDockable dockable = (DbDockable)
			view.getDockableWindowManager().getDockableWindow(dockableName);
		dockable.show(records);
	}
	@Override
	protected void singleTag(View view, String text, DbRecord record) {
		record.getSourceLink().jumpTo(view);
	}
}