package ise.plugin.svn;

import javax.swing.JMenuItem;
import org.gjt.sp.jedit.gui.DynamicContextMenuService;
import org.gjt.sp.jedit.jEdit;
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
    public JMenuItem createMenu( JEditTextArea textArea ) {
        if (textArea != null) {
            return new TextAreaContextMenu( textArea.getView() );
        }
        return new TextAreaContextMenu( jEdit.getFirstView());
    }
}