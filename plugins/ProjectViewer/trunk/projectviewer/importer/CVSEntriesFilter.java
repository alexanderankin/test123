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
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.util.HashMap;
import java.util.HashSet;

import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.jEdit;
//}}}

/**
 *	Filter that uses the CVS/Entries file to decide if a file should be accepted
 *	or not. The filter behaves a little differently depending on where it's
 *	being used: if inside a JFileChooser, it accepts directories regardless of
 *	them being on the CVS/Entries file or not, so the user can navigate freely.
 *
 *	<p>For the java.io.FilenameFilter implementation, the CVS/Entries listing is
 *	strictly enforced, even for directories. This way, no directories that are
 *	not listed there are going to be imported into the project.</p>
 *
 *	<p>"Entries" files read are kept in an internal cache so that subsequent
 *	visits to the same directory are faster.</p>
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public class CVSEntriesFilter extends ImporterFileFilter {

	//{{{ Private members
	private HashMap entries = new HashMap();
	//}}}

	//{{{ +getDescription() : String
	public String getDescription() {
		return jEdit.getProperty("projectviewer.import.filter.cvs.desc");
	} //}}}

	//{{{ +accept(File) : boolean
	/**
	 *	accept() method for the Swing JFileChooser. Accepts files only if they
	 *	are in the CVS/Entries file for the directory. All directories not named
	 *	"CVS" are accepted, so the user can navigate freely.
	 */
	public boolean accept(File file) {
		return (file.isDirectory() && !file.getName().equals("CVS"))
				|| accept(file.getParentFile(), file.getName());
	} //}}}

	//{{{ +accept(File, String) : boolean
	/**
	 *	accept() method for the FilenameFilter implementation. Accepts only
	 *	files and directories that are listed in the CVS/Entries file.
	 */
	public boolean accept(File file, String fileName) {
		return getEntries(file.getAbsolutePath()).contains(fileName) ||
				new File(file.getAbsolutePath(), fileName).isDirectory();
	} //}}}

	//{{{ -getEntries(String) : HashSet
	/**
	 *	Returns the set of files ffrom the CVS/Entries file for the given path.
	 *	In case the file has not yet been read, parse it.
	 */
	private HashSet getEntries(String dirPath) {
		HashSet h = (HashSet) entries.get(dirPath);
		if (h == null) {
			// parse file
			BufferedReader br = null;
			try {
				h = new HashSet();

				String fPath = dirPath + File.separator + "CVS" +
					File.separator + "Entries";
				br = new BufferedReader(new FileReader(fPath));
				String line;

				while ( (line = br.readLine()) != null ) {
					int idx1, idx2;
					idx1 = line.indexOf('/');
					if (idx1 != -1) {
						idx2 = line.indexOf('/', idx1 + 1);
						h.add(line.substring(idx1 + 1, idx2));
					}
				}

			} catch (FileNotFoundException fnfe) {
				// no CVS/Entries
			} catch (IOException ioe) {
				//shouldn't happen
				Log.log(Log.ERROR,this,ioe);
			} finally {
				if (br != null) try { br.close(); } catch (Exception e) { }
				entries.put(dirPath, h);
			}
		}
		return h;
	} //}}}

	//{{{ +getRecurseDescription() : String
	public String getRecurseDescription() {
		return	jEdit.getProperty("projectviewer.import.filter.cvs.rdesc");
	} //}}}

}

