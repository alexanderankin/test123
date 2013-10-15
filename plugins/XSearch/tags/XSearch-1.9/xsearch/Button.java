package xsearch;

import javax.swing.Icon;
import javax.swing.JPopupMenu;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.RolloverButton;

public class Button extends RolloverButton
{
	
	/**
	 * 
	 * @param name action name
	 * @param iconName corresponding jedit icon name
	 */
	public Button(String name, String iconName) {
		
		iconName = iconName + ".png";
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
	 * @param menu a JPopupMenu
	 */
	public void addPopupMenu(JPopupMenu menu) {
		setComponentPopupMenu(menu);

	}

}
