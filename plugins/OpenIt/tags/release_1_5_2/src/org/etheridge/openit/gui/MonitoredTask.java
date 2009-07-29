/*
 * LazyImporter jEdit Plugin (MonitoredTask.java) 
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

import java.awt.Component;

import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;

/**
 * A task that is tracked using a ProgressMonitor (ie. a graphical progress bar).
 * The task will be performed in a new thread.
 */
public abstract class MonitoredTask
{
  // the parent graphical component (the parent of the gui ProgressMonitor).
  private Component mParentComponent;
  
  // the graphical ProgressMonitor
  private ProgressMonitor mProgressMonitor;
  
  // the number of steps in the task
  private int mNumberOfSteps;
  
  // keeps track of the number of steps performed so far.
  private volatile int mStepsPerformed = -1;
  
  public MonitoredTask(Component parentComponent, String taskName, int numberOfSteps)
  {
    mParentComponent = parentComponent;
        
    mNumberOfSteps = numberOfSteps;    
        
    // initialize the ProgressMonitor
    mProgressMonitor = 
      new ProgressMonitor(parentComponent, taskName, "", 0, numberOfSteps);
    mProgressMonitor.setProgress(0);
    mProgressMonitor.setMillisToDecideToPopup(1);
    mProgressMonitor.setMillisToPopup(1);
  }
  
  public void start()
  {
    Runnable runnable = new Runnable()
    {
      public void run()
      {
        try {
          doWork();
        } catch (TaskCanceledException tce) {
          // if the user pressed the cancel button, just return from this
          // run method.
          return;
        }
        
        // after the work has been completed, make sure the task bar is complete
        if (mStepsPerformed <= mNumberOfSteps) {
          SwingUtilities.invokeLater(new Runnable()
          {
            public void run()
            {
              mProgressMonitor.setProgress(mNumberOfSteps);
            }
          });
        }
        
      }
    };
    Thread workThread = new Thread(runnable);
    workThread.start();
  }
  
  //
  // Protected Methods
  //
  
  /**
   * This method should be implemented to perform the actual work.  It should
   * call updateProgressMonitor after each step is performed.
   */
  protected abstract void doWork();
  
  protected void updateMessage(final String message)
  {
    // if the user has pressed the cancel button on the progress monitor, 
    // thrown the TaskCanceledException
    if (mProgressMonitor.isCanceled()) {
      throw new TaskCanceledException();
    }    
    
    // increment counter here (not in swingworker thread)
    final int currentStep = ++mStepsPerformed;
    
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        mProgressMonitor.setNote(message);
        mProgressMonitor.setProgress(currentStep);
      }
    });
  }
  
  //
  // Inner classes
  //
  
  private class TaskCanceledException extends RuntimeException
  {
    public TaskCanceledException()
    {
      super("Task Canceled Exception");
    }
  }
}


