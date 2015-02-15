package infoviewer.actions;

import infoviewer.InfoViewer;

import java.awt.event.ActionEvent;

public class Find extends InfoViewerAction
{
	public Find() {
		super("infoviewer.find");
	}
	protected Find(String name) {
		super(name);
	}
	public void actionPerformed(ActionEvent e)
	{
        InfoViewer v = getViewer(e);
        
		getViewer(e).focusAddressBar();
	}

    protected static String lastString; 
}
