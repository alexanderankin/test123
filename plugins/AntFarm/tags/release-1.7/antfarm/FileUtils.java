/*
 *  FileUtils.java - Plugin for running Ant builds from jEdit.
 *  Copyright (C) 2001 Brian Knowles
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
package antfarm;

import java.io.File;
import java.io.IOException;
/**
 * @author     Richard Wan
 * @created    October 22, 2001
 */
public class FileUtils
{

	/**
	 *  tries to call <code>getCanonicalPath</code> and rollsover to <code>getAbsolutePath</code>
	 *  if that fails.
	 *
	 * @param  file  Description of Parameter
	 * @return       The AbsolutePath value
	 */
	public static String getAbsolutePath( File file )
	{
		try {
			return file.getCanonicalPath();
		}
		catch ( IOException e ) {
			return file.getAbsolutePath();
		}
	}


	/**
	 *  mimics the jdk1.2 <code>getParentFile()</code>
	 *
	 * @param  file  Description of Parameter
	 * @return       The Parent value
	 */
	public static File getParent( File file )
	{
		String parent = file.getParent();
		if ( parent == null ) {
			return null;
		}
		return new File( parent );
	}


	/**
	 *  Searches for the specified String starting from the specified <code>File</code>
	 *  and continuing upwards.
	 *
	 * @param  directoryStart  Description of Parameter
	 * @param  relativeFile    Description of Parameter
	 * @return                 the specified <code>File</code> if no file can be
	 *      found.
	 */
	public static File findFile( File directoryStart, String relativeFile )
	{
		File current = new File( getAbsolutePath( directoryStart ) );
		File candidate;
		while ( current != null ) {
			candidate = new File( current, relativeFile );
			if ( candidate.exists() ) {
				return candidate;
			}
			current = getParent( current );
		}
		return directoryStart;
	}

}


