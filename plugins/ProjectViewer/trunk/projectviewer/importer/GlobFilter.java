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

//{{{ Imports
import java.io.File;

import java.util.StringTokenizer;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.MiscUtilities;

import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSManager;

import org.gjt.sp.util.Log;

import gnu.regexp.RE;
import gnu.regexp.REException;

import projectviewer.config.ProjectViewerConfig;
//}}}

/**
 *	Filter that uses the settings provided by the user (in jEdit's options)
 *	to select the files.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 *	@since		PV 2.1.1
 */
public class GlobFilter extends ImporterFileFilter {

	//{{{ +_getImportSettingsFilter()_ : GlobFilter
	/**
	 *	Returns a glob filter with the settings taken from the global
	 *	ProjectViewer "import settings".
	 */
	public static GlobFilter getImportSettingsFilter() {
		ProjectViewerConfig config = ProjectViewerConfig.getInstance();
		return new GlobFilter(
			jEdit.getProperty("projectviewer.import.filter.settings.desc"),
			jEdit.getProperty("projectviewer.import.filter.settings.rdesc"),
			config.getImportGlobs(),
			config.getExcludeDirs());
	} //}}}

	//{{{ Private members
	private String description;
	private String recurseDesc;
	private String fileGlobs;
	private String dirGlobs;

	private RE file_positive;
	private RE file_negative;
	private RE dir_negative;
	//}}}

	//{{{ +GlobFilter(String, String) : <init>
	/**
	 *	Creates a new GlobFilter based on the given parameters.
	 *
	 *	@param	fileGlobs	List of globs of files to accept (or reject if
	 *						the glob starts with !). space-separated.
	 *	@param	dirGlobs	List of globs of directory names to ignore.
	 */
	public GlobFilter(String fileGlobs, String dirGlobs) {
		this(null, null, fileGlobs, dirGlobs);
	} //}}}


	//{{{ +GlobFilter(String, String, String, String) : <init>
	/**
	 *	Creates a new GlobFilter based on the given parameters with some
	 *	description texts. Mainly used internally by PV for the
	 *	{@link #getImportSettingsFilter() import settings filter}.
	 */
	public GlobFilter(String description, String recurseDescription,
					  String fileGlobs, String dirGlobs)
	{
		this.description	= description;
		this.recurseDesc	= recurseDescription;
		this.fileGlobs		= fileGlobs;
		this.dirGlobs		= dirGlobs;
	} //}}}

	//{{{ +getDescription() : String
	public String getDescription() {
		return description;
	} //}}}

	//{{{ +accept(File) : boolean
	public boolean accept(File file) {
		return accept(file.getParentFile(), file.getName());
	} //}}}

	//{{{ +accept(File, String) : boolean
	public boolean accept(File dir, String fileName) {
		if (file_positive == null) {
			StringTokenizer globs = new StringTokenizer(fileGlobs);
			StringBuffer fPos = new StringBuffer();
			StringBuffer fNeg = new StringBuffer();
			while (globs.hasMoreTokens()) {
				String token = globs.nextToken();
				if (token.startsWith("!")) {
					fNeg.append(MiscUtilities.globToRE(token.substring(1)));
					fNeg.append("|");
				} else {
					fPos.append(MiscUtilities.globToRE(token));
					fPos.append("|");
				}
			}
			if (fNeg.length() > 0)
				fNeg.setLength(fNeg.length() - 1);
			if (fPos.length() > 0)
				fPos.setLength(fPos.length() - 1);


			globs = new StringTokenizer(dirGlobs);
			StringBuffer dirs = new StringBuffer();
			while (globs.hasMoreTokens()) {
				dirs.append(MiscUtilities.globToRE(globs.nextToken()));
				dirs.append("|");
			}
			if (dirs.length() > 0)
				dirs.setLength(dirs.length() - 1);

			try {
				if ((VFSManager.getFileVFS().getCapabilities()
						& VFS.CASE_INSENSITIVE_CAP) != 0) {
					file_positive = new RE(fPos.toString(), RE.REG_ICASE);
					file_negative = new RE(fNeg.toString(), RE.REG_ICASE);
					dir_negative = new RE(dirs.toString(), RE.REG_ICASE);
				} else {
					file_positive = new RE(fPos.toString());
					file_negative = new RE(fNeg.toString());
					dir_negative = new RE(dirs.toString());
				}
			} catch (REException re) {
				Log.log(Log.ERROR, this, re);
			}
		}

		File child = new File(dir, fileName);
		if (child.isFile()) {
			return file_positive.isMatch(fileName) && !file_negative.isMatch(fileName);
		} else {
			return !dir_negative.isMatch(fileName);
		}
	} //}}}

	//{{{ +getRecurseDescription() : String
	public String getRecurseDescription() {
		return	recurseDesc;
	} //}}}

}

