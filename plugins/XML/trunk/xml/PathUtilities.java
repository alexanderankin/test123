/*
 * PathUtilities.java - pathToURL and urlToPath
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2009, Eric Le Lay
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */
package xml;

//{{{ Imports
import java.io.File;
import java.net.URI;
import java.net.MalformedURLException;
import java.util.regex.Pattern;

import org.gjt.sp.util.Log;
//}}}

/**
 * pathToURL() and urlToPath()
 */
public final class PathUtilities
{
	
	/**
	 * a pattern for standard windows paths, e.g. : C:\temp\MyClass.java 
	 */
	public static final Pattern windowsDrivePattern = Pattern.compile("[A-Z]:\\\\.*");
	
	/**
	 * a pattern for windows UNC e.g. :
	 *  \\localhost\SHARED_C\temp\MyClass.java
	 * and long UNC, e.g. :
	 *  \\?\UNC\localhost\SHARED_C\temp\MyClass.java
	 *  \\?\C:\temp\MyClass.java
	 */
	 public static final Pattern windowsUNCPattern = Pattern.compile("\\\\\\\\.*");
	
	/**
	 * a pattern for UNIX paths e.g. :
	 *  /tmp/MyClass.java
	 */
	 public static final Pattern unixPattern = Pattern.compile("/.*");
	 
	//{{{ pathToURL() method
	/**
	 * @param	path	UNIX/Windows path or VFS path
	 * @return	path having a scheme
	 */
	public static String pathToURL(String path)
	{
		if(windowsDrivePattern.matcher(path).matches()
		  || windowsUNCPattern.matcher(path).matches()
	  	  || unixPattern.matcher(path).matches())
		{
			try
			{
				//it's a file
				return new File(path).toURI().toURL().toString();
			}
			catch(MalformedURLException ue)
			{
				Log.log(Log.ERROR,PathUtilities.class,"strange URI (apos added) '"+path+"'");
				Log.log(Log.ERROR,PathUtilities.class,ue);
				return path;
			}
		}
		else
		{
			//it's already an URL
			return path;
		}
	}
	// }}}
	
	//{{{ urlToPath() method
	/**
	 * @param	url	file:/ url
	 * @return	path without Scheme
	 */
	public static String urlToPath(String url)
	{
		if(url == null)return null;
		if(url.startsWith("file:/"))
		{
			try
			{
				//it's a file
				return new File(new URI(url)).getPath();
			}
			catch(java.net.URISyntaxException ue)
			{
				Log.log(Log.ERROR,SchemaMappingManager.class,"strange URI (apos added) '"+url+"'");
				Log.log(Log.ERROR,SchemaMappingManager.class,ue);
				return url;
			}
		}
		else
		{
			//can't convert it
			return url;
		}
	}
	// }}}
}
