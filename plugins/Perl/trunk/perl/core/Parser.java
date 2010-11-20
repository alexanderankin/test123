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
import perl.proc.PerlProcess;
import perl.proc.PerlProcess.Reader;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

public class Parser extends Thread {

	public interface ResultHandler {
		void handle(String msg, GdbResult res);
	}
	public interface GdbHandler {
		void handle(String line);
	}

	static final int InputStreamDelay = 1; 
	Debugger debugger;
	PerlProcess process;
	private Vector<ResultHandler> resultHandlers = new Vector<ResultHandler>();
	private Vector<ResultHandler> outOfBandHandlers = new Vector<ResultHandler>();
	private Vector<GdbHandler> gdbHandlers = new Vector<GdbHandler>();
	private Thread errThread;
	private boolean stopping = false;
	
	static public class GdbResult {
		Hashtable<String, Object> result = new Hashtable<String, Object>();
		int next;
		GdbResult(String line) {
			next = 0;
			while (next != -1 && next < line.length()) {
				Object [] pair = parsePair(line);
				result.put((String)pair[0], pair[1]);
				if (next != -1 && next < line.length()) {
					if (line.charAt(next) == ',')
						next++;
				}
			}
		}
		// Parsing methods
		Object [] parsePair(String line) {
			//System.err.println("parsePair:" + line.substring(next));
			String var = parseVariable(line);
			Object value = parseValue(line);
			Object [] o = new Object[2];
			o[0] = var;
			o[1] = value;
			return o;
		}
		String parseVariable(String line) {
			//System.err.println("parseVariable:" + line.substring(next));
			int index = line.indexOf('=', next);
			if (index < 1) {
				next = -1;
				return null;
			}
			int prev = next;
			next = index + 1;
			return line.substring(prev, index);
		}
		boolean isValue(String line) {
			char c = line.charAt(next);
			return (c == '{' || c == '[' || c == '"');
		}
		Object parseValue(String line) {
			//System.err.println("parseValue:" + line.substring(next));
			char c = line.charAt(next);
			switch (c) {
			case '"': return parseCString(line);
			case '{': return parseTuple(line);
			case '[': return parseList(line);
			default: return line.substring(next);
			}
		}
		private Object parseList(String line) {
			//System.err.println("parseList:" + line.substring(next));
			Vector<Object> list = new Vector<Object>();
			next++;
			while (line.charAt(next) != ']') {
				if (isValue(line)) {
					list.add(parseValue(line));
				} else {
					Object [] pair = parsePair(line);
					Hashtable<String, Object> hash = new Hashtable<String, Object>();
					hash.put((String)pair[0], pair[1]);
					list.add(hash);
				}
				char c = line.charAt(next);
				if (c == ',')
					next++;
				else if (c != ']')
					System.err.println("Problem with line:\n" + line + "\n" +
							"at char " + next);
			}
			next++;
			return list;
		}
		private Object parseTuple(String line) {
			//System.err.println("parseTuple:" + line.substring(next));
			Hashtable<String, Object> hash = new Hashtable<String, Object>();
			next++;
			while (line.charAt(next) != '}') {
				Object [] pair = parsePair(line);
				hash.put((String)pair[0], pair[1]);
				char c = line.charAt(next);
				if (c == ',')
					next++;
				else if (c != '}')
					System.err.println("Problem with line:\n" + line + "\n" +
							"at char " + next);
			}
			next++;
			return hash;
		}
		private Object parseCString(String line) {
			//System.err.println("parseCString:" + line.substring(next));
			int index = next;
			do {
				index = line.indexOf("\"", index + 1);
				if (index < 0) {
					next = index;
					return line.substring(1);
				}
			} while (line.charAt(index - 1) == '\\');
			int prev = next + 1;
			next = index + 1;
			return line.substring(prev, index);
		}
		
		// Query methods
		@SuppressWarnings("unchecked")
		public Object getValue(String path) {
			Object current = result;
			int index = 0;
			boolean cont = true;
			while (current != null && cont) {
				index = path.indexOf("/");
				String part;
				if (index < 0) {
					cont = false;
					part = path;
				} else {
					part = path.substring(0, index);
				}
				if (current instanceof Hashtable)
				{
					current = ((Hashtable<String, Object>)current).get(part);
					path = path.substring(index + 1);
				} else if (current instanceof Vector) {
					int i = Integer.parseInt(part);
					current = ((Vector)current).get(i);
					path = path.substring(index + 1);
				} else {
					return null;
				}
			}
			return current;
		}
		public String getStringValue(String path) {
			Object current = getValue(path);
			if (current == null)
				return null;
			return current.toString();
		}
	}
	
