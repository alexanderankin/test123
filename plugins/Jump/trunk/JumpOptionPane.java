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
import javax.swing.Box;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.browser.VFSBrowser;
//}}}

//{{{ class JumpOptionPane

/**
 *  Jump! options
 *
 */
public class JumpOptionPane extends AbstractOptionPane implements ActionListener
{

    private JTextField pathName;
    private JCheckBox enableJump;
    private JCheckBox parseOnSave;
    private JButton BrowseButt;
    private FontSelector font; 
    
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
        //FileSelector
        Log.log(Log.DEBUG,this,"Action command - "+e.getActionCommand());
        if (e.getActionCommand().equals("chooser"))
        {
            _save();
            String[] path = GUIUtilities.showVFSFileDialog(jEdit.getActiveView(), new String(), VFSBrowser.OPEN_DIALOG, false);
            if (path.length >0 )
            {
                pathName.setText(path[0]);
                _save();
            }
        }
        
        if (e.getActionCommand().equals("Test"))
        {
            _save();
            if (JumpPlugin.isListenerAdded == false) JumpPlugin.init();
            String mess = new String();
            if (JumpPlugin.getListener().CtagsTest()==true) mess = "JumpPlugin.ctags.test.complete";
            else mess = "JumpPlugin.ctags.test.fail";
            JumpPlugin.getListener().errorMsg(mess);
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
        
        Dimension big = new Dimension(0,25);
        enableJump = new JCheckBox(jEdit.getProperty(JumpPlugin.OPTION_PREFIX + "enable.label"));
        enableJump.setSelected(jEdit.getBooleanProperty("jump.enable", false));
        addComponent(enableJump);
        addComponent(new Box.Filler(big,big,big));
        
        
        JPanel ctags_panel = new JPanel(new BorderLayout(8,8));
        
            JPanel inner_panel = new JPanel(new BorderLayout(8,8));
                ctags_panel.setOpaque(false);
                
                parseOnSave = new JCheckBox(jEdit.getProperty(JumpPlugin.OPTION_PREFIX + "parse_on_save.label"));
                parseOnSave.setSelected(jEdit.getBooleanProperty("jump.parse_on_save", false));
                inner_panel.add(parseOnSave,BorderLayout.NORTH);
                
                JLabel lab = new JLabel(jEdit.getProperty(JumpPlugin.OPTION_PREFIX + "ctags.def.path.label"));
                inner_panel.add(lab,BorderLayout.CENTER);
                
                
                JPanel path_panel = new JPanel(new BorderLayout(8,8));
                
                    pathName = new JTextField(jEdit.getProperty(JumpPlugin.OPTION_PREFIX + "ctags.def.path.label"), 45);
                    pathName.setText(jEdit.getProperty("jump.ctags.path", jEdit.getProperty(JumpPlugin.OPTION_PREFIX + "ctags.def.path")));
                    
                    BrowseButt = new JButton("...");
                    BrowseButt.setActionCommand("chooser");
                    BrowseButt.addActionListener(this);
                
                path_panel.add(pathName, BorderLayout.CENTER);
                path_panel.add(BrowseButt, BorderLayout.EAST);
            inner_panel.add(path_panel,BorderLayout.SOUTH);
        
        ctags_panel.add(inner_panel,BorderLayout.NORTH);

//***************************************************************   
        //Fontselector
        JPanel font_panel = new JPanel(new BorderLayout(8,8));
        JLabel lab1 = new JLabel("List font:");
        font = new FontSelector(getListFont());
        
        

        
        font_panel.add(lab1,BorderLayout.WEST);
        font_panel.add(font,BorderLayout.CENTER);
        
        ctags_panel.add(font_panel,BorderLayout.CENTER);
        
//***************************************************************  

        JPanel test_panel = new JPanel(new BorderLayout(8,8));
        
            JLabel lab2 = new JLabel("Test ctags:");
            
            JButton butt = new JButton("Test");
            butt.setActionCommand("Test");
            butt.addActionListener(this);
            test_panel.add(lab2,BorderLayout.WEST);
            test_panel.add(butt, BorderLayout.CENTER);
        ctags_panel.add(test_panel, BorderLayout.SOUTH);
        
        addComponent(ctags_panel);
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
        jEdit.setFontProperty("jump.list.font", font.getFont());
        jEdit.setBooleanProperty("jump.parse_on_save", parseOnSave.isSelected());
    }
//}}}

//{{{ Font getListFont()
    private Font getListFont()   
    {
        return (jEdit.getFontProperty("jump.list.font", new Font("Monospaced", Font.PLAIN, 11)));
    }
//}}}
}
//}}}
