package sidekick;

public interface ModeOptionPane {
	// Mode has changed
	void modeSelected(String mode);
	// Save all changes (in all modes)
	void save();
	// Cancel all changes (in all modes)
	void cancel();
	// Sets whether the mode uses the default settings
	void setUseDefaults(boolean b);
	// Returns whether the mode uses the default settings
	boolean getUseDefaults(String mode);
}
