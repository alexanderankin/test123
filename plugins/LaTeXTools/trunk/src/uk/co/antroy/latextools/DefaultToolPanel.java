/*:folding=indent:
 * AbstractToolPanel.java - Abstract class representing a tool panel.
 * Copyright (C) 2002 Anthony Roy
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
package uk.co.antroy.latextools; 

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.msg.EditPaneUpdate;


public abstract class DefaultToolPanel
  extends AbstractToolPanel {

  //~ Instance/static variables ...............................................

  protected Buffer buffer;
  protected String tex;
  protected boolean bufferChanged = false;
  protected int currentCursorPosn;
  protected View view;
  //~ Constructors ............................................................

  /**
   * Creates a new DefaultToolPanel object.
   * 
   * @param view ¤
   * @param buff ¤
   */
  public DefaultToolPanel(View view, Buffer buff, String name) {
    this.buffer = buff;
    this.view = view;
    this.setName(name);
            tex = buffer.getPath();

    EditBus.addToBus(this);
  }

  public DefaultToolPanel(View view, Buffer buff) {
	  this(view,buff,"Tab");
  }
  
  //~ Methods .................................................................

  /**
   * ¤
   * 
   * @param message ¤
   */
  public void handleMessage(EBMessage message) {

    boolean bufferLoaded = (message instanceof BufferUpdate);
    if (bufferLoaded){
      BufferUpdate bu = (BufferUpdate) message;
      bufferLoaded = bufferLoaded && 
          (bu.getWhat() == BufferUpdate.CREATED || 
          bu.getWhat() == BufferUpdate.LOADED);
    }
    
    if ((message instanceof EditPaneUpdate) || bufferLoaded) {
      buffer = view.getBuffer();
      tex = buffer.getPath();

      bufferChanged = true;
      refresh();
    }
  }

  /**
   * ¤
   */
  public void refresh() {
  }

  /**
   * ¤
   */
  public void reload() {
  }
  
  public void refreshCurrentCursorPosn(){
	     currentCursorPosn = view.getTextArea().getCaretPosition(); 
  }
  

  
}
