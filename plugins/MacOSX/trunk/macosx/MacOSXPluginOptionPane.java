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

public class MacOSXPluginOptionPane extends AbstractOptionPane
{
	public MacOSXPluginOptionPane()
	{
		super("macosx");
	}

	//{{{ _init() method
	@Override
	protected void _init()
	{
		String settingsDirectory = jEdit.getSettingsDirectory();
		useQuartz = new JCheckBox(jEdit.getProperty("options.MacOSXPlugin.useQuartz"));
		useQuartz.setSelected(!new File(settingsDirectory, "noquartz").exists());
		addComponent(useQuartz);
		
		multiShortcut = new JCheckBox(jEdit.getProperty("option.MacOSXPlugin.multiShortcut"));
		multiShortcut.setSelected(jEdit.getBooleanProperty("menu.multiShortcut", false));
		addComponent(multiShortcut);
		
		addSeparator("options.MacOSXPlugin.experimental");
		
		altDispatcher = new JCheckBox(jEdit.getProperty(
			"options.MacOSXPlugin.altDispatcher"));
		altDispatcher.setSelected(MacOSXPlugin.getAlternativeDispatcher());
		addComponent(altDispatcher);
		
		disableOption = new JCheckBox(jEdit.getProperty(
			"options.MacOSXPlugin.disableOption"));
		disableOption.setSelected(MacOSXPlugin.getDisableOption());
		addComponent(disableOption);
	} //}}}

	//{{{ _save() method
	protected void _save()
	{
		setFileFlag("noquartz", !useQuartz.isSelected());
		jEdit.setBooleanProperty("menu.multiShortcut", multiShortcut.isSelected());
		MacOSXPlugin.setAlternativeDispatcher(altDispatcher.isSelected());
		MacOSXPlugin.setDisableOption(disableOption.isSelected());
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
	
	private JCheckBox useQuartz;
	private JCheckBox altDispatcher;
	private JCheckBox disableOption;
	private JCheckBox multiShortcut;
}

