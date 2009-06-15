package gatchan.jedit.lucene;

import java.util.HashMap;


public class IndexFactory
{
	private static HashMap<String, Class> indexes = new HashMap<String, Class>();

	static {
		register("File-based index", IndexImpl.class);
		register("Line-based index", LineIndexImpl.class);
	}

	public static void register(String name, Class cls)
	{
		indexes.put(name, cls);
	}
	public static String [] getIndexNames()
	{
		String [] names = new String[indexes.size()];
		indexes.keySet().toArray(names);
		return names;
	}
	public static Index createIndex(String name)
	{
		Class c = indexes.get(name);
		if (c == null)
			return null;
		Index index = null;
		try {
			index = (Index) c.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return index;
	}
}
