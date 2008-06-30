package ise.plugin.svn;

import java.util.HashMap;
import javax.swing.JMenuItem;
import org.gjt.sp.jedit.gui.DynamicContextMenuService;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.textarea.JEditTextArea;

import ise.plugin.svn.gui.TextAreaContextMenu;

/**
 * This is the implementation of DynamicContextMenuService that lets
 * this plugin add items to the text area context menu.  This is way
 * better than the kludge I'd been doing previously, which conflicted
 * with at least the ContextMenu plugin, and maybe others.  To have jEdit
 * add the SVN context menu, this class is declared in the services.xml file.
 */
public class ContextMenuService extends DynamicContextMenuService {

    // cache menu for quicker response
    private HashMap<View, TextAreaContextMenu> menus = new HashMap<View, TextAreaContextMenu>();

    // context menu is per View
    public JMenuItem createMenu( JEditTextArea textArea ) {
        View view = textArea.getView();
        TextAreaContextMenu menu = menus.get(view);
        if (menu == null) {
            menu = textArea == null ? new TextAreaContextMenu(jEdit.getFirstView()) : new TextAreaContextMenu(view);
            menus.put(view, menu);
        }
        return menu;
    }
}