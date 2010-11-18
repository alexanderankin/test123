/*
Copyright (C) 2010 Shlomy Reinstein

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package perl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.Vector;

import org.gjt.sp.jedit.jEdit;

public class LaunchConfigManager {
	
	private static LaunchConfigManager instance;
	private Vector<LaunchConfig> configurations =
		new Vector<LaunchConfig>();
	int defaultIndex = -1;
	
	static final String PREFIX = Plugin.OPTION_PREFIX;

	static public LaunchConfigManager getInstance() {
		if (instance == null)
			 instance = new LaunchConfigManager();
		return instance;
	}

	private LaunchConfigManager() {
		load();
	}
	public void add(LaunchConfig config) {
		configurations.add(config);
	}
	public void remove(LaunchConfig config) {
		int index = findIndex(config);
		if (index == -1)
			return;
		if (defaultIndex == index)
			defaultIndex = -1;
		else if (defaultIndex > index)
			defaultIndex--;
		configurations.remove(index);
	}

	private int findIndex(LaunchConfig config) {
		int index = -1;
		for (int i = 0; i < configurations.size(); i++) {
			if (configurations.get(i) == config) {
				index = i;
				break;
			}
		}
		return index;
	}
	public void setDefault(LaunchConfig config)
	{
		for (int i = 0; i < configurations.size(); i++) {
			if (configurations.get(i) == config) {
				defaultIndex = i;
				return; 
			}
		}
		add(config);
		defaultIndex = configurations.size() - 1;
	}
	public LaunchConfig getDefault()
	{
		if (defaultIndex < 0 || defaultIndex >= configurations.size())
			return null;
		return configurations.get(defaultIndex);
	}
	public Vector<LaunchConfig> get()
	{
		return configurations;
	}
	private static File getConfigFile() {
		String dir = jEdit.getSettingsDirectory() + File.separator +
			"perl";
		File f = new File(dir);
		if (! f.exists()) {
			if (! f.mkdir()) {
				System.err.println("Perl - LaunchConfigurationManager - failed to create plugin directory for saving the configurations.");
				return null;
			}
		}
		return new File(f.getAbsolutePath() + File.separator +
			"configs.xml");
	}
	public void save()
	{
		File file = getConfigFile();
		if (file == null) {
			System.err.println("Cannot save launch configurations - no configuration file");
			return;
		}
		Properties p = new Properties();
		p.setProperty("default", String.valueOf(defaultIndex));
		int count = configurations.size();
		p.setProperty("count", String.valueOf(count));
		for (int i = 0; i < count; i++)
			configurations.get(i).save("config." + i, p);
		try {
			p.store(new FileOutputStream(file), "");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void load() {
		File file = getConfigFile();
		if (file == null) {
			System.err.println("Cannot load launch configurations - no configuration file");
			return;
		}
		Properties p = new Properties();
		try	{
			p.load(new FileInputStream(file));
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Cannot load launch configurations - error while parsing configuration file");
			return;
		}
		String s = p.getProperty("default");
		try {
			defaultIndex = Integer.valueOf(s).intValue();
		} catch (Exception e) {
			System.err.println("Error reading launch configurations - default index is '" + s + "' - expecting an integer.");
			return;
		}
		s = p.getProperty("count");
		int count;
		try {
			count = Integer.valueOf(s).intValue();
		} catch (Exception e) {
			System.err.println("Error reading launch configurations - count is '" + s + "' - expecting an integer.");
			return;
		}
		configurations.clear();
		for (int i = 0; i < count; i++) {
			configurations.add(LaunchConfig.load(
				"config." + i, p));
		}
	}
	public String getNewName(String prefix) {
		for (int suffix = 0; suffix < 100; suffix++)
		{				
			String name = prefix + suffix;
			boolean found = false;
			for (LaunchConfig config: configurations)
			{
				if (config.name.equals(name)) {
					found = true;
					break;
				}
			}
			if (! found)
				return name;
		}
		return prefix;
	}
}
