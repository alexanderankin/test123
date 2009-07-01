package com.addictedtor.infonode.perspective.importer;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import org.w3c.dom.Node;

/**
 * Provides the correct importer for the given node
 */
public class ImporterProvider {

	/**
	 * Mapping between the node name we are currently importing and the importer
	 * class to use
	 */
	private static HashMap<String, Class<? extends Importer>> map = new HashMap<String, Class<? extends Importer>>();
	static {
		// info node
		addImporter("RootWindow", RootWindowImporter.class);
		addImporter("FloatingWindow", FloatingWindowImporter.class);
		addImporter("TabWindow", TabWindowImporter.class);
		addImporter("SplitWindow", SplitWindowImporter.class);
		addImporter("WindowBar", WindowBarImporter.class);

		// jedit
		addImporter("JEditDockableView", JEditDockableViewImporter.class);
		addImporter("JEditViewView", JEditViewViewImporter.class);

	}

	/**
	 * Returns the correct importer for the node
	 * 
	 * @param node
	 *            the xml node we are importing
	 * @param snapshot
	 *            the snapshot of the current perspective
	 */
	public static Importer getImporter(Node node, Snapshot snapshot) {

		String name = node.getNodeName();
		if (map.containsKey(name)) {
			try {
				Constructor<? extends Importer> c = map.get(name)
						.getConstructor(
								new Class[] { Node.class, Snapshot.class });
				return c.newInstance(new Object[] { node, snapshot });
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return new DummyImporter(node, snapshot);
	}

	/**
	 * Adds an entry to the mapping between the name of the node and importer
	 * class.
	 * 
	 * @param name
	 *            the name of the node we want to import
	 * @param importerClass
	 *            the class of the importer
	 */
	public static void addImporter(String name,
			Class<? extends Importer> importerClass) {
		map.put(name, importerClass);
	}

}
