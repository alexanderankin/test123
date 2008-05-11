package sidekick;

import java.awt.Component;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JComponent;


public class ModeOptionPaneController implements ModeOptionPane {

	public interface ModeOptionPaneDelegate {
		// Returns the UI component of the option pane
		JComponent getUIComponent();
		// Update the given properties from the UI
		void updatePropsFromUI(Object props);
		// Update the UI from the given properties
		void updateUIFromProps(Object props);
		// Create a new, initialized properties object for the specified mode
		Object createModeProps(String mode);
		// Save (commit) mode properties
		void saveModeProps(String mode, Object props);
		// Reset mode properties to the default settings
		void resetModeProps(String mode);
		// Returns whether or not the specified mode has mode-specific properties
		boolean hasModeProps(String mode);
	}
	
	private HashMap<String, Object> modeProps;
	private Set<String> useDefaults;	// Modes that use default settings
	private Object props;	// Properties of current mode
	private String mode;	// Currently selected mode
	ModeOptionPaneDelegate pane;		// The UI pane controlled by this controller
	
	public ModeOptionPaneController(ModeOptionPaneDelegate mop) {
		modeProps = new HashMap<String, Object>();
		useDefaults = new HashSet<String>();
		pane = mop;
	}
	
	public void modeSelected(String mode) {
		if (this.mode != null)
			pane.updatePropsFromUI(props);
		this.mode = mode;
		props = modeProps.get(mode);
		if (props == null) {
			props = pane.createModeProps(mode);
			if (! pane.hasModeProps(mode))
				useDefaults.add(mode);
			modeProps.put(mode, props);
		}
		pane.updateUIFromProps(props);
		setEnabled(pane.getUIComponent(), ! useDefaults.contains(mode));
	}

	public void setUseDefaults(boolean b) {
		if (b)
			useDefaults.add(mode);
		else
			useDefaults.remove(mode);
		setEnabled(pane.getUIComponent(), ! b);
	}

	public void setEnabled(JComponent c, boolean enabled) {
		c.setEnabled(enabled);
		Component [] children = c.getComponents();
		for (int i = 0; i < children.length; i++)
			if (children[i] instanceof JComponent)
				setEnabled((JComponent) children[i], enabled);
	}
	
	public void save() {
		pane.updatePropsFromUI(props);
		Iterator<String> modes = modeProps.keySet().iterator();
		while (modes.hasNext()) {
			String m = modes.next();
			if (useDefaults.contains(m))
				pane.resetModeProps(m);
			else
				pane.saveModeProps(m, modeProps.get(m));
		}
	}

	public void cancel() {
		modeProps.clear();
	}

	public boolean getUseDefaults(String mode) {
		if (modeProps.get(mode) != null)
			return useDefaults.contains(mode);
		return (! pane.hasModeProps(mode));
	}
}
