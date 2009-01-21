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
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.StandardUtilities;

import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSFile;
import org.gjt.sp.jedit.io.VFSManager;

import org.gjt.sp.util.Log;

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
public final class GlobFilter extends ImporterFileFilter {

	//{{{ +_getImportSettingsFilter()_ : GlobFilter
	/**
	 *	Returns a glob filter with the settings taken from the global
	 *	ProjectViewer "import settings".
	 */
	public static GlobFilter getImportSettingsFilter() {
		return new GlobFilter(
			jEdit.getProperty("projectviewer.import.filter.settings.desc"),
			jEdit.getProperty("projectviewer.import.filter.settings.rdesc"),
			null,
			null,
			false);
	} //}}}

	//{{{ Private members
	private boolean custom;

	private String description;
	private String recurseDesc;
	private String fileGlobs;
	private String dirGlobs;

	private Pattern file_positive;
	private Pattern file_negative;
	private Pattern dir_negative;
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
		this(null, null, fileGlobs, dirGlobs, true);
	} //}}}

	//{{{ +GlobFilter(String, String, String, String) : <init>
	/**
	 *	Creates a new GlobFilter based on the given parameters with some
	 *	description texts. Mainly used internally by PV for the
	 *	{@link #getImportSettingsFilter() import settings filter}.
	 */
	private GlobFilter(String description, String recurseDescription,
					   String fileGlobs, String dirGlobs, boolean custom)
	{
		this.description	= description;
		this.recurseDesc	= recurseDescription;
		this.fileGlobs		= fileGlobs;
		this.dirGlobs		= dirGlobs;
		this.custom			= custom;
	} //}}}

	//{{{ +getDescription() : String
	public String getDescription() {
		return description;
	} //}}}

	//{{{ +accept(VFSFile) : boolean
	public boolean accept(VFSFile file) {
		if (file_positive == null) {
			if (!custom) {
				ProjectViewerConfig config = ProjectViewerConfig.getInstance();
				fileGlobs = config.getImportGlobs();
				dirGlobs = config.getExcludeDirs();
			}

			StringTokenizer globs = new StringTokenizer(fileGlobs);
			StringBuffer fPos = new StringBuffer();
			StringBuffer fNeg = new StringBuffer();

			while (globs.hasMoreTokens()) {
				String token = globs.nextToken();
				if (token.startsWith("!")) {
					fNeg.append(StandardUtilities.globToRE(token.substring(1)));
					fNeg.append("|");
				} else {
					fPos.append(StandardUtilities.globToRE(token));
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
				dirs.append(StandardUtilities.globToRE(globs.nextToken()));
				dirs.append("|");
			}
			if (dirs.length() > 0)
				dirs.setLength(dirs.length() - 1);

			try {
				if ((VFSManager.getFileVFS().getCapabilities()
						& VFS.CASE_INSENSITIVE_CAP) != 0) {
					file_positive = Pattern.compile(fPos.toString(), Pattern.CASE_INSENSITIVE);
					file_negative = Pattern.compile(fNeg.toString(), Pattern.CASE_INSENSITIVE);
					dir_negative = Pattern.compile(dirs.toString(), Pattern.CASE_INSENSITIVE);
				} else {
					file_positive = Pattern.compile(fPos.toString());
					file_negative = Pattern.compile(fNeg.toString());
					dir_negative = Pattern.compile(dirs.toString());
				}
			} catch (PatternSyntaxException re) {
				Log.log(Log.ERROR, this, re);
			}
		}

		if (file.getType() == VFSFile.FILE) {
			return file_positive.matcher(file.getName()).matches()
				   && !file_negative.matcher(file.getName()).matches();
		} else if (file.getType() == VFSFile.DIRECTORY) {
			return !dir_negative.matcher(file.getName()).matches();
		}
		return false;
	} //}}}

	//{{{ +getRecurseDescription() : String
	public String getRecurseDescription() {
		return recurseDesc;
	} //}}}

	/**
	 * Tells whether this instance is a customized filter or a "built-in" one.
	 */
	public boolean isCustom()
	{
		return custom;
	}


	/** Returns the "include file" globs for this filter. */
	public String getFileGlobs()
	{
		return fileGlobs;
	}


	/** Returns the "ignore directories" globs for this filter. */
	public String getDirectoryGlobs()
	{
		return dirGlobs;
	}


	protected void done()
	{
		file_positive = null;
		file_negative = null;
		dir_negative = null;
		if (!custom) {
			dirGlobs = null;
			fileGlobs = null;
		}
	}

}

