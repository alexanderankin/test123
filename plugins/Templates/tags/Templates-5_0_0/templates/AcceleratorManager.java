/*
 *  AcceleratorManager.java
 *  :folding=explicit:collapseFolds=1:
 *  Copyright (C) 2002 Calvin Yu, Steve Jakob
 *  Copyright (C) 2008 Steve Jakob
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package templates;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import org.gjt.sp.jedit.Abbrevs;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.TextUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.Selection;
import org.gjt.sp.util.Log;

/**
 * Manages keyword expansion of templates.
 *
 * @author   Calvin Yu
 */
public class AcceleratorManager
{

	public final static String KEYWORD_MAPPINGS_FILE = "accelerator-mappings";

	private static AcceleratorManager instance;

	private final static String mappingRE = "(.*)\\.(.*)=(.*)";
	private static Pattern acceleratorPattern;

	/**
	 * The mappings member contains a HashMap for each mode that has at least one
	 * defined accelerator. Each of these HashMaps maps the accelerator name to the
	 * path of the template file, relative to the templates directory.
	 */
	private Map mappings;

	/**
	 * The dirtyFlag member is used to determine whether the accelerator mappings
	 * have been updated, and need to be saved to disk.
	 */
	private boolean dirtyFlag = false;

	/**
	 * Create a new <code>AcceleratorManager</code>.
	 */
	public AcceleratorManager()
	{
		mappings = new HashMap();
		loadAcceleratorMappings();
	}

	/**
	 * Set the dirtyFlag to the desired value.
	 *
	 * @param isDirty  The new dirtyFlag value
	 */
	public void setDirtyFlag(boolean isDirty)
	{
		dirtyFlag = isDirty;
	}

	/**
	 * Returns the state of the dirtyFlag.
	 *
	 * @return   The dirty value
	 */
	public boolean isDirty()
	{
		return dirtyFlag;
	}

	/**
	 * Saves all accelerators.
	 */
	public void save()
	{
		saveAcceleratorMappings();
	}

	/**
	 * Find the template for the given accelerator and mode.
	 *
	 * @return             The path for the corresponding template
	 * @param mode         The edit mode for which the accelerator is valid
	 * @param accelerator  The accelerator for the desired template
	 */
	public String findTemplatePath(String mode, String accelerator)
	{
		Map modeMap = (Map) mappings.get(mode);
		if (modeMap == null)
		{
			return null;
		}
		return (String) modeMap.get(accelerator);
	}

	/**
	 * Returns a collection of accelerators for the given mode.
	 *
	 * @return          A collection of accelerator names
	 * @param modeName  The edit mode for the desired accelerators
	 */
	public Collection getAccelerators(String modeName)
	{
		Map modeMap = (Map) mappings.get(modeName);
		if (modeMap == null)
		{
			return Collections.EMPTY_LIST;
		} else
		{
			return modeMap.keySet();
		}
	}

	/**
	 * Add a mapping of accelerator and mode to template path.
	 *
	 * @param mode          The edit mode for which the accelerator is valid
	 * @param accelerator   The accelerator to associate with the template
	 * @param templatePath  The template to associate with the given accelerator
	 *   in the given mode.
	 */
	public void addAccelerator(String mode, String accelerator, String templatePath)
	{
		if (mode == null)
		{
			throw new IllegalArgumentException("Mode cannot be null");
		}
		Map modeMap = (Map) mappings.get(mode);
		if (modeMap == null)
		{
			modeMap = new HashMap();
			mappings.put(mode, modeMap);
		}
		modeMap.put(accelerator, templatePath);
		setDirtyFlag(true);
	}

	/**
	 * Remove a acceleator mapping.
	 *
	 * @param mode         The edit mode for which the accelerator is valid
	 * @param accelerator  The accelerator mapping to be removed
	 */
	public void removeAccelerator(String mode, String accelerator)
	{
		Map modeMap = (Map) mappings.get(mode);
		if (modeMap == null)
		{
			return;
		}
		modeMap.remove(accelerator);
		setDirtyFlag(true);
	}

	/**
	 * Expand the accelerator at the current caret position.
	 *
	 * @param textArea  The jEdit TextArea into which the template text is to 
	 *    be written.
	 */
	public static void expandAccelerator(JEditTextArea textArea)
	{
		//{{{ Report error if textArea is read-only
		if (!textArea.isEditable())
		{
			GUIUtilities.error(textArea,
					"plugin.TemplatesPlugin.error.buffer-read-only", null);
			return;
		}
		//}}}

		String accelerator = parseAccelerator(textArea);
		if ("".equals(accelerator))
		{
			return;
		}

		//{{{ Select the accelerator in the text area
		textArea.selectNone();
		Selection sel = new Selection.Range(
				textArea.getCaretPosition() - accelerator.length(),
				textArea.getCaretPosition());
		textArea.setSelection(sel);
		//}}}

		//{{{ Process the accelerator
		String path = getInstance()
				.findTemplatePath(((Buffer) textArea.getBuffer()).getMode().getName(), accelerator);
		if (path == null)
		{
			System.out.println("AcceleratorManager: accelerator path is null");
			// Not a template accelerator. If Templates plugin is set up to do so,
			// try to process the text as an abbreviation
			if (TemplatesPlugin.getAcceleratorPassThruFlag())
			{
				if (!Abbrevs.expandAbbrev(GUIUtilities.getView(textArea), false))
				{
					GUIUtilities.error(textArea,
							"plugin.TemplatesPlugin.error.no-accelerator-found",
							new String[]{accelerator});
				}
			} else
			{
				GUIUtilities.error(textArea,
						"plugin.TemplatesPlugin.error.no-accelerator-found",
						new String[]{accelerator});
			}
		} else
		{
			System.out.println("AcceleratorManager: accelerator path is " + path);
			textArea.getBuffer().remove(sel.getStart(), sel.getEnd() - sel.getStart());
			((TemplatesPlugin) jEdit.getPlugin("templates.TemplatesPlugin"))
					.processTemplate(path, textArea);
		}
		//}}}
	}

