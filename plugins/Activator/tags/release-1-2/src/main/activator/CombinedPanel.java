package activator;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.DefaultFocusComponent;
import org.gjt.sp.jedit.gui.DockableWindowManager;

/**
 * Combined Activator panel. Now we don't need to occupy two 
 * DockPanel spots.
 * 
 * @author alan dot ezust at gmail.com
 *
 */

public class CombinedPanel extends JTabbedPane implements DefaultFocusComponent
{
	JScrollPane jsp;
	JScrollBar jsb;
	String dockableName;
	DockableWindowManager dwm;

	public CombinedPanel(View v, boolean selectReload) 
	{
		dwm = v.getDockableWindowManager();
		JPanel reloader = ReloadPanel.getInstance();
		JPanel activator = ActivationPanel.getInstance();
		
		jsp = new JScrollPane (reloader);
		jsp.addKeyListener(new KeyHandler());
		jsb = jsp.getVerticalScrollBar();
		int inc = jsb.getUnitIncrement();
		jsb.setUnitIncrement(inc*20);
		addTab("Activator", activator);
		addTab("Reloader", jsp);
		if (selectReload==true) 
		{
			setSelectedComponent(jsp);
			dockableName = ActivatorPlugin.RELOADER;
		}
		else 
		{
			dockableName = ActivatorPlugin.NAME;
		}
	}
    
	public void dismiss() 
	{
		dwm.hideDockableWindow(dockableName);
	}
	
	class KeyHandler implements KeyListener 
	{

		public void keyPressed(KeyEvent e)
		{
			if (e.getID() == KeyEvent.VK_ESCAPE)
				dismiss();
		}

		public void keyTyped(KeyEvent e) { }
		public void keyReleased(KeyEvent e) { }
	}
	
	public void focusOnDefaultComponent()
	{
		try {
			DefaultFocusComponent dcc = (DefaultFocusComponent) getSelectedComponent();
			dcc.focusOnDefaultComponent();
		}
		catch (Exception e) 
		{
			jsp.requestFocus();			
		}

	}
}
