package ctags.sidekick.options;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.border.EmptyBorder;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.StandardUtilities;

import sidekick.ModeOptionPane;

@SuppressWarnings("serial")
public abstract class AbstractModeOptionPane extends AbstractOptionPane {

	JComboBox modeCB;
	JCheckBox useDefaultsCheck;
	ModeOptionPane pane;
	
	public AbstractModeOptionPane(String internalName) {
		super(internalName);
		setBorder(new EmptyBorder(5, 5, 5, 5));

		Mode[] modes = jEdit.getModes();
		Arrays.sort(modes, new StandardUtilities.StringCompare<Mode>(true));
		String[] modeNames = new String[modes.length + 1];
		modeNames[0] = jEdit.getProperty("options.editing.global");
		for(int i = 0; i < modes.length; i++)
			modeNames[i + 1] = modes[i].getName();
		modeCB = new JComboBox(modeNames);
		modeCB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				modeSelected();
			}
		});
		addComponent(jEdit.getProperty("options.editing.mode"), modeCB);

		useDefaultsCheck = new JCheckBox(jEdit.getProperty("options.editing.useDefaults"));
		useDefaultsCheck.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				useDefaultsChanged();
			}
		});
		addComponent(useDefaultsCheck);

		pane = addOptionPane();
		
		modeCB.setSelectedIndex(0);
	}

	// Creates and adds the panel to the abstract option pane
	abstract protected ModeOptionPane addOptionPane();

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
		useDefaultsCheck.setSelected(pane.getUseDefaults(mode));
	}

	public void _save()
	{
		pane.save();
	}

	public void useDefaultsChanged() {
		pane.setUseDefaults(useDefaultsCheck.isSelected());
	}

}
