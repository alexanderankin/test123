package flexdock;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

@SuppressWarnings("serial")
public class OptionPane extends AbstractOptionPane {

	static final String PREFIX = Plugin.OPTION_PREFIX;
	static public final String TAB_PLACEMENT_OPTION = PREFIX + "tabPlacement";
	JComboBox tabPlacementCombo;
	
	public OptionPane()
	{
		super("Flexdock");
		setBorder(new EmptyBorder(5, 5, 5, 5));

		JPanel tabPlacementPanel = new JPanel();
		tabPlacementPanel.add(new JLabel("Tab placement:"));
		tabPlacementCombo = new JComboBox();
		tabPlacementPanel.add(tabPlacementCombo);
		tabPlacementCombo.addItem("Top");
		tabPlacementCombo.addItem("Bottom");
		tabPlacementCombo.addItem("Left");
		tabPlacementCombo.addItem("Right");
		tabPlacementCombo.setSelectedItem(jEdit.getProperty(TAB_PLACEMENT_OPTION));
		addComponent(tabPlacementPanel);
	}

	/***************************************************************************
	 * Implementation
	 **************************************************************************/
	public void save()
	{
		jEdit.setProperty(TAB_PLACEMENT_OPTION, (String) tabPlacementCombo.getSelectedItem());
	}
}
