package infoviewer.actions;

import infoviewer.InfoViewer;

import java.awt.event.ActionEvent;

import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.jEdit;

/**
 * An action to toggle whether we see the sidebar or not.
 * 
 * TODO: Make the menu item that appears a checkboxMenuItem
 * 
 * @author ezust
 * 
 */
public class ToggleSidebar extends InfoViewerAction
{
	public static final String name ="infoviewer.toggle_sidebar"; 
	InfoViewer viewer;

	public boolean isToggle() {
		return true;
	}
	
	public ToggleSidebar()
	{
		super(name);
		setSelected(jEdit.getBooleanProperty(name + ".selected"));
	}

/*	public String getCode()
	{
		// TODO Auto-generated method stub
		return "InfoViewer.getViewer(view).toggleSideBar();";
	}
*/

	public void actionPerformed(ActionEvent e)
	{
		getViewer(e).toggleSideBar();
	}

}
