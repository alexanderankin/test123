/*
 * TagFiles.java - part of the Tags plugin.
 *
 * Copyright (C) 2003 Ollie Rutherfurd (oliver@rutherfurd.net)
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
 *
 * $Id$
 */

package tags;

//{{{ imports
import java.util.Vector;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;
//}}}

public class TagFiles
{
  //{{{ private declarations
  private Vector files;
  //}}}

  //{{{ TagFiles constructor
  public TagFiles()
  {
    files = new Vector(15);
    loadTagFiles();
  } //}}}

  //{{{ getTagFiles() method
  private Vector getTagFiles()
  {
    return files;
  } //}}}

  //{{{ loadTagFiles() method
  private void loadTagFiles()
  {
    // if there are any tag index files stored using the old
    // format, load that way, and convert to new format.
    if(jEdit.getProperty("tags-tag-index-file0") != null)
    {
      Log.log(Log.NOTICE, this, "Loading tag index file using old format");

      int i = 0;
      String prop = null;
      while((prop = jEdit.getProperty("tags-tag-index-file" + i))!=null)
      {
        TagFile tf = new TagFile(prop);
        add_(tf);
        jEdit.unsetProperty("tags-tag-index-file" + i);
        jEdit.setProperty("tags.tagfile.path." + i, tf.getPath());
        jEdit.setBooleanProperty("tags.tagfile.enabled." + i, tf.isEnabled());
        Log.log(Log.DEBUG, this,
          "Saved " + "tags-tag-index-file" + i + "(" + prop + ") as " +
          "tags.tagfile.path." + i + "(" + tf.getPath() + ")");  // ##
        i++;
      }
    }
    else
    {
      int i = 0;
      String prop = null;
      while((prop = jEdit.getProperty("tags.tagfile.path." + i))!=null)
      {
        String path = jEdit.getProperty("tags.tagfile.path." + i);
        boolean enabled = jEdit.getBooleanProperty(
            "tags.tagfile.enabled." + i,true);
        TagFile tf = new TagFile(path,enabled);
        add_(tf);
        Log.log(Log.DEBUG, this, "added tag file: " + tf);  // ##
        i++;
      }
      // by default if no buffer tag files have been specified
      // we'll add the default index file for the current buffer
      if(files.size() == 0)
      {
        TagFile tf = new TagFile(Tags.getCurrentBufferTagFilename(),true);
        add_(tf);
        Log.log(Log.DEBUG, this, "added default tag index file: " + tf);  // ##
      }
    }
  } //}}}

  //{{{ save() method
  public void save()
  {
    TagFile tf = null;
    int numTagFiles = files.size();
    int i = 0;
    // save all tag files and unset both old tag file properties
    // and any no longer used tag files
    while (i < numTagFiles
          || jEdit.getProperty("tags-tag-index-file" + i) != null
          || jEdit.getProperty("tags.tagfile.path." + i) != null
    )
    {
      if(i < numTagFiles)
      {
        tf = get(i);
        if(tf != null)
        {
          Log.log(Log.DEBUG, this, "Saving " + tf); // ##
          jEdit.setProperty("tags.tagfile.path." + i,
            tf.getPath());
          jEdit.setBooleanProperty("tags.tagfile.enabled." + i,
            tf.isEnabled());
        }
        else
          Log.log(Log.ERROR, this,
            "tagFiles_(" + i + ") is NULL???");  //##
      }
      else
      {
        jEdit.unsetProperty("tags-tag-index-file" + i);
        jEdit.unsetProperty("tags.tagfile.path." + i);
        jEdit.unsetProperty("tags.tagfile.enabled." + i);
      }
      i++;
    }
    tf = null;
  } //}}}

  //{{{ add_() method
  private void add_(TagFile tf)
  {
    // in case being used by TagsCmdLine.java
    if(Tags.isJEditAvailable())
    {
      tf.currentDirIndexFile = tf.getPath().equals(
        Tags.getCurrentBufferTagFilename());
    }
    files.addElement(tf);
  } //}}}

  //{{{ add(TagFile) method
  public void add(TagFile tf)
  {
    add_(tf);

  }//}}}

  //{{{ remove() method
  public TagFile remove(int index) {
    // XXX might get an error
    TagFile tf = (TagFile)files.elementAt(index);
    files.removeElementAt(index);
    return tf;
  } //}}}

  //{{{ get() method
  public TagFile get(int index)
  {
    return (TagFile)getTagFiles().elementAt(index);
  } //}}}

  //{{{ size() method
  public int size()
  {
    return getTagFiles().size();
  } //}}}

  //{{{ getTagFileName() method
  public String getName(int index)
  {
    return get(index).getPath();
  } //}}}

  //{{{ clear() method
  public void clear()
  {
    files.removeAllElements();
  } //}}}

}

// :collapseFolds=1:noTabs=true:lineSeparator=\r\n:tabSize=2:indentSize=2:deepIndent=false:folding=explicit:
