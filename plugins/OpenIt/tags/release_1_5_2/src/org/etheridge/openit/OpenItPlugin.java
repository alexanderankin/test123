/*
 * OpenIt jEdit Plugin (OpenItPlugin.java) 
 *  
 * Copyright (C) 2003 Matt Etheridge (matt@etheridge.org)
 * Copyright (C) 2006 Denis Koryavov
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


import java.io.File;

import java.util.HashMap;
import java.util.Map;

import org.etheridge.openit.gui.FileSelectionListener;
import org.etheridge.openit.gui.FindFileWindow;
import org.etheridge.openit.OpenItProperties;
import org.etheridge.openit.sourcepath.SourcePathFile;
import org.etheridge.openit.SourcePathManager;

import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.msg.ViewUpdate;
import org.gjt.sp.jedit.View;

/**
 * @author Matt Etheridge
 */
public class OpenItPlugin extends EBPlugin {
        // maps from jEdit Views to the single import listener for that view.
        private static Map msFileSelectionListenerMap = new HashMap();
        
        // maps from jEdit Views to the single FindFileWindow instance for that
        // view.
        private static Map msFindFileWindowMap = new HashMap();
        
        // Project listener
        private static PVListener projectListener = new PVListener();
        
        //{{{ start method.
        /**
         * Called on jEdit startup
         */
        public void start() {
                View[] views = jEdit.getViews();
                for(int i = 0; i < views.length; i++) {
                        createListener(views[i]);
                }
                
                jEdit.setBooleanProperty(OpenItProperties.POP_UP_CLEAN_ON_VISIBLE, 
                        jEdit.getBooleanProperty(OpenItProperties.POP_UP_CLEAN_ON_VISIBLE, true));
                jEdit.setBooleanProperty(OpenItProperties.DISPLAY_DIRECTORIES, 
                        jEdit.getBooleanProperty(OpenItProperties.DISPLAY_DIRECTORIES, true));
                jEdit.setBooleanProperty(OpenItProperties.DISPLAY_EXTENSIONS, 
                        jEdit.getBooleanProperty(OpenItProperties.DISPLAY_EXTENSIONS, true));
                jEdit.setBooleanProperty(OpenItProperties.DISPLAY_ICONS, 
                        jEdit.getBooleanProperty(OpenItProperties.DISPLAY_ICONS, true));
                
                // get the SourcePathManager singleton to force start of polling thread
                SourcePathManager manager = SourcePathManager.getInstance();

        		projectListener.start();
        } 
        //}}}
        
        //{{{ stop method.
        /**
         * Called on jEdit shutdown
         */
        public void stop() {
        		projectListener.stop();
                SourcePathManager.getInstance().stopSourcePathPolling();
        } //}}}
        
        //{{{ handleMessage method.
        public void handleMessage(EBMessage message) {
                if (message instanceof ViewUpdate) {
                        ViewUpdate viewUpdateMessage = (ViewUpdate) message;
                        
                        if (viewUpdateMessage.getWhat() == ViewUpdate.CREATED) {
                                // add a import list listener for this view
                                View createdView = viewUpdateMessage.getView();
                                FileSelectionListener listener = new SourceFileSelectionListener(createdView);
                                msFileSelectionListenerMap.put(createdView, listener);
                                getFindFileWindow(createdView).addFileSelectionListener(listener);
                        }
                        
                        else if (viewUpdateMessage.getWhat() == ViewUpdate.CLOSED) {
                                // remove import listener from import window
                                View closedView = ((ViewUpdate) message).getView();
                                FileSelectionListener listenerToRemove = (FileSelectionListener) msFileSelectionListenerMap.get(closedView);
                                FindFileWindow window = (FindFileWindow) msFindFileWindowMap.get(closedView);
                                window.removeFileSelectionListener(listenerToRemove);
                                
                                // remove import listener and import window from maps
                                msFileSelectionListenerMap.remove(closedView);
                                msFindFileWindowMap.remove(closedView);
                        }
                        
                }
        } 
        //}}}
        
        // Action Methods - these are "called" from the actions.xml configuration file.
        
        //{{{ findFileInSourcePath method.
        /**
         * Find a file in the source path
         */
        public static void findFileInSourcePath(View view) {
                FindFileWindow findClassWindow = getFindFileWindow(view);
                
                // position in center of view
                findClassWindow.setLocationRelativeTo(view);
                
                // show window 
                if (jEdit.getBooleanProperty(OpenItProperties.POP_UP_CLEAN_ON_VISIBLE, true)) {
                        findClassWindow.clearSourceFiles();
                }
                
                // findClassWindow.setVisible(true);
                findClassWindow.showWindow();
        } 
        //}}}
        
        //{{{ addCurrentDirectoryToSourcePath method.
        /**
         * Adds the current buffer's directory to the source path.
         */
        public static void addCurrentDirectoryToSourcePath(View view) {
                // get the directory of the current buffer
                String path = view.getBuffer().getPath();
                String name = view.getBuffer().getName();
                
                // remove the name from the path
                path = path.substring(0, path.lastIndexOf(name) - 1);
                
                // if the source path has not been loaded yet, put a message on the status
                // bar and just return
                if (SourcePathManager.staticGetQuickAccessSourcePath() == null) {
                        view.getStatus().setMessageAndClear(jEdit.getProperty("openit.InitialLoadingNotComplete.StatusBarMessage"));
                        
                        // force the manager to start loading sourcepath
                        SourcePathManager.getInstance();
                        
                        return;
                }
                
                // append the new path to the end of the sourcepath property
                String stringSourcePath = jEdit.getProperty(OpenItProperties.SOURCE_PATH_STRING, "");
                stringSourcePath = (stringSourcePath.trim().endsWith(File.pathSeparator) ?
                        stringSourcePath + path : stringSourcePath + File.pathSeparator + path);
                jEdit.setProperty(OpenItProperties.SOURCE_PATH_STRING, stringSourcePath);
                
                // refresh the sourcepath
                SourcePathManager.getInstance().refreshSourcePath();
                
                // set the statusbar with an appropriate message
                view.getStatus().setMessageAndClear
                (jEdit.getProperty("openit.AddedDirectoryToSourcePath.StatusBarMessage.1") +
                        " " + path + " " + 
                        jEdit.getProperty("openit.AddedDirectoryToSourcePath.StatusBarMessage.2"));
        } 
        //}}}
        
        //{{{ getFindFileWindow method.
        private static FindFileWindow getFindFileWindow(View view) {
                FindFileWindow window = (FindFileWindow) msFindFileWindowMap.get(view);
                if (window == null) {
                        window = new FindFileWindow();
                        msFindFileWindowMap.put(view, window);
                }
                return window;
        } 
        //}}}
        
        //{{{ createListener method.
        private void createListener(View view) {
                FileSelectionListener listener = new SourceFileSelectionListener(view);
                msFileSelectionListenerMap.put(view, listener);
                getFindFileWindow(view).addFileSelectionListener(listener);
        } //}}}
        
        //{{{ SourceFileSelectionListener method.
        /**
         * Listens for import selection from popup import window.
         */
        private static class SourceFileSelectionListener implements FileSelectionListener {
                private View mView;
                
                public SourceFileSelectionListener(View view) {
                        mView = view;
                }
                
                public void fileSelected(SourcePathFile sourcePathFileSelected) {
                        if (new File(sourcePathFileSelected.getDirectoryString()).exists()) {
                                jEdit.openFile(mView, sourcePathFileSelected.getDirectoryString());
                        }
                }
        } //}}}

        // :collapseFolds=1:tabSize=8:indentSize=8:folding=explicit:
}
