package ctags.sidekick.options;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.border.EmptyBorder;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.jEdit;

@SuppressWarnings("serial")
public abstract class AbstractModeOptionPane extends AbstractOptionPane
	implements ActionListener {

	JComboBox modeCB;
	JCheckBox useDefaultsCheck;
	IModeOptionPane pane;
	
	public AbstractModeOptionPane(String internalName) {
		super(internalName);
		setBorder(new EmptyBorder(5, 5, 5, 5));

		Mode[] modes = jEdit.getModes();
		Arrays.sort(modes,new MiscUtilities.StringICaseCompare());
		String[] modeNames = new String[modes.length + 1];
		modeNames[0] = "<global defaults>";
		for(int i = 0; i < modes.length; i++)
			modeNames[i + 1] = modes[i].getName();
		modeCB = new JComboBox(modeNames);
		modeCB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				modeSelected();
			}
		});
		addComponent("Change settings for mode:", modeCB);

		useDefaultsCheck = new JCheckBox("Use default settings");
		useDefaultsCheck.addActionListener(this);
		addComponent(useDefaultsCheck);

		pane = addOptionPane();
		
		modeCB.setSelectedIndex(0);
	}

	// Creates and adds the panel to the abstract option pane
	abstract protected IModeOptionPane addOptionPane();

	private void modeSelected() {
		int index = modeCB.getSelectedIndex();
		String mode;
		if (index == 0) {
			mode = null;
			useDefaultsCheck.setEnabled(false);
		} else {
			mode = (String) modeCB.getItemAt(index); 
			useDefaultsCheck.setEnabled(true);
		}
		pane.modeSelected(mode);
	}

	public void save()
	{
		pane.save();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == useDefaultsCheck)
			pane.setUseDefaults(useDefaultsCheck.isSelected());
	}

}
