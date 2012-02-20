/*
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
package classpath;

//{{{ Imports
import java.io.File;
import javax.swing.filechooser.FileFilter;
import org.gjt.sp.jedit.jEdit;
//}}}

/**
 * A file filter that accepts only possible classpath elements
 * Classpath elements can be directories, .classpath files (like Eclipse),
 * jar files, or zip files.
 *
 * @author <a href="mailto:damienradtke@gmail.com">Damien Radtke</a>
 * @version 1.2
 */
public class ClasspathFilter extends FileFilter {

	//{{{ accept(File) : boolean
	/**
	 * Returns true if the provided file is a valid classpath element,
	 * otherwise false.
	 *
	 * @param file The file to test.
	 * @return Whether or not the file is a valid classpath element.
	 */
	@Override
	public boolean accept(File file) {
		if (file.isDirectory())
			return true;

		String filename = file.getName();
		return ".classpath".equals(filename)
			   || filename.endsWith(".jar")
			   || filename.endsWith(".zip");
	} //}}}

	//{{{ getDescription() : String
	/**
	 * Gets the description for this filter, which is displayed
	 * in the file browser.
	 *
	 * @return This filter's description.
	 */
	public String getDescription() {
		return jEdit.getProperty("classpath.filterDescription");
	} //}}}
}
