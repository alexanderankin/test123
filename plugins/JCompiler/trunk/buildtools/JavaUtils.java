/*
 * JavaUtils.java - miscelleanous utilities for java files
 * (c) 1999, 2000 Kevin A. Burton
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

import java.io.*;
import java.util.*;
import org.gjt.sp.util.Log;


/**
 *  Miscelleanous Java utilities.
 *
 *  @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
 */
public class JavaUtils
{

	/**
	 *  The constructor is private. Only static methods are used in this class.
	 */
	private JavaUtils() { }


	/**
	 *  Given a filename, finds the root folder, and then determines what
	 *  java packages exists throughout the hierarchy.
	 *
	 *  A package is identified by determining files with a .java file in
	 *  them and if they have one, makes sure its "package" reference is
	 *  correct.
	 *
	 *  @exception  IOException  if the file is not readable or does not exist
	 */
	public static String[] getPackageNames(String javafilename)
			throws IOException
	{
		Vector packages = new Vector();
		File basedir = new File(getBaseDirectory(javafilename));
		return getPackageNames(basedir);
	}


	/**
	 *  Given a directory, finds the root folder, and then determines what
	 *  java packages exists throughout the hierarchy.
	 *
	 *  A package is identified by determining files with a .java file in
	 *  them and if they have one, makes sure its "package" reference is
	 *  correct.
	 *
	 *  @exception  IOException  if the file is not readable or does not exist
	 */
	public static String[] getPackageNames(File directory) throws IOException {
		Vector packages = new Vector();
		boolean processed = false;
		String[] unknownFiles = directory.list();

		if (unknownFiles == null) {
			return new String[0];
		}

		for (int i = 0; i < unknownFiles.length; ++i) {
			String currentFileName = directory + System.getProperty("file.separator") + unknownFiles[i];
			java.io.File currentFile = new java.io.File(currentFileName);
			if (currentFile.isDirectory()) {
				// ignore all CVS directories...
				if (currentFile.getName().equals("CVS")) {
					continue;
				}
				// ok... transverse into this directory and get all the files...
				// then combine them with the current list.
				String[] morepackages = getPackageNames(currentFile);
				for (int j = 0; j < morepackages.length; ++j) {
					packages.addElement(morepackages[j]);
				}
			} else if (!processed) {
				// ok... add the file
				String add = currentFile.getAbsolutePath();
				if (add.indexOf(".java") > 0) {
					// TODO: debug messages
					System.out.println(add);
					String packagename = getPackageName(add);
					if (packagename != null) {
						packages.addElement(packagename);
						processed = true;
					}
				}
			}
		}

		// ok... move the Vector into the files list...
		String[] found = new String[packages.size()];
		packages.copyInto(found);
		return found;
	}


	/**
	 *  Given a filename return its package name. This is used when
	 *  compiling a package. Basically it looks for the first instance of
	 *  "package" in the file and returns this. The provided file must be a
	 *  java src file. This is not checked!
	 *
	 *  @return	 null, if no package or the package name
	 *  @exception  IOException  if the file is not readable or does not exist
	 */
	public static String getPackageName(String fileName) throws IOException {
		FileReader fileRdr = new FileReader(fileName);
		try {
			StreamTokenizer stok = new StreamTokenizer(fileRdr);
			// set tokenizer to skip comments
			stok.commentChar('*');
			stok.slashStarComments(true);
			stok.slashSlashComments(true);

			while (stok.nextToken() != StreamTokenizer.TT_EOF) {
				if (stok.sval == null) {
					continue;
				}
				if (stok.sval.equals("package")) {
					stok.nextToken();
					fileRdr.close();
					return stok.sval;
				} else if (stok.sval.equals("class")) {
					fileRdr.close();
					return null;
				}
			}
			fileRdr.close();
			return null;
		}
		finally {
			fileRdr.close();
		}
	}


	/**
	 * Given a java file name, finds its package name and then returns its
	 * base directory. If the java file does not contain a "package" keyword,
	 * or if an error occurs, the parent directory of the java file is returned.
	 *
	 * @exception  IOException  if the file is not readable or does not exist
	 */
	public static String getBaseDirectory(String filename) throws IOException {
		String dirname = new File(filename).getParent();
		String packagename = getPackageName(filename);

		Log.log(Log.DEBUG, "JavaUtils", "dirname=" + dirname + " packagename=" + packagename);

		if (packagename == null) {
			return dirname;
		}

		String javadir = packagename.replace('.', File.separatorChar);
		int javadirpos = dirname.lastIndexOf(javadir);

		if (javadirpos < 0) {
			// the package name is not contained in the directory path of the
			// file denoted by filename.
			return dirname;
		} else {
			return dirname.substring(0, javadirpos - 1);
		}
	}


	/**
	 *  Given a java file name, finds its package name and then returns its
	 *  fully classified class name.
	 *
	 *  @exception  IOException  if the file is not readable or does not exist
	 */
	public static String getFullClassname(String filename) throws IOException {
		String packagename = getPackageName(filename);
		if (packagename == null) {
			packagename = "";
		}
		String classfilename = new File(filename).getName();
		int lastDot = classfilename.lastIndexOf(".");
		String classname = classfilename.substring(0, lastDot);
		return packagename + "." + classname;
	}

}

