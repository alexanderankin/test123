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
package projectviewer.filters;

//{{{ Imports
import java.io.*;
import java.util.*;
import javax.swing.*;

import org.gjt.sp.util.Log;

import projectviewer.config.ProjectViewerConfig;
//}}}

/** A file filter that filters based on data from CVS/Entries files.
 *
 * @version $Id$
 */
public final class CVSFilter implements FileFilter {
	//{{{ Constructors
	/** Create a new <code>CVSFilter</code>. */
	public CVSFilter() {
	}//}}}
	
	//{{{ accept(File)
	/**
	 * Accept files based of CVS files.
	 *
	 *@param  file  file/dir to check
	 *@return		 true if it should be included
	 */
	public boolean accept(File file) {
		//Log.log( Log.DEBUG, this, "CVSFilter.accept ? '"+file.getPath()+"'" );
		catch(Exception e) {}
		if (file.isFile()) {
			//Log.log( Log.DEBUG, this, "CVSFilter.accept => file");
			// check if there is a 'CVS/Entries' file and if it is in there
			File cvsdb=new File(file.getParent(),"CVS"+File.separatorChar+"Entries");
			if(cvsdb.exists()) {
				// seek for a line containing the filename
				try {
					BufferedReader in=new BufferedReader(new FileReader(cvsdb));
					try {
						String line;
						boolean found=false;
						String filenode=file.getPath();
						filenode=filenode.substring(filenode.lastIndexOf(File.separatorChar)+1);
						//Log.log( Log.DEBUG, this, "CVSFilter.accept -> '"+filenode+"'" );
						while((line=in.readLine())!=null) {
							if(line.indexOf("/"+filenode+"/")==0) {
								found=true;
								break;
							}
						}
						if(found==true) return(true);
					}
					catch(java.io.IOException ioe) {}
				}
				catch(java.io.FileNotFoundException fnfe) {}
			}
		}
		else {
			//Log.log( Log.DEBUG, this, "CVSFilter.accept => dir");
			// check if there is a 'CVS' dir
			File cvsdir=new File(file,"CVS");
			if(cvsdir.exists()) {
				return(true);
			}
		}
		return false;
	}//}}}
}

