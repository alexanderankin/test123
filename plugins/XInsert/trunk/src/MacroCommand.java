/*
 *
 * MacroCommand.java
 * Copyright (C) 2001 Dominic Stolerman
 * dstolerman@jedit.org
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
 * @author  Dominic Stolerman
 */

public class MacroCommand extends Object implements Command {

/** Creates new MacroCommand */
  public MacroCommand(String macro) {
    this.macro = macro.substring(1);
    }

  public void run(ScriptContext sc) {
    XInsertPlugin.curContext = sc;
    try {
      Object rv = XScripter.runMacro(
        sc.getView(), (String)sc.getNode().getUserObject(), macro);
      if (rv != null)
        InsertTextCommand.insertText(rv.toString(), sc);
    }
    finally {
      XInsertPlugin.curContext = null;
    }
  }

  private final String macro;

}

// :tabSize=2:indentSize=2:noTabs=true:folding=explicit:collapseFolds=1:
