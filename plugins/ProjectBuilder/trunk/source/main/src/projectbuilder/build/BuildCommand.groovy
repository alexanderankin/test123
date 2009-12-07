package projectbuilder.build
// imports {{{
import javax.swing.JOptionPane
import javax.swing.JComponent
import javax.swing.JDialog
import java.awt.Dimension

import projectviewer.vpt.VPTProject

import console.Shell

import org.gjt.sp.jedit.jEdit as JEDIT
import org.gjt.sp.jedit.View
import org.gjt.sp.jedit.GUIUtilities
import org.gjt.sp.jedit.Macros

import groovy.swing.SwingBuilder

import common.gui.ListPanel
// }}}
/**
 * This class handles running projects and editing build commands
 * By default, ant commands are run within the JVM using AntFarm, all others through System
 * Currently, there is no option to run ant through the System shell
 */
public class BuildCommand {
	
	public static void run(View view, VPTProject proj) {
		
		String[] commands = getCommandList(proj)
		if (commands == null) {
			GUIUtilities.error(view, "projectBuilder.msg.no-build-command", null)
			return
		}
		String cmd // The final command to run
		if (commands.length>1) {
			// Prompt for which command to use
			cmd = JOptionPane.showInputDialog(view,
												"Build this project with:",
												"Build",
												JOptionPane.PLAIN_MESSAGE,
												null,
												commands,
												null)
			if (cmd == null) return
		} else {
			cmd = commands[0]
		}
		
		if (cmd.equals("ant") || cmd.startsWith("ant ")) {
			// Build in AntFarm
			String target = ""
			if (cmd.indexOf(" ") != -1) {
				target = cmd.substring(cmd.indexOf(" ")+1, cmd.length())
			}
			String buildfile = proj.getRootPath()+"/build.xml"
			JComponent console = view.getDockableWindowManager().getDockable("console")
			Shell ant = Shell.getShell("Ant");
			console.clear()
			console.run(ant, "+"+buildfile)
			ant.waitFor(console)
			console.run(ant, "!"+target)
			view.getDockableWindowManager().showDockableWindow("console")
			new BuildWatcher(console).start()
		} else {
			// Run in system shell
			view.getDockableWindowManager().addDockableWindow("console")
			JComponent console = view.getDockableWindowManager().getDockable("console")
			console.setShell("System");
			Shell system = Shell.getShell("System")
			String cd = "cd \""+proj.getRootPath()+"\""
			system.execute(console, null, console.getShellState(system), null, cd)
			system.waitFor(console)
			system.execute(console, null, console.getShellState(system), null, cmd)
			view.getDockableWindowManager().showDockableWindow("console")
		}
	}
	
	public static void editCommands(View view, VPTProject proj) {
		String _cmd = proj.getProperty("projectBuilder.command.build")
		/*
		if (_cmd == null || _cmd.length() == 0) {
			GUIUtilities.error(view, "projectBuilder.msg.no-build-command", null)
			return
		}
		*/
		JDialog dialog = new JDialog(view, "Project Build Settings")
		dialog.add(new BuildSettingsPanel(proj))
		dialog.pack()
		dialog.setLocationRelativeTo(view)
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE)
		dialog.setVisible(true)
	}
	
	public static String[] getCommandList(VPTProject proj) {
		String cmd = proj.getProperty("projectBuilder.command.build")
		if (cmd == null || cmd.length() == 0) {
			return null
		}
		String[] commands
		if (cmd.indexOf("|") != -1)
			commands = cmd.split("\\|")
		else {
			commands = new String[1]
			commands[0] = cmd
		}
		return commands
	}
	
}
