import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.OptionsDialog;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.util.Log;

import java.util.*;

import ctags.bg.*;


public class Jump
{
    
    public boolean isJumpEnabled()
    {
        if (jEdit.getBooleanProperty("jump.enable", false) == false)
        {
            GUIUtilities.message(jEdit.getActiveView(), "JumpPlugin.enable", new Object[0]);
            return false;    
        }
        return true;
    }
    
    public void showFilesJump()
    {
        if (isJumpEnabled() == false) return;
        if (JumpPlugin.isListenerAdded == false) JumpPlugin.addListener();
        new FilesJumpAction().showList();
    }
    
    public void showTagsJump()
    {
        if (isJumpEnabled() == false) return;
        if (JumpPlugin.isListenerAdded == false) JumpPlugin.addListener();
        TagsJumpAction tja = new TagsJumpAction();
        if (tja.parse())
        {
            tja.showList();
        }
    }
    
    public void showProjectJump()
    {
        if (isJumpEnabled() == false) return;
        if (JumpPlugin.isListenerAdded == false) JumpPlugin.addListener();
        JumpPlugin.pja.JumpToTag();
    }
    
    public void reloadTagsOnProject()
    {
        if (isJumpEnabled() == false) return;
        JumpPlugin.reloadTagsOnProject();  
    }
 
}
