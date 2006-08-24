/*
 * OpenIt jEdit Plugin (FileSelectionListener.java) 
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
 
package org.etheridge.openit.gui;

import org.etheridge.openit.sourcepath.SourcePathFile;

/**
 * Any classes that wish to receive notification of a file being
 * selected from an FindFileWindow should implement this interface and 
 * register itself with the FindFileWindow.
 */
public interface FileSelectionListener
{
  /**
   * Callback method that will be called when a file has been
   * selected from a FindFileWindow.
   *
   * @param sourcePathFileSelected the SourcePathFile that was selected.
   */
  public void fileSelected(SourcePathFile sourcePathFileSelected);
}
