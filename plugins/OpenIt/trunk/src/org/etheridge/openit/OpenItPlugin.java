/*
 * OpenIt jEdit Plugin (OpenItPlugin.java) 
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
 
import java.awt.Dimension;

import java.io.File;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.etheridge.openit.gui.FileSelectionListener;
import org.etheridge.openit.gui.FindFileWindow;
import org.etheridge.openit.OpenItProperties;
import org.etheridge.openit.options.PopupOptionsPane;
import org.etheridge.openit.options.SourcePathOptionsPane;
import org.etheridge.openit.sourcepath.SourcePathFile;
import org.etheridge.openit.SourcePathManager;

import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.gui.OptionsDialog;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.msg.ViewUpdate;
import org.gjt.sp.jedit.OptionGroup;
import org.gjt.sp.jedit.View;

/**
 * @author Matt Etheridge
 */
public class OpenItPlugin extends EBPlugin
{
  // maps from jEdit Views to the single import listener for that view.
  private static Map msFileSelectionListenerMap = new HashMap();
  
  // maps from jEdit Views to the single FindFileWindow instance for that
  // view.
  private static Map msFindFileWindowMap = new HashMap();
  
  
  //
  // jEdit framework/callback methods
  //
    
  /**
   * Called on jEdit startup
   */
  public void start()
  {
    // if the plugin is loaded by "deferred" loading, we should get all views 
    // and add a listener for each.  jEdit 4.1 loaded everything at startup
    // so we used to be able to do this inital load when we got a ViewUpdate
    // message, however, jEdit4.2 cannot rely on this.   
    View[] views = jEdit.getViews();
    for(int i = 0; i < views.length; i++) {
      createListener(views[i]);
    }    
    
    // get the SourcePathManager singleton to force start of polling thread
    SourcePathManager manager = SourcePathManager.getInstance();
 }

  /**
   * Called on jEdit shutdown
   */
  public void stop()
  {
    // tell the source path manager to stop its polling
    SourcePathManager.getInstance().stopSourcePathPolling();
  }

  /**
   *  Description of the Method
   *
   * @param  dialog  Description of the Parameter
   */
  public void createOptionPanes(OptionsDialog dialog)
  {
     OptionGroup optionGroup = new OptionGroup(jEdit.getProperty("options.OpenIt.label"));
     optionGroup.addOptionPane(new SourcePathOptionsPane());
     optionGroup.addOptionPane(new PopupOptionsPane());
     dialog.addOptionGroup(optionGroup);
  }

  /**
   *  Description of the Method
   *
   * @param  menuItems  Description of the Parameter
   */
  public void createMenuItems(Vector menuItems)
  {
    menuItems.addElement(GUIUtilities.loadMenu("openit.menu"));
  }

  public void handleMessage(EBMessage message)
  {
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
  
  //
  // Action Methods - these are "called" from the actions.xml configuration file.
  //
    
  /**
   * Find a file in the source path
   */
  public static void findFileInSourcePath(View view)
  {
    FindFileWindow findClassWindow = getFindFileWindow(view);

    // position in center of view
    Dimension viewSize = view.getSize();
    findClassWindow.setLocation( (int) view.getLocationOnScreen().getX() + ((viewSize.width - findClassWindow.getSize().width) / 2),
     (int) view.getLocationOnScreen().getY() + ((viewSize.height - findClassWindow.getSize().height) / 2));
  
    // show window 
    if (jEdit.getBooleanProperty(OpenItProperties.POP_UP_CLEAN_ON_VISIBLE, true)) {
      findClassWindow.clearSourceFiles();
    }
    
    findClassWindow.show();
    findClassWindow.setVisible(true);
  }
   
  //
  // Private Helper Methods
  //
   
  private static FindFileWindow getFindFileWindow(View view)
  {
    FindFileWindow window = (FindFileWindow) msFindFileWindowMap.get(view);
    if (window == null) {
      window = new FindFileWindow();
      msFindFileWindowMap.put(view, window);
    }
    return window;
  }
  
  private void createListener(View view)
  {
    FileSelectionListener listener = new SourceFileSelectionListener(view);
    msFileSelectionListenerMap.put(view, listener);
    getFindFileWindow(view).addFileSelectionListener(listener);
  }
  
  //
  // Inner Classes
  //
  
  /**
   * Listens for import selection from popup import window.
   */
  private static class SourceFileSelectionListener implements FileSelectionListener
  {
    private View mView;
    
    public SourceFileSelectionListener(View view)
    {
      mView = view;
    }
    
    public void fileSelected(SourcePathFile sourcePathFileSelected)
    {
      if (new File(sourcePathFileSelected.getDirectoryString()).exists()) {
        jEdit.openFile(mView, sourcePathFileSelected.getDirectoryString());
      }
    }
    
  }
}
