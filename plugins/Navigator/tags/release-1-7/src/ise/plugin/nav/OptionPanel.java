package ise.plugin.nav;

import javax.swing.*;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

/**
 * @author Dale Anson
 */
@SuppressWarnings("serial")
public class OptionPanel extends AbstractOptionPane
{
	private static final String Name = "navigator";
	
	private JCheckBox groupByFile = null;
	private JCheckBox showOnToolbar = null;

	public OptionPanel()
	{
		super(Name);
	}

	public void _init()
	{
		addComponent(new JLabel("<html><h3>Navigator</h3>"));
		groupByFile = new JCheckBox(jEdit.getProperty("navigator.options.groupByFile.label"));
		groupByFile.setSelected(getGroupByFileProp());
        addComponent( groupByFile );
		showOnToolbar = new JCheckBox(jEdit.getProperty("navigator.options.showOnToolbar.label"));
        showOnToolbar.setSelected(getShowOnToolbarProp());
        addComponent( showOnToolbar );
	}

	public void _save()
	{
		jEdit.setBooleanProperty(Name + ".groupByFile", groupByFile.isSelected());
		jEdit.setBooleanProperty(Name + ".showOnToolbar", showOnToolbar.isSelected());
	    NavigatorPlugin.setToolBars();
	}

	public static boolean getShowOnToolbarProp()
	{
		return jEdit.getBooleanProperty(Name + ".showOnToolbar");
	}
	public static boolean getGroupByFileProp()
	{
		return jEdit.getBooleanProperty(Name + ".groupByFile");
	}
}
