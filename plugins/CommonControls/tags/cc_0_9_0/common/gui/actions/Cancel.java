package common.gui.actions;

import java.awt.event.*;
import javax.swing.*;

/**
 * Description of the Class
 *
 * @author     mace
 * @created    May 28, 2003
 * @modified   $Date$ by $Author$
 * @version    $Revision$
 */
public class Cancel extends CustomAction {
	private JDialog _dialog;

	public Cancel(JDialog d) {
		super("Cancel");
		_dialog = d;
	}

	public void actionPerformed(ActionEvent e) {
		_dialog.dispose();
	}
}

