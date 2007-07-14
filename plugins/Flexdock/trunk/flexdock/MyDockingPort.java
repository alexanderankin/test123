/**
 * 
 */
package flexdock;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.defaults.DefaultDockingPort;
import org.gjt.sp.jedit.jEdit;

@SuppressWarnings("serial")
public class MyDockingPort extends DefaultDockingPort {

	FlexDockWindowManager dwm;
	
	public MyDockingPort(FlexDockWindowManager dwm) {
		this.dwm = dwm;
	}
	
	@Override
	public boolean dock(Dockable dockable, String region) {
		if (dockable.getPersistentId().equals("Main"))
			this.setSingleTabAllowed(false);
		return super.dock(dockable, region);
	}

	@Override
	protected int getInitTabPlacement() {
		String placement = jEdit.getProperty(OptionPane.TAB_PLACEMENT_OPTION);
		if (placement.equalsIgnoreCase("top"))
			return JTabbedPane.TOP;
		if (placement.equalsIgnoreCase("bottom"))
			return JTabbedPane.BOTTOM;
		if (placement.equalsIgnoreCase("left"))
			return JTabbedPane.LEFT;
		return JTabbedPane.RIGHT;
	}

	@Override
	protected JTabbedPane createTabbedPane() {
		JTabbedPane pane = super.createTabbedPane();
		pane.addMouseListener(new MyMouseListener());
		return pane;
	}
	
	public class MyMouseListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent ev) {
			super.mouseClicked(ev);
			if (ev.getButton() != MouseEvent.BUTTON3)
				return;
			JPopupMenu popup = new JPopupMenu();
			JTabbedPane pane = (JTabbedPane)ev.getComponent();
			final Component c = pane.getSelectedComponent();
			JMenuItem closeItem = new JMenuItem("Close");
			closeItem.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					String id = DockingManager.getDockable(c).getPersistentId();
					dwm.hideDockableWindow(id);
				}
			});
			popup.add(closeItem);
			JMenuItem floatItem = new JMenuItem("Float");
			floatItem.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					String id = DockingManager.getDockable(c).getPersistentId();
					dwm.floatDockableWindow(id);
				}
			});
			popup.add(floatItem);
			popup.show(MyDockingPort.this.getDockedComponent(), ev.getX(), ev.getY());
		}
	}
}