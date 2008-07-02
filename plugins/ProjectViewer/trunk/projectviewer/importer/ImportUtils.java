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

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import projectviewer.config.ExtensionManager;
import projectviewer.vpt.VPTProject;

/**
 *	A collection of functions useful when dealing with importing
 * 	files.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 *	@since		PV 3.0.0
 */
public final class ImportUtils
{

	private static final String FILTER_CFG_KEY			= ".filterid";
	private static final String FILTER_CFG_FILES_GLOB	= ".globfilter.files";
	private static final String FILTER_CFG_DIRS_GLOB	= ".globfilter.directories";


	/**
	 * Return the list of filters available for importing files.
	 *
	 * @return A list with all the filters known to PV.
	 */
	public static List<ImporterFileFilter> getFilters()
	{
		List<ImporterFileFilter> ffilters;
		ffilters = new ArrayList<ImporterFileFilter>();
		ffilters.add(GlobFilter.getImportSettingsFilter());
		ffilters.add(new CVSEntriesFilter());

		List<Object> exts = ExtensionManager.getInstance()
											.loadExtensions(ImporterFileFilter.class);

		if (exts != null && exts.size() > 0) {
			for (Object o : exts) {
				ffilters.add((ImporterFileFilter) o);
			}
		}

		return ffilters;
	}


	/**
	 * Saves the configuration about the selected filter to the
	 * project.
	 *
	 * @param	p			Properties object.
	 * @param	filter		The filter instance.
	 * @param	propRoot	Root of the configuration property names.
	 */
	public static void saveFilter(Properties p,
								  ImporterFileFilter filter,
								  String propRoot)
	{
		assert (p != null) : "No properties object!";
		assert (filter != null) : "Filter is null!";
		assert (propRoot != null) : "Prop root is null!";

		p.setProperty(propRoot + FILTER_CFG_KEY, filter.getId());
		if (filter instanceof GlobFilter && ((GlobFilter)filter).isCustom()) {
			GlobFilter gf = (GlobFilter) filter;
			/*
			 * If using a custom GlobFilter, save both the filter ID and
			 * the configuration of the filter.
			 */
			p.setProperty(propRoot + FILTER_CFG_FILES_GLOB,
								gf.getFileGlobs());
			p.setProperty(propRoot + FILTER_CFG_DIRS_GLOB,
								gf.getDirectoryGlobs());
		} else {
			p.remove(propRoot + FILTER_CFG_FILES_GLOB);
			p.remove(propRoot + FILTER_CFG_DIRS_GLOB);
		}
	}


	/**
	 * Loads the filter information from the project, and tries to
	 * identify a matching filter in the given list.
	 *
	 * @param	p			Properties object.
	 * @param	filters		List of filters to search.
	 * @param	propRoot	Root of the configuration property names.
	 *
	 * @return The filter that matches the project config, or null
	 *         if not found.
	 */
	public static ImporterFileFilter loadFilter(Properties p,
												List<ImporterFileFilter> filters,
												String propRoot)
	{
		ImporterFileFilter filter = null;

		assert (p != null) : "No properties object!";
		assert (filters != null) : "No filters provided!";
		assert (propRoot != null) : "Prop root is null!";

		/* Try the new configuration first. */
		String filterId = p.getProperty(propRoot + FILTER_CFG_KEY);
		if (filterId != null) {
			for (ImporterFileFilter f : filters) {
				if (filterId.equals(f.getId())) {
					filter = f;
					break;
				}
			}
		}

		/* Found filter. Still need to check if it's a custom glob filter. */
		if ((filter != null && filter instanceof GlobFilter) ||
			GlobFilter.class.getName().equals(filterId)) {
			String fileGlobs = p.getProperty(propRoot + FILTER_CFG_FILES_GLOB);
			String dirGlobs = p.getProperty(propRoot + FILTER_CFG_DIRS_GLOB);

			if (fileGlobs != null && dirGlobs != null) {
				filter = new GlobFilter(fileGlobs, dirGlobs);
			}
		}

		/* Try old style config if all else fails. */
		if (filter == null) {
			try {
				int idx = Integer.parseInt(p.getProperty(propRoot + ".filteridx"));
				if (idx < filters.size()) {
					filter = filters.get(idx);
				}
				p.remove(propRoot + ".filteridx");
			} catch (NumberFormatException nfe) {
				p.remove(propRoot + ".filteridx");
			}
		}

		return filter;
	}


	/**
	 * Cleans the configuration related to filters from the given object.
	 *
	 * @param	p			Properties object.
	 * @param	propRoot	Root of the configuration property names.
	 */
	public static void cleanConfig(Properties p,
								   String propRoot)
	{
		p.remove(propRoot + FILTER_CFG_KEY);
		p.remove(propRoot + FILTER_CFG_FILES_GLOB);
		p.remove(propRoot + FILTER_CFG_DIRS_GLOB);
	}


	/** Non-instantiable. */
	private ImportUtils()
	{
		/* never called. */
	}

}

