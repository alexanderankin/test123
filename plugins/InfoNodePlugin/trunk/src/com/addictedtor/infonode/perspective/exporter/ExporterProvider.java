package com.addictedtor.infonode.perspective.exporter;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.FloatingWindow;
import net.infonode.docking.RootWindow;
import net.infonode.docking.SplitWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;
import net.infonode.docking.WindowBar;

import com.addictedtor.infonode.JEditDockableView;
import com.addictedtor.infonode.JEditViewView;

/**
 * Provides the correct exporter for a docking window
 */
public class ExporterProvider {

	/**
	 * Mapping between the class of the DockingWindow we want to stream to XML
	 * and the exporter class
	 */
	private static HashMap<Class<? extends DockingWindow>, Class<? extends Exporter>> map = new HashMap<Class<? extends DockingWindow>, Class<? extends Exporter>>();
	static {
		// info node
		addExporter(RootWindow.class, RootWindowExporter.class);
		addExporter(FloatingWindow.class, FloatingWindowExporter.class);
		addExporter(TabWindow.class, TabWindowExporter.class);
		addExporter(SplitWindow.class, SplitWindowExporter.class);
		addExporter(View.class, ViewExporter.class);
		addExporter(WindowBar.class, WindowBarExporter.class);

		// jedit
		addExporter(JEditDockableView.class, JEditDockableViewExporter.class);
		addExporter(JEditViewView.class, JEditViewViewExporter.class);

	}

	/**
	 * Returns the correct exporter for the window
	 * 
	 * @param window
	 *            the window we want to stream to XML
	 */
	public static Exporter getExporter(DockingWindow window) {

		Class<? extends DockingWindow> clazz = window.getClass();
		if (map.containsKey(clazz)) {
			try {
				Constructor<? extends Exporter> c = map.get(clazz)
						.getConstructor(new Class[] { clazz });
				return c.newInstance(new Object[] { window });
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return new DummyExporter();
	}

	/**
	 * Adds an entry to the mapping between window class and exporter class.
	 * These mappings are used to determine which exporter class is used to
	 * stream the layout of a window instance of a given class. The exporter
	 * provider finds at runtime the class of the window and makes an exporter
	 * suitable for that class.
	 * 
	 * @param windowClass
	 *            the class for which we want to use a custom exporter
	 * @param exporterClass
	 *            the class of the exporter
	 */
	public static void addExporter(Class<? extends DockingWindow> windowClass,
			Class<? extends Exporter> exporterClass) {
		map.put(windowClass, exporterClass);
	}

}
