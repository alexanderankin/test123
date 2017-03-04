/*
 * HyperlinksPlugin.java - The Hyperlinks plugin
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2007, 2009 Matthieu Casanova
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
package gatchan.jedit.hyperlinks;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.jedit.textarea.JEditTextArea;

import java.io.*;
import org.gjt.sp.util.XMLUtilities;
import org.gjt.sp.util.Log;
import java.util.*;
import gatchan.jedit.hyperlinks.configurable.*;

/**
 * @author Matthieu Casanova
 * @version $Id: HighlightPlugin.java,v 1.20 2006/06/21 09:40:32 kpouer Exp $
 */
public class HyperlinksPlugin extends EditPlugin
{
	// members for configurable hyperlinks
	private HashMap<String, ArrayList<ConfigurableHyperlinkData>> sources;
	private ConfigurableHyperlinksHandler handler;
	private File hyperlinksFile;
	
	@Override
    public void start()
	{
		EditBus.addToBus(this);
		View view = jEdit.getFirstView();
		while (view != null)
		{
			EditPane[] panes = view.getEditPanes();
			for (int i = 0; i < panes.length; i++)
			{
				JEditTextArea textArea = panes[i].getTextArea();
				initTextArea(textArea);
			}
			view = view.getNext();
		}
		
		// initialize configurable hyperlinks
		handler = new ConfigurableHyperlinksHandler();
		hyperlinksFile = new File(getPluginHome(), "hyperlinks.xml");
		if(!hyperlinksFile.exists())
		{
			try 
			{
				// create empty template if hyperlinks.xml doesn't exist
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
        EditBus.removeFromBus(this);
		View view = jEdit.getFirstView();
		while (view != null)
		{
			EditPane[] panes = view.getEditPanes();
			for (int i = 0; i < panes.length; i++)
			{
				JEditTextArea textArea = panes[i].getTextArea();
				uninitTextArea(textArea);
			}
			view = view.getNext();
		}
	}

	private static void uninitTextArea(JEditTextArea textArea)
	{
		HyperlinkManager clientProperty = (HyperlinkManager) textArea.getClientProperty(HyperlinkManager.class);
		clientProperty.dispose();
		textArea.putClientProperty(HyperlinkManager.class, null);
	}

	private static void initTextArea(JEditTextArea textArea)
	{
		textArea.putClientProperty(HyperlinkManager.class, new HyperlinkManager(textArea));
	}

    @EditBus.EBHandler
	public void handlePropertiesChanged(PropertiesChanged message)
	{
        HyperlinkTextAreaPainter.color = jEdit.getColorProperty("options.hyperlink.color.value");
    }

    @EditBus.EBHandler
	public void handleEditPaneMessage(EditPaneUpdate message)
	{
		JEditTextArea textArea = message.getEditPane().getTextArea();
		Object what = message.getWhat();

		if (what == EditPaneUpdate.CREATED)
		{
			initTextArea(textArea);
		}
		else if (what == EditPaneUpdate.DESTROYED)
		{
			uninitTextArea(textArea);
		}
	}
	
	// methods for configurable hyperlinks
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
			  "new gatchan.jedit.hyperlinks.configurable.ConfigurableHyperlinkSource(\""
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