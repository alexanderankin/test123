/*
 * AcceleratorManager.java
 * Copyright (C) 2002 Calvin Yu
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
package templates;

import java.io.*;
import java.util.*;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.Selection;
import org.gjt.sp.util.Log;
import gnu.regexp.*;

/**
 * Manages keyword expansion of templates.
 *
 *@author Calvin Yu
 */
public class AcceleratorManager
{

   public final static String KEYWORD_MAPPINGS_FILE = "accelerator-mappings";

   private static AcceleratorManager instance;
   
   private static final String mappingRE = "(.*)\\.(.*)=(.*)";
   private static RE acceleratorFilter = null;

   /**
    * The mappings member contains a HashMap for each mode that has at least  
	* one defined accelerator. Each of these HashMaps maps the accelerator 
	* name to the path of the template file, relative to the templates 
	* directory.
	*/
   private Map mappings;
   
   /**
    * The dirtyFlag member is used to determine whether the accelerator 
	* mappings have been updated, and need to be saved to disk.
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
	*/
   public void setDirtyFlag(boolean isDirty) {
	   dirtyFlag = isDirty;
   }
   
   /**
    * Returns the state of the dirtyFlag.
	*/
   public boolean isDirty() {
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
    */
   public String findTemplatePath(String mode, String accelerator)
   {
      Map modeMap = (Map) mappings.get(mode);
      if (modeMap == null) {
         return null;
      }
      return (String) modeMap.get(accelerator);
   }

   /**
    * Returns a collection of accelerator for the given mode.
    */
   public Collection getAccelerators(String modeName)
   {
      Map modeMap = (Map) mappings.get(modeName);
      if (modeMap == null) {
         return Collections.EMPTY_LIST;
      } else {
         return modeMap.keySet();
      }
   }

   /**
    * Add a mapping.
    */
   public void addAccelerator(String mode, String accelerator, String templatePath)
   {
      if (mode == null) {
         throw new IllegalArgumentException("Mode cannot be null");
      }
      Map modeMap = (Map) mappings.get(mode);
      if (modeMap == null) {
         modeMap = new HashMap();
         mappings.put(mode, modeMap);
      }
      modeMap.put(accelerator, templatePath);
	  setDirtyFlag(true);
   }

   /**
    * Remove a acceleator mapping.
    */
   public void removeAccelerator(String mode, String accelerator)
   {
      Map modeMap = (Map) mappings.get(mode);
      if (modeMap == null) {
         return;
      }
      modeMap.remove(accelerator);
	  setDirtyFlag(true);
   }

   /**
    * Expand the accelerator at the current caret position.
    */
   public static void expandAccelerator(JEditTextArea textArea)
   {
      textArea.selectNone();
      String c = "A";
      int idx = textArea.getCaretPosition() - 1;
      while (!Character.isWhitespace(c.charAt(0)) && idx >= 0) {
         c = textArea.getText(idx--, 1);
      }
      if (idx >= 0) {
         idx += 2;
      }
      Selection sel = new Selection.Range(idx, textArea.getCaretPosition());
      textArea.setSelection(sel);
      String accelerator = textArea.getSelectedText();
      String path = getInstance()
         .findTemplatePath(textArea.getBuffer().getMode().getName(), accelerator);
      if (path == null) {
         GUIUtilities.error(textArea, 
		 		"plugin.TemplatesPlugin.error.no-accelerator-found",
                new String[] {path});
      } else {
         textArea.getBuffer().remove(sel.getStart(), sel.getEnd() - sel.getStart());
         ((TemplatesPlugin) jEdit.getPlugin("templates.TemplatesPlugin"))
            .processTemplate(path, textArea);
      }
   }

   /**
    * Returns an instance of <code>AcceleratorManager</code>.
    */
   public static AcceleratorManager getInstance()
   {
      if (instance == null) {
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
      if (!mappingsFile.exists()) {
         return;
      }
	  if (acceleratorFilter == null) {
      	try {
      		acceleratorFilter = new RE(mappingRE,RE.REG_ICASE);
      	} catch (gnu.regexp.REException e) { }		// this shouldn't happen
	  }
	  try {
         in = new BufferedReader(new FileReader(mappingsFile));
		 String line = null;
		 String modeName = null;
		 String acceleratorName = null;
		 String acceleratorPath = null;
		 while ((line = in.readLine()) != null) {
			REMatch acceleratorMatch = acceleratorFilter.getMatch(line);
		 	if (acceleratorMatch != null) {
		 		modeName = acceleratorMatch.toString(1);
				acceleratorName = acceleratorMatch.toString(2);
				acceleratorPath = acceleratorMatch.toString(3);
				addAccelerator(modeName, acceleratorName, acceleratorPath);
		 	}
		 }
		 setDirtyFlag(false);
	  } catch (IOException e) {
         Log.log(Log.ERROR, this, e);
      } finally {
		 try {
         	in.close();
		 } catch (IOException ioe) {
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
	  if (!isDirty()) {
		  return;
	  }
	  BufferedWriter out = null; 
      try {
         String filePath = getMappingsFilePath();
         out = new BufferedWriter(new FileWriter(filePath));
		 for (Iterator i = mappings.entrySet().iterator(); i.hasNext();) {
            Map.Entry modeEntry = (Map.Entry) i.next();
            String modeName = modeEntry.getKey().toString();
            Map modeMap = (Map) modeEntry.getValue();
            for (Iterator j = modeMap.entrySet().iterator(); j.hasNext();) {
               Map.Entry acceleratorEntry = (Map.Entry) j.next();
               out.write(modeName + "." 
			   		+ acceleratorEntry.getKey().toString()
					+ "="
					+ acceleratorEntry.getValue().toString());
               out.newLine();
            }
         }
		 setDirtyFlag(false);
      } catch (IOException e) {
         Log.log(Log.ERROR, this, e);
      } finally {
		 try {
         	out.close();
		 } catch (IOException ioe) {
			 Log.log(Log.ERROR, this, ioe);
		 }
      }
   }

   /**
    * Returns the path of the mappings file.
    */
   private static String getMappingsFilePath()
   {
      return MiscUtilities.constructPath(
         TemplatesPlugin.getVelocityDirectory(), KEYWORD_MAPPINGS_FILE);
   }

}
