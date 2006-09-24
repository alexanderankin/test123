package sidekick;

import java.lang.reflect.Method;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;

import org.gjt.sp.jedit.OperatingSystem;

public class RolloverButton extends org.gjt.sp.jedit.gui.RolloverButton
{
	public RolloverButton() {}
	
	public RolloverButton(Icon icon) {
		super(icon);
	}
	
	/**
	 * If running java 1.5, adds a popup menu.
	 * If 1.4, does nothing (but at least compiles). 
	 * If someone wants to make this work under java 1.4, be my guest.
	 * 
	 * @param menu a JPopupMenu
	 */
	public void addPopupMenu(JPopupMenu menu) {
		if(OperatingSystem.hasJava15()) try {
			Class c = JComponent.class;
			Method m = c.getMethod("setComponentPopupMenu", new Class[] {JPopupMenu.class});
			m.invoke(this, new Object[] {menu});
		}
		catch (Exception e) {}
		else 
		{
			// do something in java14 
		}

	}
}
