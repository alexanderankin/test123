 //{{{ imports
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.OptionsDialog;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.util.Log;

import java.util.*;
import java.io.*;
import ctags.bg.*;

import projectviewer.*;
import projectviewer.vpt.*;
import projectviewer.event.*;
//}}}

//{{{ class JumpPlugin
/**
 *  Description of the Class
 *
 *@author     pa
 *@created    30 јпрель 2003 г.
 */
public class JumpPlugin extends EditPlugin
{

//{{{ fields
    public final static String NAME = "JumpPlugin";
    public final static String MENU = "JumpPlugin.menu";
    public final static String PROPERTY_PREFIX = "plugin.JumpPlugin.";
    public final static String OPTION_PREFIX = "options.JumpPlugin.";
    
    public static Jump jump_actions;
    
    public static ProjectJumpAction pja = new ProjectJumpAction();
    public static JumpEventListener listener = new JumpEventListener();
    public static boolean isListenerAdded = false;
//}}}

//{{{ Constructor
   /**
     *  Constructor for the JumpPlugin object
     */
    // public JumpPlugin()
    // {
        // super();
    // }
//}}}

//{{{ void start()
    public void start()
    {
        jump_actions = new Jump();

    }
//}}}
    
//{{{ JumpEventListener getListener()
    public static JumpEventListener getListener()
    {
        return listener;   
    }
//}}}

//{{{ boolean reloadTagsOnProject()
    public static boolean reloadTagsOnProject()
    {  if (jump_actions.isJumpEnabled() == false) return false;
       ProjectViewer pv = ProjectViewer.getViewer(jEdit.getActiveView());
       VPTProject pr = PVActions.getCurrentProject(jEdit.getActiveView());
           return (listener.reloadTags(pv,pr));
    }
//}}}


//{{{ void addListener()
    public static void addListener()
    {
         //VPTProject Pr;
         //Pr = PVActions.getCurrentProject(jEdit.getActiveView());
         View v = jEdit.getActiveView();
         if (PVActions.getCurrentProject(v) != null)
         {
             ProjectViewer.getViewer(v).addProjectViewerListener(listener, v); 
         }
         isListenerAdded = true;
    }
//}}}
    
//{{{ void createMenuItems
    public void createMenuItems(Vector menuItems)
    {
        menuItems.addElement(GUIUtilities.loadMenu(MENU));
    }
//}}}

//{{{ void createOptionPanes
    public void createOptionPanes(OptionsDialog od)
    {
        od.addOptionPane(new JumpOptionPane());
    }
//}}}
    
//{{{ void stop()
    public void stop()
    {
        this.getListener().ctags_bg.saveBuffer(getListener().ctags_buff, getListener().PROJECT_TAGS.toString() );   
    }
//}}}
}
//}}}
