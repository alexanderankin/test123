package activator;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

/**
 * Combined Activator panel. Now we don't need to occupy two 
 * DockPanel spots.
 * 
 * @author alan dot ezust at gmail.com
 *
 */

public class CombinedPanel extends JTabbedPane
{
    public CombinedPanel(boolean selectReload) {
        JPanel activator = ActivationPanel.getInstance();
		JPanel reloader = ReloadPanel.getInstance();
		JScrollPane jsp = new JScrollPane (reloader);
		JScrollBar jsb = jsp.getVerticalScrollBar();
		int inc = jsb.getUnitIncrement();
		jsb.setUnitIncrement(inc*20);
		addTab("Activator", activator);
		addTab("Reloader", jsp);
        if (selectReload==true) {
            setSelectedComponent(jsp);
        }
    }
    
	public CombinedPanel() {
		this(false);
	}
}
