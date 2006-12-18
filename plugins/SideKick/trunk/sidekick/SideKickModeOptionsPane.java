package sidekick;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import org.gjt.sp.jedit.jEdit;



/**
 * 
 * Mode-Specific options for SideKick.
 * 
 * @author ezust
 *
 */
public class SideKickModeOptionsPane extends ModeOptionsPane
{
	JCheckBox showStatusWindow;
	JCheckBox treeFollowsCaret;
	JComboBox autoExpandTreeDepth;

	public SideKickModeOptionsPane() 
	{
		super("sidekick.mode");
	}
		
	protected void _init() {
			
		showStatusWindow = new JCheckBox(jEdit.getProperty("options." + SideKick.SHOW_STATUS));
		addComponent(showStatusWindow);
		
		treeFollowsCaret = new JCheckBox(jEdit.getProperty("options.sidekick.tree-follows-caret"));
		addComponent(treeFollowsCaret);

		autoExpandTreeDepth = new JComboBox();
		addComponent(jEdit.getProperty("options.sidekick.auto-expand-tree-depth"), autoExpandTreeDepth);
//		autoExpandTreeDepth.addActionListener(new ActionHandler());
		autoExpandTreeDepth.addItem(ModeOptionsDialog.ALL);
		for (int i = 0; i <= 10; i++)
			autoExpandTreeDepth.addItem(String.valueOf(i));
		addComponent(autoExpandTreeDepth);
		_load();
	}
	
	protected void _load() 
	{
		
		boolean tfc = getBooleanProperty(SideKick.FOLLOW_CARET);
		treeFollowsCaret.setSelected(tfc);
		showStatusWindow.setSelected(getBooleanProperty(SideKick.SHOW_STATUS));
		int item = getIntegerProperty(SideKick.AUTO_EXPAND_DEPTH, 1) + 1;
		autoExpandTreeDepth.setSelectedIndex(item);
	}
	
	protected void _save() 
	{
		setBooleanProperty(SideKick.FOLLOW_CARET, treeFollowsCaret.isSelected());
		setBooleanProperty(SideKick.SHOW_STATUS, showStatusWindow.isSelected());
		String value = (String)autoExpandTreeDepth.getSelectedItem();
		String depth = value.equals(ModeOptionsDialog.ALL) ? "-1" : value;
		setProperty(SideKick.AUTO_EXPAND_DEPTH, depth);
	}

	protected void _reset()
	{
		clearModeProperty(SideKick.FOLLOW_CARET);
		clearModeProperty(SideKick.AUTO_EXPAND_DEPTH);
		clearModeProperty(SideKick.SHOW_STATUS);
	}	

}