	public Parser(Debugger debugger, PerlProcess process) {
		this.debugger = debugger;
		this.process = process;
	}
	
	public void addResultHandler(ResultHandler rh)
	{
		resultHandlers.add(rh);
	}
	public void removeResultHandler(ResultHandler rh)
	{
		resultHandlers.remove(rh);
	}
	public void addOutOfBandHandler(ResultHandler rh)
	{
		outOfBandHandlers.add(rh);
	}
	public void removeOutOfBandHandler(ResultHandler rh)
	{
		outOfBandHandlers.remove(rh);
	}
	public void addGdbHandler(GdbHandler gh)
	{
		gdbHandlers.add(gh);
	}
	public void removeGdbHandler(GdbHandler gh)
	{
		gdbHandlers.remove(gh);
	}
	String extractString(String line)
	{
		if (line.startsWith("\""))
			line = line.substring(1);
		if (line.endsWith("\""))
			line = line.substring(0, line.length() - 1);
		if (line.endsWith("\\n"))
			line = line.substring(0, line.length() - 2) + "\n";
		return line;
	}
	void parse(String line) {
		//System.err.println("Parsing line: " + line);
		debugger.gdbRecord(line + "\n");
		int len = line.length();
		if (len == 0)
			return;
		int lineBegin = -1;
		char c;
		do {
			lineBegin++;
			c = line.charAt(lineBegin);
		} while (c >= '0' && c <= '9' && lineBegin < len - 1);
		switch (c) {
		case '@':
			// Ignore - remote target records
			return;
		case '&':
			// Debugging messages produced by GDB internals
			debugger.gdbMessage(extractString(line.substring(lineBegin + 1)));
			return;
		case '~':
			// Gdb CLI record
			String l = extractString(line.substring(lineBegin + 1));
			for (int i = 0; i < gdbHandlers.size(); i++)
				gdbHandlers.get(i).handle(l);
			return;
		case '^':
		case '*':
			// Result ('^') or out-of-band ('*') record
			int sepIndex = line.indexOf(",");
			String msg = null;
			GdbResult res = null;
			if (sepIndex < 0) {
				msg = line.substring(lineBegin + 1);
			} else {
				msg = line.substring(lineBegin + 1, sepIndex);
				res = new GdbResult(line.substring(sepIndex + 1));
			}
			if (c == '^') {
				if (msg.equals("running"))
					GdbState.setState(GdbState.State.RUNNING);
				for (int i = 0; i < resultHandlers.size(); i++)
					resultHandlers.get(i).handle(msg, res);
			} else {
				for (int i = 0; i < outOfBandHandlers.size(); i++)
					outOfBandHandlers.get(i).handle(msg, res);
			}
			return;
		}
		if (line.startsWith("(gdb)"))
			return;
		// Program output
		debugger.programRecord(line + "\n");
	}
	
	@Override
	public void run() {
		String line;
		errThread = new Thread() {
			public void run() {
				String line;
				Reader gdbError = process.getGdbErrorReader();
				while (! stopping) {
					try {
						while (gdbError.ready()) {
							if ((line=gdbError.readLine()) != null)
								debugger.programError(line + "\n");
						}
					} catch (IOException e) {
					}
					try {
						sleep(InputStreamDelay);
					} catch (InterruptedException e) {
					}
				}
			}
		};
		errThread.start();
		GdbState.addStateListener(new ParserStateListener());
		while (! stopping) {
			Reader gdbOutput = process.getGdbOutputReader();
			try {
				while (gdbOutput.ready()) {
					if ((line=gdbOutput.readLine()) != null)
						parse(line);
				}
			} catch (IOException e) {
			}
			try {
				sleep(InputStreamDelay);
			} catch (InterruptedException e) {
			}
		}
	}

	private class ParserStateListener implements StateListener {
		public void stateChanged(perl.core.GdbState.State prev,
				perl.core.GdbState.State current) {
			if (current != GdbState.State.IDLE)
				return;
			stopping = true;
			outOfBandHandlers.clear();
			resultHandlers.clear();
			gdbHandlers.clear();
		}
		
	}
}