	/**
	 * Get the accelerator at the current caret position. Much of this method was
	 * borrowed from the org.gjt.sp.jedit.Abbrevs class.
	 *
	 * @return          A string containing the accelerator, if found. Otherwise,
	 *      an empty string.
	 * @param textArea  The current text area.
	 */
	private static String parseAccelerator(JEditTextArea textArea)
	{
		//{{{ Make sure there's text to be parsed

		// If the line is blank, there can't be an accelerator present.
		int line = textArea.getCaretLine();
		int lineStart = textArea.getLineStartOffset(line);
		int caret = textArea.getCaretPosition();
		String lineText = textArea.getLineText(line);
		if (lineText.length() == 0)
		{
			GUIUtilities.error(textArea,
					"plugin.TemplatesPlugin.error.no-accelerator-found",
					new String[]{""});
			return "";
		}

		// If the cursor is at the head of the line, there's no accelerator.
		int pos = caret - lineStart;
		if (pos == 0)
		{
			GUIUtilities.error(textArea,
					"plugin.TemplatesPlugin.error.no-accelerator-found",
					new String[]{""});
			return "";
		}
		//}}}

		// Parse the accelerator
		int wordStart = TextUtilities.findWordStart(lineText, pos - 1,
				textArea.getBuffer().getStringProperty("noWordSep"));
		String accelerator = lineText.substring(wordStart, pos);

		return accelerator;
	}

	/**
	 * Returns an instance of <code>AcceleratorManager</code>.
	 *
	 * @return   The <code>AcceleratorManager</code> instance
	 */
	public static AcceleratorManager getInstance()
	{
		if (instance == null)
		{
			instance = new AcceleratorManager();
		}
		return instance;
	}

	/**
	 * Load keyword mappings.
	 */
	private void loadAcceleratorMappings()
	{
		BufferedReader in = null;
		File mappingsFile = new File(getMappingsFilePath());
		if (!mappingsFile.exists())
		{
			return;
		}
		if (acceleratorPattern == null)
		{
			// Create regex pattern for matching accelerators
			try
			{
				acceleratorPattern = Pattern.compile(mappingRE, 
						Pattern.CASE_INSENSITIVE);
			}
			catch (PatternSyntaxException pe)
			{
				// This won't happen if the programmer did his job right.
				Log.log(Log.ERROR, this, 
					"PatternSyntaxException in accelerator RE: " + mappingRE);
			}
			catch (IllegalArgumentException iae)
			{
				// This won't happen if the programmer did his job right.
				Log.log(Log.ERROR, this, 
					"IllegalArgumentException in accelerator RE: " + mappingRE);
			}
		}
		try
		{
			in = new BufferedReader(new FileReader(mappingsFile));
			String line = null;
			String modeName = null;
			String acceleratorName = null;
			String acceleratorPath = null;
			while ((line = in.readLine()) != null)
			{
				Matcher m = acceleratorPattern.matcher(line);
				if (m.matches())
				{
					modeName = m.group(1);
					acceleratorName = m.group(2);
					acceleratorPath = m.group(3);
					addAccelerator(modeName, acceleratorName, acceleratorPath);
				}
			}
			setDirtyFlag(false);
		}
		catch (IOException e)
		{
			Log.log(Log.ERROR, this, e);
		}
		finally
		{
			try
			{
				in.close();
			}
			catch (IOException ioe)
			{
				Log.log(Log.ERROR, this, ioe);
			}
		}
	}

	/**
	 * Save keyword mappings.
	 */
	private void saveAcceleratorMappings()
	{
		// No point in saving the accelerator mappings if nothing has changed
		if (!isDirty())
		{
			return;
		}
		BufferedWriter out = null;
		try
		{
			String filePath = getMappingsFilePath();
			out = new BufferedWriter(new FileWriter(filePath));
			for (Iterator i = mappings.entrySet().iterator(); i.hasNext(); )
			{
				Map.Entry modeEntry = (Map.Entry) i.next();
				String modeName = modeEntry.getKey().toString();
				Map modeMap = (Map) modeEntry.getValue();
				for (Iterator j = modeMap.entrySet().iterator(); j.hasNext(); )
				{
					Map.Entry acceleratorEntry = (Map.Entry) j.next();
					out.write(modeName + "."
							 + acceleratorEntry.getKey().toString()
							 + "="
							 + acceleratorEntry.getValue().toString());
					out.newLine();
				}
			}
			setDirtyFlag(false);
		}
		catch (IOException e)
		{
			Log.log(Log.ERROR, this, e);
		}
		finally
		{
			try
			{
				out.close();
			}
			catch (IOException ioe)
			{
				Log.log(Log.ERROR, this, ioe);
			}
		}
	}

	/**
	 * Returns the path of the accelerator mappings file.
	 *
	 * @return   The path of the accelerator mappings file.
	 */
	private static String getMappingsFilePath()
	{
		return MiscUtilities.constructPath(
				TemplatesPlugin.getVelocityDirectory(), KEYWORD_MAPPINGS_FILE);
	}

}

