/*
 * Session.java - represents a jEdit session
 * Copyright (c) 2001 Dirk Moebius
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


import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import com.microstar.xml.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;


public class Session implements Cloneable
{

	private String name;
	private String filename;
	private Vector allFiles;
	private String currentFile;
	private Hashtable properties;


	public Session(String name)
	{
		setName(name);
		this.allFiles = new Vector();
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
		return allFiles.contains(file);
	}


	public Enumeration getAllFiles()
	{
		return allFiles.elements();
	}


	/**
	 * Loads and opens the session.
	 * The session is loaded from the XML file in the sessions repository,
	 * then the files of the session are opened.
	 *
	 * @param view  where to display error message boxes.
	 * @return  true  if the session was loaded successfully and all buffers
	 *   have been opened.
	 */
	public boolean open(View view)
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

		// open session files:
		Enumeration enum = allFiles.elements();
		while(enum.hasMoreElements())
			jEdit.openFile(null, enum.nextElement().toString());

		// open session's recent buffer:
		if(currentFile != null)
		{
			Buffer buffer = jEdit.openFile(null, currentFile);
			if(buffer != null)
				view.setBuffer(buffer);
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
		allFiles.removeAllElements();

		for(Buffer buffer = jEdit.getFirstBuffer(); buffer != null; buffer = buffer.getNext())
			if(!buffer.isUntitled())
				addFile(buffer.getPath());

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


	public void addFile(String file)
	{
		if(hasFile(file))
			Log.log(Log.DEBUG, this, "addFile: session " + name + ": already contains file " + file + " - not added.");
		else
			allFiles.addElement(file);
	}


	/**
	 * Clears the session's contents: forgets all open files and properties,
	 * and the name of the current file.
	 */
	public void clear()
	{
		allFiles.removeAllElements();
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
		clone.allFiles = (Vector) this.allFiles.clone();
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

		Reader reader = new BufferedReader(new FileReader(filename));
		XmlParser parser = new XmlParser();
		parser.setHandler(new SessionXmlHandler(parser));
		parser.parse(null, null, reader);
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
		Enumeration enum = allFiles.elements();
		while(enum.hasMoreElements())
		{
			String filename = enum.nextElement().toString();
			out.write("      <FILE filename=\"");
			out.write(ParseUtilities.encodeXML(filename));
			out.write('"');
			if(filename.equals(getCurrentFile()))
				out.write(" isCurrent=\"true\"");
			out.write("/>");
			out.newLine();
		}
	}


	private void saveProperties(BufferedWriter out) throws IOException
	{
		Enumeration enum = properties.keys();
		while(enum.hasMoreElements())
		{
			String key = enum.nextElement().toString();
			String value = getProperty(key);
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


	private class SessionXmlHandler extends HandlerBase
	{
		SessionXmlHandler(XmlParser parser)
		{
			this.parser = parser;
		}


		public void doctypeDecl(String name, String publicId, String systemId) throws XmlException
		{
			if (name.equalsIgnoreCase("SESSION"))
				return;
			else
				throw new XmlException(
					"DOCTYPE must be SESSION",
					"SESSION",
					parser.getLineNumber(),
					parser.getColumnNumber()
				);
		}


		public Object resolveEntity(String publicId, String systemId) throws IOException
		{
			if (systemId.equals("session.dtd"))
				return new BufferedReader(new InputStreamReader(
					this.getClass().getResourceAsStream("session.dtd")));
			return null;
		}


		public void attribute(String att, String value, boolean isSpecified) throws XmlException
		{
			if("name".equals(att))
				currentName = value;
			else if("filename".equals(att))
				currentFilename = value;
			else if ("isCurrent".equals(att))
			{
				if(value == null)
					currentIsCurrent = false;
				else
					currentIsCurrent = Boolean.valueOf(value).booleanValue();
			}
			else if ("key".equals(att))
				currentPropKey = value;
			else if ("value".equals(att))
				currentPropValue = value; // Note: value may be null, if missing
			else
				throw new XmlException(
					"unknown attribute: " + att,
					"SESSION",
					parser.getLineNumber(),
					parser.getColumnNumber()
				);
		}


		public void endElement(String elementName)
		{
			if(elementName == null)
				return;

			if("SESSION".equals(elementName))
			{
				if(!currentName.equals(Session.this.name))
				{
					Log.log(Log.WARNING, this,
						jEdit.getProperty("sessions.manager.warning.load.ambigious.message",
							new Object[] { Session.this.name, currentName }));
					Session.this.name = currentName;
				}
			}
			else if("FILE".equals(elementName))
			{
				addFile(currentFilename);
				if(currentIsCurrent)
					setCurrentFile(currentFilename);
			}
			else if("PROP".equals(elementName))
				properties.put(currentPropKey, currentPropValue);
		}


		private XmlParser parser;
		private String currentName;
		private String currentFilename;
		private String currentPropKey;
		private String currentPropValue;
		private boolean currentIsCurrent;
	}


}

