/*
 * ConsolePlugin.java - Console plugin
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 1999, 2004 Slava Pestov
 * Portions copyright (C) 1999, 2000 Kevin A. Burton
 * Revised 2005, 2010 by Alan Ezust
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

package console;

// {{{ Imports
import java.io.File;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JOptionPane;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.EditBus.EBHandler;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.jedit.msg.DynamicMenuChanged;
import org.gjt.sp.jedit.msg.EditorStarted;
import org.gjt.sp.jedit.msg.PluginUpdate;
import org.gjt.sp.jedit.msg.ViewUpdate;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.StringList;

import console.commando.CommandoCommand;
import console.commando.CommandoToolBar;
import errorlist.DefaultErrorSource;


// }}}

/**
 * ConsolePlugin
 *
 * @version  $Id$
 */

public class ConsolePlugin extends EditPlugin
{
	// {{{ Instance and static variables

	private static String consoleDirectory;
	private static String userCommandDirectory;
	private static ActionSet allCommands;
	private static ActionSet shellSwitchActions;
	static CommandoToolBar toolBar = null;
	private static Map<String, ShellDescriptor> baseShells;
	// }}}

	// {{{ static final Members
	public static final String MENU = "plugin.console.ConsolePlugin.menu";
	public static final String CMD_PATH = "/console/bsh/";
	/**
	 * Return value of {@link #parseLine()} if the text does not match a
	 * known error pattern.
	 */
	public static final int NO_ERROR = -1;
	// }}} static final Members

	// {{{ getSystemShell() method
	public static SystemShell getSystemShell()
	{
		try 
		{
			Console console = getConsole(jEdit.getActiveView());
			if (console != null)
			{
				Shell shell = console.getShell();
				if (shell instanceof SystemShell)
					return (SystemShell)shell;
			}
			return (SystemShell) ServiceManager.getService(Shell.SERVICE, "System");
		}
		catch(Exception e) 
		{
			return null;
		}
	} // }}}

	// {{{ getShellSwitchActions()
	/**
	 * @return a dynamically generated list of actions based on which
	 *          console Shells are available.
	 */

	 public static ActionSet getShellSwitchActions()
	 {
		return shellSwitchActions;
	} // }}}

	// {{{ getAllCommands()
	/**
	   @return all commands that are represented as
	           .xml commando files.
	   */
	public static ActionSet getAllCommands()
	{
		return allCommands;
	}
	// }}}

	// {{{ start() method
	public void start()
	{

		BeanShell.getNameSpace().addCommandPath(CMD_PATH, getClass());
		// systemCommandDirectory = MiscUtilities.constructPath(".",
		// "commando");

		// Normally plugins don't use this directory.
		String settings = jEdit.getSettingsDirectory();
		if (settings != null)
		{
			// TODO: MIGRATE this to use getPluginHome() API ?
			consoleDirectory = MiscUtilities.constructPath(settings, "console");

			userCommandDirectory = MiscUtilities.constructPath(consoleDirectory,
				"commando");
			File file = new File(userCommandDirectory);
			if (!file.exists())
				file.mkdirs();
		}
		allCommands = new ActionSet("Plugin: Console - Commando Commands");
		shellSwitchActions = new ActionSet("Plugin: Console - Shell Switchers");
		rescanCommands();
		CommandoToolBar.init();
		EditBus.addToBus(this);
		

	} // }}}
	
	// {{{ loadShells() method
	// load the base shells (that is, those proviced via the service mechanism)
	// and the shells created by the user
	private void loadShells() {
		// get details of base shells, that is, shells that are provided by either
		// this plugin or other plugins, not user shells, which are loaded next.
		loadBaseShells();
		
		// load user-created shells
		loadUserShells();
	} // }}}
	
