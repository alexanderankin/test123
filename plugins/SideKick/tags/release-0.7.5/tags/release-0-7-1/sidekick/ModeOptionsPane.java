package sidekick;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JComboBox;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.jEdit;


//{{{ SideKickPropertiesPane class
/**
 * A specialized properties pane which has mode-overridable properties. 
 * Includes convenience methods for getting and setting mode properties, with global
 * defaults. 
 * 
 * @author ezust
 *
 */

abstract public class ModeOptionsPane 
	extends AbstractOptionPane implements ItemListener
{

	String mode = ModeOptionsDialog.ALL;
	
	/**
	 * Load mode properties into input components,
	 * using global props if mode props are not set.
	 *
	 */
	
	final public void load() {
		if (initialized) _load();
	}

	/**
	 * Override this method. Call @ref load().
	 */
	protected abstract void _load();
	
	/**
	 * Un-sets all mode properties, so that the global defaults will be used instead.
	 *
	 */
	protected abstract void _reset();
	
	
	protected ModeOptionsPane(String name) {
		super(name);
	}

	protected String getMode() {
		return mode;
	}
	
	/* {{{ Mode Property setter/getter convenience functions
	*/
	
	public void clearModeProperty(String key) {
		jEdit.unsetProperty(modePrefix(mode, key));
	}
	
	public static void setIntegerProperty(String mode, String key, int value) {
		jEdit.setIntegerProperty(modePrefix(mode, key), value);
	}

	public static boolean getBooleanProperty(String mode, String key) {
		if (jEdit.getProperty(modePrefix(mode, key)) == null)
			return jEdit.getBooleanProperty(key);
		else return jEdit.getBooleanProperty(modePrefix(mode, key));
	}
	
	public void setBooleanProperty(String key, boolean val) {
		jEdit.setBooleanProperty(modePrefix(mode, key), val);
	}
	
	public static int getIntegerProperty(String mode, String key, int def) {
		if (jEdit.getProperty(modePrefix(mode, key)) != null) {
			return jEdit.getIntegerProperty(modePrefix(mode, key), def);
		}
		else {
			return jEdit.getIntegerProperty(key, def);
		}
	}
	
	public static void setProperty(String mode, String key, String value) {
		jEdit.setProperty(modePrefix(mode,key), value);
	}
	
	public static String getProperty(String mode, String key) {
		String retval = jEdit.getProperty(modePrefix(mode, key));
		if (retval == null) return jEdit.getProperty(key);
		else return retval;
	}
	
	public static String modePrefix(String mode, String key) {
		if (mode != null && !mode.equals(ModeOptionsDialog.ALL)) 
			return "mode." +mode + "." + key;
		else return key;
	}
	
	public void setIntegerProperty(String key, int value) {
		setIntegerProperty(getMode(), key, value);
	}
	
	public void setProperty(String key, String value) {
		setProperty(getMode(), key, value);
	}
	
	/**
	 * @param a the property name.
	 * @return a mode-specific property, depending on what mode is selected in the combo box
	 * of the SideKickProperties dialog.
	 */
	public String getProperty(String key) {
		return getProperty(getMode(), key);
	}

	public boolean getBooleanProperty(String key ) {
		return getBooleanProperty(getMode(), key);
	}
	public int getIntegerProperty(String key, int def) {
		return getIntegerProperty(getMode(), key, def);
	}
	// }}}

	
	public void itemStateChanged(ItemEvent e)
	{
		if (e.getSource() instanceof JComboBox) {
			save();
			mode = e.getItem().toString();
			load();
		}
	}

	
}
