

package contextmenu;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.DynamicContextMenuService;
import javax.swing.JMenuItem;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.util.Log;


public class ContextMenuService extends DynamicContextMenuService{
	public JMenuItem createMenu(JEditTextArea ta) {
		if (ta == null || ta.getBuffer() == null) {
			return null;
		}
		String mode = ta.getBuffer().getMode().getName();
		JMenuItem item = ContextMenuPlugin.getMenuForMode(mode, ContextMenuPlugin.CACHE_POPUP);
		if (item == null) {
			item = ContextMenuPlugin.getCustomizeModeItem(jEdit.getProperty("contextmenu.customize-mode"));
		}
		return item;
	}
}
