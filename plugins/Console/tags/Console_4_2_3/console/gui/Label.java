package console.gui;

import java.awt.Font;

import javax.swing.JLabel;

import org.gjt.sp.jedit.jEdit;

public class Label extends JLabel
{
	public Label(String propertyName) 
	{
		String text = jEdit.getProperty(propertyName);
		setText(text);
	}
	public Label(String propertyName, int halign) {
		this(propertyName);
		setHorizontalAlignment(halign);
	}
}