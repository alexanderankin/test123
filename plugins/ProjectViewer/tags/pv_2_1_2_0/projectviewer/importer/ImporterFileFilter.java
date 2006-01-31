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
import java.io.FilenameFilter;
import javax.swing.filechooser.FileFilter;
//}}}

/**
 *	File filter implementation used when importing files into a project. It
 *	implements both of Java's file filter classes
 *	(javax.swing.filechooser.FileFilter and interface java.io.FilenameFilter)
 *	and provides a method that returns a description string to use when
 *	asking the user about recursion into imported directories.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public abstract class ImporterFileFilter extends FileFilter
											implements FilenameFilter {

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
	public String toString() {
		return getRecurseDescription();
	}

}

