/*
* PVListener.java
* Copyright (c) Thu Jun 22 14:02:09 MSD 2006 Denis Koryavov
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

package org.etheridge.openit;

import org.gjt.sp.jedit.jEdit;

import projectviewer.event.ProjectViewerAdapter;
import projectviewer.event.ProjectViewerEvent;

public class PVListener extends ProjectViewerAdapter {
        public void projectLoaded(ProjectViewerEvent evt) {
                refresh(evt.getProject().getRootPath());
        }
        
        public void projectRemoved(ProjectViewerEvent evt) {
                refresh("");
        }
        
        public void groupActivated(ProjectViewerEvent evt) {
                refresh("");
        }
        
        
        
        private void refresh(String sourcePath) {
                if (jEdit.getBooleanProperty(OpenItProperties.EXPORT_PATH_FROM_PROJECT_VIEWER)) { 
                        jEdit.setProperty(OpenItProperties.SOURCE_PATH_STRING, sourcePath);
                        SourcePathManager.getInstance().refreshSourcePath();
                }
        }
        
        
        
        
        
        
} 
