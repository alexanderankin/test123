package gdb.launch;

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
}
