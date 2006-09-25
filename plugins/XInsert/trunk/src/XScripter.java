/*
* Wed Jul 26 16:09:28 2000
*
* XScripter.java - Running/Managing XInsert Scripts
* Copyright (C) 1999 Dominic Stolerman
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
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;
import java.util.Vector;
import java.util.Enumeration;
import java.io.File;


/**
*
* @author     Dominic Stolerman
* @created    20 September 2000
*/
public class XScripter
{
/**
  * Constructor for the XScripter object
  */
  private XScripter() {}

/**
 * @deprecated use getSubstituteFor(View, String, XtreeNode) instead as it does exactly the same
 */
  public static String _getSubstituteFor(View parent, String in, XTreeNode node) {
    return getSubstituteFor(parent, in, node);
    }

  private static Command getCommand(View parent, XTreeNode node, String command) {
    char c = command.charAt(0);
    if(c == '$') {
      // Substitute variable
      //Log.log(Log.DEBUG, XScripter.class, "Adding substitute variable (\"" + command + "\") command to queue");
      return new SubVariableCommand(command);
      }
    else if(c == '!') {
      // Run Java function
      //Log.log(Log.DEBUG, XScripter.class, "Adding run java (\"" + command + "\") command to queue");
      return new RunJavaCommand(command);
      }
    else if(c == '@') {
      // Run macro comand
      //Log.log(Log.DEBUG, XScripter.class, "Adding run macro (\"" + command + "\") command to queue");
      return new MacroCommand(command);
      }
    else if(c == '%') {
      // Set variable
      //Log.log(Log.DEBUG, XScripter.class, "Adding set variable (\"" + command + "\") command to queue");
      return new ShowDialogCommand(command);
      }
    else {
      doError(command, "Command not recognised");
      return new InsertTextCommand(command);
      }
    }

  public static void runMacro(View parent, String name, String macro) {
    Log.log(Log.DEBUG, XScripter.class, "Running runMacro for name=" + name);
    BeanShell.eval( parent, macro, false);
    }

  public static void runNamedMacro(View parent, String name, String path) {
	Log.log(Log.DEBUG, XScripter.class, "Running runNamedMacro for item named " + name + ", path=" + path);
	// NOTE: old-style macro names
	if(path.startsWith("play-macro@")) {
		path = path.substring(11);
		}
	Macros.Macro macro = Macros.getMacro(path);
	if(macro != null) {
		// NOTE: this is the internal representation of a macro
		macro.invoke(parent);
		}
	else {
		// NOTE: this is the alternative representation: the macro's full path
		File macroFile = new File(path);
		if(macroFile.exists()) {
			BeanShell.runScript(parent, path, true, false);
			}
		else {
			Log.log(Log.ERROR, XScripter.class,
				"Could not find macro named " + path);
			}
		}
  	}


  /**
  *  searches for a variable recursivley through nodes for variable, returns
  *  the first it finds.
  *
  * @param  parent  the view to be used for view specific variables
  * @param  key     the variable name
  * @param  node    the tree node from where to start the search
  * @return         the variable value or null if the variable is not found
  */

  public static String getSubstituteFor(View parent, String key, XTreeNode node) {
	XTreeNode parentNode = node;
	String val = null;
	do {
		if(parentNode.hasVariables() && parentNode.containsVariable(key)) {
			val = parentNode.getVariable(key);
			}
	} while ((parentNode = (XTreeNode) parentNode.getParent()) != null);
	if(val == null && XInsertPlugin.containsVariable(key)) {
		val = XInsertPlugin.getVariable(key);
		}
	if(val == null && parent != null) {
		val = XInsertPlugin.getViewSpecificVariable(parent, key);
		}
	return val == null ? null : MiscUtilities.escapesToChars(val);
	}

  /**
  *  insert text.
  *
  * @param  text  The node content to be inserted.
  */
  public static void insertText(View view, String text, XTreeNode node) {
    CommandQueue queue = new CommandQueue();
    ScriptContext context = new ScriptContext(view, node, queue);
    Buffer buffer = view.getBuffer();
    buffer.beginCompoundEdit();
    InsertTextCommand.insertText(text, context);
    buffer.endCompoundEdit();
    }

