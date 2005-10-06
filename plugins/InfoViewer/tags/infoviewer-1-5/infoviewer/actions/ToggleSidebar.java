package infoviewer.actions;

import infoviewer.InfoViewer;

import java.awt.event.ActionEvent;

import org.gjt.sp.jedit.EditAction;

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
	InfoViewer viewer;

	public ToggleSidebar(InfoViewer v)
	{
		super("infoviewer.toggle_sidebar");
		viewer = v;
	}

	public void actionPerformed(ActionEvent e)
	{
		viewer.toggleSideBar();
	}

}
