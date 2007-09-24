package ctags.sidekick.options;

import javax.swing.JLabel;
import javax.swing.JTextField;

import org.gjt.sp.jedit.jEdit;

import ctags.sidekick.Plugin;

@SuppressWarnings("serial")
public class CtagsCmdOptionsPane extends ModeOptionPanel<CtagsCmdOptionsPane.Props> {

	public static class Props {
		Props(String s) {
			options = s;
		}
		String options;
	}
	
	static private final String CTAGS_MODE_OPTIONS_LABEL = "options.CtagsSideKick.mode.ctags_options_label";
	private JTextField ctagsCmdOptions;

	public CtagsCmdOptionsPane() {
		add(new JLabel(jEdit.getProperty(CTAGS_MODE_OPTIONS_LABEL)));
		add(ctagsCmdOptions = new JTextField(30));
	}
	
	@Override
	protected Props createModeProps(String mode) {
		return new Props(ModeOptionsPane.getProperty(mode,
			Plugin.CTAGS_MODE_OPTIONS));
	}

	@Override
	protected void resetModeProps(String mode) {
		ModeOptionsPane.clearModeProperty(mode, Plugin.CTAGS_MODE_OPTIONS);
	}

	@Override
	protected void saveModeProps(String mode, Props props) {
		ModeOptionsPane.setProperty(mode, Plugin.CTAGS_MODE_OPTIONS, props.options);
	}

	@Override
	protected void updatePropsFromUI(Props props) {
		props.options = ctagsCmdOptions.getText();
	}

	@Override
	protected void updateUIFromProps(Props props) {
		ctagsCmdOptions.setText(props.options);
	}

}
