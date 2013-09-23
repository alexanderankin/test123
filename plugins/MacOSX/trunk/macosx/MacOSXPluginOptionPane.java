/*
 * MacOSXPluginOptionPane.java - Option pane for MacOSXPlugin
 *
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

package macosx;

//{{{ Imports
import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;

import javax.swing.JCheckBox;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.util.IOUtilities;
import org.gjt.sp.util.Log;
//}}}

@SuppressWarnings("serial")
public class MacOSXPluginOptionPane extends AbstractOptionPane
{
	
	private JCheckBox ctrlAltSwapped;
	private JCheckBox disableOption;
	private JCheckBox multiShortcut;

	public MacOSXPluginOptionPane()
	{
		super("MacOSXPlugin");
	}

	//{{{ _init() method
	@Override
	protected void _init()
	{
		String settingsDirectory = jEdit.getSettingsDirectory();
		
		multiShortcut = new JCheckBox(jEdit.getProperty("option.MacOSXPlugin.multiShortcut"));
		multiShortcut.setSelected(jEdit.getBooleanProperty("menu.multiShortcut", false));
		addComponent(multiShortcut);
		
		// addSeparator("options.MacOSXPlugin.experimental");
				
		disableOption = new JCheckBox(jEdit.getProperty(
			"options.MacOSXPlugin.disableOption"));
		disableOption.setSelected(MacOSXPlugin.getDisableOption());
		addComponent(disableOption);
		
		ctrlAltSwapped = new JCheckBox(jEdit.getProperty(
			"options.MacOSXPlugin.ctrlAltSwapped"));
		ctrlAltSwapped.setSelected(MacOSXPlugin.isCtrlAltSwapped());
		addComponent(ctrlAltSwapped);
		
	} //}}}

	//{{{ _save() method
	protected void _save()
	{		
		jEdit.setBooleanProperty("menu.multiShortcut", multiShortcut.isSelected());
		MacOSXPlugin.setDisableOption(disableOption.isSelected());
		MacOSXPlugin.setCtrlAltSwapped(ctrlAltSwapped.isSelected());
	} //}}}
	
	//{{{ setFileFlag() method
	private void setFileFlag(String fileName, boolean present)
	{
		String settingsDirectory = jEdit.getSettingsDirectory();
		if(settingsDirectory != null)
		{
			File file = new File(settingsDirectory, fileName);
			if (!present)
			{
				file.delete();
			}
			else
			{
				FileOutputStream out = null;
				try
				{
					out = new FileOutputStream(file);
					out.write('\n');
					out.close();
				}
				catch(IOException io)
				{
					Log.log(Log.ERROR,this,io);
				}
				finally
				{
					IOUtilities.closeQuietly(out);
				}
			}
		}
	} //}}}
	
}

