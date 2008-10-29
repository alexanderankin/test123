/**
 * 
 */
package sn;

import java.util.Vector;

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
		Vector<DbRecord> tags = DbAccess.lookup(desc, tag, false);
		if (tags != null && tags.size() > 0)
			tags.get(0).getSourceLink().jumpTo(view);
	}
}