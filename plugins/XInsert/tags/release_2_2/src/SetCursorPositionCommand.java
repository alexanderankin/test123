/*
 * SetCursorPositionCommand.java
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


import org.gjt.sp.jedit.textarea.JEditTextArea;

public class SetCursorPositionCommand implements Command
{
  public SetCursorPositionCommand(final int pos)
  {
    this.pos = pos;
  }
  
  public void run(ScriptContext sc)
  {
    JEditTextArea area = sc.getView().getTextArea();
    area.setCaretPosition(pos);
  }
  
  private final int pos;
}
