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
 */
public class JumpPlugin extends EditPlugin
{

//{{{ fields
    public final static String NAME = "JumpPlugin";
    public final static String MENU = "JumpPlugin.menu";
    public final static String PROPERTY_PREFIX = "plugin.JumpPlugin.";
    public final static String OPTION_PREFIX = "options.JumpPlugin.";
    
    public static Jump jump_actions;
    public static ProjectJumpAction pja;
    public static JumpEventListener listener;
    public static boolean isListenerAdded = false;
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
    {  
       if (!jump_actions.isJumpEnabled()) return false;
       if (ProjectViewer.getViewer(jEdit.getActiveView()) != null) 
       {
            
           ProjectViewer pv = ProjectViewer.getViewer(jEdit.getActiveView());
           VPTProject pr = PVActions.getCurrentProject(jEdit.getActiveView());
               return (listener.reloadTags(pv,pr));
       }
       
       return false;
    }
//}}}


//{{{ void init()
//  Init all classes here, instead of in start() to avoid long starup time
    public static void init()
    {
         //if (isListenerAdded == false) return;
         pja = new ProjectJumpAction();
         listener = new JumpEventListener();
         
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
        if (jEdit.getBooleanProperty("jump.enable", false) == false || isListenerAdded == false) return;
        try
        {
        this.getListener().ctags_bg.saveBuffer(getListener().ctags_buff, getListener().PROJECT_TAGS.toString() );
        }
        catch (Exception e)
        {
            Log.log(Log.DEBUG,this,"JumpPlugin: failed to save ctags_buff on stop()");   
        }
    }
//}}}
}
//}}}
