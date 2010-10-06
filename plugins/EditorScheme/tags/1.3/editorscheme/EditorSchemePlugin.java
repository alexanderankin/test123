/*
 * EditorSchemePlugin.java - 'Color/Style Schemes' for jEdit.
 * Copyright (C) 2000-2003 Ollie Rutherfurd
 *
 * :folding=explicit:collapseFolds=1:
 *
 * {{{ This program is free software; you can redistribute it and/or
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
 * }}}
 *
 * $Id: EditorSchemePlugin.java,v 1.8 2003/11/10 14:23:18 orutherfurd Exp $
 */

package editorscheme;

//{{{ imports
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import java.util.Collections;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.zip.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.StandardUtilities;
//}}}

/**
 * Editor Scheme Plugin allows user to save sets of color and style properties
 * as a 'scheme'.  Schemes can be used to easily switch between sets of color
 * and style properties.
 * @author Ollie Rutherfurd
 */
public class EditorSchemePlugin extends EditPlugin
{

	public static final String NAME = "editor-scheme";

	// schemes loaded from jar
	private static ArrayList<EditorScheme> packagedSchemes;
	// user schemes and schemes loaded from jar
	private static ArrayList<EditorScheme> schemes = null;
	// location of user's schemes
	private static String userSchemesPath;


	//{{{ start()
	public void start()
	{
		userSchemesPath = MiscUtilities.constructPath(
			jEdit.getSettingsDirectory(), "schemes");
		File dir = new File(userSchemesPath);

		if(!dir.exists())
		{
			dir.mkdir();
			Log.log(Log.NOTICE, EditorSchemePlugin.class, 
				"created user schemes dir: " + userSchemesPath);
		}
	}//}}}


	//{{{ loadSchemes()
	/**
	* Loads (or reloads) schemes provided by the plugin and user's schemes.
	* @since 0.4.0
	*/
	public static void loadSchemes()
	{
		if(schemes == null)
			schemes = new ArrayList<EditorScheme>();
		else
			schemes.clear();
		loadBuiltinSchemes();
		loadUserSchemes();
		Collections.sort(schemes,
			new StandardUtilities.StringCompare<EditorScheme>());
	}//}}}


	//{{{ getSchemes()
	/**
	* Get ArrayList of EditorScheme objects.
	* @since 0.4.0
	*/
	public static ArrayList<EditorScheme> getSchemes()
	{
		if(schemes == null)
		{
			schemes = new ArrayList<EditorScheme>();
			loadSchemes();
		}
		return schemes;
	}//}}}


	//{{{ loadBuiltinSchemes()
	/**
	* Loads schemes packaged with plugin.
	* @since 0.4.0
	*/
	private static void loadBuiltinSchemes()
	{
		// if packaged schemes haven't been loaded, load them
		if(packagedSchemes == null)
		{
			packagedSchemes = new ArrayList<EditorScheme>();

			try
			{
				ZipFile zip = new ZipFile(jEdit.getPlugin(
					"editorscheme.EditorSchemePlugin")
					.getPluginJAR().getPath());

				Enumeration entries = zip.entries();

				while(entries.hasMoreElements())
				{
					ZipEntry entry = (ZipEntry)entries.nextElement();
					if(entry.getName().endsWith(EditorScheme.EXTENSION))
					{
						packagedSchemes.add(
							new EditorScheme(zip.getInputStream(entry)));
					}
				}
			}
			catch(IOException ioe)
			{
				Log.log(Log.ERROR, EditorSchemePlugin.class
					,"Error loading schemes from jar: " + ioe.toString());
			}
		}

		for(int i = 0; i < packagedSchemes.size(); i++)
			schemes.add(packagedSchemes.get(i));
	}//}}}


	///{{{ loadUserSchemes()
	/**
	* Loads scheme files from user's schemes directory.
	* @since 0.4.0
	*/
	private static void loadUserSchemes()
	{
		
		File dir = new File(userSchemesPath);

		String[] files = dir.list(new FilenameFilter(){
			public boolean accept(File dir, String name)
			{
				return name.toLowerCase().endsWith(EditorScheme.EXTENSION);
			}
		});

		for(int i = 0; i < files.length; i++)
		{
			String file = dir.getPath() + File.separator + files[i];
			EditorScheme scheme = new EditorScheme(file);
			scheme.setReadOnly(false);
			schemes.add(scheme);
		}

	}//}}}


}

