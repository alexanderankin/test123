package ctags.sidekick.options;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.gjt.sp.jedit.jEdit;

import sidekick.ModeOptionPaneController;

import ctags.sidekick.Plugin;

@SuppressWarnings("serial")
public class CtagsCmdOptionsPane extends JPanel
	implements ModeOptionPaneController.ModeOptionPaneDelegate {

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
	
	public JComponent getUIComponent() {
		return this;
	}

	public Object createModeProps(String mode) {
		return new Props(SideKickModeOptionsPane.getProperty(mode,
			Plugin.CTAGS_MODE_OPTIONS));
	}

	public void resetModeProps(String mode) {
		SideKickModeOptionsPane.clearModeProperty(mode, Plugin.CTAGS_MODE_OPTIONS);
	}

	public void saveModeProps(String mode, Object props) {
		SideKickModeOptionsPane.setProperty(mode, Plugin.CTAGS_MODE_OPTIONS, ((Props)props).options);
	}

	public void updatePropsFromUI(Object props) {
		((Props)props).options = ctagsCmdOptions.getText();
	}

	public void updateUIFromProps(Object props) {
		ctagsCmdOptions.setText(((Props)props).options);
	}

	public boolean hasModeProps(String mode) {
		return SideKickModeOptionsPane.modePropertyExists(mode, Plugin.CTAGS_MODE_OPTIONS);
	}

}
