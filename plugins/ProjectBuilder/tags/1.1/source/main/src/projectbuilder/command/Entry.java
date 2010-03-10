package projectbuilder.command;
// imports {{{
import java.util.Properties;
// }}} imports
public class Entry {
	protected String command;
	protected String name;
	public Entry(String prop) {
		String[] entry = prop.split(":");
		name = entry[0];
		command = entry[1];
	}
	public String toString() { return name; }
	public String getCommand() { return command; }
	public String getName() { return name; }
	public String getProp() { return name+":"+command; }
	public ParsedCommand parse() { return new ParsedCommand(); }
	public class ParsedCommand {
		private String type;
		private Properties props;
		public ParsedCommand() {
			int bracket = command.indexOf("[");
			type = command.substring(0, bracket);
			String _props = command.substring(bracket+1, command.indexOf("]", bracket));
			props = new Properties();
			String[] list = _props.split(",");
			for (int i=0; i<list.length; i++) {
				int equals = list[i].indexOf("=");
				if (equals == -1) continue;
				props.setProperty(list[i].substring(0, equals),
					list[i].substring(equals+1, list[i].length()));
			}
		}
		public String type() { return this.type; }
		public String getProperty(String prop) { return (String) props.get(prop); }
	}
}
