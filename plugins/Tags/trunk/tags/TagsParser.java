/*
 * TagsParser.java
 * Copyright (c) 2001 Kenrick Drew
 * kdrew@earthlink.net
 *
 * This file is part of TagsPlugin
 *
 * TagsPlugin is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * TagsPlugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package tags; 

import java.io.*;
import java.lang.System.*;
import java.util.*;

import org.gjt.sp.jedit.View;

/**
 * The tag parser interface. 
 *
 * @author Kenrick Drew
 */
public interface TagsParser {
  
  public void reinitialize();
  
  /**
   * Finds the tag line in the tag file and returns it.
   *
   * @param tagFileName The tag file path.
   * @param tagToLookFor The tag to look for in the file.
   * @return Vector of tag lines found in the file, null if it couldn't find it. 
   */
  public boolean findTagLines(String tagFileName, String tagToLookFor);
  public boolean findTagLines(String tagFileName, String tagToLookFor, 
                             View view);
  
  public Vector getTagLines();
  
  /**
   * Returs the number of found tags
   *
   * @return The number of found tags.
   */
  public int getNumberOfFoundTags();
  
  /**
   * Returns the tag definition file name.
   *
   * @return Path to the file with the tag definition found with 
   * <code>findTagLines()</code>.  If tag wasn't found, returns null.
   */
  public String getDefinitionFileName(int index);

  /**
   * Returns the tag defintion search string. 
   *
   * @return Search string to use when looking for the tag defintion in 
   * the tag defintion file.  If tag defintion search string is a number,
   * returns null.  If tag wasn't found, returns null.
   */
  public String getDefinitionSearchString(int index);

  /**
   * Returns the tag defintion line number.
   *
   * @return The line number of the tag definition in the tag definition file.
   * If one should use the search string, <code>-1</code> will be returned.
   * If tag wasn't found, returns null.
   */
  public int getDefinitionLineNumber(int index);

  public String getCollisionChooseString(int index);
  
  public String getTagLine(int index);
  
  public String getTag();
  
  public String toString();
}

