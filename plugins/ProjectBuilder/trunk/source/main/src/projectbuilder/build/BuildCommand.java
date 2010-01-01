package projectbuilder.build;
// imports {{{
import javax.swing.JOptionPane;
import javax.swing.JComponent;
import javax.swing.JDialog;
import java.awt.Dimension;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import projectviewer.vpt.VPTProject;

import console.Shell;
import console.Console;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.Macros;
import org.gjt.sp.jedit.gui.DockableWindowManager;

import groovy.swing.SwingBuilder;

import common.gui.ListPanel;
// }}}
/**
 * This class handles running projects and editing build commands
 * By default, ant commands are run within the JVM using AntFarm, all others through System
 * Currently, there is no option to run ant through the System shell
 */
public class BuildCommand {
	
	public static JDialog settings;
	
	public static void run(final View view, final VPTProject proj) {
		
		String[] commands = getCommandList(proj);
		if (commands == null) {
			GUIUtilities.error(view, "projectBuilder.msg.no-build-command", null);
			return;
		}
		final String cmd; // The final command to run
		if (commands.length>1) {
			// Prompt for which command to use
			cmd = (String) JOptionPane.showInputDialog(view,
												"Build this project with:",
												"Build",
												JOptionPane.PLAIN_MESSAGE,
												null,
												commands,
												null);
			if (cmd == null) return;
		} else {
			cmd = commands[0];
		}
		
		final DockableWindowManager wm = view.getDockableWindowManager();
		
		new Thread(new Runnable() {
			public void run() {
				if ((cmd.equals("ant") || cmd.startsWith("ant ")) && jEdit.getBooleanProperty("projectBuilder.run-ant-in-jvm")) {
					// Build in AntFarm
					String target = "";
					if (cmd.indexOf(" ") != -1) {
						target = cmd.substring(cmd.indexOf(" ")+1, cmd.length());
					}
					String buildfile = proj.getRootPath()+"/build.xml";
					wm.addDockableWindow("console");
					Console console = (Console) wm.getDockable("console");
					Shell ant = Shell.getShell("Ant");
					console.clear();
					console.run(ant, "+"+buildfile);
					ant.waitFor(console);
					console.run(ant, "!"+target);
					view.getDockableWindowManager().showDockableWindow("console");
					new BuildWatcher(console).start();
				} else {
					// Run in system shell
					wm.addDockableWindow("console");
					Console console = (Console) wm.getDockable("console");
					console.setShell("System");
					Shell system = Shell.getShell("System");
					String cd = "cd \""+proj.getRootPath()+"\"";
					system.execute(console, null, console.getShellState(system), null, cd);
					system.waitFor(console);
					system.execute(console, null, console.getShellState(system), null, cmd);
					view.getDockableWindowManager().showDockableWindow("console");
				}
			}
		}).start();
	}
	
	public static void editCommands(View view, VPTProject proj) {
		String _cmd = proj.getProperty("projectBuilder.command.build");
		/*
		if (_cmd == null || _cmd.length() == 0) {
			GUIUtilities.error(view, "projectBuilder.msg.no-build-command", null)
			return
		}
		*/
		settings = new JDialog(view, "Project Build Settings");
		settings.add(new BuildSettingsPanel(proj));
		settings.pack();
		settings.setLocationRelativeTo(view);
		settings.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		settings.setVisible(true);
		// TODO: Get Escape to close the dialog
		settings.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					settings.dispose();
				}
			}
			public void keyTyped(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {}
		});
	}
	
	public static String[] getCommandList(VPTProject proj) {
		String cmd = proj.getProperty("projectBuilder.command.build");
		if (cmd == null || cmd.length() == 0) {
			return null;
		}
		String[] commands;
		if (cmd.indexOf("|") != -1)
			commands = cmd.split("\\|");
		else {
			commands = new String[1];
			commands[0] = cmd;
		}
		return commands;
	}
	
}
