package gdb.launch;

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
	public String [] getEnvironmentArray() {
		HashMap<String, String> env = new HashMap<String, String>(System.getenv());
		String [] userEnv = getEnvironment().split(",");
		for (int i = 0; i < userEnv.length; i++) {
			String [] var = userEnv[i].split("=", 2);
			if (var.length == 2)
				env.put(var[0], var[1]);
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
