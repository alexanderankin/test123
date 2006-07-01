package xsearch;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JToggleButton;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;

public class ToggleButton extends JToggleButton
{
	String propName;
	String pressed, released;
	
	public ToggleButton(String propName) {
		this(propName, propName, propName);
	}
	
	/**
	 * 
	 * @param propName the property name with a prefix of "xsearch."
	 * @param onName  the name of the icon for when it is pressed
	 * @param offName the name of the icon for when it is released
	 */
	public ToggleButton(String propName, String onName, String offName) {
		this.propName = propName;
		pressed = onName;
		released = offName;
		String onIconName = "jeditresource:/XSearch.jar!/icons/" + onName + ".png";
		String offIconName = "jeditresource:/XSearch.jar!/icons/" + offName + ".png";		
		Icon onIcon = GUIUtilities.loadIcon(onIconName);
		Icon offIcon = GUIUtilities.loadIcon(offIconName);
		setIcon(offIcon);
		setPressedIcon(onIcon);
		
		String tooltipText = jEdit.getProperty("search.button." + propName );
		String mn = jEdit.getProperty("search.button." + propName + ".mnemonic");
		if (mn != null) {
			char nmemonic = mn.charAt(0);
			setMnemonic(nmemonic);
		}
		
		load();
		addActionListener(new ToggleMessagesHandler());
	}
	
	void save() {
		boolean isPressed = isSelected();
		jEdit.setBooleanProperty("xsearch." + propName + ".pressed", isPressed);
	}
	
	void load() {
		boolean isPressed = jEdit.getBooleanProperty("xsearch." + propName + ".pressed");
		setSelected(isPressed);
		String tooltip = isPressed ? pressed: released;
		String toolTipText = jEdit.getProperty("search.button." +propName);
		if (toolTipText != null) {setToolTipText(toolTipText); }

	}

	
	class ToggleMessagesHandler implements ActionListener {
		boolean lastValue;

		public void actionPerformed(ActionEvent e)
		{
			save();
			
		}
		
	}
}
