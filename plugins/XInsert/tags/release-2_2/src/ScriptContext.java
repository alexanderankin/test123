/*
 * ScriptContext.java
 * Copyright (C) 16 December 2000 Dominic Stolerman
 * dominic@sspd.org.uk
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
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.util.Log;

/**
 *
 * @author  Dominic Stolerman 
 */
public class ScriptContext extends Object {

  /** Creates new ScriptContext */
  public ScriptContext(final View view, final XTreeNode node, final CommandQueue queue) {
    this.view = view;
    this.node = node;
    this.queue = queue;
    this.textarea = view.getTextArea();
    String sel = textarea.getSelectedText();
    this.selection = (sel == null)
    	? ""
	: sel;
    }

  public View getView() {
    return view;
  }

  public XTreeNode getNode() {
    return node;
  }

  public String getSelection() {
    return selection;
  }

  public CommandQueue getCommandQueue() {
    return queue;
  }
  
  private final String selection;
  private final View view;
  private final JEditTextArea textarea;
  private final XTreeNode node;
  private final CommandQueue queue;

}

