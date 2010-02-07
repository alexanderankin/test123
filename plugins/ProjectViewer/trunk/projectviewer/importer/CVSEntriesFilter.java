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
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import org.gjt.sp.util.IOUtilities;
import org.gjt.sp.util.Log;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSFile;

import projectviewer.PVActions;
import projectviewer.VFSHelper;
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
 *	<p>Since PV 2.1.1, this filter also looks for .svn/entries files when
 *	they're available.</p>
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public class CVSEntriesFilter extends ImporterFileFilter {

	//{{{ Private members
	private Map<String,Set<String>> entries = new HashMap<String,Set<String>>();
	//}}}

	//{{{ +getDescription() : String
	public String getDescription() {
		return jEdit.getProperty("projectviewer.import.filter.cvs.desc");
	} //}}}

	//{{{ +accept(VFSFile) : boolean
	/**
	 *	accept() method for the FilenameFilter implementation. Accepts only
	 *	files and directories that are listed in the CVS/Entries file.
	 */
	public boolean accept(VFSFile file) {
		String fname = file.getName();
		if (file.getType() == VFSFile.DIRECTORY) {
			return (!fname.equals("CVS") && !fname.equals(".svn"));
		} else {
			VFS vfs = file.getVFS();
			String parent = file.getVFS().getParentOfPath(file.getPath());
			return getEntries(vfs, parent).contains(fname);
		}
	} //}}}

	//{{{ -getEntries(String) : HashSet
	/**
	 *	Returns the set of files from the CVS/Entries file for the given path.
	 *	In case the file has not yet been read, parse it.
	 */
	private Set<String> getEntries(VFS vfs,
								   String dirPath)
	{
		Set<String> h = entries.get(dirPath);
		if (h == null) {
			BufferedReader br = null;
			Object session;
			String fPath;
			View view;

			h = new HashSet<String>();
			view = jEdit.getActiveView();

			fPath = vfs.constructPath(dirPath, "CVS");
			fPath = vfs.constructPath(fPath, "Entries");
			session = VFSHelper.createVFSSession(vfs, fPath, view);

			try
			{
				InputStream in;

				if (vfs._getFile(session, fPath, view) != null) {
					in = vfs._createInputStream(session, fPath, false, view);
					br = new BufferedReader(new InputStreamReader(in));

					String line;
					while ((line = br.readLine()) != null) {
						int idx1, idx2;
						idx1 = line.indexOf('/');
						if (idx1 != -1) {
							idx2 = line.indexOf('/', idx1 + 1);
							h.add(line.substring(idx1 + 1, idx2));
						}
					}
				} else {
					// no CVS/Entries. Try .svn/entries
					getSubversionEntries(vfs, h, dirPath);
				}
			} catch (IOException ioe) {
				Log.log(Log.ERROR,this,ioe);
			} finally {
				IOUtilities.closeQuietly(br);
				entries.put(dirPath, h);
				VFSHelper.endVFSSession(vfs, session, view);
			}
		}
		return h;
	} //}}}

	//{{{ -getSubversionEntries(Set, String) : void
	/**
	 * Searches in subversion directories for 1.3 or 1.4 format ".svn/entries"
	 * files, parses them and returns the list of filenames in a target set.
	 *
	 * @param vfs The VFS where the entries file is.
	 * @param target a target Set<String> of filenames to fill
	 * @param dirPath the location to search for subversion entries.
	 */
	private void getSubversionEntries(VFS vfs,
									  Set<String> target,
									  String dirPath)
	{
		boolean isXml = true;
		Object session;
		String fPath;
		View view;

		view = jEdit.getActiveView();
		fPath = vfs.constructPath(dirPath, ".svn");
		fPath = vfs.constructPath(fPath, "entries");
		session = VFSHelper.createVFSSession(vfs, fPath, view);

		InputStream in = null;
		try {
			in = vfs._createInputStream(session, fPath, false, view);
			if (in.markSupported()) {
				in.mark(1);
				isXml = (in.read() == (int) '<');
				in.reset();
			} else {
				isXml = (in.read() == (int) '<');
				in.close();
				in = vfs._createInputStream(session, fPath, false, view);
			}
			in = new BufferedInputStream(in);
		} catch (IOException ioe) {
			IOUtilities.closeQuietly(in);
			VFSHelper.endVFSSession(vfs, session, view);
			return;
		}

		if (isXml) {
			try {
				XMLReader parser = PVActions.newXMLReader(new SubversionEntriesHandler(target));
				parser.parse(new InputSource(in));
			} catch (SAXException saxe) {
				Log.log(Log.ERROR, this, saxe);
			} catch (IOException ioe) {
				Log.log(Log.ERROR, this, ioe);
			} finally {
				IOUtilities.closeQuietly(in);
				VFSHelper.endVFSSession(vfs, session, view);
			}
		} else {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			int read;
			boolean lastWasDelimiter = false;
			String fileName = null;
			try {
				while ((read = in.read()) != -1) {
					if (read == 0x0A) {
						byte[] data = bos.toByteArray();
						if (!lastWasDelimiter) {
							lastWasDelimiter = (data.length == 1 && data[0] == 0x0C);
						} else if (fileName == null) {
							fileName = new String(data, "UTF-8");
						} else {
							String type = new String(data, "UTF-8");
							if (type.equals("file")) {
								target.add(fileName);
							}
							fileName = null;
							lastWasDelimiter = false;
						}
						bos.reset();
					} else {
						bos.write(read);
					}
				}
			} catch (IOException ioe) {
				Log.log(Log.ERROR, this, ioe);
			} finally {
				IOUtilities.closeQuietly(in);
				VFSHelper.endVFSSession(vfs, session, view);
			}
		}

	} //}}}

	//{{{ +getRecurseDescription() : String
	public String getRecurseDescription() {
		return	jEdit.getProperty("projectviewer.import.filter.cvs.rdesc");
	} //}}}

	//{{{ -class _SubversionEntriesHandler_
	/**
	 * This is only for Subversion 1.3 and earlier. Version 1.4 and later no longer
	 * use XML formats to store entries files.
	 */
	private static class SubversionEntriesHandler extends DefaultHandler {

		private Set<String> target;

		private boolean addEntry;
		private String	path;

		//{{{ +SubversionEntriesHandler(Set) : <init>
		public SubversionEntriesHandler(Set<String> target) {
			this.target 	= target;
			this.addEntry	= false;
			this.path		= null;
		} //}}}

		//{{{ +startElement(String, String, String, Attributes) : void
		public void startElement(String uri, String localName,
								 String qName, Attributes attrs)
		{
			if (qName.equals("entry")) {
				this.path = attrs.getValue("name");
				this.addEntry = "file".equals(attrs.getValue("kind"));
			}
		} //}}}

		//{{{ +endElement(String, String, String) : void
		public void endElement(String uri, String localName, String qName) {
			if (qName.equals("entry")) {
				if (this.addEntry && this.path != null)
					this.target.add(this.path);
				this.addEntry = false;
				this.path = null;
			}
		} //}}}

	} //}}}

}


