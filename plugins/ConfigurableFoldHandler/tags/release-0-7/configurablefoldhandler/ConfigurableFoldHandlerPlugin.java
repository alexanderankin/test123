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

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JOptionPane;

import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditBus.EBHandler;
import org.gjt.sp.jedit.buffer.FoldHandler;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.Selection;

/**
 * plugin to insert a configurable fold handler into jEdit
 */
public class ConfigurableFoldHandlerPlugin extends EditPlugin
{
	private static final String MANUAL_FOLDS = "tempFolds";

	public static final FoldStrings DEFAULT_FOLD_STRINGS =
		new FoldStrings("{", "}");
		
	private String[] modeNames;
	private HashMap<String, FoldStrings> defaultModeStringsMap =
		new HashMap<String, FoldStrings>();
	
	// default fold strings for modes / buffers that have none specified
	private FoldStrings defFoldStrings;
	
	// store the fold strings for specific buffers and edit modes
	private HashMap<JEditBuffer, FoldStrings> bufferStrings =
		new HashMap<JEditBuffer, FoldStrings>();
	private HashMap<String, FoldStrings> modeStrings =
		new HashMap<String, FoldStrings>();
	
	private static ConfigurableFoldHandlerPlugin instance;
	
	public ConfigurableFoldHandlerPlugin()
	{
		instance = this;
	}

	@EBHandler
	public void handlePropertiesChanged(PropertiesChanged pc)
	{
		readProperties();
	}

	@EBHandler
	public void handleEditPaneUpdate(EditPaneUpdate epu)
	{
		// the only message I can see when a buffer is closed is this one
		// so at this point check if the old buffer has closed
		if (epu.getWhat().equals(EditPaneUpdate.BUFFER_CHANGED))
			checkBuffers();
	}

	private static String getFoldFileFor(Buffer buffer)
	{
		String path = buffer.getPath();
		VFS vfs = VFSManager.getVFSForPath(path);
		return vfs.getParentOfPath(path) + '.' + vfs.getFileName(path) +
			".manualFolds";
	}

	// Loads persistent manual folds for a loaded buffer, and saves
	// persistent manual folds for a closed / saved buffer.
	@EBHandler
	public void handleBufferUpdate(BufferUpdate bu)
	{
		Object what = bu.getWhat();
		Buffer buffer = bu.getBuffer();
		FoldHandler foldHandler = buffer.getFoldHandler();
		/* Clear the configurable fold handler's cached properties
		 * if the mode might have changed. BufferUpdate.PROPERTIES_CHANGED
		 * is always sent when eiter global or buffer options are changed.
		 */
		if ((foldHandler instanceof ConfigurableFoldHandler) &&
			bu.getWhat().equals(BufferUpdate.PROPERTIES_CHANGED))
		{
			((ConfigurableFoldHandler)foldHandler).propertiesChanged();
		}
		String path = getFoldFileFor(bu.getBuffer());
		if (what.equals(BufferUpdate.LOADED))
		{
			File f = new File(path);
			if (! f.exists())
				return;
			ManualFolds mf = getManualFoldsFor(buffer, true);
			if (mf.load(path))
				buffer.invalidateCachedFoldLevels();
		}
		else if (what.equals(BufferUpdate.SAVED) ||
				what.equals(BufferUpdate.CLOSED))
		{
			ManualFolds mf = getManualFoldsFor(buffer, false);
			if (mf != null)
				mf.save(path);
		}
	}

	/**
	 * checks if any of the buffers with their own fold strings have closed and
	 * if so removes references to them from bufferStrings
	 */
	private void checkBuffers()
	{
		Buffer[] buffers = jEdit.getBuffers();
		int i;
		
loop:	for(Iterator<JEditBuffer> iter = bufferStrings.keySet().iterator();
			iter.hasNext(); )
		{
			Buffer curBuffer = (Buffer)iter.next();
			
			for(i = 0; i < buffers.length; i++)
				if(buffers[i] == curBuffer)
					continue loop;
			
			bufferStrings.remove(curBuffer);
		}
	}
	
	public void start()
	{
		super.start();
		
		Mode[] modes = jEdit.getModes();
		modeNames = new String[modes.length];
		
		for(int i = 0; i < modes.length; i++)
		{
			modeNames[i] = modes[i].getName();
		
			String startProp = "configurablefoldhandler.mode." + modeNames[i] +
				".default.startfold";
			
			String endProp = "configurablefoldhandler.mode." + modeNames[i] +
				".default.endfold";
			
			String regexProp = "configurablefoldhandler.mode." + modeNames[i] +
				".default.use-regex";
			
			String modeFoldStart  = jEdit.getProperty(startProp);
			String modeFoldEnd    = jEdit.getProperty(endProp);
			boolean isRegex       = jEdit.getBooleanProperty(regexProp, false);
			
			if(modeFoldStart != null && modeFoldEnd != null)
			{
				defaultModeStringsMap.put(modeNames[i], new FoldStrings(
					modeFoldStart, modeFoldEnd, isRegex));
			}
		}
		readProperties();
		EditBus.addToBus(this);
	}

	public void stop()
	{
		EditBus.removeFromBus(this);
	}

