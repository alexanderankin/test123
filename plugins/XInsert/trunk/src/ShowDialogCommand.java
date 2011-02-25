/*

 *

 * ShowDialogCommand.java
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

import org.gjt.sp.jedit.View;

/**
 *
 * @author  Dominic Stolerman
 */

public class ShowDialogCommand extends Object implements Command {

  /** Creates a new ShowDialogCommand */

  public ShowDialogCommand(String command)  {
    this.command = command;
    }

  public void run(ScriptContext sc) {
    View parent = sc.getView();
    XTreeNode node = sc.getNode();
    String _command = command;
    // System.out.println("_command=" + _command);
    boolean giveOpt = (_command.charAt(_command.length() -1) == '?');
    String key = _command.substring(1, XScripter.findWordEnd(_command, 2, null));
    // System.out.println("key=" + key);
    _command = _command.substring(key.length() + 1, giveOpt ? _command.length() - 1 : _command.length()).trim();
    // System.out.println("_command=" + _command);
    if(_command.startsWith("%set")) {
      _command = _command.substring(4).trim();
      if(_command.startsWith("$"))
        node.addVariable(key, XScripter.getSubstituteFor(parent,_command, node));
      else {
        _command = Utilities.replace(_command, "\\$", "$");
        node.addVariable(key, _command);
        }
      return;

    }

    String[] opts = Utilities.findStrings(_command);
    String message;
    String[] _opts;

    if(opts != null && opts.length > 0) {
      message = opts[0];
      _opts = new String[opts.length - 1];
      System.arraycopy(opts, 1, _opts, 0, _opts.length);
      opts = null;
      }
    else {
      message = "Please enter a value for " + key + ":";
      _opts = new String[0];
      }
    if(_opts.length == 0)
      node.addVariable(key, XScripter.showInputDialog(parent, message, key, XScripter.getSubstituteFor(parent, key, node)));
    else if(_opts.length == 1)
      node.addVariable(key, XScripter.showInputDialog(parent, message, key, _opts[0]));
    else {
      String val = XScripter.showComboDialog(parent, message, key, _opts, XScripter.getSubstituteFor(parent, key, node), giveOpt);
      // System.out.println("key=" + key + " value=" + val);
      node.addVariable(key, val);
      }
    }

  private final String command; 

}

