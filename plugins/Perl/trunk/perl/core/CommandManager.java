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

package perl.core;

import perl.core.GdbState.StateListener;
import perl.core.Parser.GdbResult;
import perl.core.Parser.ResultHandler;
import perl.proc.PerlProcess;

import java.io.IOException;
import java.util.Vector;

public class CommandManager extends Thread {

	Vector<Command> commands = new Vector<Command>();
	PerlProcess process;
	Parser parser = null;
	private final Command StopCommand = new Command(null, "***Stop***", null);
	private Integer cid = new Integer(0);
	
	public class Command {
		String cmd;
		ResultHandler handler;
		Integer id;
		boolean done = false;
		public Command(Integer id, String cmd, ResultHandler handler) {
			this.id = id;
			this.cmd = cmd;
			this.handler = handler;
		}
		public void run() {
			ResultHandler wrapper = null;
			wrapper = new ResultHandler() {
				public void handle(String msg, GdbResult res) {
					if (handler != null)
						handler.handle(msg, res);
					synchronized(id) {
						done = true;
						id.notify();
					}
				}
			};
			parser.addResultHandler(wrapper);
			try {
				//System.err.println("CommandManager: " + cmd);
				Debugger.getInstance().commandRecord(cmd + "\n");
				process.getGdbInputWriter().write(cmd + "\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Wait for result
			synchronized(id) {
				try {
					if (! done)
						id.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			parser.removeResultHandler(wrapper);
		}
	}
	public CommandManager(PerlProcess process, Parser parser) {
		this.process = process;
		this.parser = parser;
	}
	// Add a command to be executed next (before the other registered commands)
	public void addNow(String cmd, ResultHandler handler) {
		synchronized(cid) {
			cid = Integer.valueOf(cid.intValue() + 1);
		}
		Command c = new Command(cid, cmd, handler);
		addNow(c);
	}
	public void addImmediateExecution(String cmd) {
		Debugger.getInstance().commandRecord(cmd + "\n");
		try {
			process.getGdbInputWriter().write(cmd + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void addNow(Command c) {
		synchronized(commands) {
			commands.add(0, c);
			commands.notify();
		}
	}
	public void addNow(String cmd) {
		addNow(cmd, null);
	}
	public void add(String cmd, ResultHandler handler) {
		synchronized(cid) {
			cid = Integer.valueOf(cid.intValue() + 1);
		}
		Command c = new Command(cid, cmd, handler);
		synchronized(commands) {
			commands.add(c);
			commands.notify();
		}
	}
	public void add(String cmd) {
		add(cmd, null);
	}
	public Command next() {
		Command cmd = null;
		synchronized(commands) {
			if (commands.isEmpty())
				try {
					commands.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			cmd = commands.remove(0);
		}
		return cmd;
	}
	public void run() {
		GdbState.addStateListener(new CommandManagerStateListener());
		while (true) {
			Command cmd = next();
			if (cmd == StopCommand)
				break;
			cmd.run();
		}
	}

	private class CommandManagerStateListener implements StateListener {
		public void stateChanged(perl.core.GdbState.State prev,
				perl.core.GdbState.State current) {
			if (current != GdbState.State.IDLE)
				return;
			addNow(StopCommand);
		}
		
	}
}
