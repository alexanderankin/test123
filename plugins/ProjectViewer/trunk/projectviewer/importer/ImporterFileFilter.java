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

import java.io.IOException;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSFile;

import org.gjt.sp.util.Log;

import projectviewer.VFSHelper;
import org.gjt.sp.jedit.io.VFSFileFilter;
import org.gjt.sp.jedit.io.VFSManager;

/**
 *	File filter implementation used when importing files into a project.
 *	It provides some PV-specific extensions to the jEdit filter interface.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public abstract class ImporterFileFilter implements VFSFileFilter {

	/**
	 *	This method will be called by the toString() method when showing this
	 *	filter as an option to the dialog shown when asking about whether the
	 *	user wants to recurse into the directories selected for importing.
	 *
	 *	<p>It should return a short, descriptive string of what the filter does,
	 *	gererally prefixed with "Yes,". For example, "Yes, import all files."
	 *	or "Yes, use the CVS/Entries file."</p>
	 */
	public abstract String getRecurseDescription();

	/** Calls getRecurseDescription(). */
	public String toString()
	{
		return getRecurseDescription();
	}

	/** Calls {@link VFSFileFilter#accept(VFSFile)}. */
	public boolean accept(String url)
	{
		VFS vfs = VFSManager.getVFSForPath(url);
		if (vfs != null) {
			View v = jEdit.getActiveView();
			Object session = VFSHelper.createVFSSession(vfs, url, v);
			try {
				VFSFile f = vfs._getFile(session, url, v);
				return accept(f);
			} catch (IOException ioe) {
				Log.log(Log.ERROR, this, "Error getting VFS file", ioe);
			} finally {
				VFSHelper.endVFSSession(vfs, session, v);
			}
		}
		return false;
	}

	/**
	 * This method should return a string that uniquely identifies the
	 * filter. The string is used when persisting information related
	 * to the filter, and later used to match the configuration to the
	 * filter.
	 *
	 * The default implementation returns the class name.
	 */
	public String getId()
	{
		return getClass().getName();
	}

	/**
	 * Called by project viewer after the filter is used for importing.
	 * Filters that cache data should override this method and clean up
	 * any caches so that future imports are unaffected by the state.
	 */
	protected void done() {

	}

}

