/*
 * Copyright (c) 2007 Marcelo Vanzin
 *
 * :tabSize=4:indentSize=4:noTabs=false:maxLineLen=0:
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
package common.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 *	<p>An "atomic" output stream. It is "atomic" in the sense that the
 *	target file won't be overwritten until "close()" is called, at which
 *	point a rename is done. A point of note that for this stream to
 *	actually be atomic, "rename" needs to be atomic, such as in POSIX
 *	systems. Windows, for example, doesn't have this property, so this
 *	stream tries to do the next best thing (delete the original, then
 *	rename).</p>
 *
 *	<p>This stream works a little bit differently than other streams, in the
 *	sense that it has a second close method called {@link #rollback()}. This
 *	method just discards the temporary file used to write the data and leaves
 *	the target file untouched. The only way to overwrite the target file is
 *	to call {@link #close()}. If the instance is left for garbage collection,
 *	<code>rollback()</code> will be called during finalization, instead of
 *	<code>close().</code></p>
 *
 *	@author		Marcelo Vanzin
 *	@since		CC 0.9.4
 */
public final class AtomicOutputStream extends OutputStream
{

	private File			target;
	private File			temp;
	private OutputStream 	out;

	/**
	 *	Creates a new atomic stream.
	 *
	 *	@see	#AtomicOutputStream(File)
	 */
	public AtomicOutputStream(String path)
		throws IOException
	{
		this(new File(path));
	}

	/**
	 *	Creates a new atomic stream. The user must have permission to write
	 *	to the directory of <code>path</code>, otherwise this will throw
	 *	an IOException.
	 *
	 *	@param	path			The path of the target file.
	 *	@throws	IOException		If the temp file cannot be created.
	 */
	public AtomicOutputStream(File path)
		throws IOException
	{
		if (path.exists() && !path.canWrite()) {
			throw new IOException("Can't write to " + path.getAbsolutePath());
		}
		this.temp	= File.createTempFile(path.getName(), ".tmp", path.getParentFile());
		this.out 	= new FileOutputStream(temp);
		this.target	= path;
	}

	/**
	 *	Closes the temporary stream and renames the temp file to the target file.
	 *	It is safe to call this method several times, or after a rollback()
	 *	(it won't do anything in those cases).
	 *
	 *	@throws	IOException	If there's a problem closing the temp stream, or
	 *						renaming the temp file to the target file.
	 */
	public void close() throws IOException
	{
		if (out == null) {
			return;
		}
		try {
			out.close();
			if (!temp.renameTo(target)) {
				/*
				 * Windows doesn't allow renaming to a file that already exists.
				 * So take a "slow, non-atomic" path in this case. This is
				 * pretty ugly and absolutely not atomic, but I'm trying to be
				 * really paranoid.
				 */
				if (target.exists()) {
					File backup = File.createTempFile(target.getName(),
													  ".backup",
													  target.getParentFile());
					backup.delete();
					if (!target.renameTo(backup) || !temp.renameTo(target)) {
						if (backup.exists() && !target.exists()) {
							backup.renameTo(target);
						}
						throw new IOException("Can't rename temp file to " + target.getName());
					}
					backup.delete();
				} else {
					throw new IOException("Can't rename temp file to " + target.getName());
				}
			}
		} finally {
			out = null;
			temp = null;
			target = null;
		}
	}

	public void flush() throws IOException
	{
		out.flush();
	}

	public void write(byte[] b) throws IOException
	{
		out.write(b);
	}

	public void write(byte[] b, int off, int len) throws IOException
	{
		out.write(b, off, len);
	}

	public void write(int b) throws IOException
	{
		out.write(b);
	}

	/**
	 *	Removes the temporary file without overwriting the target.
	 *	It is safe to call this method several times, or after a close()
	 *	(it won't do anything in those cases).
	 */
	public void rollback()
	{
		if (out == null) {
			return;
		}
		try {
			out.close();
			temp.delete();
		} catch (IOException ioe) {
			// ignore.
		} finally {
			out = null;
			temp = null;
			target = null;
		}
	}

	/** For the forgetful. Remember kids: streams want to be free(d). */
	protected void finalize()
	{
		rollback();
	}

}

