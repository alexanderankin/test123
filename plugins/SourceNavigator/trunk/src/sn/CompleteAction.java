/**
 * 
 */
package sn;

import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

public class CompleteAction extends EditAction {
	private static final String COMPLETE_ACTION_PREFIX = "source-navigator-complete-";
	public CompleteAction(DbDescriptor desc) {
		super(COMPLETE_ACTION_PREFIX + desc.name);
		jEdit.setTemporaryProperty(
			COMPLETE_ACTION_PREFIX + desc.name + ".label", "Complete " + desc.label);
	}
	@Override
	public void invoke(View view) {
	}
}