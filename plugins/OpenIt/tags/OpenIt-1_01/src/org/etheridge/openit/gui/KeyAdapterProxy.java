/*
 * OpenIt jEdit Plugin (KeyAdapterProxy.java) 
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

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JComponent;

/**
 * A KeyListener that forwards all its events to a specified JComponent.
 */
public class KeyAdapterProxy implements KeyListener
{
  private JComponent mTarget;
  
  public KeyAdapterProxy(JComponent target)
  {
    mTarget = target;
  }
  
  /**
   * This method should be overridden if the event should only be forwarded
   * in certain conditions. (defaults to true)
   */
  protected boolean shouldForwardEvent(KeyEvent e)
  {
    return true;
  }
    
  //
  // KeyListener Interface
  //
  
  public void keyPressed(KeyEvent e)
  {
    handleKeyEvent(e);
  }
  
  public void keyReleased(KeyEvent e)
  {
    handleKeyEvent(e);
  }
   
  public void keyTyped(KeyEvent e)
  {
    handleKeyEvent(e);
  }
  
  //
  // private helper methods
  //
  
  private void handleKeyEvent(KeyEvent e)
  {
    if (shouldForwardEvent(e)) {
      mTarget.dispatchEvent(e);
    }
  }
 
}
