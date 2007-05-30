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

package gdb.core;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Vector;

import gdb.core.GdbState.StateListener;
import gdb.core.Parser.GdbResult;
import gdb.core.Parser.ResultHandler;

public class CommandManager extends Thread {

	Vector<Command> commands = new Vector<Command>();
	BufferedWriter stdOutput = null;
	Parser parser = null;
	int id = 0;
	private final Command StopCommand = new Command(null, "***Stop***", null);
	
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
				Debugger.getInstance().commandRecord(">>> CommandManager: " + cmd + "\n");
				stdOutput.write(cmd + "\n");
				stdOutput.flush();
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
	public CommandManager(Process p, Parser parser) {
		stdOutput = new BufferedWriter(
				new OutputStreamWriter(p.getOutputStream()));
		this.parser = parser;
	}
	// Add a command to be executed next (before the other registered commands)
	public void addNow(String cmd, ResultHandler handler) {
		Integer cid = Integer.valueOf(id);
		id++;
		Command c = new Command(cid, cmd, handler);
		addNow(c);
	}
	public void addImmediateExecution(String cmd) {
		Debugger.getInstance().commandRecord(">>> CommandManager: " + cmd + "\n");
		try {
			stdOutput.write(cmd + "\n");
			stdOutput.flush();
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
		Integer cid = Integer.valueOf(id);
		id++;
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
		public void stateChanged(gdb.core.GdbState.State prev,
				gdb.core.GdbState.State current) {
			if (current != GdbState.State.IDLE)
				return;
			addNow(StopCommand);
		}
		
	}
}
