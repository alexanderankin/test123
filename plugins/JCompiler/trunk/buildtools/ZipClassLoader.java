/*
 * ZipClassLoader.java - a class loader that loads classes from a zip file
 * (c) 2001 Dirk Moebius
 * With inspirations from Slava Pestov
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


import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import org.gjt.sp.jedit.MiscUtilities;


/**
 * A class loader for classes in ZIP/JAR files.
 *
 * <b>Attention!</b><p>
 * This class only compiles on JDK 1.2 or higher. On JDK 1.1 you get an
 * compile error, that this class should be declared abstract. This is no
 * longer true on JDK 1.2.
 */
public class ZipClassLoader extends ClassLoader {

	private ZipClassLoader(String path) throws IOException {
		zipFile = new ZipFile(path);
	}


	private ZipClassLoader(File file) throws IOException {
		zipFile = new ZipFile(file);
	}


	/**
	 * Creates a new <tt>ZipClassLoader</tt> instance and returns it.
	 *
	 * @param  path  the path name denoting the ZIP/JAR file.
	 * @return the new <tt>ZipClassLoader</tt> instance.
	 */
	public static ClassLoader getInstance(String path) throws IOException {
		return new ZipClassLoader(path);
	}


	/**
	 * Creates a new <tt>ZipClassLoader</tt> instance and returns it.
	 *
	 * @param  path  the ZIP/JAR file.
	 * @return the new <tt>ZipClassLoader</tt> instance.
	 */
	public static ClassLoader getInstance(File file) throws IOException {
		return new ZipClassLoader(file);
	}


	public Class findClass(String clazz) throws ClassNotFoundException {
		Class cls = findLoadedClass(clazz);

		if (cls != null)
			return cls;

		if (zipFile == null)
			return findOtherClass(clazz);

		String name = MiscUtilities.classToFile(clazz);

		try {
			ZipEntry entry = zipFile.getEntry(name);

			if(entry == null)
				return findOtherClass(clazz);

			InputStream in = zipFile.getInputStream(entry);

			int len = (int) entry.getSize();
			byte[] data = new byte[len];
			int success = 0;
			int offset = 0;
			while (success < len) {
				len -= success;
				offset += success;
				success = in.read(data, offset, len);
				if (success == -1)
					throw new ClassNotFoundException(clazz + " in file " + zipFile.getName() + ": invalid zip entry");
			}

			cls = defineClass(clazz, data, 0, data.length);
			return cls;
		}
		catch(IOException io) {
			throw new ClassNotFoundException(clazz + " because of IOException: " + io);
		}
	}


	public InputStream getResourceAsStream(String name) {
		if (zipFile == null)
			return null;

		try {
			ZipEntry entry = zipFile.getEntry(name);
			if (entry == null)
				return getSystemResourceAsStream(name);
			else
				return zipFile.getInputStream(entry);
		}
		catch (IOException io) {
			return null;
		}
	}


	/**
	 * Closes the ZIP/JAR file.
	 */
	public void finalize() {
		if (zipFile == null)
			return;

		try { zipFile.close(); }
		catch(IOException io) {}

		zipFile = null;
	}


	private Class findOtherClass(String clazz) throws ClassNotFoundException {
		// Defer to whoever loaded us:
		ClassLoader loader = getClass().getClassLoader();
		if (loader != null)
			return loader.loadClass(clazz);

		// Doesn't exist in any other class loader, look in system classes:
		return findSystemClass(clazz);
	}


	private ZipFile zipFile;

}

