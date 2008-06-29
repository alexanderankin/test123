
package voxspellcheck;

import javax.swing.JMenuItem;

import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.gui.DynamicContextMenuService;
import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.menu.EnhancedMenuItem;

public class VoxSpellMenuService extends DynamicContextMenuService
{
    public JMenuItem createMenu(JEditTextArea ta)
    {
        
        EditPane ep = ta.getView().getEditPane();
        if (ep == null)
            return null;
        VoxSpellPainter painter = VoxSpellPlugin.getVoxSpellPainter(ep);
        if (painter == null)
            return null;
        
        if (painter.check(ta)) {
            return new EnhancedMenuItem("Reset Word", "voxspellcheck.resetWord",
                                        jEdit.getActionContext());
        } else {
            return new EnhancedMenuItem("Ignore Word", "voxspellcheck.ignoreWord",
                                        jEdit.getActionContext());
        }
    }
}
