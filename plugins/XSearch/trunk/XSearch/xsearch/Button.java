package xsearch;

import java.lang.reflect.Method;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.OperatingSystem;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.RolloverButton;

public class Button extends RolloverButton
{
	
	public Button(String name) {
		String iconName = "jeditresource:/XSearch.jar!/icons/" + name + ".png";
		Icon icon = GUIUtilities.loadIcon(iconName);
		String tooltipText = jEdit.getProperty("search.button." + name );
		String mn = jEdit.getProperty("search.button." + name + ".mnemonic");
		setIcon(icon);
		setToolTipText(tooltipText);
		if (mn != null) {
			char nmemonic = mn.charAt(0);
			setMnemonic(nmemonic);
		}

		
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
