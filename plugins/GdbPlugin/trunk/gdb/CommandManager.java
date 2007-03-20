package gdb;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Vector;

import gdb.Parser.GdbResult;
import gdb.Parser.ResultHandler;

public class CommandManager extends Thread {

	private static CommandManager instance = null;
	Vector<Command> commands = new Vector<Command>();
	BufferedWriter stdOutput = null;
	Parser parser = null;
	int id = 0;
	
	public class Command {
		String cmd;
		ResultHandler handler;
		Integer id;
		boolean wait;
		boolean done = false;
		// Add a CLI command
		public Command(String cmd) {
			this.cmd = cmd;
			id = null;
			handler = null;
			wait = false;
		}
		public Command(Integer id, String cmd, ResultHandler handler) {
			this.id = id;
			this.cmd = cmd;
			this.handler = handler;
			wait = true;
		}
		public void run() {
			ResultHandler wrapper = null;
			if (wait) {
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
			}
			try {
				System.err.println("CommandManager: " + cmd);
				stdOutput.write(cmd + "\n");
				stdOutput.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (wait) {
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
	}
	public CommandManager(Process p, Parser parser) {
		instance = this;
		stdOutput = new BufferedWriter(
				new OutputStreamWriter(p.getOutputStream()));
		this.parser = parser;
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
	public void addCLI(String cmd) {
		Command c = new Command(cmd);
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
		while (true) {
			Command cmd = next();
			cmd.run();
		}
	}
	public static CommandManager getInstance() {
		return instance ;
	}
}
