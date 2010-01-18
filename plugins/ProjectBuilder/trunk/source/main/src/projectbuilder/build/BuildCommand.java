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
 * This class handles building projects and editing build commands
 * Ant commands are routed through AntFarm, others are run in the system shell
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
			// TODO: For ant commands, try and find a way to display a cleaner description, such as "ANT build"
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
				if (cmd.startsWith("ANT[")) {
					// Build in AntFarm
					String[] props = cmd.substring(4, cmd.indexOf("]")).split(",");
					String buildfile = "";
					String target = "";
					for (int i = 0; i<props.length; i++) {
						String p = props[i];
						if (p.startsWith("target="))
							target = p.substring(7);
						else if (p.startsWith("buildfile="))
							buildfile = p.substring(10);
					}
					wm.addDockableWindow("console");
					Console console = (Console) wm.getDockable("console");
					Shell ant = Shell.getShell("Ant");
					console.run(ant, "+"+buildfile);
					ant.waitFor(console);
					console.run(ant, "!"+target);
					view.getDockableWindowManager().showDockableWindow("console");
					new BuildWatcher(console).start();
					// QUESTION: Is there a way to run AntFarm commands without having the AntFarm window pop up? All we need is the shell.
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
