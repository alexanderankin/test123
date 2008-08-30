/*
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
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
package projectviewer.gui;

import java.awt.GridBagConstraints;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.AbstractOptionPane;


/**
 *  A base class for option panes that makes some common tasks easier.
 *
 *  @author		Marcelo Vanzin
 *	@version	$Id$
 *	@since		PV 3.0.0
 */
public abstract class OptionPaneBase extends AbstractOptionPane
{

	/**
	 * Creates a new option pane.
	 *
	 * @param	name		Name of the pane.
	 * @param	propRoot	Root of properties retrieved with {@link #prop(String)}.
	 */
	public OptionPaneBase(String name,
						  String propRoot)
	{
		super(name);
		this.propRoot = propRoot + ".";
	}


	/**
	 * Adds a component adding an optional label, and setting its tooltip
	 * if one is available. Component is added with the fill property set to
	 * GridBagConstraints.HORIZONTAL.
	 *
	 * @param	comp		Component to add.
	 * @param	labelProp	Label property name. Tooltip is the same property,
	 *						with ".tooltip" added.
	 */
	protected void addComponent(JComponent comp,
								String labelProp)
	{
		JLabel label = null;

		if (labelProp != null) {
			String lText = prop(labelProp);
			if (lText != null) {
				label = new JLabel(lText);
			}

			lText = prop(labelProp + ".tooltip");
			if (lText != null) {
				comp.setToolTipText(lText);
			}
		}

		if (label == null) {
			addComponent(comp, GridBagConstraints.HORIZONTAL);
		} else {
			addComponent(label, comp, GridBagConstraints.HORIZONTAL);
		}
	}


	/**
	 * Shortcut for adding a checkbox with an optional tooltip.
	 *
	 * @param	label		Property name for the label / tooltip.
	 * @param	selected	Whether the checkbox is checked.
	 *
	 * @return The checkbox (already added to the UI).
	 */
	protected JCheckBox addCheckBox(String label,
									boolean selected)
	{
		JCheckBox cb = new JCheckBox(prop(label));
		String tooltip = prop(label + ".tooltip");
		if (tooltip != null) {
			cb.setToolTipText(tooltip);
		}
		cb.setSelected(selected);
		addComponent(cb);
		return cb;
	}


	/**
	 * Shortcut for retrieving jEdit properties, avoiding ridiculously long
	 * lines.
	 *
	 * @param	property	Property name.
	 *
	 * @return Property value (may be null).
	 */
	protected String prop(String property)
	{
		return jEdit.getProperty(propRoot + property);
	}


	/* Fields. */
	private final String propRoot;

}
