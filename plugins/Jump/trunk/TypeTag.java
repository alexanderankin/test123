// * :tabSize=4:indentSize=4:
// * :folding=explicit:collapseFolds=1:

//{{{ imports
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.jedit.msg.*;

import org.gjt.sp.jedit.gui.EnhancedDialog;
import org.gjt.sp.jedit.gui.HistoryTextField;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dimension;
import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
//}}}

public class TypeTag extends EnhancedDialog
{

private HistoryTextField history;
private TypeTagActionListener listener;
    
//{{{ public TypeTag
    public TypeTag()
    {
        super (jEdit.getActiveView(), "Jump to tag", true);
        listener = new TypeTagActionListener();
    }
//}}}
    
//{{{ void prepareGUI()
    private void prepareGUI()
    {    
        JPanel content = new JPanel(new BorderLayout(7,7));
		content.setBorder(new EmptyBorder(0,12,12,12));
		setContentPane(content);
        
        JLabel title = new JLabel("Type a tag to jump to");

        String ModelName ="jump.tag_history.project."+JumpPlugin.getActiveProjectBuffer().PROJECT_NAME;
        history = new HistoryTextField(ModelName, true, false);
        history.setColumns(35); 
        
        JPanel buttons = new JPanel(new BorderLayout(7,7));
        JButton FindButt = new JButton("Find");
        FindButt.addActionListener(listener);
        FindButt.setActionCommand("find");
        
        buttons.add(FindButt, BorderLayout.EAST);
        buttons.add(history, BorderLayout.CENTER);
        
        content.add(title, BorderLayout.NORTH);
        content.add(buttons, BorderLayout.CENTER);
    }
//}}}

//{{{ void _show()
public void _show()
{
    prepareGUI();
    
    Dimension screen = getToolkit().getScreenSize();
	Dimension size = new Dimension(getWidth(), getHeight());
    setLocation((screen.width - size.width) / 2,
			(screen.height - size.height) / 2);
    GUIUtilities.loadGeometry(this,"jump.type_tag");
    pack();
    show();
    GUIUtilities.requestFocus(this,history);   
}
//}}}

//{{{ void ok()
    public void ok()
    {
        GUIUtilities.saveGeometry(this,"jump.type_tag");
        history.addCurrentToHistory();
        String search = history.getText().trim();
        dispose();
        JumpPlugin.pja.getTagBySelection(search);
    }
//}}}
    
//{{{ void cancel()
    public void cancel()
    {
        GUIUtilities.saveGeometry(this,"jump.type_tag");
        dispose();
    }

//}}}

//{{{ class HistoryActionListener
private class TypeTagActionListener implements ActionListener
{
    public void actionPerformed(ActionEvent e) 
    {
        if (e.getActionCommand().equals("find"))
        {
            ok();       
        }
    }
}
//}}}    

}
