/*
 *  Copyright (c) 2008 TownsfolkDesigns.com
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package scripting;

import console.Console;
import console.Output;
import console.Shell;
import java.awt.Color;
import java.io.StringWriter;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.swing.SwingUtilities;
import org.gjt.sp.jedit.ActionSet;
import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

public class ScriptEngineShell extends Shell {
 
   private ScriptEngine scriptEngine;
 
   public ScriptEngineShell() {
      super("Script Engine Shell");
      Log.log(Log.DEBUG, ScriptEngineShell .class,
             "Creating new ScriptEngineShell");




      ScriptEnginePlugin plugin = (ScriptEnginePlugin) jEdit.getPlugin("scripting.ScriptEnginePlugin");
      ScriptEngineDelegate delegate = plugin.getScriptEngineDelegate();
        // get the engines primed.
      delegate.getScriptEngineManager();
      for (Mode mode : delegate.getRegisteredModes()) {
         Log.log(Log.DEBUG, ScriptEngineShell .class,
                "Registering new shell for mode: " + mode);




         ScriptEngine engine = delegate.getScriptEngineForMode(mode);
         Shell shell = new ScriptEngineShell("scriptengine." + mode.getName(),
                engine);





         Shell.registerShell(shell);
      }
   }
 
    /*
     * Constructor for ScriptEngineShell
     */ 
   public ScriptEngineShell(String name, ScriptEngine scriptEngine) {
      super(name);
      this.scriptEngine = scriptEngine;
   }
 
   public void execute(final Console console, String input, Output output,
          Output error, final String command) {





 
      if (scriptEngine == null) {
            // if the script engine is null it's the base shell. Use the command as the name for the other shell.
         output.print(Color.green, "Switching to " + command + " console.");
         output.commandDone();
         SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               try {
                  Thread.sleep(500);
               } catch (Exception e) {
                        // ignore just set the shell
               }
                    //console.setShell("scriptengine." + command);
               ActionSet actionSet = jEdit.getActionSetForAction("console.shell.scriptengine." + command + "-show");
               System.out.println("actionSet: " + actionSet);
               if (actionSet != null) {
                  EditAction []actions = actionSet.getActions();
                  if (actions != null) {
                     for (EditAction action : actions) {
                        action.invoke(console.getView());
                     }
                  }
               }
            }
         } );
         return;
      } else {
         System.out.println("input: " + input + " | command: " + command);
         StringWriter outWriter = new StringWriter();
         ScriptContext scriptContext = ScriptEngineUtilities.getDefaultScriptContext(console.getView());
         scriptContext.setWriter(outWriter);
         scriptEngine.setContext(scriptContext);
         Object returnVal = null;
         try {
            returnVal = scriptEngine.eval(command);

            if (scriptEngine.get(ScriptEnginePlugin.SCRIPT_VALUE_VARIABLE) != null) {
               returnVal = scriptEngine.get(ScriptEnginePlugin.SCRIPT_VALUE_VARIABLE);
            }

         } catch (Exception e) {
            Log.log(Log.ERROR, ScriptEngineShell .class,
                   "Error executing script - content: \n" + command, e);



         }
         output.print(Color.black, outWriter.toString());
         if (returnVal != null) {
            output.print(Color.blue, String.valueOf(returnVal));
         }
         output.commandDone();
      }
   }
 
}
