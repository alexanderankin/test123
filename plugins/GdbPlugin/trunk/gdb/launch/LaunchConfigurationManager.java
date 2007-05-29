package gdb.launch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Vector;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.XMLUtilities;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import debugger.jedit.Plugin;

public class LaunchConfigurationManager {
	
	private static final String DEBUGGER_GO_BASE_LABEL = "debugger-go.base.label";
	private static final String DEBUGGER_GO_LABEL = "debugger-go.label";
	private static LaunchConfigurationManager instance;
	private Vector<LaunchConfiguration> configurations =
		new Vector<LaunchConfiguration>();
	int defaultIndex;
	
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
	private static String getConfigDirectory() {
		String dir = jEdit.getSettingsDirectory() + File.separator + "gdbplugin";
		File f = new File(dir);
		if (! f.exists())
			f.mkdir();
		return dir;
	}
	static private String LAUNCH_CONFIG_ELEMENT = "launchConfiguration";
	static private String PROGRAM_ELEMENT = "program";
	static private String DIRECTORY_ELEMENT = "directory";
	static private String ARGUMENTS_ELEMENT = "arguments";
	static private String ENVIRONMENT_ELEMENT = "environment";
	
	public void save()
	{
		String configDir = getConfigDirectory();
		for (int i = 0; i < configurations.size(); i++) {
			LaunchConfiguration config = configurations.get(i);
			String filePath = configDir + "/" + config.getName();
			File configFile = new File(filePath);
			PrintWriter w;
			try {
				w = new PrintWriter(new FileWriter(configFile));
				w.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
				w.println();
				w.println("<" + LAUNCH_CONFIG_ELEMENT + ">");
				w.println(createElement(1, PROGRAM_ELEMENT, config.getProgram()));
				w.println(createElement(1, ARGUMENTS_ELEMENT, config.getArguments()));
				w.println(createElement(1, DIRECTORY_ELEMENT, config.getDirectory()));
				w.println(createElement(1, ENVIRONMENT_ELEMENT, config.getEnvironment()));
				w.println("</" + LAUNCH_CONFIG_ELEMENT + ">");
				w.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		jEdit.setIntegerProperty(DEFAULT_CONFIGURATION, defaultIndex);
	}

	private String createElement(int column, String name, String value) {
		StringBuffer sb = new StringBuffer();
		while (column > 0) {
			sb.append("  ");
			column--;
		}
		return sb.toString() + "<" + name + ">" + value + "</" + name + ">";
	}
	public void load() {
		File configDir = new File(getConfigDirectory());
		if (! configDir.canRead())
			return;
		File[] files = configDir.listFiles(new FilenameFilter() {
			static private final String XML_SUFFIX = ".xml";
			public boolean accept(File dir, String name) {
				return (name.endsWith(XML_SUFFIX));
			}
		});
		defaultIndex = jEdit.getIntegerProperty(DEFAULT_CONFIGURATION, 0);
		configurations.clear();
		int configs = files.length;
		for (int i = 0; i < configs; i++) {
			File file = files[i];
			String name = file.getName();
			name = name.substring(0, name.length() - 4);
			LaunchConfigurationHandler handler =
				new LaunchConfigurationHandler(name);
			try
			{
				XMLUtilities.parseXML(new FileInputStream(file), handler);
			}
			catch(IOException e)
			{
				Log.log(Log.ERROR,LaunchConfigurationManager.class,e);
			}
			configurations.add(handler.getLaunchConfiguration());
		}
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
	static class LaunchConfigurationHandler extends DefaultHandler {

		String name;
		String element = null;
		HashMap<String, String> attributes = new HashMap<String, String>();
		
		LaunchConfigurationHandler(String name) {
			this.name = name;
		}
		@Override
		public void startElement(String nsURI, String localName,
				String qualifiedName, Attributes attr) throws SAXException
		{
			element = localName;
		}
		@Override
		public void characters(char[] ch, int start, int len)
				throws SAXException {
			if (element == null)
				return;
			String s = new String(ch, start, len);
			attributes.put(element, s);
		}
		public LaunchConfiguration getLaunchConfiguration() {
			return new LaunchConfiguration(name,
					attributes.get(PROGRAM_ELEMENT),
					attributes.get(ARGUMENTS_ELEMENT),
					attributes.get(DIRECTORY_ELEMENT),
					attributes.get(ENVIRONMENT_ELEMENT));
		}
		@Override
		public void endElement(String uri, String localName, String name)
				throws SAXException {
			element = null;
		}
	}

}
