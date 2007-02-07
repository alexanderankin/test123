/*
 * :tabSize=4:indentSize=4:noTabs=true:
 * :folding=explicit:collapseFolds=1:
 *
 * (c) 2007 Marcelo Vanzin
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
package poptions;

import java.util.LinkedList;
import java.util.List;

import javax.swing.JComboBox;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.util.Log;

import common.gui.EasyOptionPane;
import projectviewer.config.ProjectOptions;

/**
 *  Option pane with configurable editing actions.
 *
 *  @author     Marcelo Vanzin
 *  @version    $Id$
 *  @since      POP 0.1.0
 */
public class EditingOptionPane extends ChildOptionPane
{

    public EditingOptionPane()
    {
        super("poptions-editing");

        List lst = new LinkedList();
        lst.add("combo,options.editing.wrap,poptions.buffer.wrap,none:soft:hard");
        lst.add("ecombo,options.editing.maxLineLen,poptions.buffer.maxLineLen,0:72:76:80");
        lst.add("ecombo,options.editing.tabSize,poptions.buffer.tabSize,2:4:8");
        lst.add("ecombo,options.editing.indentSize,poptions.buffer.indentSize,2:4:8");
        lst.add("separators,options.general.lineSeparator,poptions.buffer.lineSeparator");
        lst.add("encodings,options.general.encoding,poptions.buffer.encoding");
        lst.add("checkbox,options.editing.noTabs,poptions.buffer.noTabs");
        lst.add("checkbox,options.editing.deepIndent,poptions.buffer.deepIndent");
        setComponentSpec(lst);
    }

	protected Object createComponent(String type, String label,
									 String value, String config)
	{
        if ("encodings".equals(type)) {
            JComboBox combo = new JComboBox();
			String[] encodings = MiscUtilities.getEncodings(true);

			if (encodings != null)
			for (String encoding : encodings) {
                combo.addItem(encoding);
			}

            if (value == null) {
                value = jEdit.getProperty("buffer.encoding");
            }

            combo.setSelectedItem(value);
            addComponent(label, combo);
            return combo;
        } else if ("separators".equals(type)) {
            JComboBox combo = new JComboBox();
            combo.addItem("Unix (\\n)");
            combo.addItem("DOS/Windows (\\r\\n)");
            combo.addItem("MacOS (\\r)");

            if (value == null) {
                value = jEdit.getProperty("buffer.lineSeparator");
            }

            if ("\r\n".equals(value)) {
                combo.setSelectedIndex(1);
            } else if ("\r".equals(value)) {
                combo.setSelectedIndex(2);
            } else {
                combo.setSelectedIndex(0);
            }

            addComponent(label, combo);
            return combo;
        }
		return null;
	}

    protected String parseComponent(Object comp, String name)
    {
        if (name.equals("poptions.buffer.lineSeparator")) {
            String ret = (String) ((JComboBox)comp).getSelectedItem();
            if (ret.startsWith("Unix")) {
                ret = "\n";
            } else if (ret.startsWith("DOS")) {
                ret = "\r\n";
            } else if (ret.startsWith("MacOS")) {
                ret = "\r";
            } else {
                // WTF?
                Log.log(Log.ERROR, this, "ShouldNotReachHere(): " + ret);
                ret = "\n";
            }
            return ret;
        } else {
            return super.parseComponent(comp, name);
        }
    }

}

