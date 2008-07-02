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

import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import org.gjt.sp.util.PropertiesBean;

import projectviewer.vpt.VPTProject;


/**
 *	A timer task for triggering auto-reimport of projects.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 *	@since		PV 3.0.0
 */
public class AutoReimporter extends TimerTask
{

	/* Config keys. */
	private static final String AR_ROOT			= "projectviewer.auto_reimport";
	public static final String AR_CFG_PERIOD	= AR_ROOT + ".period";
	public static final String AR_CFG_CURR_ONLY	= AR_ROOT + ".current_dirs";


	/**
	 * Static constructor. Loads the configuration options from the
	 * given object, and if auto-reimport is enabled, create a new
	 * timer task and register it with the given timer.
	 *
	 * @param	p		The affected project.
	 * @param	timer	Where to add the task.
	 *
	 * @return The auto-reimport task (null if not created).
	 */
	public static AutoReimporter create(VPTProject p,
										Timer timer)
	{
		assert (p != null) : "No project provided.";
		assert (timer != null) : "No timer provided.";

		Options o = new Options();
		o.load(p.getProperties());

		if (o.getPeriod() > 0) {
			AutoReimporter ar = new AutoReimporter(p, o);
			timer.schedule(ar,
						   o.getPeriod() * 1000 * 60,
						   o.getPeriod() * 1000 * 60);
			return ar;
		}

		return null;
	}


	/** Performs the re-import of the project's files. */
	public void run()
	{
		System.err.println("AutoReimporter.run(): not implemented!");
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
		public void setPeriod(long p)
		{
			this.period = p;
		}


		/** Returns the periodicity of reimports, in minutes. */
		public long getPeriod()
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
		private long period;
		private boolean currentDirsOnly;
		private ImporterFileFilter filter;

	}

}

