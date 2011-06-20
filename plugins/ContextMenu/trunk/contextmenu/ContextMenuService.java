

package contextmenu;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.DynamicContextMenuService;
import javax.swing.JMenuItem;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import java.awt.event.MouseEvent;


public class ContextMenuService extends DynamicContextMenuService {
	
	public JMenuItem[] createMenu(JEditTextArea ta, MouseEvent evt) {
		if (!jEdit.getBooleanProperty("contextmenu.in-popup")
			|| ta == null || ta.getBuffer() == null)
		{
			return null;
		}
		JMenuItem[] items = new JMenuItem[1];
		String mode = ta.getBuffer().getMode().getName();
		JMenuItem item = ContextMenuPlugin.getMenuForMode(mode, ContextMenuPlugin.CACHE_POPUP);
		if (item == null) {
			item = ContextMenuPlugin.getCustomizeModeItem(jEdit.getProperty("contextmenu.customize-mode"));
		}
		items[0] = item;
		return items;
	}
	
}
