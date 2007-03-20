package gdb;

import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Vector;

public class Parser extends Thread {

	public interface ResultHandler {
		void handle(String msg, GdbResult res);
	}
	public interface GdbHandler {
		void handle(String line);
	}

	Debugger debugger;
	private BufferedReader stdInput;
	private Vector<ResultHandler> resultHandlers = new Vector<ResultHandler>();
	private Vector<ResultHandler> outOfBandHandlers = new Vector<ResultHandler>();
	private Vector<GdbHandler> gdbHandlers = new Vector<GdbHandler>();
	
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
	
	Parser(Debugger debugger, Process gdbProcess) {
		this.debugger = debugger;
        stdInput = new BufferedReader(new 
                InputStreamReader(gdbProcess.getInputStream()));
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
		System.err.println("Parsing line: " + line);
		char c = line.charAt(0);
		switch (c) {
		case '@':
		case '&':
			// Ignore - debugging and remote target records
			return;
		case '~':
			// Gdb CLI record
			String l = extractString(line.substring(1));
			if (gdbHandlers.isEmpty()) {
				debugger.gdbRecord(l);
				return;
			}
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
				msg = line;
			} else {
				msg = line.substring(1, sepIndex);
				res = new GdbResult(line.substring(sepIndex + 1));
			}
			if (c == '^') {
				for (int i = 0; i < resultHandlers.size(); i++)
					resultHandlers.get(i).handle(msg, res);
			} else {
				for (int i = 0; i < outOfBandHandlers.size(); i++)
					outOfBandHandlers.get(i).handle(msg, res);
			}
			return;
		}
		if (line.startsWith("(gdb)")) {
			debugger.gdbRecord(line + "\n");
			return;
		}
		// Program output
		debugger.programRecord(line + "\n");
	}
	
	@Override
	public void run() {
		String line;
		try {
			while ((line=stdInput.readLine()) != null)
			{
				parse(line);
			}
		} catch (HeadlessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
