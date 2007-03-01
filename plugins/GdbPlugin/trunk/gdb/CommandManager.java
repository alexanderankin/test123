package gdb;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Vector;

import gdb.Parser.GdbResult;
import gdb.Parser.ResultHandler;

public class CommandManager extends Thread {

	Vector<Command> commands = new Vector<Command>();
	BufferedWriter stdOutput = null;
	Debugger debugger = null;
	Parser parser = null;
	int id = 0;
	
	public class Command {
		String cmd;
		ResultHandler handler;
		Integer id;
		public Command(String cmd, ResultHandler handler) {
			this.cmd = cmd;
			this.handler = handler;
		}
		public Command(Integer id, String cmd, ResultHandler handler) {
			this.id = id;
			this.cmd = cmd;
			this.handler = handler;
		}
		public void run() {
			ResultHandler wrapper = null;
			if (handler != null) {
				wrapper = new ResultHandler() {
					public void handle(String msg, GdbResult res) {
						handler.handle(msg, res);
						synchronized(id) {
							id.notify();
						}
					}
				};
				parser.addResultHandler(wrapper);
			}
			try {
				stdOutput.write(cmd + "\n");
				stdOutput.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (handler != null) {
				// Wait for result
				synchronized(id) {
					try {
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
	public CommandManager(Debugger debugger, Process p, Parser parser) {
		this.debugger = debugger;
		stdOutput = new BufferedWriter(
				new OutputStreamWriter(p.getOutputStream()));
		this.parser = parser;
	}
	public void add(String cmd, ResultHandler handler) {
		Integer cid = new Integer(id);
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
		while (true) {
			Command cmd = next();
			cmd.run();
		}
	}
}
