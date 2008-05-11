/*
 * OptionalOptions.java -
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * (c) 2006 Alan Ezust
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package optional;

import javax.swing.JCheckBox;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;



/**
   Option pane for Optional plugin.
   Not currently used.
   */

public class OptionalOptions extends AbstractOptionPane
{
	JCheckBox useCombined;
	public OptionalOptions() 
	{
		super("optional");
	}
	protected void _init() {
		String useCombinedOptions = jEdit.getProperty("optional.combined.label");
		useCombined = new JCheckBox(useCombinedOptions);
		useCombined.setSelected(jEdit.getBooleanProperty("optional.combined"));
		addComponent(useCombined);
	}

	protected void _save() {
		boolean combined = useCombined.isSelected();
		jEdit.setBooleanProperty("optional.combined", combined);
	}
}
