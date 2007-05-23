package gdb.launch;

import java.util.Vector;

import org.gjt.sp.jedit.jEdit;

import debugger.jedit.Plugin;

public class LaunchConfigurationManager {
	private static final String DEBUGGER_GO_BASE_LABEL = "debugger-go.base.label";
	private static final String DEBUGGER_GO_LABEL = "debugger-go.label";
	private static LaunchConfigurationManager instance;
	private Vector<LaunchConfiguration> configurations =
		new Vector<LaunchConfiguration>();
	int defaultIndex;
	
	static private final String ListValueSeparator = "<->";
	
	static final String PREFIX = Plugin.OPTION_PREFIX;
	static final String CONFIGURATIONS = PREFIX + "configurations";
	static final String PROGRAMS = PREFIX + "programs";
	static final String ARGUMENTS = PREFIX + "arguments";
	static final String DIRECTORIES = PREFIX + "directories";
	static final String ENVIRONMENTS = PREFIX + "environments";
	static final String DEFAULT_CONFIGURATION = "default_configuration";

	private LaunchConfigurationManager() {
		load();
	}
	private void checkSingleConfiguration() {
		if (configurations.size() == 1)
			setDefaultIndex(0);
	}
	public void add(LaunchConfiguration config) {
		configurations.add(config);
		checkSingleConfiguration();
	}
	public void remove(int index) {
		if (defaultIndex == index)
			defaultIndex--;
		configurations.remove(index);
		checkSingleConfiguration();
	}
	public int size() {
		return configurations.size();
	}
	private void load(String propName, Vector<String> objects)
	{
		String list = jEdit.getProperty(propName);
		if (list == null)
			return;
		String [] items = list.split(ListValueSeparator);
		for (int i = 0; i < items.length; i++)
			objects.add(items[i]);
	}
	private void ensureSize(Vector<String> list, int n)
	{
		int size = list.size();
		for (int i = size; i < n; i++)
			list.add("");
	}
	public Vector<String> getNames()
	{
		Vector<String> names = new Vector<String>();
		for (int i = 0; i < configurations.size(); i++)
			names.add(configurations.get(i).getName());
		return names;
	}
	public int getDefaultIndex()
	{
		return defaultIndex;
	}
	public void setDefaultIndex(int index)
	{
		defaultIndex = index;
		if (index < 0 || index >= configurations.size())
			return;
		jEdit.setProperty(DEBUGGER_GO_LABEL,
				jEdit.getProperty(DEBUGGER_GO_BASE_LABEL) +
				" [" + configurations.get(index) + "]");
		jEdit.propertiesChanged();
	}
	public LaunchConfiguration getDefault()
	{
		if (defaultIndex < 0 || defaultIndex >= configurations.size())
			return null;
		return configurations.get(defaultIndex);
	}
	public Vector<LaunchConfiguration> get()
	{
		return configurations;
	}
	public LaunchConfiguration getByIndex(int index)
	{
		if (index < 0 || index >= configurations.size())
			return null;
		return configurations.get(index);
	}
	public String getName(int index)
	{
		if (index < 0 || index >= configurations.size())
			return null;
		return configurations.get(index).getName();
	}
	public void save()
	{
		StringBuffer names = new StringBuffer();
		StringBuffer programs = new StringBuffer();
		StringBuffer arguments = new StringBuffer();
		StringBuffer directories = new StringBuffer();
		StringBuffer environments = new StringBuffer();
		for (int i = 0; i < configurations.size(); i++) {
			LaunchConfiguration config = configurations.get(i);
			String sep = (i > 0) ? ListValueSeparator : "";
			names.append(sep + config.getName());
			programs.append(sep + config.getProgram());
			arguments.append(sep + config.getArguments());
			directories.append(sep + config.getDirectory());
			environments.append(sep + config.getEnvironment());
		}
		jEdit.setProperty(CONFIGURATIONS, names.toString());
		jEdit.setProperty(PROGRAMS, programs.toString());
		jEdit.setProperty(ARGUMENTS, arguments.toString());
		jEdit.setProperty(DIRECTORIES, directories.toString());
		jEdit.setProperty(ENVIRONMENTS, environments.toString());
		jEdit.setIntegerProperty(DEFAULT_CONFIGURATION, defaultIndex);
	}

	public void load() {
		Vector<String> names = new Vector<String>();
		Vector<String> programs = new Vector<String>();
		Vector<String> arguments = new Vector<String>();
		Vector<String> directories = new Vector<String>();
		Vector<String> environments = new Vector<String>();
		load(CONFIGURATIONS, names);
		int configs = names.size();
		load(PROGRAMS, programs);
		ensureSize(programs, configs);
		load(ARGUMENTS, arguments);
		ensureSize(arguments, configs);
		load(DIRECTORIES, directories);
		ensureSize(directories, configs);
		load(ENVIRONMENTS, environments);
		ensureSize(environments, configs);
		defaultIndex = jEdit.getIntegerProperty(DEFAULT_CONFIGURATION, 0);
		configurations.clear();
		for (int i = 0; i < configs; i++)
			configurations.add(new LaunchConfiguration(
					names.get(i),
					programs.get(i),
					arguments.get(i),
					directories.get(i),
					environments.get(i)));
		// The following is required for the side effects (change menu label)
		setDefaultIndex(defaultIndex);
	}
	static public LaunchConfigurationManager getInstance() {
		if (instance == null)
			 instance = new LaunchConfigurationManager();
		return instance;
	}
	public String getNewName(String prefix) {
		Vector<String> names = getNames();
		for (int suffix = 0; suffix < 100; suffix++)
		{				
			String name = prefix + suffix;
			boolean found = false;
			for (int i = 0; i < names.size(); i++)
			{
				if (names.get(i).equals(name)) {
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
