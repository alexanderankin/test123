/*
 * OpenIt jEdit Plugin (SourcePathManager.java) 
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

import org.etheridge.openit.OpenItProperties;
import org.etheridge.openit.sourcepath.QuickAccessSourcePath;
import org.etheridge.openit.sourcepath.SourcePath;

import org.gjt.sp.jedit.jEdit;

/**
 * Controls access a single instance of the source path and handles the automatic
 * refreshing of the source path.
 */
public class SourcePathManager
{
  // singleton instance
  private static SourcePathManager msSourcePathManagerSingleton;

  private QuickAccessSourcePath mQuickAccessSourcePath;
  private SourcePath mSourcePath;
  
  // polling thread
  private SourcePathRefreshThread mPollingThread;
  
  // default and minimum polling periods
  public static final int DEFAULT_POLLING_INTERVAL = 60; // seconds
  public static final int MINIMUM_POLLING_INTERVAL = 5; // seconds
  
  public synchronized static SourcePathManager getInstance()
  {
    if (msSourcePathManagerSingleton == null) {
      msSourcePathManagerSingleton = new SourcePathManager();
    }
    
    return msSourcePathManagerSingleton;
  }
  
  private SourcePathManager()
  {
    // create the polling thread for the first time
    mPollingThread = new SourcePathRefreshThread();
  }
  
  /**
   * Refreshes the current source path (and the quickaccess source path).
   * This is done in a worker thread, so a call to getSourcePath() may not 
   * result in the refreshed copy of the source path straight away.
   */
  public void refreshSourcePath()
  {
    // stop the current polling thread
    mPollingThread.stopPollingThread();
    
    // create a new polling thread
    mPollingThread = new SourcePathRefreshThread();
  }
  
  public synchronized QuickAccessSourcePath getQuickAccessSourcePath()
  {
    return mQuickAccessSourcePath;
  }
  
  public synchronized SourcePath getSourcePath()
  {
    return mSourcePath;
  }
  
  public void stopSourcePathPolling()
  {
    if (mPollingThread != null) {
      mPollingThread.stopPollingThread();
      mPollingThread = null;
    }
  }
  
  public static synchronized QuickAccessSourcePath staticGetQuickAccessSourcePath()
  {
    // if this singleton is not initialized, then return null
    if (msSourcePathManagerSingleton == null) {
      return null;
    }
    
    // otherwise, must be loaded so return it
    return msSourcePathManagerSingleton.getQuickAccessSourcePath();
  }
  
  //
  // private helper methods
  //
  
  /**
   * Recreates the sourcePath and quick access source path.
   */
  private void recreateSourcePaths()
  {
    // create both sourcepaths and store as local variables
    SourcePath sourcePath = new SourcePath(jEdit.getProperty(OpenItProperties.SOURCE_PATH_STRING, ""));
    QuickAccessSourcePath quickAccessSourcePath = new QuickAccessSourcePath(sourcePath);
    
    synchronized(this) {
      mSourcePath = sourcePath;
      mQuickAccessSourcePath = quickAccessSourcePath;
    }
  }
  
  //
  // Inner Classes
  //
  
  private class SourcePathRefreshThread extends Thread
  {
    private volatile boolean mStopRequested;
    
    public SourcePathRefreshThread()
    {
      // start the thread on construction
      start();
    }
    
    public void run()
    {
      // reset flag
      mStopRequested = false;
      
      // continue looping until interrupted.
      while (!mStopRequested) {
        
        // do work here
        recreateSourcePaths();

        // explicity garbage collect
        System.gc();

        // sleep for specified time
        try {
          Thread.sleep(jEdit.getIntegerProperty(OpenItProperties.SOURCE_PATH_POLLING_INTERVAL, DEFAULT_POLLING_INTERVAL) * 1000);
        } catch (InterruptedException ie) {
          Thread.currentThread().interrupt(); // reassert
        }
      }
      
    }
    
    public void stopPollingThread()
    {
      // set flag
      mStopRequested = true;
      
      // interrupt the thread
      interrupt();
    }
  }
  
}
