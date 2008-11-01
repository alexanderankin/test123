/**
 * 
 */
package sn;

import java.util.Vector;

import javax.swing.JOptionPane;

import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

public class JumpAction extends EditAction {
	private static final String JUMP_ACTION_PREFIX = "source-navigator-jump-";
	private DbDescriptor desc;
	public JumpAction(DbDescriptor desc) {
		super(JUMP_ACTION_PREFIX + desc.name);
		jEdit.setTemporaryProperty(
				JUMP_ACTION_PREFIX + desc.name + ".label", "Jump to " + desc.label);
		this.desc = desc;
	}
	@Override
	public void invoke(View view) {
		String tag = SourceNavigatorPlugin.getEditorInterface().getTagForJump(
			view, "\\w+");
		if (tag == null || tag.length() == 0) {
			JOptionPane.showMessageDialog(
				view, SourceNavigatorPlugin.getMessage("no-tag-for-jump"));
			return;
		} 
		Vector<DbRecord> tags = DbAccess.lookupByKey(desc, tag, false);
		if (tags == null || tags.isEmpty()) {
			JOptionPane.showMessageDialog(
				view, SourceNavigatorPlugin.getMessage("no-tags-found"));
			return;
		}
		// Single tag found - jump
		if (tags.size() == 1) {
			tags.get(0).getSourceLink().jumpTo(view);
			return;
		}
		// Multiple tags found - show found tags in table
		String dockableName = SourceNavigatorPlugin.getDockableName(desc);
		view.getDockableWindowManager().showDockableWindow(dockableName);
		DbDockable dockable = (DbDockable)
			view.getDockableWindowManager().getDockableWindow(dockableName);
		dockable.show(tags);
	}
}