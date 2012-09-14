/*
 * ConfigurableHyperlinksPlugin.java - The ConfigurableHyperlinks plugin
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2012 Patrick Eibl
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package configurablehyperlinks;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import java.io.*;
import org.gjt.sp.util.XMLUtilities;
import org.gjt.sp.util.Log;
import java.util.*;


/**
 * @author Patrick Eibl
 */
public class ConfigurableHyperlinksPlugin extends EditPlugin
{
	private HashMap<String, ArrayList<ConfigurableHyperlinkData>> sources;
	private ConfigurableHyperlinksHandler handler;
	private File hyperlinksFile;
	
	@Override
	public void start()
	{
		handler = new ConfigurableHyperlinksHandler();
		hyperlinksFile = new File(getPluginHome(), "hyperlinks.xml");
		if(!hyperlinksFile.exists())
		{
			try 
			{
				hyperlinksFile.getParentFile().mkdirs();
				BufferedWriter out = new BufferedWriter(new FileWriter(hyperlinksFile));
				out.write("<?xml version=\"1.0\"?>\n");
				out.write("<!DOCTYPE HYPERLINKSOURCES SYSTEM \"hyperlinks.dtd\">\n");
				out.write("<HYPERLINKSOURCES>\n</HYPERLINKSOURCES>");
				out.close();
			} catch (IOException e) 
			{
				Log.log(Log.ERROR,this,"No hyperlinks.xml found, and couldn't create one");
			}
		}
		parseHyperlinksXML();
		registerConfigurableHyperlinkSources();
	}

	@Override
	public void stop()
	{
	}
	
	public void reloadConfigurableHyperlinkSources()
	{
		unregisterConfigurableHyperlinkSources();
		parseHyperlinksXML();
		registerConfigurableHyperlinkSources();
	}
	
	public ArrayList<ConfigurableHyperlinkData> getHyperlinkData(String name)
	{
		return sources.get(name);
	}
	
	private void registerConfigurableHyperlinkSources()
	{
		for(String sourceName : sources.keySet())
		{
			ServiceManager.registerService("gatchan.jedit.hyperlinks.HyperlinkSource",
			  sourceName,
			  "new configurablehyperlinks.ConfigurableHyperlinkSource(\""
			  + sourceName + "\");", // cross fingers for no quotes/backslashes in sourceName
			  getPluginJAR());
		}
	}
	
	private void unregisterConfigurableHyperlinkSources()
	{
		ServiceManager.unloadServices(getPluginJAR());
	}
	
	private void parseHyperlinksXML()
	{
		try
		{
			XMLUtilities.parseXML(new FileInputStream(hyperlinksFile), handler);
			sources = handler.getSources();
		}
		catch(IOException e)
		{
			Log.log(Log.ERROR,this,e);
		}
	}

}