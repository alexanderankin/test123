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

import java.util.HashSet;
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
 */
public class ImportSettingsFilter extends ImporterFileFilter {

	//{{{ Private members
	private static RE file_positive;
	private static RE file_negative;
	private static RE dir_negative;
	//}}}

	//{{{ getDescription() method
	public String getDescription() {
		return jEdit.getProperty("projectviewer.importer-filter");
	} //}}}

	//{{{ accept(File) method
	public boolean accept(File file) {
		return accept(file.getParentFile(), file.getName());
	} //}}}

	//{{{ accept(File, String) method
	public boolean accept(File file, String fileName) {
		if (file_positive == null) {
			ProjectViewerConfig config = ProjectViewerConfig.getInstance();
			StringTokenizer globs = new StringTokenizer(config.getImportGlobs());
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


			globs = new StringTokenizer(config.getExcludeDirs());
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

		File child = new File(file, fileName);
		if (child.isFile()) {
			return file_positive.isMatch(fileName) && !file_negative.isMatch(fileName);
		} else {
			return !dir_negative.isMatch(fileName);
		}
	} //}}}

	//{{{ +getRecurseDescription() : String
	public String getRecurseDescription() {
		return	jEdit.getProperty("projectviewer.import.yes-settings");
	} //}}}

}

