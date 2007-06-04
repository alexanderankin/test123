package gdb.variables;

import java.util.HashMap;
import java.util.Iterator;

import org.gjt.sp.jedit.jEdit;

import debugger.jedit.Plugin;

@SuppressWarnings("serial")
public class TypeMacroMap extends HashMap<String, String> {
	static final String PREFIX = Plugin.OPTION_PREFIX;
	private static final String TYPE_MACRO_MAP_PROP = PREFIX + "type_macro_map.";
	private static final String TYPE_MACRO_MAP_SIZE = TYPE_MACRO_MAP_PROP + "size";
	private static TypeMacroMap instance = null;
	
	private TypeMacroMap() {
		load();
	}
	private String getTypePropName(int index) {
		return TYPE_MACRO_MAP_PROP + index + ".type";
	}
	private String getMacroPropName(int index) {
		return TYPE_MACRO_MAP_PROP + index + ".macro";
	}
	public static TypeMacroMap getInstance() {
		if (instance == null)
			instance = new TypeMacroMap();
		return instance;
	}
	public void load() {
		int n = jEdit.getIntegerProperty(TYPE_MACRO_MAP_SIZE, 0);
		for (int i = 0; i < n; i++) {
			String type = jEdit.getProperty(getTypePropName(i));
			String macro = jEdit.getProperty(getMacroPropName(i));
			put(type, macro);
		}
	}
	public void save() {
		Iterator<String> keys = keySet().iterator();
		int i = 0;
		while (keys.hasNext()) {
			String type = keys.next();
			String macro = get(type);
			jEdit.setProperty(getTypePropName(i), type);
			jEdit.setProperty(getMacroPropName(i), macro);
			i++;
		}
		jEdit.setIntegerProperty(TYPE_MACRO_MAP_SIZE, i);
	}
}
