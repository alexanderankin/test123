/*
 * Roster.java - A list of things to do, used in various places
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2002 Carmine Lucarelli
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

package macroManager;

//{{{ Imports
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.io.*;
import java.net.*;
import java.util.zip.*;
import java.util.*;
import org.gjt.sp.jedit.io.VFSManager; // we use VFSManager.error() method
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;
//}}}

class Roster
{
	//{{{ Roster constructor
	Roster()
	{
		operations = new Vector();
	} //}}}

	//{{{ addOperation() method
	void addOperation(Operation op)
	{
		for(int i = 0; i < operations.size(); i++)
		{
			if(operations.elementAt(i).equals(op))
				return;
		}

		operations.addElement(op);
	} //}}}

	//{{{ getOperationCount() method
	int getOperationCount()
	{
		return operations.size();
	} //}}}

	//{{{ isEmpty() method
	boolean isEmpty()
	{
		return operations.size() == 0;
	} //}}}

	//{{{ performOperations() method
	boolean performOperations(MacroManagerProgress progress)
	{
		for(int i = 0; i < operations.size(); i++)
		{
			Operation op = (Operation)operations.elementAt(i);
			if(op.perform(progress))
				progress.done(true);
			else
			{
				progress.done(false);
				return false;
			}

			if(Thread.interrupted())
				return false;
		}

		return true;
	} //}}}

	//{{{ Private members
	private Vector operations;

	static interface Operation
	{
		boolean perform(MacroManagerProgress progress);
		boolean equals(Object o);
	} //}}}

	//{{{ Install class
	static class Install implements Operation
	{
		Install(String url, String installDirectory)
		{
			// catch those hooligans passing null urls
			if(url == null)
				throw new NullPointerException();

			this.url = url;
			this.installDirectory = installDirectory;
		}

		public boolean perform(final MacroManagerProgress progress)
		{
			try
			{
				String fileName = MiscUtilities.getFileName(url);
				progress.downloading(fileName);
				String path = download(progress,fileName,url);
				if(path == null)
				{
					// interrupted download
					return false;
				}

				return true;
			}
			catch(InterruptedIOException iio)
			{
				// do nothing, user clicked 'Stop'
				return false;
			}
			catch(final IOException io)
			{
				Log.log(Log.ERROR,this,io);

				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						String[] args = { io.getMessage() };
						GUIUtilities.error(null,"ioerror",args);
					}
				});

				return false;
			}
			catch(Exception e)
			{
				Log.log(Log.ERROR,this,e);

				return false;
			}
		}

		public boolean equals(Object o)
		{
			if(o instanceof Install
				&& ((Install)o).url.equals(url))
			{
				/* even if installDirectory is different */
				return true;
			}
			else
				return false;
		}

		// private members
		private String url;
		private String installDirectory;

		private String download(MacroManagerProgress progress,
			String fileName, String url) throws Exception
		{
			URLConnection conn = new URL(url).openConnection();
			progress.setMaximum(Math.max(0,conn.getContentLength()));

			String path = MiscUtilities.constructPath(installDirectory, fileName);

			if(!copy(progress,conn.getInputStream(),
				new FileOutputStream(path),true,true))
				return null;

			return path;
		}

		private boolean copy(MacroManagerProgress progress,
			InputStream in, OutputStream out, boolean canStop,
			boolean doProgress) throws Exception
		{
			in = new BufferedInputStream(in);
			out = new BufferedOutputStream(out);

			byte[] buf = new byte[4096];
			int copied = 0;
loop:			for(;;)
			{
				int count = in.read(buf,0,buf.length);
				if(count == -1)
					break loop;

				if(doProgress)
				{
					copied += count;
					progress.setValue(copied);
				}

				out.write(buf,0,count);
				if(canStop && Thread.interrupted())
				{
					in.close();
					out.close();
					return false;
				}
			}

			in.close();
			out.close();
			return true;
		}
	} //}}}
}
