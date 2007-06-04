package gdb.variables;

import java.util.HashMap;
import java.util.Iterator;

import org.gjt.sp.jedit.jEdit;

@SuppressWarnings("serial")
public class TypeMacroMap extends HashMap<String, String> {
	private static TypeMacroMap instance = null;
	
	private TypeMacroMap() {
		load();
	}
	public static TypeMacroMap getInstance() {
		if (instance == null)
			instance = new TypeMacroMap();
		return instance;
	}
	public void load() {
		int n = jEdit.getIntegerProperty("options.debugger.type_macro_map.size", 0);
		for (int i = 0; i < n; i++) {
			String type = jEdit.getProperty("options.debugger.type_macro_map." +
					String.valueOf(i) + ".type");
			String macro = jEdit.getProperty("options.debugger.type_macro_map." +
					String.valueOf(i) + ".macro");
			put(type, macro);
		}
	}
	public void save() {
		jEdit.setIntegerProperty("options.debugger.type_macro_map.size", size());
		Iterator<String> kit = this.keySet().iterator();
		int i = 0;
		while (kit.hasNext()) {
			String type = kit.next();
			String macro = get(kit);
			jEdit.setProperty("options.debugger.type_macro_map." +
					String.valueOf(i) + ".type", type);
			jEdit.setProperty("options.debugger.type_macro_map." +
					String.valueOf(i) + ".macro", macro);
			i++;
		}
	}
}
