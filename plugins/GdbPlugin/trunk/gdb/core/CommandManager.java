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
				Debugger.getInstance().gdbRecord(">>> CommandManager: " + cmd + "\n");
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
