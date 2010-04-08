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

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.jedit.textarea.TextArea;

import java.util.List;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.SimpleScriptContext;


/**
 *
 * @author elberry
 */
public abstract class ScriptEngineUtilities {


   /**
    * Gets the default ScriptContext which provides the default variables available to all scripts.
    * <br>
    * Currently the default ScriptContext contains:
    * <ul>
    *    <li>view: Active jEdit View.</li>
    *    <li>buffer: Active jEdit Buffer.</li>
    *    <li>editPane: Active jEdit EditPane.</li>
    *    <li>textArea: Active jEdit TextArea.</li>
    *    <li>wm: jEdit Window Manager.</li>
    *    <li>scriptPath: The active Buffer's path.</li>
    * </ul>
    * @param view
    * @return The default ScriptContext.
    */
   public static ScriptContext getDefaultScriptContext(View view) {
      ScriptContext defaultScriptContext = new SimpleScriptContext();

      synchronized (view) {
         Buffer buffer = view.getBuffer();
         EditPane editPane = view.getEditPane();
         TextArea textArea = view.getTextArea();
         DockableWindowManager wm = view.getDockableWindowManager();
         String scriptPath = buffer.getPath();
         defaultScriptContext.setAttribute("view", view, ScriptContext.ENGINE_SCOPE);
         defaultScriptContext.setAttribute("buffer", buffer, ScriptContext.ENGINE_SCOPE);
         defaultScriptContext.setAttribute("editPane", editPane, ScriptContext.ENGINE_SCOPE);
         defaultScriptContext.setAttribute("textArea", textArea, ScriptContext.ENGINE_SCOPE);
         defaultScriptContext.setAttribute("wm", wm, ScriptContext.ENGINE_SCOPE);
         defaultScriptContext.setAttribute("scriptPath", scriptPath, ScriptContext.ENGINE_SCOPE);
      }

      return defaultScriptContext;
   }

   /**
    * Finds the first extension used by the ScriptEngine associated with the given edit Mode.
    * @param mode
    * @return The first extension used by the ScriptEngine associated with the given edit Mode.
    */
   public static String getScriptExtension(Mode mode) {
      String scriptExtension = null;
      ScriptEngine engine = new ScriptEngineDelegate().getScriptEngineForMode(mode);

      if (engine != null) {
         ScriptEngineFactory factory = engine.getFactory();
         List<String> extensions = factory.getExtensions();

         // just pick the first one.
         scriptExtension = extensions.get(0);
      }

      return scriptExtension;
   }

}
