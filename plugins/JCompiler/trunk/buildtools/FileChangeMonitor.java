/*
 * FileChangeMonitor.java - monitor status of files
 * (c) 1999, 2000 Kevin A. Burton
 * (c) 2001 Dirk Moebius
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

package buildtools;

import java.util.*;
import java.io.File;


/**
 * Provides a way to determine if files in a source directory are newer
 * than their corresponding counterparts in a destination directory.
 *
 * @author <A HREF="mailto:dmoebius@gmx.net">Dirk M&ouml;bius</A>
 */

public class FileChangeMonitor
{

	private String sourcedir = null;
	private String sourceext = null;
	private String destdir = null;
	private String destext = null;


	/**
	 * Looks at the given source directory and checks all files
	 * with a certain extension to be newer than files with the
	 * same name (but different extension) in the given destination
	 * directory.
	 *
	 * @param  sourcedir  the source directory
	 * @param  sourceext  the extension for files to be checked in the
	 *			source directory
	 * @param  destdir  the destination directory. If destdir is null,
	 *			then destdir = sourcedir.
	 * @param  destext  the extension for files to be checked in the
	 *			destination directory
	 */
	public FileChangeMonitor(String sourcedir, String sourceext, String destdir, String destext) {
		if (sourcedir == null) {
			throw new IllegalArgumentException("sourcedir may not be null");
		} else {
			this.sourcedir = sourcedir;
		}
		if (sourceext == null) {
			throw new IllegalArgumentException("sourceext may not be null");
		} else {
			this.sourceext = sourceext;
		}
		if (destdir == null) {
			this.destdir = sourcedir;
		} else {
			this.destdir = destdir;
		}
		if (destext == null) {
			throw new IllegalArgumentException("destext may not be null");
		} else {
			this.destext = destext;
		}
	}


	/**
	 * Returns the files that have been changed for this monitor.
	 */
	public String[] getChangedFiles() {
		String[] files = FileUtils.getFilesFromExtension(this.sourcedir, new String[] { this.sourceext });
		Vector v = new Vector();

		for (int i = 0; i < files.length; ++i) {
			String sourcefilename = files[i];
			String basefilename = sourcefilename.substring(
				this.sourcedir.length(),
				sourcefilename.length() - this.sourceext.length());
			String destfilename = this.destdir + basefilename + this.destext;

			File sourcefile = new File(sourcefilename);
			File destfile = new File(destfilename);

			if (!destfile.exists() || sourcefile.lastModified() > destfile.lastModified()) {
				v.addElement(sourcefilename);
			}
		}

		String[] changed = new String[v.size()];
		v.copyInto(changed);
		return changed;
	}


	/**
	 * Returns all files that are being monitored in the source directory
	 * (with the given source extension).
	 */
	public String[] getAllFiles() {
		return FileUtils.getFilesFromExtension(this.sourcedir, new String[] { this.sourceext });
	}

}

