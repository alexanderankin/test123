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

/**
 * Manages keyword expansion of templates.
 *
 *@author Calvin Yu
 */
public class AcceleratorManager
{

   public final static String KEYWORD_MAPPINGS_FILE = "accelerator-mappings";

   private static AcceleratorManager instance;

   private Map mappings;

   /**
    * Create a new <code>AcceleratorManager</code>.
    */
   public AcceleratorManager()
   {
      mappings = new HashMap();
      loadAcceleratorMappings();
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
      DataInputStream in = null;
      File mappingsFile = new File(getMappingsFilePath());
      if (!mappingsFile.exists()) {
         return;
      }
      try {
         in = new DataInputStream(new FileInputStream(mappingsFile));
         int modeCount = in.readInt();
         for (int i=0; i<modeCount; i++) {
            String mode = in.readUTF();
            int acceleratorCount = in.readInt();
            Map modeMap = (Map) mappings.get(mode);
            if (modeMap == null) {
               modeMap = new HashMap(acceleratorCount);
               mappings.put(mode, modeMap);
            }
            for (int j=0; j<acceleratorCount; j++) {
               String accelerator = in.readUTF();
               String templatePath = in.readUTF();
               modeMap.put(accelerator, templatePath);
            }
         }
      } catch (IOException e) {
         Log.log(Log.ERROR, this, e);
      } finally {
         IO.close(in);
      }
   }

   /**
    * Save keyword mappings.
    */
   private void saveAcceleratorMappings()
   {
      DataOutputStream out = null;
      try {
         String filePath = getMappingsFilePath();
         out = new DataOutputStream(new FileOutputStream(filePath));
         out.writeInt(mappings.size());
         for (Iterator i = mappings.entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            out.writeUTF(entry.getKey().toString());
            Map modeMap = (Map) entry.getValue();
            out.writeInt(modeMap.size());
            for (Iterator j = modeMap.entrySet().iterator(); j.hasNext();) {
               Map.Entry insertEntry = (Map.Entry) j.next();
               out.writeUTF(insertEntry.getKey().toString());
               out.writeUTF(insertEntry.getValue().toString());
            }
         }
      } catch (IOException e) {
         Log.log(Log.ERROR, this, e);
      } finally {
         IO.close(out);
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
