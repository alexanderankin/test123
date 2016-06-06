/*          DO WHAT THE FRAK YOU WANT TO PUBLIC LICENSE (WTFPL)
                    Version 4, October 2012
	    Based on the wtfpl: http://sam.zoy.org/wtfpl/

 Copyright © 2012 Alan Ezust

 Everyone is permitted to copy and distribute verbatim or modified
 copies of this license document, and changing it is allowed as long
 as the name is changed.

            DO WHAT THE FRAK YOU WANT TO PUBLIC LICENSE
   TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION

  0. You just DO WHAT THE FRAK YOU WANT TO.
  1. It is provided "as is" without any warranty whatsoever.
*/
package console.ssh;


import javax.swing.JCheckBox;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

@SuppressWarnings("serial")
public class OptionPane extends AbstractOptionPane {
	JCheckBox xforward;
	JCheckBox showTasks;


	public OptionPane() {
		super("sshconsole");
	}


	protected void _init() {
		xforward = new JCheckBox(jEdit.getProperty("options.sshconsole.xforward"));
		xforward.setSelected(jEdit.getBooleanProperty("sshconsole.xforward"));
		addComponent(xforward);

		showTasks = new JCheckBox(jEdit.getProperty("options.sshconsole.showtasks"));
		showTasks.setSelected(jEdit.getBooleanProperty("sshconsole.showtasks"));
		addComponent(showTasks);
	}

	protected void _save() {
		jEdit.setBooleanProperty("sshconsole.xforward", xforward.isSelected());
		jEdit.setBooleanProperty("sshconsole.showtasks", showTasks.isSelected());
	}

}
