package myDoggy;

import javax.swing.JComboBox;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;
import org.noos.xing.mydoggy.PushAwayMode;

@SuppressWarnings("serial")
public class OptionPane extends AbstractOptionPane {

	private static final String PREFIX = "options.mydoggy.";
	private static final String PUSH_AWAY_MODE_PROP = PREFIX + "pushAwayMode";
	private static final String PUSH_AWAY_MODE_LABEL = PUSH_AWAY_MODE_PROP + ".label";
	JComboBox pushAwayMode;
	
	public OptionPane() {
		super("mydoggy");
	}

	public static PushAwayMode getPushAwayModeProp() {
		String selected = jEdit.getProperty(PUSH_AWAY_MODE_PROP,
				PushAwayMode.HORIZONTAL.toString());
		return PushAwayMode.valueOf(selected);
	}
	
	@Override
	protected void _init() {
		String [] pushAwayModes = new String[] {
				PushAwayMode.HORIZONTAL.toString(),
				PushAwayMode.VERTICAL.toString(),
				PushAwayMode.ANTICLOCKWISE.toString(),
				PushAwayMode.MOST_RECENT.toString()
		};
		pushAwayMode = new JComboBox(pushAwayModes);
		pushAwayMode.setSelectedItem(getPushAwayModeProp().toString());
		addComponent(jEdit.getProperty(PUSH_AWAY_MODE_LABEL), pushAwayMode);
	}

	@Override
	protected void _save() {
		jEdit.setProperty(PUSH_AWAY_MODE_PROP, (String)pushAwayMode.getSelectedItem());
		jEdit.propertiesChanged();
	}

}
