/*
 * PVListener.java
 * Copyright (c) Thu Jun 22 MSD 2006 Denis Koryavov
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

import javax.swing.SwingUtilities;

import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.jEdit;

import projectviewer.event.StructureUpdate;
import projectviewer.event.ViewerUpdate;
import projectviewer.vpt.VPTNode;

public class PVListener implements EBComponent {

        public void projectLoaded(VPTNode node) {
                refresh(node.getNodePath());
        }
        
        public void projectRemoved(VPTNode node) {
                refresh("");
        }
        
        public void groupActivated(VPTNode node) {
                refresh("");
        }
        
        private void refresh(final String sourcePath) {
                if (jEdit.getBooleanProperty(OpenItProperties.IMPORT_FILES_FROM_CURRENT_PROJECT)) {
                        SwingUtilities.invokeLater(new Runnable() {
                                        public void run() {
                                                jEdit.setProperty(OpenItProperties.SOURCE_PATH_STRING, sourcePath);
                                                SourcePathManager.getInstance().refreshSourcePath();
                                        }
                        });
                }
        }
    	public void handleMessage(EBMessage message) {
    		if (message instanceof StructureUpdate) {
    			StructureUpdate su = (StructureUpdate) message;
    			if (su.getType() == StructureUpdate.Type.PROJECT_ADDED)
    				projectLoaded(su.getNode());
    			else if (su.getType() == StructureUpdate.Type.PROJECT_REMOVED)
    				projectRemoved(su.getNode());
    		} else if (message instanceof ViewerUpdate) {
    			ViewerUpdate vu = (ViewerUpdate) message;
    			if (vu.getType() == ViewerUpdate.Type.GROUP_ACTIVATED)
    				groupActivated(vu.getNode());
    			else if (vu.getType() == ViewerUpdate.Type.PROJECT_LOADED)
    				projectLoaded(vu.getNode());
    		}
    	}
    	
    	public void start() {
    		EditBus.addToBus(this);
    	}
    	public void stop() {
    		EditBus.removeFromBus(this);
    	}
} 
