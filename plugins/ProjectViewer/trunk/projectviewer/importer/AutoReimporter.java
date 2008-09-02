/*
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package projectviewer.importer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.Timer;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSFile;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.PropertiesBean;

import projectviewer.VFSHelper;
import projectviewer.vpt.VPTDirectory;
import projectviewer.vpt.VPTFile;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;


/**
 *	A timer task for triggering auto-reimport of projects.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 *	@since		PV 3.0.0
 */
public class AutoReimporter implements ActionListener
{

	/* Config keys. */
	private static final String AR_ROOT			= "projectviewer.auto_reimport";


	/**
	 * Static constructor. Loads the configuration options from the
	 * given object, and if auto-reimport is enabled, create a new
	 * timer task and register it with the given timer.
	 *
	 * @param	p		The affected project.
	 *
	 * @return The timer object that manages the task.
	 */
	public static Timer create(VPTProject p)
	{
		assert (p != null) : "No project provided.";

		Options o = new Options();
		o.load(p.getProperties());

		if (o.getPeriod() > 0) {
			AutoReimporter ar = new AutoReimporter(p, o);
			Timer t = new Timer(o.getPeriod() * 1000 * 60, ar);
			t.start();
			return t;
		}

		return null;
	}


	/** Performs the re-import of the project's files. */
	public void actionPerformed(ActionEvent ae)
	{
		Importer imp;
		if (options.getCurrentOnly()) {
			imp = new AutoReimporterImpl(project);
		} else {
			imp = new ReImporter(project, null);
			((FileImporter)imp).fnf = options._getFilter();
		}
		imp.doImport();
	}


	/** Private constructor. */
	private AutoReimporter(VPTProject p,
						   Options o)
	{
		this.project = p;
		this.options = o;
	}


	/* Fields. */
	private final VPTProject project;
	private final Options options;


	/**	Class that encapsulates the auto-reimport options. */
	public static class Options extends PropertiesBean
	{

		public Options()
		{
			super(AR_ROOT);
		}


		/** Set the periodicity of the auto-reimport, in minutes. */
		public void setPeriod(int p)
		{
			this.period = p;
		}


		/** Returns the periodicity of reimports, in minutes. */
		public int getPeriod()
		{
			return period;
		}


		/**
		 * Sets whether the auto-reimport will only check existing
		 * directories in the project. Any new directories found
		 * during the scan will be ignored.
		 */
		public void setCurrentOnly(boolean b)
		{
			this.currentDirsOnly = b;
		}


		/** Returns whether to only look at current directories. */
		public boolean getCurrentOnly()
		{
			return currentDirsOnly;
		}


		/**
		 * Sets the filter to use during reimport. The underscore is
		 * to avoid the super class to treat it as a java bean property.
		 */
		public void _setFilter(ImporterFileFilter filter)
		{
			this.filter = filter;
		}


		/**
		 * Returns the file filter to use during reimport. The
		 * underscore is to avoid the super class to treat it as
		 * a java bean property.
		 */
		public ImporterFileFilter _getFilter()
		{
			return filter;
		}


		/**
		 * Saves the information, including the filter info.
		 *
		 * @param	p		Properties object.
		 */
		public void save(Properties p)
		{
			ImportUtils.saveFilter(p, filter, AR_ROOT);
			super.save(p);
		}


		/**
		 * Loads the information, including the filter info.
		 *
		 * @param	p		Properties object.
		 */
		public void load(Properties p)
		{
			load(p, ImportUtils.getFilters());
		}


		/**
		 * Loads the information, using the given list of filters as
		 * a base for the filter search.
		 *
		 * @param	p		Properties object.
		 * @param	filters	List of ImporterFileFilter.
		 */
		public void load(Properties p,
						 List<ImporterFileFilter> filters)
		{
			assert (filters != null) : "no filters provided";

			if (filters == null) {
				filters = ImportUtils.getFilters();
			}
			filter = ImportUtils.loadFilter(p, filters, AR_ROOT);
			super.load(p);
		}


		/**
		 * Cleans the config options related to this object.
		 *
		 * @param	p		Properties object.
		 */
		public void clean(Properties p)
		{
			super.clean(p);
			ImportUtils.cleanConfig(p, AR_ROOT);
		}


		/* Fields. */
		private int period;
		private boolean currentDirsOnly;
		private ImporterFileFilter filter;

	}

	/**
	 * Implements the automatic reimport logic when the user chooses
	 * not to import new directories. Traverse the project and, for
	 * each directory found, remove all files, and do a non-recursive
	 * listing to get the new files.
	 */
	private class AutoReimporterImpl extends Importer
	{

		private List<VPTDirectory> dirs;
		private Map<VFS,Object> sessions;

		private AutoReimporterImpl(VPTProject project)
		{
			super(project, null);
			dirs = new ArrayList<VPTDirectory>();
			sessions = new HashMap<VFS,Object>();
		}

		protected void internalDoImport()
		{
			try {
				/* Step 1: re-import files from the project root. */
				removeFiles(project);
				importFiles(project);

				/*
				 * Step 2: recurse into directories.
				 * Also marks the directory for removal, in case it ends up
				 * being empty after importing is done.
				 */
				while (!dirs.isEmpty()) {
					VPTDirectory dir = dirs.remove(0);
					removeFiles(dir);
					removeDirectory(dir);
					importFiles(dir);
				}

				/* Step 3: close all opened VFS sessions. */
				for (VFS vfs : sessions.keySet()) {
					Object session = sessions.get(vfs);
					VFSHelper.endVFSSession(vfs,
											session,
											jEdit.getActiveView());
				}
			} catch (IOException ioe) {
				Log.log(Log.ERROR, this, "I/O error re-importing project", ioe);
			}
		}


		/**
		 * Removes all files under a given node, without traversing
		 * child directories. Any subdirectories seen while looking
		 * at the node's children are added to the internal "dirs"
		 * list to be analysed later.
		 */
		private void removeFiles(VPTNode node)
		{
			for (int i = 0; i < node.getChildCount(); i++) {
				VPTNode n = (VPTNode) node.getChildAt(i);
				if (n.isFile()) {
					removeFile((VPTFile)n);
				} else if (n.isDirectory()) {
					dirs.add((VPTDirectory)n);
				}
			}
		}


		/**
		 * Imports the files directly under the given node. Does not
		 * recurse into subdirectories.
		 */
		private void importFiles(VPTNode dest)
			throws IOException
		{
			VFS vfs = VFSManager.getVFSForPath(dest.getNodePath());
			Object session = getSession(vfs, dest.getNodePath());
			String[] children;

			children = vfs._listDirectory(session,
										  dest.getNodePath(),
										  options._getFilter(),
										  false,
										  jEdit.getActiveView(),
										  false,
										  true);

			if (children == null || children.length == 0) {
				return;
			}

			for (String url: children) {
				VFSFile file = VFSHelper.getFile(url);
				if (file != null &&
					file.getType() == VFSFile.FILE) {
					findChild(url, dest, true);
				}
			}
		}


		/**
		 * Retrieves a VFS session from the cache, creating a new one
		 * if a miss occurs.
		 */
		private Object getSession(VFS vfs,
								  String path)
		{
			Object session = sessions.get(vfs);
			if (session == null) {
				session = VFSHelper.createVFSSession(vfs,
													 path,
													 jEdit.getActiveView());
				sessions.put(vfs, session);
			}
			return session;
		}

	}

}

