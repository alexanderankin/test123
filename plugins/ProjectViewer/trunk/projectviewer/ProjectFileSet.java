/*
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package projectviewer;

import java.util.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.search.*;
import org.gjt.sp.util.Log;

public class ProjectFileSet implements SearchFileSet {
	ArrayList fileset;
	Iterator it;

	public ProjectFileSet(ProjectDirectory dir) {
		fileset=new ArrayList();
		// add all files from dir to fileset
		addFiles(dir);
	}
	
	private void addFiles(ProjectDirectory dir) {
		Iterator i=dir.files();
		while(i.hasNext()) {
			fileset.add(i.next());
		}
		i=dir.subdirectories();
		while(i.hasNext()) {
			addFiles((ProjectDirectory)i.next());
		}
	}

	public String getCode() {
		return(null);
	}

	public int getFileCount(View view) {
		return(fileset.size());
	}

	public String[] getFiles(View view) {
		return((String[])(fileset.toArray()));	
	}

	public String getFirstFile(View view) {
		it=fileset.iterator();
		return((String)(it.next()));
	}

	public String getNextFile(View view, String path) {
		return((String)(it.next()));
	}
}

