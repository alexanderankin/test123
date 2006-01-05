package infoviewer.actions;

import infoviewer.InfoViewer;

import java.awt.event.ActionEvent;

public class FindNext extends Find
{
	public FindNext() {
		super("infoviewer.findnext");
	}
	public void actionPerformed(ActionEvent e)
	{
        InfoViewer v = getViewer(e);
        
	}

}
