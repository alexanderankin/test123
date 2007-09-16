package ctags.sidekick.options;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.gjt.sp.jedit.jEdit;

import ctags.sidekick.Plugin;

@SuppressWarnings("serial")
public class ModeCtagsInvocationPane extends JPanel implements IModeOptionPane {

	static private final String CTAGS_MODE_OPTIONS_LABEL = "options.CtagsSideKick.mode.ctags_options_label";
	private JTextField ctagsOptions;
	private HashMap<String, String> modeOptions;
	private String mode;
	
	public ModeCtagsInvocationPane() {
		JLabel optionsLabel = new JLabel(jEdit.getProperty(CTAGS_MODE_OPTIONS_LABEL));
		ctagsOptions = new JTextField(30);
		add(optionsLabel);
		add(ctagsOptions);
		modeOptions = new HashMap<String, String>();
		mode = null;
	}
	
	public void modeSelected(String mode) {
		if (this.mode != null)
			modeOptions.put(this.mode, ctagsOptions.getText());
		this.mode = mode;
		String options = modeOptions.get(mode);
		if (options == null) {
			options = ModeOptionsPane.getProperty(mode, Plugin.CTAGS_MODE_OPTIONS);
			modeOptions.put(mode, options);
		}
		ctagsOptions.setText(options);
	}

	public void resetCurrentMode() {
		String options = modeOptions.get(null);
		modeOptions.put(mode, options);
		ctagsOptions.setText(options);
	}

	public void save() {
		modeOptions.put(this.mode, ctagsOptions.getText());
		Iterator entries = modeOptions.entrySet().iterator();
		while (entries.hasNext()) {
			Entry e = (Entry) entries.next();
			String mode = (String) e.getKey();
			String options = (String) e.getValue();
			ModeOptionsPane.setProperty(mode, Plugin.CTAGS_MODE_OPTIONS, options);
		}
	}

}
