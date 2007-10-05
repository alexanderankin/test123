/*
 *
 * NamedMacroCommand.java
 * Copyright (C) 2001 John Gellene
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

/**
 *
 * @author  John Gellene
 * @version 0.1
 */
public class NamedMacroCommand extends Object implements Command
{

  /** Creates new MacroCommand */
  public NamedMacroCommand(String macroName) 
  {
    this.macroName = macroName.substring(1);
  }

  public void run(ScriptContext sc)
  {
    XScripter.runNamedMacro(sc.getView(), (String)sc.getNode().getUserObject(), macroName);
  }
  
  private final String macroName;
}

