//{{{ IMPORTS
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.OptionsDialog;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.util.Log;

import java.util.*;
import java.io.*;
import ctags.bg.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.AbstractOptionPane;
//}}}

//{{{ class JumpOptionPane

/**
 *  Jump! options
 *
 *@author     pavlikus
 *@created    2 May 2003
 */
public class JumpOptionPane extends AbstractOptionPane implements ActionListener
{

    private JTextField pathName;
    private JCheckBox enableJump;
    
//{{{ Constructor
    /**
     *  Constructor for the JumpOptionPane object
     */
    public JumpOptionPane()
    {
        super("JumpPlugin");
    }
//}}}

//{{{ actionPerformed

    /**
     *  Button TEST action (reload ctags on curr. project)
     *
     *@param  e  Description of the Parameter
     */
    public void actionPerformed(ActionEvent e)
    {
        _save();
        if (JumpPlugin.reloadTagsOnProject() == true)
        {
            GUIUtilities.message(jEdit.getActiveView(), "JumpPlugin.ctags.test.complete", new Object[0]);
        }
    }
//}}}


//{{{ void _init()
    /**
     *  
     */
    public void _init()
    {
        setBorder(new EmptyBorder(15, 15, 15, 15));
        
        enableJump = new JCheckBox(jEdit.getProperty(JumpPlugin.OPTION_PREFIX + "enable.label"));
        enableJump.setSelected(jEdit.getBooleanProperty("jump.enable", false));
        addComponent(enableJump);
        
        JLabel lab = new JLabel(jEdit.getProperty(JumpPlugin.OPTION_PREFIX + "ctags.def.path.label"));
        addComponent(lab);

        pathName = new JTextField(jEdit.getProperty(JumpPlugin.OPTION_PREFIX + "ctags.def.path.label"), 45);
        pathName.setText(jEdit.getProperty("jump.ctags.path", jEdit.getProperty(JumpPlugin.OPTION_PREFIX + "ctags.def.path")));
        addComponent(pathName);

        JLabel lab1 = new JLabel("If any of project is active now, you can test ctags by pressing TEST button");
        addComponent(lab1);

        JButton butt = new JButton("TEST");
        butt.addActionListener(this);
        addComponent(butt);

    }
//}}}

//{{{ void _save()
    /**
     *  Description of the Method
     */
    public void _save()
    {
        jEdit.setProperty("jump.ctags.path", pathName.getText().trim());
        jEdit.setBooleanProperty("jump.enable", enableJump.isSelected());
    }
//}}}

}
//}}}
