package configurablefoldhandler;

/*
 * ConfigurableFoldHandlerPlugin.java
 *
 * Copyright (c) 2002 C.J.Kent
 *
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=custom:collapseFolds=0:
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

import java.util.Vector;
import java.util.Hashtable;
import javax.swing.text.Segment;
import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.gui.OptionsDialog;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.buffer.FoldHandler;

/**
 * plugin to insert a configurable fold handler into jEdit
 */
public class ConfigurableFoldHandlerPlugin extends EBPlugin
{
	private ConfigurableFoldHandler foldHandler;
	private String[] modeNames;
	
	private static ConfigurableFoldHandlerPlugin instance;
	
	public ConfigurableFoldHandlerPlugin()
	{
		instance = this;
	}
	
	public void handleMessage(EBMessage msg)
	{
		if(msg instanceof PropertiesChanged)
			readProperties();
		else if(msg instanceof EditPaneUpdate)
		{
			Object what = ((EditPaneUpdate)msg).getWhat();
			
			// the only message I can see when a buffer is closed is this one
			// so at this point check if the old buffer has closed
			if(what.equals(EditPaneUpdate.BUFFER_CHANGED))
				foldHandler.checkBuffers();
		}
	}
	
	public void start()
	{
		super.start();
		foldHandler = new ConfigurableFoldHandler();
		
		Mode[] modes = jEdit.getModes();
		modeNames = new String[modes.length];
		
		for(int i = 0; i < modes.length; i++)
			modeNames[i] = modes[i].getName();
		
		// will a message arrive or do I need to do this here?
		readProperties();
		
		foldHandler.registerFoldHandler(foldHandler);
	}
	
	/**
	 * reads the fold strings from the properties file and sets them 
	 */
	private void readProperties()
	{
		String foldStart = jEdit.getProperty(
			"configurablefoldhandler.startfold",
			ConfigurableFoldHandler.DEFAULT_FOLD_STRINGS.getStartString());
		
		String foldEnd   = jEdit.getProperty(
			"configurablefoldhandler.endfold",
			ConfigurableFoldHandler.DEFAULT_FOLD_STRINGS.getEndString());
		
		boolean useRegex = jEdit.getBooleanProperty(
			"configurablefoldhandler.use-regex", false);
		
		foldHandler.setDefaultFoldStrings(new FoldStrings(foldStart, foldEnd,
			useRegex));
		
		String startProp;
		String endProp;
		String regexProp;
		String modeFoldStart;
		String modeFoldEnd;
		boolean isRegex;
		
		for(int i = 0; i < modeNames.length; i++)
		{
			startProp = "configurablefoldhandler.mode." + modeNames[i] +
				".startfold";
			
			endProp = "configurablefoldhandler.mode." + modeNames[i] +
				".endfold";
			
			regexProp = "configurablefoldhandler.mode." + modeNames[i] +
				".use-regex";
			
			modeFoldStart = jEdit.getProperty(startProp);
			modeFoldEnd   = jEdit.getProperty(endProp);
			isRegex       = jEdit.getBooleanProperty(regexProp, false);
			
			if(modeFoldStart != null && modeFoldEnd != null)
			{
				foldHandler.setModeFoldStrings(modeNames[i],
					new FoldStrings(modeFoldStart, modeFoldEnd, isRegex));
			}
		}
	}
	
	public void createOptionPanes(OptionsDialog dialog)
	{
		dialog.addOptionPane(new ConfigurableFoldHandlerOptionsPane(this));
	}
	
	/**
	 * called when any fold string settings are changed. due to the way that the
	 * buffer recalculates folds it is necessary to unregister the current fold
	 * handler, create a new fold handler and register it. otherwise there's no
	 * way to get the buffer to recalculate its folds
	 */
	void foldStringsChanged()
	{
		ConfigurableFoldHandler oldFoldHandler = foldHandler;
		foldHandler = new ConfigurableFoldHandler(
			oldFoldHandler.getAllBufferFoldStrings());
		readProperties();
		FoldHandler.unregisterFoldHandler(oldFoldHandler);
		FoldHandler.registerFoldHandler(foldHandler);
	}
	
	public void createMenuItems(Vector menuItems)
	{
		menuItems.addElement(GUIUtilities.loadMenu(
			"configurablefoldhandler.menu"));
	}
	
	/**
	 * required by the buffer fold strings dialog to get an instance of the
	 * plugin
	 */
	static ConfigurableFoldHandlerPlugin getInstance()
	{
		return instance;
	}
	
	/**
	 * called by the buffer fold strings dialog when the user clicks OK
	 */
	public void setBufferFoldStrings(Buffer buffer, FoldStrings foldStrings)
	{
		ConfigurableFoldHandler oldFoldHandler = foldHandler;
		foldHandler = new ConfigurableFoldHandler(oldFoldHandler);
		
		foldHandler.setBufferFoldStrings(buffer, foldStrings);
		
		FoldHandler.unregisterFoldHandler(oldFoldHandler);
		FoldHandler.registerFoldHandler(foldHandler);
		buffer.propertiesChanged();
	}
	
	/**
	 * returns the fold strings for the specified buffer to allow the buffer
	 * fold strings dialog to populate its fields when it's created. if the
	 * buffer doesn't have any fold strings specified then this method returns
	 * null
	 */
	public FoldStrings getBufferFoldStrings(Buffer buffer)
	{
		return foldHandler.getBufferFoldStrings(buffer);
	}
	
	public FoldStrings getModeFoldStrings(String modeName)
	{
		return foldHandler.getModeFoldStrings(modeName);
	}
	
	public FoldStrings getDefaultFoldStrings()
	{
		return foldHandler.getDefaultFoldStrings();
	}
}
