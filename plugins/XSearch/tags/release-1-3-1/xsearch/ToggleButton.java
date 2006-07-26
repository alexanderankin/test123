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

	Icon pressedIcon ;
	Icon releasedIcon ;
	
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
		
		pressedIcon = GUIUtilities.loadIcon(onName + ".png");
		releasedIcon = GUIUtilities.loadIcon(offName + ".png");
		
		String tooltipText = jEdit.getProperty("search.button." + propName );
		setToolTipText(tooltipText);
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
		boolean p = jEdit.getBooleanProperty("xsearch." + propName + ".pressed");
		setSelected(p);
		if (p) {
			setIcon(pressedIcon);
		}
		else setIcon(releasedIcon);

	}

	
	class ToggleMessagesHandler implements ActionListener {
		boolean lastValue;

		public void actionPerformed(ActionEvent e)
		{
			boolean p = isSelected();
			if (p) {
				setIcon(pressedIcon);
			}
			else setIcon(releasedIcon);
			save();
			
		}
		
	}
}
