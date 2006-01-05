package infoviewer.actions;

import java.awt.event.ActionEvent;

public class OpenLocation extends InfoViewerAction
{
	public OpenLocation() {
		super("infoviewer.openlocation");
	}
	public void actionPerformed(ActionEvent e)
	{
		getViewer(e).focusAddressBar();
	}

}
