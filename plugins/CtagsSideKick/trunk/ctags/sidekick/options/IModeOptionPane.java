package ctags.sidekick.options;

public interface IModeOptionPane {
	// Mode has changed
	void modeSelected(String mode);
	// Save all changes (in all modes)
	void save();
	// Sets whether the mode uses the default settings
	void setUseDefaults(boolean b);
}
