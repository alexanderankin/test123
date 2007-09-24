package ctags.sidekick.options;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JPanel;

abstract public class ModeOptionPanel<Props> extends JPanel implements IModeOptionPane {

	private HashMap<String, Props> modeProps;
	private Set<String> useDefaults;	// Modes that use default settings
	private Props props;	// Properties of current mode
	private String mode;	// Currently selected mode

	// Update the given properties from the UI
	abstract protected void updatePropsFromUI(Props props);
	// Update the UI from the given properties
	abstract protected void updateUIFromProps(Props props);
	// Create a new, initialized properties object for the specified mode
	abstract protected Props createModeProps(String mode);
	// Save (commit) mode properties
	abstract protected void saveModeProps(String mode, Props props);
	// Reset mode properties to the default settings
	abstract protected void resetModeProps(String mode);
	
	public ModeOptionPanel() {
		modeProps = new HashMap<String, Props>();
		useDefaults = new HashSet<String>();
	}
	
	public void modeSelected(String mode) {
		if (this.mode != null)
			updatePropsFromUI(props);
		this.mode = mode;
		props = modeProps.get(mode);
		if (props == null) {
			props = createModeProps(mode);
			modeProps.put(mode, props);
		}
		updateUIFromProps(props);
		setEnabled(! useDefaults.contains(mode));
	}

	public void setUseDefaults(boolean b) {
		if (b)
			useDefaults.add(mode);
		else
			useDefaults.remove(mode);
		setEnabled(! b);
	}

	public void save() {
		updatePropsFromUI(props);
		Iterator<String> modes = modeProps.keySet().iterator();
		while (modes.hasNext()) {
			String m = modes.next();
			if (useDefaults.contains(m))
				resetModeProps(m);
			else
				saveModeProps(m, modeProps.get(m));
		}
	}
}
;