
package voxspellcheck;

import java.lang.StringBuffer;

import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;

import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.gui.DynamicContextMenuService;
import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.menu.EnhancedMenuItem;

public class VoxSpellMenuService extends DynamicContextMenuService
{
	public JMenuItem[] createMenu(JEditTextArea ta, MouseEvent evt)
    {
        EditPane ep = ta.getView().getEditPane();
        if (ep == null)
            return null;
        VoxSpellPainter painter = VoxSpellPlugin.getVoxSpellPainter(ep);
        if (painter == null)
            return null;
        
        StringBuffer word = new StringBuffer();
        JMenuItem[] items = null;
        int pos = ta.xyToOffset(evt.getX(), evt.getY());
        if (painter.check(ta, pos, word)) {
            // Check to see if it was ignored or added to the user dictionary.
            // If it was then add the ability to reset.
            if (painter.check(ta, pos, word, true)) {
                items = new EnhancedMenuItem[1];
                items[0] = new EnhancedMenuItem("VoxSpell: reset - \""+word+"\"", "voxspellcheck.resetWord",
                                                jEdit.getActionContext());
            }
        } else {
            items = new EnhancedMenuItem[2];
            items[0] = new EnhancedMenuItem("VoxSpell: add - \""+word+"\"", "voxspellcheck.ignoreWord",
                                            jEdit.getActionContext());
            items[1] = new EnhancedMenuItem("VoxSpell: ignore - \""+word+"\"", "voxspellcheck.addWord",
                                            jEdit.getActionContext());
        }
        return items;
    }
}