  /**
  *  runs an XInsertScript.
  *
  * @param  view    The view to run the script in.
  * @param  script  The node content to be run as a script.
  * @param  node    The node from where to start searching for variable substitutions
  */
  public static void runXInsertScript(View view, String script, XTreeNode node) {
    CommandQueue queue = new CommandQueue();
    Buffer buffer = view.getBuffer();
    buffer.beginCompoundEdit();
    try {
      char[] chars = script.toCharArray();
      int start = 0;
      for(int i = start; i < chars.length; i++) {
        switch (chars[i]) {
          case '{':
            if( chars[i + 1] == '$' || chars[i + 1] == '@' || chars[i + 1] == '!' ||
            chars[i + 1] == '%' || chars[i + 1] == '#' || chars[i + 1] == '*' ||
            chars[i + 1] == '&') {
                //Log.log(Log.DEBUG, XScripter.class, "Adding insert text (\"" + text + "\") command to queue");
                //Insert the text between the last command and this one
                String text = script.substring(start, i);
                queue.add(new InsertTextCommand(text));
                int j;
                inner:
                for(j = i; j < chars.length; j++) {
                    if(chars[j] == '}' && chars[j - 1] != '\\') { //If end of command
                        String cmd = script.substring(i + 1, j);
                        cmd = Utilities.replace(cmd, "\\}", "}");
                        queue.add(getCommand(view, node, cmd));  //Add this command to queue
                        break inner;
                        }
                    }
                i = j; //set parsing to continue at the end of the command
                start = j + 1; //set the start position for the next insert text command
                }
            break;
            }
          }
      String remainder = script.substring(start, script.length());
      queue.add(new InsertTextCommand(remainder)); //Insert the text left over
      //Run commands in queue
      ScriptContext context = new ScriptContext(view, node, queue);
      queue.executeAll(context);
      }
    catch (StringIndexOutOfBoundsException e) {
      doError("Unknown", "Missing \"}\"");
      return;
      }
    catch (Exception e) {
	  doError("Unknown", "Syntax error in script - Execution Aborted", e);
	  return;
	  }
    finally {
      buffer.endCompoundEdit();
      }
    }

  /**
  *  Finds the end of the word at position <code>pos</code> in <code>line</code>.
  *  <p>this is a slightly modified version of {@link org.gjt.sp.jedit.textarea.TextUtilities#findWordEnd(String, int, String)}</P>
  */
  public static int findWordEnd(String line, int pos, String noWordSep) {
    if(pos != 0) pos--;
    char ch = line.charAt(pos);
    if(noWordSep == null) {
      noWordSep = "";
      }
    boolean selectNoLetter = (!Character.isLetterOrDigit(ch) && noWordSep.indexOf(ch) == -1);
    int wordEnd = line.length();
    for(int i = pos; i < line.length(); i++) {
      ch = line.charAt(i);
      if(selectNoLetter ^ (!Character.isLetterOrDigit(ch) && noWordSep.indexOf(ch) == -1)) {
        wordEnd = i;
        break;
        }
      }
    return wordEnd;
    }

/**
  *  There was an error in executing the script
  *
  * @param  command  The command that was running
  * @param  message  A message to the user
  */
  public static void doError(String command, String message) {
    doError(command, message, null);
    }

/**
  *  Logs an error which threw an exception
  *
  * @param  command  The command that was running
  * @param  message  A message to the user
  * @param  ex       The exception
  */
  public static void doError(String command, String message, Exception ex) {
    JOptionPane.showMessageDialog(null, "There was an error running the XInsert Script:\nCommand: "
       + command
       + ((message == null) ? "" : "\nMessage: " + message)
       + ((ex == null) ? "" : "\nException: " + ex.toString()),
       "Error Running XinsertScript", JOptionPane.ERROR_MESSAGE);
    Log.log(Log.ERROR, XScripter.class, command + ":" + message);
    Log.log(Log.ERROR, XScripter.class, ex);
  }

  public static void doError(String command, Exception ex) {
    doError(command, null, ex);
    }

  public static String showInputDialog(View parent, String key, String defValue) {
    return showInputDialog(parent, "Please enter a value for \"" + key + "\"", key, defValue);
    }

  public static String showInputDialog(View parent, String message, String key, String defValue) {
    InputDialog id = new InputDialog(parent, key, message, defValue);
    return id.showDialog();
    }

  public static String showComboDialog(View parent, String message, String key, String[] opts, String defValue, boolean allowUser) {
    InputDialog id = new InputDialog(parent, key, message, defValue, opts, allowUser);
    return id.showDialog();
    }
  }

