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

import org.gjt.sp.jedit.Macros;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

import java.io.File;
import java.io.FileReader;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;


/**
 *
 * @author elberry
 */
public class ScriptExecutionDelegate {

   private ScriptEngineDelegate scriptEngineManager;

   public ScriptExecutionDelegate() {
      this(new ScriptEngineDelegate());
   }

   public ScriptExecutionDelegate(ScriptEngineDelegate scriptEngineManager) {
      this.scriptEngineManager = scriptEngineManager;
   }

   public Object evaluateBuffer(View view) {
      String bufferText = view.getTextArea().getText();
      Mode bufferMode = view.getBuffer().getMode();
      ScriptContext scriptContext = ScriptEngineUtilities.getDefaultScriptContext(view);

      return evaluateString(bufferText, bufferMode.getName(), scriptContext);
   }

   public Object evaluateSelection(View view) {
      String bufferText = view.getTextArea().getSelectedText();
      Mode bufferMode = view.getBuffer().getMode();
      ScriptContext scriptContext = ScriptEngineUtilities.getDefaultScriptContext(view);

      return evaluateString(bufferText, bufferMode.getName(), scriptContext);
   }

   public Object evaluateString(String script, String engineName, ScriptContext scriptContext) {
      Object returnVal = null;

      ScriptEngine engine = getScriptEngineManager().getScriptEngineForName(engineName);

      if ((engine != null) && (script != null) && !script.equals("")) {
         engine.setContext(scriptContext);

         // TODO: Add ability for ScriptEnginePlugins to provide extra context items and an init script.

         try {
            Log.log(Log.DEBUG, ScriptExecutionDelegate.class, "Executing Script - content: \n" + script);
            returnVal = engine.eval(script);

            if (engine.get(ScriptEnginePlugin.SCRIPT_VALUE_VARIABLE) != null) {
               returnVal = engine.get(ScriptEnginePlugin.SCRIPT_VALUE_VARIABLE);
            }

            Log.log(Log.DEBUG, ScriptExecutionDelegate.class, "Script executed - return val: " + returnVal);
         } catch (Exception e) {
            Log.log(Log.ERROR, ScriptExecutionDelegate.class, "Error executing script - content: \n" + script, e);
         }
      } else {
         Macros.message(jEdit.getActiveView(), "Could not find \"" + engineName + "\" Script Engine.");
      }

      return returnVal;
   }

   public ScriptEngineDelegate getScriptEngineManager() {
      return scriptEngineManager;
   }

   public void setScriptEngineManager(ScriptEngineDelegate scriptEngineManager) {
      this.scriptEngineManager = scriptEngineManager;
   }
}
