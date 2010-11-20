/*
Copyright (C) 2007  Shlomy Reinstein

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

package perl.launch;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class LaunchConfiguration {
	private String name;
	private String program;
	private String arguments;
	private String directory;
	private String environment;
	public LaunchConfiguration(String name, String program, String arguments,
			String directory, String environment) {
		set(name, program, arguments, directory, environment);
	}
	public LaunchConfiguration createDuplicate() {
		return new LaunchConfiguration(name, program, arguments, directory,
				environment);
	}
	public String getName() {
		return name;
	}
	public String getProgram() {
		return program;
	}
	public String getArguments() {
		return arguments;
	}
	public String getDirectory() {
		return directory;
	}
	public String getEnvironment() {
		return environment;
	}
	// getEnvironmentArray adds the user specified env to the current env
	public String [] getEnvironmentArray() {
		HashMap<String, String> env = new HashMap<String, String>(System.getenv());
		if (environment != null) {
			String [] userEnv = environment.split(",");
			for (int i = 0; i < userEnv.length; i++) {
				String [] var = userEnv[i].split("=", 2);
				if (var.length == 2)
					env.put(var[0], var[1]);
			}
		}
		String [] envArray = new String[env.size()];
		Iterator<Entry<String, String>> varIter = env.entrySet().iterator();
		int i = 0;
		while (varIter.hasNext()) {
			Entry<String, String> var = varIter.next();
			envArray[i] = var.getKey() + "=" + var.getValue();
			i++;
		}
		return envArray;
	}
	public void set(String name, String program, String arguments,
			String directory, String environment) {
		setName(name);
		setProgram(program);
		setArguments(arguments);
		setDirectory(directory);
		setEnvironment(environment);
	}
	public void setName(String configName) {
		name = configName;
	}
	public void setProgram(String prog) {
		program = prog;
	}
	public void setArguments(String args) {
		arguments = args;
	}
	public void setDirectory(String dir) {
		directory = dir;
	}
	public void setEnvironment(String env) {
		environment = env;
	}
	public String toString() {
		return getName();
	}
}
