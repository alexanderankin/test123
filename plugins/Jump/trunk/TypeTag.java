//{{{ imports
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.search.*;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.jedit.gui.*;

import java.awt.event.*;
import java.awt.Component;
import java.awt.Font;
import java.awt.BorderLayout;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import java.util.*;
import java.io.*;

import projectviewer.*;
import projectviewer.vpt.*;
import projectviewer.event.*;

import ctags.bg.*;
import java.awt.Dimension;
//}}}

//{{{ class TypeTag
public class TypeTag extends EnhancedDialog
{

    private HistoryTextField history;
    
//{{{ public TypeTag
    public TypeTag(View view)
    {
        super (jEdit.getActiveView(), "Jump to tag", true);
    }
//}}}
    
//{{{ void prepareGUI()
    private void prepareGUI()
    {    
        JPanel content = new JPanel(new BorderLayout(7,7));
		content.setBorder(new EmptyBorder(0,12,12,12));
		setContentPane(content);
        
        JLabel title = new JLabel("Type a tag to jump to");

        String ModelName ="jump.tag_history.project."+JumpPlugin.listener.PROJECT_NAME;
        history = new HistoryTextField(ModelName, true, false);
        history.setColumns(35); 
        
        JPanel buttons = new JPanel(new BorderLayout(7,7));
        JButton FindButt = new JButton("Find");
        FindButt.addActionListener(new TypeTagActionListener());
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
//}}}
