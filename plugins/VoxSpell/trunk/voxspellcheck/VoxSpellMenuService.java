
package voxspellcheck;


import java.util.ArrayList;

import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;

import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.gui.DynamicContextMenuService;
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
        
        int pos;
        if (evt == null) {
            pos = ta.getCaretPosition();
        } else {
            pos = ta.xyToOffset(evt.getX(), evt.getY());
        }
        
        StringBuffer word = new StringBuffer();
        JMenuItem[] items = null;
        if (painter.check(pos, word)) {
            // Check to see if it was ignored or added to the user dictionary.
            // If it was then add the ability to reset.
            if (painter.check(pos, word, true)) {
                items = new EnhancedMenuItem[1];
                items[0] = new EnhancedMenuItem("reset - \""+word+"\"", "voxspellcheck.resetWord",
                                                jEdit.getActionContext());
            }
        } else {
            
            // Miss-spelled word. Grab the top 3 suggestions and put those in
            // the menu. Follow with the standard suggest, add, and ignore
            // actions.
            
            SuggestionTree suggestion_tree = VoxSpellPlugin.getSuggestionTree();
            if (suggestion_tree == null)
                return null;
            
            ArrayList<String> suggestions = suggestion_tree.getSuggestions(word.toString());
            int num_suggestions = java.lang.Math.min(3, suggestions.size());
            
            // 3 static entries + an unknown number of shortcut suggestions.
            items = new JMenuItem[3 + num_suggestions];
            for (int i = 0; i < num_suggestions; ++i) {
                final String old_word = word.toString();
                final String sug = VoxSpellPlugin.matchCase(old_word, suggestions.get(i));
                final JEditTextArea textArea = ta;
                final int caret_pos = pos;
                items[i] = new JMenuItem("\""+word+"\" -> \""+sug+"\"");
                items[i].addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent e)
                    {
                        textArea.setCaretPosition(caret_pos);
                        VoxSpellPlugin.selectWordAtCaret(textArea);
                        textArea.setSelectedText(sug);
                    }
                });
            }
            
            items[num_suggestions] = new EnhancedMenuItem("More suggestions", "voxspellcheck.suggest",
                                                          jEdit.getActionContext());
            items[num_suggestions + 1] = new EnhancedMenuItem("Add - \""+word+"\"", "voxspellcheck.addWord",
                                                              jEdit.getActionContext());
            items[num_suggestions + 2] = new EnhancedMenuItem("Ignore - \""+word+"\"", "voxspellcheck.ignoreWord",
                                                              jEdit.getActionContext());
        }
        return items;
    }
}