	/**
	 * reads the fold strings from the properties file and sets them 
	 */
	public void readProperties()
	{
		String foldStart = jEdit.getProperty(
			"configurablefoldhandler.startfold",
			DEFAULT_FOLD_STRINGS.getStartString());
		
		String foldEnd   = jEdit.getProperty(
			"configurablefoldhandler.endfold",
			DEFAULT_FOLD_STRINGS.getEndString());
		
		boolean useRegex = jEdit.getBooleanProperty(
			"configurablefoldhandler.use-regex", false);
		
		defFoldStrings = new FoldStrings(foldStart, foldEnd, useRegex);
		
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
				modeStrings.put(modeNames[i], new FoldStrings(modeFoldStart,
					modeFoldEnd, isRegex));
			}
			else
			{
				modeStrings.remove(modeNames[i]);
			}
		}
	}
	
	/**
	 * called when any fold string settings are changed.
	 */
	public void foldStringsChanged()
	{
		readProperties();
		Buffer[] buffers = jEdit.getBuffers();
		
		for(int i = 0; i < buffers.length; i++)
		{
			if(buffers[i].getFoldHandler() instanceof ConfigurableFoldHandler)
			{
				buffers[i].invalidateCachedFoldLevels();
			}
		}
	}
	
	/**
	 * Returns the {@link FoldCounter} for <code>buffer</code>.
	 */
	public FoldCounter getCounter(JEditBuffer buffer, String mode)
	{
		FoldStrings foldStrings = bufferStrings.get(buffer);
		
		if(foldStrings == null)
			foldStrings = modeStrings.get(mode);
		
		if(foldStrings == null)
			foldStrings = defFoldStrings;
		
		if(!foldStrings.doFolding())
			return null;

		return foldStrings.getFoldCounter(); 
	}
	
	/**
	 * required by the buffer fold strings dialog to get an instance of the
	 * plugin
	 */
	public static ConfigurableFoldHandlerPlugin getInstance()
	{
		return instance;
	}
	
	/**
	 * called by the buffer fold strings dialog when the user clicks OK
	 */
	public void setBufferFoldStrings(Buffer buffer, FoldStrings foldStrings)
	{
		if(foldStrings == null)
		{
			bufferStrings.remove(buffer);
		}
		else
		{
			bufferStrings.put(buffer, foldStrings);
		}
		buffer.invalidateCachedFoldLevels();
	}
	
	/**
	 * returns the fold strings for the specified buffer to allow the buffer
	 * fold strings dialog to populate its fields when it's created. if the
	 * buffer doesn't have any fold strings specified then this method returns
	 * null
	 */
	public FoldStrings getBufferFoldStrings(Buffer buffer)
	{
		return bufferStrings.get(buffer);
	}
	
	/**
	 * Returns the {@link FoldStrings} for the specified edit mode. If there are
	 * no strings for the specified mode or the name doesn't correspond to a
	 * valid mode then the default fold strings will be returned
	 *
	 * @param modeName the name of the mode whose fold strings are required
	 * @return the {@link FoldStrings} for the specified mode or
	 * <code>null</code> if none are set for that mode (or if the mode name isn't
	 * valid)
	 */
	public FoldStrings getModeFoldStrings(String modeName)
	{
		return modeStrings.get(modeName);
	}
	
	/**
	 * Returns the default {@link FoldStrings} for the specified edit mode.
	 * These are used by the buffer fold strings dialog and plugin options pane
	 * as suggested strings for buffers / modes with no strings.
	 */
	public FoldStrings getDefaultModeFoldStrings(String modeName)
	{
		return defaultModeStringsMap.get(modeName);
	}
	
	/**
	 * Returns the {@link FoldStrings} used for any <code>Buffer</code> that
	 * doesn't have a specific set of strings specified and whose mode doesn't
	 * either
	 *
	 * @return the {@link FoldStrings} used for any <code>Buffer</code> that
	 * doesn't have a specific set of strings specified and whose mode doesn't
	 * either
	 */
	public FoldStrings getDefaultFoldStrings()
	{
		return defFoldStrings;
	}

	private static ManualFolds getManualFoldsFor(JEditBuffer buffer, boolean create)
	{
		ManualFolds mf = (ManualFolds) buffer.getProperty(MANUAL_FOLDS);
		if (create && mf == null)
		{
			mf = new ManualFolds();
			buffer.setProperty(MANUAL_FOLDS, mf);
		}
		return mf;
	}

	/*
	 * Creates a manual fold for the selected line range.
	 */
	static public void createManualFold(JEditTextArea ta, boolean persistent)
	{
		JEditBuffer buffer = ta.getBuffer();
		if (! (buffer.getFoldHandler() instanceof ConfigurableFoldHandler))
		{
			JOptionPane.showMessageDialog(ta.getView(), jEdit.getProperty(
				"configurablefoldhandler.wrongfoldhandler"));
			return;
		}
		Selection [] sel = ta.getSelection();
		if (sel.length != 1)
			return;
		ManualFolds mf = getManualFoldsFor(buffer, true);
		int start = sel[0].getStartLine();
		int end = sel[0].getEndLine();
		// If the selection ends in the first offset of a line,
		// do not include this line in the fold.
		if (sel[0].getEnd() == buffer.getLineStartOffset(end))
			end--;
		mf.add(start, end, persistent);
		buffer.invalidateCachedFoldLevels();
	}
	/**
	 * Removes the manual fold at the caret position. 
	 */
	static public void removeManualFold(JEditTextArea ta)
	{
		JEditBuffer buffer = ta.getBuffer();
		if (! (buffer.getFoldHandler() instanceof ConfigurableFoldHandler))
		{
			JOptionPane.showMessageDialog(ta.getView(), jEdit.getProperty(
				"configurablefoldhandler.wrongfoldhandler"));
			return;
		}
		ManualFolds mf = getManualFoldsFor(buffer, false);
		if (mf == null)
			return;
		if (mf.remove(ta.getCaretLine()))
		{
			buffer.invalidateCachedFoldLevels();
			if (mf.isEmpty())
			{
				// Save before removing the manual folds object
				if (buffer instanceof Buffer)
					mf.save(getFoldFileFor((Buffer) buffer));
				buffer.setProperty(MANUAL_FOLDS, null);
			}
		}
	}
}
