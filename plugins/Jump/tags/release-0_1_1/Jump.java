import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;

public class Jump
{
    
    public boolean isJumpEnabled()
    {
        if (!jEdit.getBooleanProperty("jump.enable", false))
        {
            GUIUtilities.message(jEdit.getActiveView(), "JumpPlugin.enable", null);
            return false;    
        }
        return true;
    }

    public void showFilesJump()
    {
        if (!isJumpEnabled()) return;
        if (!JumpPlugin.isListenerAdded) JumpPlugin.init();
        new FilesJumpAction().showList();
    }
    
    public void showTagsJump()
    {
        if (!isJumpEnabled()) return;
        if (!JumpPlugin.isListenerAdded) JumpPlugin.init();
        TagsJumpAction tja = new TagsJumpAction();
        if (tja.parse())
        {
            tja.showList();
        }
    }
    
    public void showProjectJump()
    {
        if (!isJumpEnabled()) return;
        if (!JumpPlugin.isListenerAdded) JumpPlugin.init();
        JumpPlugin.pja.JumpToTag();
    }
    
    public void reloadTagsOnProject()
    {
        if (!isJumpEnabled()) return;
        if (!JumpPlugin.isListenerAdded) JumpPlugin.init();
        JumpPlugin.reloadTagsOnProject();  
    }
    
    public void historyJump()
    {
        if (!isJumpEnabled()) return;
        if (!JumpPlugin.isListenerAdded) JumpPlugin.init();
        JumpPlugin.pja.JumpToPreviousTag();
    }
    
    public void jumpByInput()
    {
        if (!isJumpEnabled()) return;
        if (!JumpPlugin.isListenerAdded) JumpPlugin.init();
        JumpPlugin.pja.JumpToTagByInput();
    }
}
