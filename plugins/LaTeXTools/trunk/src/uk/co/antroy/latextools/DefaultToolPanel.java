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
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.awt.Dimension;


public abstract class DefaultToolPanel
  extends AbstractToolPanel {

  //~ Instance/static variables ...............................................

  protected Buffer buffer;
  protected String tex;
  protected boolean bufferChanged = false;
  protected int currentCursorPosn;
  protected View view;                       //Binary flags: reload-refresh
  public static final int REFRESH = 1,               //           0 1
                          RELOAD  = 2,               //           1 0
			  RELOAD_AND_REFRESH = 3;    //           1 1
  protected Action refresh,
	         reload;

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

    if (message instanceof EditPaneUpdate) {
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
  
  protected JPanel createButtonPanel(int buttonTypes){
	  JPanel jp = new JPanel();
	  
	  createActions();
	  
	  if ((buttonTypes & REFRESH) == REFRESH){
		  JButton b = new JButton(refresh);
		  b.setPreferredSize(new Dimension(20,20));
		  b.setToolTipText(jEdit.getProperty("panel.text.refresh"));
		  jp.add(b);
	  }
	  
	  if ((buttonTypes & RELOAD) == RELOAD){
		  JButton b = new JButton(reload);
		  b.setPreferredSize(new Dimension(20,20));
		  b.setToolTipText(jEdit.getProperty("panel.text.reload"));
		  jp.add(b);
	  }
	  
	  return jp;
	  
  }
  
  	 private void createActions(){
		 refresh = new AbstractAction("",loadIcon("/images/ref.gif")){
			 public void actionPerformed(ActionEvent e){
				 refresh();
			 }
		 };
		 reload = new AbstractAction("",loadIcon("/images/rel.gif")){
			 public void actionPerformed(ActionEvent e){
				 reload();
			 }
		 };
	 }
	 
	static ImageIcon loadIcon( String filename )
	{
		return new ImageIcon( DefaultToolPanel.class.getResource( filename ) );
	}

  
}
