/*
 * Session.java - represents a jEdit session
 * Copyright (c) 2001 Dirk Moebius
 * Copyright (c) 2008 Steve Jakob
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

package sessions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.XMLUtilities;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class Session implements Cloneable
{

	/** Session Property name for Session base directory */
	public static final String BASE_DIRECTORY = "basedir";
	/** Session Property name for Session default edit mode */
	public static final String DEFAULT_MODE = "mode";

	private String name;
	private String filename;
	private String currentFile;
	private Hashtable properties, sessionFiles;


	public Session(String name)
	{
		setName(name);
		this.sessionFiles = new Hashtable();
		this.properties = new Hashtable();
	}


	public String getName()
	{
		return name;
	}


	public void setName(String name)
	{
		this.name = name;
		this.filename = SessionManager.createSessionFileName(name);
	}

	/**
	 * Rename this session. This changes both the logical name and the filename.
	 * @param newName The new name for the session.
	 * @return <code>true</code> if the rename succeeds, <code>false</code> otherwise.
	 */
	public boolean rename(String newName)
	{
		String oldName = this.name;
		File oldFile = new File(this.filename);
		File newFile = new File(SessionManager.createSessionFileName(newName));
		if (oldFile.renameTo(newFile) == false)
		{
			// rename failed, so ...
			return false;
		}
		setName(newName);
		// Re-save so that the file contains the updated Session name
		try {
			saveXML();
		} catch (IOException ioe) {
			setName(oldName);
			return false;
		}
		return true;
	}


	public String getFilename()
	{
		return filename;
	}


	public String toString()
	{
		return name;
	}


	public void setCurrentFile(String file)
	{
		if(hasFile(file))
			currentFile = file;
		else
			Log.log(Log.DEBUG, this, "setCurrentFile: session " + name + ": doesn't contain file " + file + " - use addFile() first.");
	}


	public String getCurrentFile()
	{
		return currentFile;
	}


	/**
	 * Get a session property.
	 *
	 * @return the session property value, of null if the specified key
	 *    cannot be found.
	 */
	public String getProperty(String key)
	{
		Object value = properties.get(key);
		return value == null ? null : value.toString();
	}


	/**
	 * Set a session property.
	 * This sends out a SessionPropertyChanged message on EditBus.
	 * Note that having a value of <code>null</code> is the same as
	 * removeProperty(key), in which case a SessionPropertyRemoved
	 * message is sent out on EditBus.
	 *
	 * @throws NullPointerException if key is null.
	 */
	public void setProperty(String key, String value)
	{
		if(value != null)
		{
			Object oldValue = properties.put(key, value);
			EditBus.send(new SessionPropertyChanged(
				SessionManager.getInstance(), this, key,
				oldValue != null ? oldValue.toString() : null, value));
		}
		else
			removeProperty(key);
	}


	// being nice to developers: some convenience methods for storing/retrieving primitives

	public int getIntProperty(String key, int defaultValue) { return ParseUtilities.toInt(getProperty(key), defaultValue); }
	public long getLongProperty(String key, long defaultValue) { return ParseUtilities.toLong(getProperty(key), defaultValue); }
	public boolean getBooleanProperty(String key, boolean defaultValue) { return ParseUtilities.toBoolean(getProperty(key), defaultValue); }
	public float getFloatProperty(String key, float defaultValue) { return ParseUtilities.toFloat(getProperty(key), defaultValue); }
	public double getDoubleProperty(String key, double defaultValue) { return ParseUtilities.toDouble(getProperty(key), defaultValue); }
	public String getProperty(String key, String defaultValue) { String s = getProperty(key); return s != null ? s : defaultValue; }

	public void setIntProperty(String key, int value) { setProperty(key, String.valueOf(value)); }
	public void setLongProperty(String key, long value) { setProperty(key, String.valueOf(value)); }
	public void setBooleanProperty(String key, boolean value) { setProperty(key, String.valueOf(value)); }
	public void setFloatProperty(String key, float value) { setProperty(key, String.valueOf(value)); }
	public void setDoubleProperty(String key, double value) { setProperty(key, String.valueOf(value)); }


	/**
	 * Remove a session property.
	 * If the property was part of this session, a SessionPropertyRemoved
	 * message is sent out on EditBus. If the session didn't contain
	 * the property, nothing is sent.
	 *
	 * @throws NullPointerException if key is null.
	 */
	public void removeProperty(String key)
	{
		if(properties.containsKey(key))
		{
			Object oldValue = properties.remove(key);
			EditBus.send(new SessionPropertyRemoved(
				SessionManager.getInstance(), this, key,
				oldValue != null ? oldValue.toString() : null));
		}
	}


	public boolean hasFile(String file)
	{
		return sessionFiles.containsKey(file);
	}


	/**
	 * Returns a <code>java.util.Enumeration</code> containing 
	 * <code>java.io.File</code> objects corresponding to the files 
	 * managed by this <code>Session</code> object.
	 */
	public Enumeration getAllFiles()
	{
		return sessionFiles.elements();
	}


	/**
	 * Returns a <code>java.util.Enumeration</code> containing 
	 * <code>String</code> objects corresponding to the names of the files 
	 * managed by this <code>Session</code> object.
	 */
	public Enumeration getAllFilenames()
	{
		return sessionFiles.keys();
	}


	/**
	 * Loads and opens the session.
	 * The session is loaded from the XML file in the sessions repository,
	 * then the files of the session are opened.
	 *
	 * @see #open(org.gjt.sp.jedit.View, boolean)
	 * @param view  where to display error message boxes.
	 * @return  true  if the session was loaded successfully and all buffers
	 *   have been opened.
	 */
	public boolean open(View view)
	{
		return open(view, true);
	}


	/**
	 * Loads and optionally opens the session.
	 * The session is loaded from the XML file in the sessions repository,
	 * then the files of the session are opened.
	 *
	 * @param view  where to display error message boxes.
	 * @param openFiles whether or not to open all the session files in addition
	 *   to loading and parsing the XML file (with its custom properties).
	 * @return  true  if the session was loaded successfully and all buffers
	 *   have been opened.
	 */
	public boolean open(View view, boolean openFiles)
	{
		Log.log(Log.DEBUG, this, "open: name=" + name);

		try
		{
			loadXML();
		}
		catch (IOException io)
		{
			Log.log(Log.ERROR, this, io);
			SessionManager.showErrorLater(view, "ioerror", new Object[] { io.getMessage() });
			return false;
		}
		catch (Exception e)
		{
			// this is probably a xml parse exception
			Log.log(Log.ERROR, this, e);
			SessionManager.showErrorLater(view, "sessions.manager.error.load", new Object[] { name, e.getMessage() });
			return false;
		}

		if (openFiles)
		{
			// open session files:
			Iterator it = sessionFiles.values().iterator();
			while(it.hasNext())
			{
				SessionFile sf = (SessionFile)it.next();
				Hashtable props = sf.getBufferProperties();
				jEdit.openFile(view, null, sf.getPath(), false, props);
			}

			// open session's recent buffer:
			if(currentFile != null)
			{
				Buffer buffer = jEdit.getBuffer(currentFile);
				if(buffer != null)
					view.setBuffer(buffer);
			}
		}

		return true;
	}


	/**
	 * Saves the session.
	 *
	 * @param view  The view the session is being saved from.
	 * @return true, if the session was saved successfully,
	 *   false if an IOException occurred.
	 */
	public boolean save(View view)
	{
		Log.log(Log.DEBUG, this, "save: name=" + name);

		if (view != null)
			view.getEditPane().saveCaretInfo();

		// Right now, the session's file list is cleared and filled again with
		// the current list of open jEdit buffers, but this behavior could be
		// changed in the future...
		sessionFiles.clear();

		for(Buffer buffer = jEdit.getFirstBuffer(); buffer != null; buffer = buffer.getNext())
			if(!buffer.isUntitled())
				addFile(buffer.getPath(), buffer.getStringProperty(Buffer.ENCODING));

		if(view != null)
			currentFile = view.getBuffer().getPath();

		try
		{
			saveXML();
		}
		catch (IOException io) {
			Log.log(Log.ERROR, this, io);
			SessionManager.showErrorLater(view, "ioerror", new Object[] { io.getMessage() });
			return false;
		}

		return true;
	}
	
	/**
	 * Has the the list of opened files changed?
	 *
	 * @return <code>true</code> if the file list has not changed
	 *   since the previous load/update, 
	 *   <code>false</code> if some file(s) have been opened/closed
	 */
	public boolean hasFileListChanged()
	{
		Vector currentFiles = new Vector();
		
		for(Buffer buffer = jEdit.getFirstBuffer(); buffer != null; buffer = buffer.getNext())
			if(!buffer.isUntitled())
				currentFiles.addElement(buffer.getPath());
		
		Vector allFiles = new Vector(sessionFiles.values());
		if (allFiles.equals(currentFiles))
			return false;
		
		return true;
	}

	/**
	 * Add a file to the session's file list using the default character encoding.
	 */
	public void addFile(String file)
	{
		if(hasFile(file))
			Log.log(Log.DEBUG, this, "addFile: session " + name + ": already contains file " + file + " - not added.");
		else
			addFile(file, jEdit.getProperty("buffer.encoding",
					System.getProperty("file.encoding")));
	}

	/**
	 * Add a file to the session's file list using the supplied character encoding.
	 */
	public void addFile(String file, String encoding)
	{
		if(hasFile(file))
			Log.log(Log.DEBUG, this, "addFile: session " + name + ": already contains file " + file + " - not added.");
		else
			sessionFiles.put(file, new SessionFile(file, encoding));
	}

	/**
	 * Clears the session's contents: forgets all open files and properties,
	 * and the name of the current file.
	 */
	public void clear()
	{
		sessionFiles.clear();
		properties.clear();
		currentFile = null;
	}


	public Object clone()
	{
		return getClone();
	}


	public Session getClone()
	{
		Session clone = new Session(this.name);
		clone.sessionFiles = (Hashtable) this.sessionFiles.clone();
		clone.properties = (Hashtable) this.properties.clone();
		return clone;
	}


	/**
	 * Loads the session, but does not open any files in jEdit.
	 */
	public void loadXML() throws Exception
	{
		Log.log(Log.DEBUG, this, "loadXML: name=" + name + " filename=" + filename);

		clear();

		// Reader reader = new BufferedReader(new FileReader(filename));
		XMLUtilities.parseXML(new FileInputStream(filename), new SessionXmlHandler());
		//XmlParser parser = new XmlParser();
		//parser.setHandler(new SessionXmlHandler(parser));
		//parser.parse(null, null, reader);
	}


	/**
	 * Saves the session.
	 */
	public void saveXML() throws IOException
	{
		Log.log(Log.DEBUG, this, "saveXML: name=" + name + " filename=" + filename);

		BufferedWriter out = new BufferedWriter(new FileWriter(filename));
		// write header
		out.write("<?xml version=\"1.0\"?>");
		out.newLine();
		out.write("<!DOCTYPE SESSION SYSTEM \"session.dtd\">");
		out.newLine();
		out.newLine();
		// write session name
		out.write("<SESSION name=\"");
		out.write(name);
		out.write("\">");
		out.newLine();
		// write files
		out.write("  <FILES>");
		out.newLine();
		saveFiles(out);
		out.write("  </FILES>");
		out.newLine();
		out.newLine();
		// write properties
		out.write("  <PROPERTIES>");
		out.newLine();
		saveProperties(out);
		out.write("  </PROPERTIES>");
		out.newLine();
		out.write("</SESSION>");
		out.newLine();
		out.close();
	}


	private void saveFiles(BufferedWriter out) throws IOException
	{
		Enumeration myEnum = sessionFiles.elements();
		while(myEnum.hasMoreElements())
		{
			SessionFile sf = (SessionFile)myEnum.nextElement();
			String filename = sf.getPath().replace('\\','/');
			out.write("      <FILE filename=\"");
			out.write(ParseUtilities.encodeXML(filename));
			out.write('"');
			if(filename.equals(getCurrentFile().replace('\\','/')))
				out.write(" isCurrent=\"true\"");
			// Write character encoding info, carat position
			Buffer buff = jEdit.getBuffer(filename);
			if (buff != null)
			{
				String encoding = buff.getStringProperty(Buffer.ENCODING);
				if (encoding != null && encoding.length() > 0)
					out.write(" encoding=\"" + encoding + "\"");
				Integer carat = new Integer(buff.getIntegerProperty(
					Buffer.CARET, 0));
				out.write(" carat=\"" + carat.toString() + "\"");
			}
			out.write("/>");
			out.newLine();
		}
	}


	private void saveProperties(BufferedWriter out) throws IOException
	{
		Enumeration myEnum = properties.keys();
		while(myEnum.hasMoreElements())
		{
			String key = myEnum.nextElement().toString();
			String value = getProperty(key);
			Log.log(Log.DEBUG, this, "Writing PROP: " + key);
			out.write("      <PROP key=\"");
			out.write(ParseUtilities.encodeXML(key));
			out.write('"');
			// if value is null, don't write a value="" element:
			if(value != null)
			{
				out.write(" value=\"");
				out.write(ParseUtilities.encodeXML(value));
				out.write('"');
			}
			out.write("/>");
			out.newLine();
		}
	}


	private class SessionXmlHandler extends DefaultHandler
	{
		boolean atStart = false;
		SessionXmlHandler()
		{
		}

		public void startDocument() throws SAXException
		{
			super.startDocument();
			atStart = true;
		}

		public void startElement(String uri, String localName, String name, Attributes attributes)
			throws SAXException
		{
			if (atStart)
			{
				if (localName.equalsIgnoreCase("session"))
				{
					atStart = false;
					return;
				}
				else
				{
					throw new SAXException("DOCTYPE must be SESSION");
				}
			}
			if (name.equalsIgnoreCase("prop"))
			{
				String key = attributes.getValue("key");
				String value = attributes.getValue("value");
				properties.put(key, value);
			}
			else if (name.equalsIgnoreCase("file"))
			{
				String filePath = attributes.getValue("filename");
				String encoding = attributes.getValue("encoding");
				boolean isCurrent = "true".equalsIgnoreCase(attributes.getValue("isCurrent"));
				addFile(filePath, encoding);
				if (isCurrent)
				{
					setCurrentFile(filePath);
				}
			}
			super.startElement(uri, localName, name, attributes);
		}

		public InputSource resolveEntity(String publicId, String systemId) throws IOException
		{
			if (systemId.endsWith("session.dtd"))
				return new InputSource(new BufferedReader(new InputStreamReader(this.getClass()
					.getResourceAsStream("session.dtd"))));
			return null;
		}
	}
}