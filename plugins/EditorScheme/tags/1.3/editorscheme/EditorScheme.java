/*
 * EditorScheme.java - Color/Style Scheme for jEdit.
 * Copyright (C) 2000 Ollie Rutherfurd
 *
 * :folding=explicit:collapseFolds=1:
 *
 * {{{This program is free software; you can redistribute it and/or
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.}}}
 *
 * $Id: EditorScheme.java,v 1.6 2003/11/10 14:23:18 orutherfurd Exp $
 */


package editorscheme;

//{{{ imports
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.util.Log;
import java.io.*;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.Properties;
//}}}


/**
*	Set of color/style properties for jEdit.
*/
public class EditorScheme
{
	private Properties properties = new Properties();
	private String filename;
	private boolean readOnly;

	public static final String EXTENSION = ".jedit-scheme";
	private static ArrayList<EditorScheme.PropertyGroup> propertyGroups;

	static {
		propertyGroups = new ArrayList<EditorScheme.PropertyGroup>();
		StringTokenizer names = new StringTokenizer(
			jEdit.getProperty("editor-scheme.property-groups"));
		while(names.hasMoreElements()){
			String name = names.nextToken();
			propertyGroups.add(new EditorScheme.PropertyGroup(name));
		}
	}


	/**
	* Default constructor, only readonly set.
	*/
	public EditorScheme()
	{
		setFilename("");
		setReadOnly(true);
	}


	/**
	* Loads scheme from a file
	*/
	public EditorScheme(String filename)
	{
		this();
		this.setFilename(filename);
		load();
	}


	/**
	* Loads scheme from a stream
	*/
	public EditorScheme(InputStream inputStream)
	{
		this();
		load(inputStream);
	}


	/**
	* applys the scheme (sets the properties)
	*/
	public void apply()
	{
		for(int i=0; i < propertyGroups.size(); i++)
		{
			PropertyGroup group = (PropertyGroup)propertyGroups.get(i);
			if(group.apply)
			{
				ArrayList names = group.getPropertyNames();
				for(int j=0; j < names.size(); j++)
				{
					String name = (String)names.get(j);
					String value = (String)properties.get(name);
					jEdit.setProperty(name,value);
				}
			}
		}

		jEdit.propertiesChanged();
		jEdit.saveSettings();
	}


	/**
	* Sets all properties based on current values.
	*/
	public void getFromCurrent()
	{
		for(int i=0; i < propertyGroups.size(); i++)
		{
			PropertyGroup group = (PropertyGroup)propertyGroups.get(i);
			ArrayList names = group.getPropertyNames();
			for(int j=0; j < names.size(); j++)
			{
				String name = (String)names.get(j);
				String value = jEdit.getProperty(name);
				if(value != null)
					properties.put(name,value);
			}
		}
	}


	/**
	* Loads scheme properties from <i>filename</i>.
	*/
	public void load()
	{
		FileInputStream inputStream = null;
		try
		{
			inputStream = new FileInputStream(this.filename);
			BufferedInputStream bufferedStream = 
				new BufferedInputStream(inputStream);
			properties.load(bufferedStream);
		}
		catch(IOException ioe)
		{
			Log.log(Log.ERROR, this, "loading scheme failed ["
				+ this.filename + "]: " + ioe.toString());
		}
		finally
		{
			if(inputStream != null)
			try
			{
				inputStream.close();
			}
			catch(IOException ioe){}        // NOPMD
		}
	}


	/**
	 * load scheme properties from an InputStream
	 */
	public void load(InputStream inputStream)
	{
		try
		{
			this.readOnly = true;
			properties.load(new BufferedInputStream(inputStream));
		}
		catch(IOException ioe)
		{
			Log.log(Log.ERROR, this, "loading scheme from stream failed ["
				+ this.filename + "]: " + ioe.toString());
		}
	}

	/**
	* return name for toString()
	*/
	public String toString()
	{
		return getName();
	}

	/**
	* Saves scheme properties to <i>filename</i>.
	*/
	public void save() 
		throws IOException
	{
		FileOutputStream outputStream;

		outputStream =  new FileOutputStream(this.filename, false);
		properties.store(outputStream, 
			"jEdit Editor Scheme\n#:mode=properties:lineSeparator=\\n:");
		outputStream.close();
	}

	/**
	* properties for the scheme
	*/
	public final static String[] SCHEME_PROPERTY_NAMES = {
		"scheme.name",
	};


	/**
	 * return the name of the scheme.
	 */
	public String getName()
	{
		return (String) properties.get("scheme.name");
	}

	/**
	 * set the name of the scheme.
	 */
	public void setName(String newValue)
	{
		properties.put("scheme.name", newValue);
	}

	/**
	 * return the filename for the scheme.
	 */
	public String getFilename()
	{
		return this.filename;
	}

	/**
	 * set the filename for the scheme.
	 */
	public void setFilename(String filename)
	{
		this.filename = filename;
	}

	/**
	* default directory for saving new themes
	*/
	public static String getDefaultDir()
	{
		return MiscUtilities.constructPath(
			jEdit.getSettingsDirectory() , "schemes");
	}

	/**
	 * return all properties for scheme
	 */
	public Properties getProperties()
	{
		return this.properties;
	}

	/**
	 * return readonly status
	 */
	public boolean getReadOnly()
	{
		return this.readOnly;
	}

	/**
	 * set readonly status
	 */
	public void setReadOnly(boolean readOnly)
	{
		this.readOnly = readOnly;
	}

	/**
	 * return a scheme property, or null if it doesn't exist.
	 */
	public String getProperty(String name)
	{
		return (String)this.properties.get(name);
	}

	/**
	 * set a property for the scheme
	 * @param name property name
	 * @param value property value
	 */
	public void setProperty(String name, String value)
	{
		this.properties.put(name,value);
	}

	/**
	 * Groups of properties (ErrorList,WhiteSpace,etc..)
	 */
	public static ArrayList getPropertyGroups()
	{
		return propertyGroups;
	}


	//{{{ PropertyGroup class
	/**
	 * Encapsulation of a group of properties 
	 * in an EditorScheme.
	 */
	public static class PropertyGroup
	{

		/**
		 * @param name internal name for group
		 */
		PropertyGroup(String name)
		{
			this.name = name;
			this.label = jEdit.getProperty(
				"editor-scheme." + name + ".name", name);
			this.apply = jEdit.getBooleanProperty(
				"editor-scheme." + name + ".apply", true);

			StringTokenizer names = new StringTokenizer(
				jEdit.getProperty(
					"editor-scheme." + name + "-props"));
			while(names.hasMoreElements())
				properties.add(names.nextToken());
		}

		public String toString()
		{
			return this.label;
		}

		/**
		 * Returns names of properties in this group.
		 */
		public ArrayList getPropertyNames()
		{
			return this.properties;
		}

		/**
		 * Sets properties from scheme.  But does NOT call
		 * jEdit.propertiesChanged() and jEdit.saveProperties()
		 * @param scheme scheme to get properties from
		 */
		public void apply(EditorScheme scheme)
		{
			for(int i=0; i < properties.size(); i++)
			{
				String name = (String)properties.get(i);
				String value = (String)scheme.getProperty(name);
				if(value != null)
					jEdit.setProperty(name,value);
			}
		}

		// group name
		String name;
		// display label
		String label;
		// whether to use this group, by default
		boolean apply;
		// property names
		ArrayList<String> properties = new ArrayList<String>();

	}//}}}


}
