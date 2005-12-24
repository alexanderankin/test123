package console.gui;

import javax.swing.JButton;

import org.gjt.sp.jedit.jEdit;

public class Button extends JButton
{
	public Button(String propertyName) 
	{
		String label = jEdit.getProperty(propertyName);
		setText(label);
	}

}
