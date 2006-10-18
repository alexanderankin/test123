package sidekick;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Enumeration;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.gjt.sp.jedit.BeanShell;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.OptionGroup;
import org.gjt.sp.jedit.OptionPane;
import org.gjt.sp.jedit.ServiceManager;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.OptionsDialog;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.StringList;

/**
 * An options dialog which has mode-settings like the EditingOptionPane. 
 * 
 * It creates an OptionPane for each SideKick parser that defines the proper property,
 * and is currently loaded.
 * 
 *  Plugins can add an OptionPane to SideKick's options by defining properties of this form
 *  
 *  options.sidekick.SERVICENAME.code = new DerivedSideKickPropertiesPane(view, buffer, name);
 *  
 * You can add regular OptionPanes but for OptionPanes which are derived from 
 * SideKickPropertiesPane, these optionpanes will be "mode aware" and reflect the
 * values specific to the mode selected in the mode combobox of the SideKickPropertiesDialog.
 * 
 * @author ezust
 *
 */

public class SideKickPropertiesDialog extends OptionsDialog
{
	public static final String ALL="ALL";
	ModeOptionsPane mop;
	OptionTreeModel paneTreeModel;
	StringList modes;
	JComboBox modeCombo;	
	JButton useDefaultsCheck;
	
	public SideKickPropertiesDialog(View v) {
		super(v, "options.sidekick.settings", "sidekick.mode");
	}
	
	public String getMode() {
		return modeCombo.getSelectedItem().toString();
	}
	
	
	protected OptionTreeModel createOptionTreeModel()
	{
		modes = new StringList(jEdit.getModes());
		
		Collections.sort(modes,new MiscUtilities.StringICaseCompare());
		modes.add(0, ALL);
		modeCombo = new JComboBox(modes.toArray());
		useDefaultsCheck = new JButton(jEdit.getProperty("options.editing.useDefaults"));
		JLabel editModeLabel = new JLabel(jEdit.getProperty("buffer-options.mode"));
		
		GridLayout gl = new GridLayout(1, 3);
		JPanel editModePanel = new JPanel(gl);
		// JLabel spacer = new JLabel(" ");
		//for (int i=0; i<3; ++i) editModePanel.add(spacer);

		editModePanel.add(useDefaultsCheck);
		editModePanel.add(editModeLabel);
		editModePanel.add(modeCombo);
		
		
		JPanel content = (JPanel) getContentPane();
		content.add(editModePanel, BorderLayout.NORTH);
		
		
		mop = new ModeOptionsPane();
		modeCombo.addItemListener(mop);
		
		paneTreeModel = new OptionTreeModel();
		OptionGroup root = (OptionGroup) (paneTreeModel.getRoot());
		root.addOptionPane(mop);
		// iterate through all parsers and get their name, attempt to get an option pane. 
		StringList serviceNames = new StringList(
			ServiceManager.getServiceNames(SideKickParser.SERVICE));
		Collections.sort(serviceNames,  new MiscUtilities.StringICaseCompare());
		serviceNames.add(0, ALL);
		for (String service: serviceNames) 
		{
			String code = jEdit.getProperty("options.sidekick.parser." + service + ".code");
			if (code == null) continue;
			OptionPane optionPane = (OptionPane) BeanShell.eval(
				jEdit.getActiveView(), BeanShell.getNameSpace(), code);
			if (optionPane == null) continue;
			
			if (optionPane instanceof SideKickPropertiesPane) {
				SideKickPropertiesPane spp = (SideKickPropertiesPane) optionPane;
				modeCombo.addItemListener(spp);
			}
			root.addOptionPane(optionPane);	
		}
		ActionHandler actionListener = new ActionHandler();

		modeCombo.addActionListener(actionListener);
		useDefaultsCheck.addActionListener(actionListener);

		String currentMode = jEdit.getActiveView().getBuffer().getMode().getName();
		modeCombo.setSelectedItem(currentMode);

		
		return paneTreeModel;
	}

	private void load(Object obj)
	{
		if(obj instanceof OptionGroup)
		{
			OptionGroup grp = (OptionGroup)obj;
			Enumeration members = grp.getMembers();
			while(members.hasMoreElements())
			{
				load(members.nextElement());
			}
		}
		else if(obj instanceof SideKickPropertiesPane)
		{
			try
			{
				((SideKickPropertiesPane)obj)._load();
			}
			catch(Throwable t)
			{
				Log.log(Log.ERROR,this,"Error loading options:");
				Log.log(Log.ERROR,this,t);
			}
		}
		
	} //}}}

	
	
	protected void load() {
		load(getDefaultGroup());
	}
	
	protected OptionGroup getDefaultGroup()
	{
		return (OptionGroup) paneTreeModel.getRoot();
	}

	class ActionHandler implements ActionListener
	{
		//{{{ actionPerformed() method
		public void actionPerformed(ActionEvent evt)
		{
			Object source = evt.getSource();
			if(source == modeCombo)
			{
				String m = getMode();
				useDefaultsCheck.setEnabled(!m.equals(ALL));			
			}
			else if (source == useDefaultsCheck) try {
				SideKickPropertiesPane spp = ((SideKickPropertiesPane)currentPane);
				spp._reset();
				spp._load();
			}
			catch (ClassCastException cce) {
					Log.log(Log.NOTICE, this, "Wrong kind of pane?", cce);
			}
				
		}
	} //}}}

}