	// {{{ loadBaseShells() method
	// Here's a crappy hack -- ServiceManager doesn't expose its list of 
	// service details, so this bit of awesomeness breaks in and gets it anyway.
	public static void loadBaseShells() {
		baseShells = new HashMap<String, ShellDescriptor>();
		try {
			Class sm = Class.forName("org.gjt.sp.jedit.ServiceManager");
			Field f = sm.getDeclaredField("serviceMap");
			f.setAccessible(true);
			Map smMap = (Map)f.get(null);
			for (Object o : smMap.values()) {
				Field field = o.getClass().getDeclaredField("clazz");
				field.setAccessible(true);
				String clazz = (String)field.get(o);
				// there are lots of services, only want those that are shells
				if (!"console.Shell".equals(clazz))
					continue;
				ConsolePlugin.ShellDescriptor d = new ConsolePlugin.ShellDescriptor();
				field = o.getClass().getDeclaredField("name");
				field.setAccessible(true);
				d.name = (String)field.get(o);
				field = o.getClass().getDeclaredField("code");
				field.setAccessible(true);
				// code will be null only if the plugin providing the service
				// is not yet activated. 
				d.code = (String)field.get(o);
				if (d.code != null)
					d.code = d.code.trim();
				// might need the plugin jar later to force the plugin to 
				// activate
				field = o.getClass().getDeclaredField("plugin");
				field.setAccessible(true);
				d.plugin = (PluginJAR)field.get(o);
				baseShells.put(d.name, d);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	} // }}}
	
	// {{{ getBaseShellNames() method
	public static String[] getBaseShellNames() {
		ArrayList<String> names = new ArrayList<String>(baseShells.keySet());
		return names.toArray(new String[names.size()]);
	} // }}}
	
	// {{{ getBaseShellCode() method
	/**
	 * @param name The name of the shell to get the constructor code for.
 	 * @return The beanshell code that creates the shell. This may return null in
 	 * the case that the plugin providing the shell is not yet activated.
 	 */
	public static String getBaseShellCode(String name) {
		if (name == null)
			return null;
		ShellDescriptor d = baseShells.get(name);
		if (d == null)
			return null;
		return d.code;	
	} // }}}
	
	// {{{ getBaseShellPluginJAR() method
	/**
 	 * @param name The shell service name.	
 	 * @return The PluginJAR that provides a shell service.	
 	 */
	public static PluginJAR getBaseShellPluginJAR(String name) {
		if (name == null)
			return null;
		ShellDescriptor d = baseShells.get(name);
		if (d == null)
			return null;
		return d.plugin;	
	} // }}}
	
	// {{{ ShellDescriptor class
	/**
 	 * More or less the same as the Descriptor class from ServiceManager, this
 	 * keeps track of shell creation details.
 	 */
	public static class ShellDescriptor {
		public String name = "";
		public String code = "";
		public PluginJAR plugin;
		
		public String toString() {
			return new StringBuilder(name).append(", ").append(code).append(", ").append(plugin).toString();	
		}
		
		@Override
		public int hashCode()
		{
			return 31 * name.hashCode() + code.hashCode();
		}

		public boolean equals(Object o)
		{
			if(o instanceof ShellDescriptor)
			{
				ShellDescriptor d = (ShellDescriptor)o;
				return d.name.equals(name)
					&& d.code.equals(code);
			}
			else
				return false;
		}
	} // }}}
	
	// {{{ loadUserShells() method
	private void loadUserShells() {
		String userShells = jEdit.getProperty("console.userShells", "");
		if (!userShells.isEmpty()) 
		{
			String[] shellNames = userShells.split("[,]");
			StringList optOut = StringList.split(jEdit.getProperty("console.userShells.optOut", ""), "[,]");
			for (String name : shellNames)
			{
				if (name == null || name.isEmpty() || optOut.contains(name))
					continue;
				String code = jEdit.getProperty("console.userShells." + name + ".code");
				ServiceManager.registerService(Shell.SERVICE, name, code, null);
				Shell shell = (Shell)ServiceManager.getService(Shell.SERVICE, name);
				shell.setIsUserShell(true);
				shell.setName(name);
			}
		}
	} // }}}
	
	// {{{ getAllShells() method
	private List<Shell> getAllShells() {
		List<Shell> shells = new ArrayList<Shell>();
		String[] shellNames = Shell.getShellNames();
		if (shellNames != null && shellNames.length > 0) 
		{
			for (String name : shellNames)
			{
				if (name == null || name.isEmpty())
					continue;
				shells.add(Shell.getShell(name));
			}
		}
		return shells;
	} // }}}

	// {{{ parseLine()
	/** parseLine()
	 * Publicly documented class for parsing output of user defined
	 * programs through the system shell error parser.
	 *
	 * @return -1 if no error/warning, or an ErrorType.
	 *      Possible values are:
	 * 	@see errorlist.ErrorSource#ERROR
	 * 	@see errorlist.ErrorSource#WARNING
	 *
	 * Although it is possible derived ErrorSources will return custom error codes.
	 */
	public static synchronized int parseLine(View view,
		String text, String directory, DefaultErrorSource errorSource)
	{
		CommandOutputParser parser = getParser(view, directory, errorSource);
		return parser.processLine(text, false);
	} //}}}

	// {{{ stop() method
	public void stop()
	{
		// clean up edit bus
		EditBus.removeFromBus(this);
		jEdit.removeActionSet(allCommands);
		jEdit.removeActionSet(shellSwitchActions);
		allCommands.removeAllActions();
		shellSwitchActions.removeAllActions();
		// since there can now be multiple system shells, need to find them
		// all and call beforeStopping on each one.
		List<Shell> allShells = getAllShells();
		for (Shell shell : allShells) {
			if (shell instanceof SystemShell) {
				((SystemShell)shell).beforeStopping();	
			}
		}

		// ??? Does this really get all the Console objects that are in memory?
		View[] views = jEdit.getViews();
		for (int i = 0; i < views.length; i++) {
			Console console = getConsole(views[i]);
			if (console != null)
				console.unload();
		}
		BeanShell.getNameSpace().addCommandPath(CMD_PATH, getClass());
		CommandoToolBar.remove();
	} // }}}

	// {{{ handleViewUpdate() method
	@EBHandler
	public void handleViewUpdate(ViewUpdate vmsg)
	{
		if (vmsg.getWhat() == ViewUpdate.CREATED)
		{
			View v = vmsg.getView();
			CommandoToolBar.create(v);
		}
		if (vmsg.getWhat() == ViewUpdate.CLOSED) {
			View v = vmsg.getView();
			Console c = getConsole(v);
			if (c != null) c.unload();
			CommandoToolBar.remove(v);
		}
	}
	// }}}

	// {{{ handlePluginUpdate() method
	@EBHandler
	public void handlePluginUpdate(PluginUpdate msg)
	{
		rescanShells();
		
		if (PluginUpdate.REMOVED.equals(msg.getWhat())) {
			if (baseShells != null) { 
				PluginJAR plugin = msg.getPluginJAR();
				if (plugin == null) {
					return;	
				}
				List<String> removed = new ArrayList<String>();
				for (String name : baseShells.keySet()) {
					ShellDescriptor d = baseShells.get(name);
					if (d != null && plugin.equals(d.plugin)) {
						removed.add(name);
					}
				}
				StringList userShells = new StringList(Shell.getUserShellNames());
				StringList optOut = StringList.split(jEdit.getProperty("console.userShells.optOut", ""), "[,]");
				for (String name : removed) {
					baseShells.remove(name);
					userShells.remove(name);
					optOut.remove(name);
					ServiceManager.unregisterService(Shell.SERVICE, name);
					jEdit.unsetProperty("console.userShells." + name + ".code");
				}
				jEdit.setProperty("console.userShells", userShells.join(","));
				jEdit.setProperty("console.userShells.optOut", optOut.join(","));
			}
		}
		
	}
	// }}}
	
	@EBHandler
	public void handleEditorStarted(EditorStarted msg) {
		loadShells();	
	}

	// {{{ getConsoleSettingsDirectory() method
	public static String getConsoleSettingsDirectory()
	{
		return consoleDirectory;
	}
	// }}}

	// {{{ scanDirectory()
	/**
	 * Given a filename, performs translations so that it's a command name
	 */

	public static void scanDirectory(String directory)
	{
		if (directory != null)
		{
			File[] files = new File(directory).listFiles();
			if (files != null)
			{
				for (int i = 0; i < files.length; i++)
				{
					File file = files[i];
					String fileName = file.getAbsolutePath();
					if (!fileName.endsWith(".xml") || file.isHidden())
						continue;
					EditAction action = CommandoCommand.create(fileName);
					allCommands.addAction(action);
				}
			}
		}
	}
	// }}}

	// {{{ scanJarFile()

	public static void scanJarFile()
	{

		// TODO: scan contents of a resource directory instead of using this property
		String defaultCommands = jEdit.getProperty("commando.default");
		StringList sl = StringList.split(defaultCommands, " ");
		for (String name: sl) {
			String key = "commando." + name;
			if (allCommands.contains(key)) {
				// skip over those that have user overridden versions already loaded

				continue;
			}


			String resourceName = "/console/commands/" + name + ".xml";
			// System.out.println ("GetResource: " + resourceName);
			URL url = Console.class.getResource(resourceName);
			if (url != null)
			{
				EditAction action = CommandoCommand.create(url);
				allCommands.addAction(action);
			}
			else
			{
				Log.log(Log.ERROR, "ConsolePlugin", "Unable to access resource: "
					+ resourceName);
			}
		}
	}
	// }}}

	// {{{ rescanShells()
	static void rescanShells()
	{
		jEdit.removeActionSet(shellSwitchActions);
		shellSwitchActions.removeAllActions();
		for (String shell: Shell.getShellNames())
		{
			EditAction ac1 = new Shell.SwitchAction(shell);
			EditAction ac2 = new Shell.ToggleAction(shell);
			shellSwitchActions.addAction(ac1);
			shellSwitchActions.addAction(ac2);
		}
		jEdit.addActionSet(shellSwitchActions);
		redoKeyboardBindings(shellSwitchActions);
		EditBus.send(new DynamicMenuChanged(MENU));
	} // }}}

	// {{{ rescanCommands()
	/** Dynamicly generates two ActionSets, one for Commando commands,
	    and one for Shells.
	    Grabs the commando files from the jar file as well as user settings.
	 */
	public static void rescanCommands()
	{
		/*
		if (allCommands.size() > 1)
			return; */
		jEdit.removeActionSet(allCommands);
		allCommands.removeAllActions();

		scanDirectory(userCommandDirectory);
		scanJarFile();
		redoKeyboardBindings(allCommands);
		rescanShells();
		jEdit.addActionSet(allCommands);
		Log.log(Log.DEBUG, ConsolePlugin.class, "Loaded " + allCommands.size()
				+ " Actions");
		jEdit.propertiesChanged();

	} // }}}

	// {{{ redoKeyboardBindings
	/**
		A fix for keyboard bindings that are dynamically generated.
	*/
	static private void redoKeyboardBindings(ActionSet actionSet)
	/* Code duplication from jEdit.initKeyBindings() is bad, but
	   otherwise invoking 'rescan commando directory' will leave
	   old actions in the input handler
	*/
	{
		EditAction[] ea = actionSet.getActions();
		for (int i = 0; i < ea.length; ++i)
		{
			String shortcut1 = jEdit.getProperty(ea[i].getName() + ".shortcut");
			if (shortcut1 != null)
				jEdit.getInputHandler().addKeyBinding(shortcut1, ea[i]);

			String shortcut2 = jEdit.getProperty(ea[i].getName() + ".shortcut2");
			if (shortcut2 != null)
				jEdit.getInputHandler().addKeyBinding(shortcut2, ea[i]);
		}
	} // }}}

	// {{{ getSwitchActions()
	/** @return an array of "Shell Switcher" actions, some that toggle and others
	 * that just select and focus in the Console dockable.
	 */
	public static EditAction[] getSwitchActions() {
		EditAction[] actions = getShellSwitchActions().getActions();
		Arrays.sort(actions, new ActionCompare());
		return actions;
	} // }}}

	// {{{ getCommandoCommands() method
	/** @return only the visible commando commands as EditActions, in
	 *  a sorted array */
	public static EditAction[] getCommandoCommands()
	{

		String[] names = allCommands.getActionNames();
		TreeMap<String, EditAction> actions = new TreeMap<String, EditAction>();
		for (String name: names) {
			String label=name;
			if (label.startsWith("commando.")) {
				label = name.substring(9);
			}
			boolean visible = jEdit.getBooleanProperty("commando.visible." + label, true);
			if (visible) {
				actions.put(label, allCommands.getAction(name));
			}
		}
		EditAction[] ar = new EditAction[actions.size()];
		return actions.values().toArray(ar);
	} // }}}

	// {{{ compile() method
	public static void compile(View view, Buffer buffer)
	{
		SystemShell systemShell = getSystemShell();
		String mode = buffer.getMode().getName();

		// Check for a custom compile command
		if (jEdit.getBooleanProperty("mode."+mode+".compile.use-custom")) {
			String command = jEdit.getProperty("mode."+mode+".compile.custom", "");
			view.getDockableWindowManager().showDockableWindow("console");
			Console console = (Console) getConsole(view);
			Console.ShellState state = console.getShellState(systemShell);

			if (buffer.isDirty())
			{
				Object[] args = { buffer.getName() };
				int result = GUIUtilities.confirm(view, "commando.not-saved-compile", args,
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
				if (result == JOptionPane.YES_OPTION)
				{
					if (!buffer.save(view, null, true))
						return;
				}
				else if (result != JOptionPane.NO_OPTION)
					return;
			}

			systemShell.execute(console, null, state, null, command);
			return;
		}

		// If it got here, run commando as normal
		String compiler = buffer.getStringProperty("commando.compile");
		if (compiler == null || compiler.length() == 0)
		{
			GUIUtilities.error(view, "commando.no-compiler", null);
			return;
		}

		CommandoCommand command = (CommandoCommand) allCommands.getAction("commando."
			+ compiler);
		if (command == null)
		{
			GUIUtilities.error(view, "commando.no-command", new String[] { compiler });
		}
		else
		{
			if (buffer.isDirty())
			{
				Object[] args = { buffer.getName() };
				int result = GUIUtilities.confirm(view, "commando.not-saved-compile", args,
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
				if (result == JOptionPane.YES_OPTION)
				{
					if (!buffer.save(view, null, true))
						return;
				}
				else if (result != JOptionPane.NO_OPTION)
					return;
			}

			command.invoke(view);
		}
	} // }}}

	// {{{ getConsole() static method
	static public Console getConsole(View v) {
		if (v == null)
			return null;
		DockableWindowManager dwm = v.getDockableWindowManager();
		return (Console) dwm.getDockable("console");
	}
	// }}}

	// {{{ run() method
	public static void run(View view, Buffer buffer)
	{
		SystemShell systemShell = getSystemShell();
		String mode = buffer.getMode().getName();

		// Check for a custom compile command
		if (jEdit.getBooleanProperty("mode."+mode+".run.use-custom")) {
			String command = jEdit.getProperty("mode."+mode+".run.custom", "");
			view.getDockableWindowManager().showDockableWindow("console");
			Console console = (Console) getConsole(view);
			Console.ShellState state = console.getShellState(systemShell);

			if (buffer.isDirty())
			{
				Object[] args = { buffer.getName() };
				int result = GUIUtilities.confirm(view, "commando.not-saved-run", args,
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
				if (result == JOptionPane.YES_OPTION)
				{
					if (!buffer.save(view, null, true))
						return;
				}
				else if (result != JOptionPane.NO_OPTION)
					return;
			}

			systemShell.execute(console, null, state, null, command);
			return;
		}

		// If it got here, run commando as normal
		String interpreter = buffer.getStringProperty("commando.run");
		if (interpreter == null || interpreter.length() == 0)
		{
			GUIUtilities.error(view, "commando.no-interpreter", null);
			return;
		}

		CommandoCommand command = (CommandoCommand) allCommands.getAction("commando."
			+ interpreter);
		if (command == null)
		{
			GUIUtilities.error(view, "commando.no-command",
				new String[] { interpreter });
		}
		else
		{
			if (buffer.isDirty())
			{
				Object[] args = { buffer.getName() };
				int result = GUIUtilities.confirm(view, "commando.not-saved-run",
					args, JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.WARNING_MESSAGE);
				if (result == JOptionPane.YES_OPTION)
				{
					if (!buffer.save(view, null, true))
						return;
				}
				else if (result != JOptionPane.NO_OPTION)
					return;
			}

			command.invoke(view);
		}
	} // }}}

	// {{{ compileProject() method
	public static void compileProject(View view) {
		runProjectCommand(view, "compile");
	} // }}}

	// {{{ runProject() method
	public static void runProject(View view) {
		runProjectCommand(view, "run");
	} // }}}

	// {{{ runProjectCommand() method
	private static void runProjectCommand(View view, String prop) {
		if (jEdit.getPlugin("projectviewer.ProjectPlugin") == null) {
			GUIUtilities.error(view, "console.pv.not-installed", null);
			return;
		}

		projectviewer.vpt.VPTProject project =
			projectviewer.ProjectViewer.getActiveProject(view);
		if (project == null) {
			GUIUtilities.error(view, "console.pv.no-active-project", null);
			return;
		}

		String cmd = project.getProperty("console."+prop);
		if (cmd == null) cmd = "";
		if (cmd.equals("")) {
			// ask the user if they want to define the command now
			int res = GUIUtilities.confirm(view, "console.pv.no-command",
				new String[] { prop }, JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);

			if (res != JOptionPane.YES_OPTION)
				return;

			projectviewer.PVActions.editProject(project, "pv.commands");

			cmd = project.getProperty("console." + prop);
			if (cmd == null || cmd.trim().isEmpty())
				return;
		}

		view.getDockableWindowManager().showDockableWindow("console");
		final Console console = (Console) getConsole(view);
		if (jEdit.getBooleanProperty("console.clearBeforeExecute"))
			console.clear();
		String runDir = project.getProperty("console.runDir");
		if (runDir == null) runDir = project.getRootPath();
		console.chDir(runDir, true);
		console.run(console.getShell(), console.getOutput(), cmd);
	} // }}}

	// {{{ getPackageName() method
	/**
	 * A utility method that returns the name of the package containing the
	 * current buffer.
	 * note: these might not be needed anymore as of 4.3pre3
	 * @param buffer The buffer
	 */
	public static String getPackageName(Buffer buffer)
	{
		StringReader in = new StringReader(buffer.getText(0, buffer.getLength()));

		try
		{
			StreamTokenizer stok = new StreamTokenizer(in);

			// set tokenizer to skip comments
			stok.slashStarComments(true);
			stok.slashSlashComments(true);

			while (stok.nextToken() != StreamTokenizer.TT_EOF)
			{
				if (stok.sval == null)
					continue;
				if (stok.sval.equals("package"))
				{
					stok.nextToken();
					in.close();
					return stok.sval;
				}
				else if (stok.sval.equals("class"))
				{
					in.close();
					return null;
				}
			}

			in.close();
		}
		catch (IOException io)
		{
			// can't happen
			throw new InternalError();
		}

		return null;
	} // }}}

	// {{{ getClassName() method

	/**
	 * Returns the name of the specified buffer without the extension,
	 * appended to the buffer's package name.
	 * note: this might not be needed with the new JARClassloader
	 *
	 * @param buffer
	 *                The buffer
	 */
	public static String getClassName(Buffer buffer)
	{
		String pkg = getPackageName(buffer);
		String clazz = MiscUtilities.getBaseName(buffer.getPath());
		if (pkg == null)
			return clazz;
		else
			return pkg + '.' + clazz;
	} // }}}

	// {{{ getPackageRoot() method
	/**
	 * Returns the directory containing the root of the package of the
	 * current buffer. For example, if the buffer is located in
	 * <code>/home/slava/Stuff/example/Example.java</code> and contains a
	 * <code>package example</code> statement, this method will return
	 * <code>/home/slava/Stuff</code>.
	 *
	 * @param buffer
	 *                The buffer
	 */
	public static String getPackageRoot(Buffer buffer)
	{
		String pkg = getPackageName(buffer);
		String path = MiscUtilities.getParentOfPath(buffer.getPath());
		if (path.endsWith(File.separator))
			path = path.substring(0, path.length() - 1);

		if (pkg == null)
			return path;

		pkg = pkg.replace('.', File.separatorChar);
		if (path.endsWith(pkg))
			return path.substring(0, path.length() - pkg.length());
		else
			return path;
	} // }}}

	// {{{ expandSystemShellVariables() method
	/**
	 * Expands embedded environment variables in the same manner as the
	 * system shell.
	 *
	 * @param view
	 *                The view
	 * @param text
	 *                The string to expand
	 */
	public static String expandSystemShellVariables(View view, String text)
	{
		return getSystemShell().expandVariables(view, text);
	} // }}}

	// {{{ getSystemShellVariableValue() method
	/**
	 * Returns the value of the specified system shell environment variable.
	 *
	 * @param view The view
	 * @param var  The variable name
	 */
	public static String getSystemShellVariableValue(View view, String var)
	{
		return getSystemShell().getVariableValue(view, var);
	} // }}}

	// {{{ setSystemShellVariableValue() method
	/**
	 * Sets the value of the specified system shell environment variable.
	 *
	 * @param var The variable name
	 * @param value The value
	 */
	public static void setSystemShellVariableValue(String var, String value)
	{
		getSystemShell().getVariables().put(var, value);
	} // }}}

	// {{{ getUserCommandDirectory()
	public static String getUserCommandDirectory()
	{
		return userCommandDirectory;
	}
	// }}}

	// {{{ private methods
	// {{{ getParser()
	private static HashMap<View, CommandOutputParser> sm_parsers;
	// TODO - make this uniqueify on errorsource - given a new errorsource,
	// return a new parser.
	private static CommandOutputParser getParser(View v, String dir, DefaultErrorSource es) {
		if (sm_parsers == null) {
			sm_parsers = new HashMap<View, CommandOutputParser>();
		}
		CommandOutputParser retval = sm_parsers.get(v);
		Console console = ConsolePlugin.getConsole(v);
		if (retval == null) {
			retval = new CommandOutputParser(v, es, console.getPlainColor());
			sm_parsers.put(v, retval);
		}
		retval.setDirectory(dir);
		return retval;
	} // }}}

	// }}}

	// {{{ Inner classes

	// {{{ ActionCompare class
	static class ActionCompare implements Comparator<EditAction>
	{
		public int compare(EditAction a1, EditAction a2)
		{
			return a1.getLabel().compareTo(a2.getLabel());
		}
	} // }}}

	// }}}

} // }}}

