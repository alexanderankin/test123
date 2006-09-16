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
  
  // the last opened file chooser directory
  public static final String LAST_OPENED_FILE_CHOOSER_DIRECTORY = "OpenIt.LastOpenedFileChooserDirectory";
  
  // the excludes regular expression string
  public static final String EXCLUDES_REGULAR_EXPRESSION = "OpenIt.ExcludesRegularExpression";
  
  // ignore case for excludes files regular expression? 
  public static final String IGNORE_CASE_EXCLUDES_FILE_REGULAR_EXPRESSION = "OpenIt.IgnoreCaseExcludeFilesRegularExpression";
  
  // the excludes directories regular expression string
  public static final String EXCLUDES_DIRECTORIES_REGULAR_EXPRESSION = "OpenIt.ExcludesDirectoriesRegularExpression";
  
  // ignore case for excludes directories regular expression? 
  public static final String IGNORE_CASE_EXCLUDES_DIRECTORIES_REGULAR_EXPRESSION = "OpenIt.IgnoreCaseExcludeDirectoriesRegularExpression";
  
  // polling property
  public static final String SOURCE_PATH_POLLING_INTERVAL = "OpenIt.SourcePathPollingInterval";
  
  // clean popup when popup visible?
  public static final String POP_UP_CLEAN_ON_VISIBLE = "OpenIt.CleanPopupOnVisible";
  
  // case sensitive file name matching?
  public static final String POP_UP_CASE_SENSITIVE_FILE_MATCHING = "OpenIt.CaseSensitiveFileMatching";
  
  // filter applied?
  public static final String POP_UP_FILTER_APPLIED = "OpenIt.PopupFilterApplied";
  
  //
  // Display Properties
  //
  
  public static String IMPORT_FILES_FROM_CURRENT_PROJECT = "OpenIt.ExportPathFromProjectViewer";
  
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
  
  // should packages be displayed?
  public static final String PATHS_IN_JAVA_STYLE = "OpenIt.Display.JavaStylePaths";
  
}
