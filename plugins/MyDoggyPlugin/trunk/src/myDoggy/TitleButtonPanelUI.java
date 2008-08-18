/**
 * 
 */
package myDoggy;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import org.noos.xing.mydoggy.ToolWindowType;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;
import org.noos.xing.mydoggy.plaf.ui.look.FullToolWindowTitleButtonPanelUI;

public class TitleButtonPanelUI extends FullToolWindowTitleButtonPanelUI {

    public static ComponentUI createUI(JComponent c) {
        return new TitleButtonPanelUI();
    }

    public TitleButtonPanelUI() {
    }
    
	@SuppressWarnings("serial")
	private class FloatingFreeAction extends TitleBarAction {
		public FloatingFreeAction() {
            super("toolWindow.floatingFreeButton." + toolWindow.getId(),
            	MyDoggyKeySpace.FLOATING_INACTIVE, "Floating without an anchor button");
		}
		public void actionPerformed(ActionEvent e) {
			toolWindow.setType(ToolWindowType.FLOATING_FREE);
		}
		public void propertyChange(PropertyChangeEvent evt) {
		}
	}
	@Override
	protected void installComponents() {
		super.installComponents();
        addTitleBarAction(new FloatingFreeAction());
	}
}