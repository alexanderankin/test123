package common.gui.actions;

import java.awt.event.*;
import java.net.*;
import javax.swing.*;


/**
 * Description of the Class
 *
 * @author     mace
 * @created    October 24, 2002
 * @modified   $Date$ by $Author$
 * @version    $Revision$
 */
public abstract class CustomAction extends AbstractAction {
	public final static int CTRL = KeyEvent.CTRL_MASK;
	public final static int ALT = KeyEvent.ALT_MASK;
	public final static int SHIFT = KeyEvent.SHIFT_MASK;
	public final static int META = KeyEvent.META_MASK;

	/**
	 * The Action's tool tip text is set to it's name by default.
	 *
	 * @param name  Description of the Parameter
	 */
	public CustomAction(String name) {
		super(name);
		setActionCommand(name);
		setToolTipText(name);
	}

	public CustomAction(String name, Icon icon) {
		super(name, icon);
		setActionCommand(name);
		setToolTipText(name);
	}

	public void setToolTipText(String text) {
		putValue(SHORT_DESCRIPTION, text);
	}

	public void setContextualHelp(String text) {
		putValue(LONG_DESCRIPTION, text);
	}

	public void setAccelerator(KeyStroke ks) {
		putValue(ACCELERATOR_KEY, ks);
	}

	public void setAccelerator(int key, int modifiers) {
		KeyStroke ks = KeyStroke.getKeyStroke(key, modifiers);
		setAccelerator(ks);
	}

	public void setMnemonic(int key) {
		putValue(MNEMONIC_KEY, new Integer(key));
	}

	public void setName(String name) {
		putValue(NAME, name);
	}

	public void setIcon(String file) {
		//URL iconURL = ClassLoader.getSystemResource(file);
		ImageIcon icon = new ImageIcon(file);
		setIcon(icon);
	}

	public void setIcon(Icon i) {
		putValue(SMALL_ICON, i);

	}

	public void setActionCommand(String command) {
		putValue(ACTION_COMMAND_KEY, command);
	}
}

