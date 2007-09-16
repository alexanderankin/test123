package ctags.sidekick.options;

public interface IModeOptionPane {
	// Mode has changed
	void modeSelected(String mode);
	// Save all changes (in all modes)
	void save();
	// Reset current mode options to the defaults
	void resetCurrentMode();
}
