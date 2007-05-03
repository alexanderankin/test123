/*
Copyright (C) 2007  Shlomy Reinstein
Merged into the Docker plugin by Alan Ezust

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

package docker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.gjt.sp.jedit.ActionSet;
import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.PerspectiveManager;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.XMLUtilities;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class Perspective {
	
	private static ActionSet actions = null;
	private static PerspectiveFileFilter filter = new PerspectiveFileFilter();
	
	static public void save(View view) {
		JFileChooser fc = new JFileChooser(new File(getConfigDirectory()));
		fc.addChoosableFileFilter(filter);
		fc.showSaveDialog(view);
	    File sel = fc.getSelectedFile();
	    if (sel == null)
			return;
		DockableWindowManager dockMan = view.getDockableWindowManager();
		Hashtable<String, String[]> dockables = new Hashtable<String, String[]>();
		dockables.put(DockableWindowManager.LEFT,
				dockMan.getLeftDockingArea().getDockables());
		dockables.put(DockableWindowManager.RIGHT,
				dockMan.getRightDockingArea().getDockables());
		dockables.put(DockableWindowManager.TOP,
				dockMan.getTopDockingArea().getDockables());
		dockables.put(DockableWindowManager.BOTTOM,
				dockMan.getBottomDockingArea().getDockables());
		PrintWriter w;
		try {
			w = new PrintWriter(new FileWriter(sel));
			w.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
			w.println();
			w.println("<perspective>");
			Enumeration<String> keys = dockables.keys();
			while (keys.hasMoreElements()) {
				String key = keys.nextElement();
				w.println(" <" + key + ">");
				String [] windows = dockables.get(key);
				for (int i = 0; i < windows.length; i++) {
					String window = windows[i];
					boolean visible = dockMan.isDockableWindowVisible(window);
					w.println("  <window name=\"" + window +
							"\" visible=\"" + visible + "\" />");
				}
				w.println(" </" + key + ">");
			}
			w.println("</perspective>");
			w.close();
			addAction(getPerspectiveName(sel));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private static String getConfigFile(String name) {
		return getConfigDirectory() + File.separator + name + ".xml";
	}
	private static String getConfigDirectory() {
		String dir = jEdit.getSettingsDirectory() + File.separator + "perspective";
		File f = new File(dir);
		if (! f.exists())
			f.mkdir();
		return dir;
	}

	static private void remove(String [] dockables) {
		for (int i = 0; i < dockables.length; i++)
			jEdit.setProperty(dockables[i] + ".dock-position",
					DockableWindowManager.FLOATING);
	}
	static private String[] getPerspectives() {
		File dir = new File(getConfigDirectory());
		if (! dir.canRead())
			return null;
		File[] files = dir.listFiles(filter);
		String[] perspectives = new String[files.length];
		for (int i = 0; i < files.length; i++) {
			perspectives[i] = getPerspectiveName(files[i]);
		}
		return perspectives;
	}
	static private String getPerspectiveName(File file) {
		String name = file.getName();
		if (filter.accept(file))
			return name.substring(0, name.length() - 4);
		return name;
	}
	static public void load(View view) {
		JFileChooser fc = new JFileChooser(new File(getConfigDirectory()));
		fc.addChoosableFileFilter(filter);
		fc.showOpenDialog(view);
	    File sel = fc.getSelectedFile();
	    if (sel == null)
			return;
		loadPerspective(view, sel.getAbsolutePath());
	}
	public static void loadPerspective(View view, String file) {
		PerspectiveHandler handler = new PerspectiveHandler();
		try
		{
			XMLUtilities.parseXML(new FileInputStream(file), handler);
		}
		catch(IOException e)
		{
			Log.log(Log.ERROR,PerspectiveManager.class,e);
		}
		DockableWindowManager dockMan = view.getDockableWindowManager();
		// Remove all existing dockables
		remove(dockMan.getLeftDockingArea().getDockables());
		remove(dockMan.getRightDockingArea().getDockables());
		remove(dockMan.getTopDockingArea().getDockables());
		remove(dockMan.getBottomDockingArea().getDockables());
		// Now put all the dockables from the loaded perspective
		Hashtable<String, Vector<String> > dockables = handler.getDockables(); 
		Enumeration<String> keys = dockables.keys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			Vector<String> windows = dockables.get(key);
			for (int i = 0; i < windows.size(); i++)
			{
				String name = windows.get(i);
				jEdit.setProperty(name + ".dock-position", key);
				dockMan.hideDockableWindow(name);
			}
		}
		jEdit.propertiesChanged();
		// Restore the "visible" state
		HashSet<String> visible = handler.getVisible();
		Iterator<String> it = visible.iterator();
		while (it.hasNext()) {
			String name = it.next();
			dockMan.showDockableWindow(name);
		}
	}
	
	static class PerspectiveHandler extends DefaultHandler {

		private String area = null;
		private Hashtable<String, Vector<String> > dockables =
			new Hashtable<String, Vector<String> >();
		private static final HashSet<String> areas = new HashSet<String>();
		private HashSet<String> visible = new HashSet<String>();
		
		PerspectiveHandler() {
			areas.add(DockableWindowManager.LEFT);
			areas.add(DockableWindowManager.RIGHT);
			areas.add(DockableWindowManager.TOP);
			areas.add(DockableWindowManager.BOTTOM);
		}
		@Override
		public void startElement(String nsURI, String localName,
				String qualifiedName, Attributes attr) throws SAXException
		{
			if (areas.contains(localName)) {
				area = localName;
				return;
			}
			if (localName.equalsIgnoreCase("window")) {
				String name = attr.getValue("name");
				if (name == null)
					return;
				if (! dockables.containsKey(area))
					dockables.put(area, new Vector<String>());
				dockables.get(area).add(name);
				String isVisible = attr.getValue("visible");
				if (isVisible == null)
					return;
				if (isVisible.equals("true"))
					visible.add(name);
			}
		}
		public Hashtable<String, Vector<String> > getDockables() {
			return dockables;
		}
		public HashSet<String> getVisible() {
			return visible;
		}
	}
	public static void removeActions() {
		jEdit.removeActionSet(actions);
	}
	private static void addAction(String perspective) {
		if (actions != null && (! actions.contains(perspective)))
			actions.addAction(new ChangePerspectiveAction(perspective));
	}
	public static void createActions() {
		actions = new ActionSet("Plugin: Docker - Perspectives");
		String[] perspectives = getPerspectives();
		for (int i = 0; i < perspectives.length; i++)
			addAction(perspectives[i]);
		jEdit.addActionSet(actions);
		actions.initKeyBindings();
	}

	static public class ChangePerspectiveAction extends EditAction {
		private String perspective;
		
		public ChangePerspectiveAction(String perspective) {
			super(perspective);
			jEdit.setTemporaryProperty(perspective + ".label", perspective);
			this.perspective = perspective;
		}

		@Override
		public void invoke(View view) {
			loadPerspective(view, getConfigFile(perspective));
		}

	}
	static private class PerspectiveFileFilter extends FileFilter implements FilenameFilter {
		static private final String XML_SUFFIX = ".xml";
		@Override
		public boolean accept(File f) {
			return accept(f.getParentFile(), f.getName()); 
		}
		@Override
		public String getDescription() {
			return "Perspective (" + XML_SUFFIX + ")";
		}
		public boolean accept(File dir, String name) {
			return name.endsWith(XML_SUFFIX);
		}
	}
}
