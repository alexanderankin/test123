/*
 * OpenIt jEdit Plugin (OpenItProperties.java) 
 *  
 * Copyright (C) 2003 Matt Etheridge (matt@etheridge.org)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
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

package org.etheridge.openit;

public interface OpenItProperties
{
  // stores the full source path string
  public static final String SOURCE_PATH_STRING = "OpenIt.SourcePathString";
  
  // polling property
  public static final String SOURCE_PATH_POLLING_INTERVAL = "OpenIt.SourcePathPollingInterval";
  
  // clean popup when popup visible?
  public static final String POP_UP_CLEAN_ON_VISIBLE = "OpenIt.CleanPopupOnVisible";
  
  // case sensitive file name matching?
  public static final String POP_UP_CASE_SENSITIVE_FILE_MATCHING = "OpenIt.CaseSensitiveFileMatching";
  
  // persisted filter string
  public static final String POP_UP_FILTER_STRING = "OpenIt.PopupFilterString";
  
  // filter applied?
  public static final String POP_UP_FILTER_APPLIED = "OpenIt.PopupFilterApplied";
  
  //
  // Display Properties
  //
  
  // should packages be displayed?
  public static final String DISPLAY_PACKAGES = "OpenIt.Display.DisplayPackages";
  
  // should directories be displayed?
  public static final String DISPLAY_DIRECTORIES = "OpenIt.Display.DisplayDirectories";
  
  // should file extensions be displayed?
  public static final String DISPLAY_EXTENSIONS = "OpenIt.Display.DisplayExtensions";
  
  // should icons be displayed?
  public static final String DISPLAY_ICONS = "OpenIt.Display.DisplayIcons";
  
  // should the file size be displayed?
  public static final String DISPLAY_SIZE = "OpenIt.Display.DisplaySize";
  
}
