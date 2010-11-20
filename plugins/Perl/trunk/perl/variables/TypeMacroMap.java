package perl.variables;

import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gjt.sp.jedit.jEdit;

import debugger.jedit.Plugin;

@SuppressWarnings("serial")
public class TypeMacroMap extends HashMap<String, String> {
	static final String PREFIX = Plugin.OPTION_PREFIX;
	private static final String TYPE_MACRO_MAP_PROP = PREFIX + "type_macro_map.";
	private static final String TYPE_MACRO_MAP_SIZE = TYPE_MACRO_MAP_PROP + "size";
	public static final String TYPE_PATTERN = PREFIX + "type_pattern";
	public static final String TYPE_REPLACEMENT = PREFIX + "type_replacement";
	
	private static TypeMacroMap instance = null;
	private Pattern typePattern;
	private String typePatternString;
	private String typeReplacement;
	
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
	public String getInferredType(String type) {
		if (type == null || typePattern == null)
			return type;
		Matcher m = typePattern.matcher(type);
		return m.replaceAll(typeReplacement);
	}
	public void load() {
		int n = jEdit.getIntegerProperty(TYPE_MACRO_MAP_SIZE, 0);
		for (int i = 0; i < n; i++) {
			String type = jEdit.getProperty(getTypePropName(i));
			String macro = jEdit.getProperty(getMacroPropName(i));
			put(type, macro);
		}
		setTypeInferrence(jEdit.getProperty(TYPE_PATTERN),
				jEdit.getProperty(TYPE_REPLACEMENT));
	}
	public void setTypeInferrence(String pattern, String replacement) {
		typePatternString = pattern;
		typeReplacement = replacement;
		typePattern = Pattern.compile(typePatternString);
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
		jEdit.setProperty(TYPE_PATTERN, typePatternString);
		jEdit.setProperty(TYPE_REPLACEMENT, typeReplacement);
	}
}
