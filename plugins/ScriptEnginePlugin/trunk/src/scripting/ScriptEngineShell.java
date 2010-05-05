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
   private Mode mode;
 
    /*
     * Constructor for ScriptEngineShell
     */ 
   public ScriptEngineShell(Mode mode) {
      super(mode.getName());
      this.mode = mode;
      initScriptEngine();
   }
 
   public void execute(Console console, String input, Output output, Output error, String command) {

      StringWriter outWriter = new StringWriter();
      ScriptContext engineContext = scriptEngine.getContext();
      ScriptContext scriptContext = ScriptEngineUtilities.getDefaultScriptContext(console.getView());
      engineContext.setWriter(outWriter);
      if (! "clear".equals(command)) {
			// overwrite old context bindings with new ones. This allows for the creation of variables in the console to be
			// used later.
         engineContext.getBindings(ScriptContext.ENGINE_SCOPE).putAll(scriptContext.getBindings(ScriptContext.ENGINE_SCOPE));
      } else {
			// overwrite entire old context with new one.
         initScriptEngine();
         scriptEngine.setContext(scriptContext);
         output.commandDone();
         return;
      }

      Object returnVal = null;
      try {
         returnVal = scriptEngine.eval(command);

         if (scriptEngine.get(ScriptEnginePlugin.SCRIPT_VALUE_VARIABLE) != null) {
            returnVal = scriptEngine.get(ScriptEnginePlugin.SCRIPT_VALUE_VARIABLE);
         }

      } catch (Exception e) {
         String message = e.getMessage();
         Log.log(Log.ERROR, ScriptEngineShell .class, "Error executing script - returnVal: " + returnVal + " | content: \n" + command, e);
         output.print(console.getErrorColor(), message);
      }
      output.print(Color.black, outWriter.toString());
      if (returnVal != null) {
         output.print(console.getInfoColor(), String.valueOf(returnVal));
      }
      output.commandDone();
   }
 
   private void initScriptEngine() {
      ScriptEnginePlugin plugin = (ScriptEnginePlugin) jEdit.getPlugin("scripting.ScriptEnginePlugin");
      ScriptEngineDelegate delegate = plugin.getScriptEngineDelegate();
      scriptEngine = delegate.getScriptEngineForMode(mode);
   }
 
}
