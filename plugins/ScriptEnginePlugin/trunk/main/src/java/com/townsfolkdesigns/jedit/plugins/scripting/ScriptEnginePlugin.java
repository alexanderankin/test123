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
package com.townsfolkdesigns.jedit.plugins.scripting;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.JARClassLoader;
import org.gjt.sp.jedit.Macros;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.ServiceManager;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.textarea.TextArea;
import org.gjt.sp.util.Log;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.SimpleScriptContext;


/**
 *
 * @author elberry
 */
public class ScriptEnginePlugin extends EditPlugin {

   private static Map<Mode, ScriptEngineService> scriptEngineServices =
      new ConcurrentHashMap<Mode, ScriptEngineService>();

   public static Object evaluateBuffer(View view) {
      String bufferText = view.getTextArea().getText();
      Mode bufferMode = view.getBuffer().getMode();
      ScriptContext scriptContext = getDefaultScriptContext(view);

      return evaluateString(bufferText, bufferMode.getName(), scriptContext);
   }

   public static Object evaluateSelection(View view) {
      String bufferText = view.getTextArea().getSelectedText();
      Mode bufferMode = view.getBuffer().getMode();
      ScriptContext scriptContext = getDefaultScriptContext(view);

      return evaluateString(bufferText, bufferMode.getName(), scriptContext);
   }

   public static Object evaluateString(String script, String engineName, ScriptContext scriptContext) {
      Object returnVal = null;

      ScriptEngine engine = getScriptEngineForName(engineName);

      if ((engine != null) && (script != null) && !script.equals("")) {
         engine.setContext(scriptContext);

         try {
            Log.log(Log.DEBUG, ScriptEnginePlugin.class, "Executing Script - content: \n" + script);
            returnVal = engine.eval(script);
            Log.log(Log.DEBUG, ScriptEnginePlugin.class, "Script executed - return val: " + returnVal);
         } catch (Exception e) {
            Log.log(Log.ERROR, ScriptEnginePlugin.class, "Error executing script - content: \n" + script, e);
         }
      } else {
         Macros.message(jEdit.getActiveView(), "Could not find \"" + engineName + "\" Script Engine.");
      }

      return returnVal;
   }

   public static ScriptEngine getScriptEngineForMode(Mode mode) {
      ScriptEngine engine = null;

      if (scriptEngineServices.containsKey(mode)) {
         ScriptEngineService service = scriptEngineServices.get(mode);
         engine = getScriptEngineForName(mode.getName());
      } else {
         Log.log(Log.ERROR, ScriptEnginePlugin.class, "No ScriptEngine registered for mode: " + mode.getName());
      }

      return engine;
   }

   public static ScriptEngine getScriptEngineForName(String engineName) {
      ScriptEngine engine = null;
      ScriptEngineManager manager = getScriptEngineManager();

      /* With jsr-223, this always returns a size 0, but when jEdit moves to java 6, this will work correctly.
      Log.log(Log.DEBUG, ScriptEnginePlugin.class,
         "ScriptEngineManager - factories: " + manager.getEngineFactories().size());

      for (ScriptEngineFactory factory : manager.getEngineFactories()) {
         Log.log(Log.DEBUG, ScriptEnginePlugin.class,
            "ScriptEngineFactory - name: " + factory.getEngineName() + " | language name: " +
            factory.getLanguageName());
      }
       */

      try {
         engine = manager.getEngineByName(engineName);
      } catch (Exception e) {
         Log.log(Log.ERROR, ScriptEnginePlugin.class, "Could not locate script engine named: " + engineName, e);
      }

      if (engine == null) {
         Log.log(Log.DEBUG, ScriptEnginePlugin.class, "Could not locate script engine named: " + engineName);
      }

      return engine;
   }

   public static ScriptEngine getScriptEngineForView(View view) {
      Mode bufferMode = view.getBuffer().getMode();
      ScriptEngine engine = getScriptEngineForMode(bufferMode);

      return engine;
   }

   @Override
   public void start() {
   }

   @Override
   public void stop() {
   }

   private static ScriptContext getDefaultScriptContext(View view) {
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

   private static ScriptEngineManager getScriptEngineManager() {
      ScriptEngineManager manager = new ScriptEngineManager(new JARClassLoader());

      Log.log(Log.DEBUG, ScriptEnginePlugin.class, "Searching for ScriptEngine Services.");

      String[] scriptEngineServiceNames = ServiceManager.getServiceNames(ScriptEngineService.class.getName());
      ScriptEngineService service = null;

      Log.log(Log.DEBUG, ScriptEnginePlugin.class,
         "ScriptEngineService's registered: " + scriptEngineServiceNames.length);

      Mode serviceMode = null;

      for (String serviceName : scriptEngineServiceNames) {
         service = (ScriptEngineService) ServiceManager.getService(ScriptEngineService.class.getName(), serviceName);
         scriptEngineServices.put(service.getMode(), service);
         serviceMode = service.getMode();
         Log.log(Log.DEBUG, ScriptEnginePlugin.class,
            "ScriptEngine Service found - mode: " + serviceMode.getName() + " | engine class: " +
            service.getEngineFactoryClass());

         Class factoryClass = service.getEngineFactoryClass();

         try {
            ScriptEngineFactory factory = (ScriptEngineFactory) factoryClass.newInstance();
            Log.log(Log.DEBUG, ScriptEnginePlugin.class,
               "Registering ScriptEngineFactory with manager - name: " + factory.getEngineName() + " | mode: " +
               serviceMode.getName() + " | class: " + factoryClass);

            // Use the Mode as the ScriptEngine name for consistency. Some engine's are named not for their languages.
            // EG. The Javascript Engine used, is called "Mozilla Rhino" and not "javascript".
            manager.registerEngineName(serviceMode.getName(), factory);

            Log.log(Log.DEBUG, ScriptEnginePlugin.class,
               "\"" + serviceMode.getName() + "\" ScriptEngine is registered: " +
               (manager.getEngineByName(serviceMode.getName()) != null));


         } catch (Exception e) {
            Log.log(Log.ERROR, ScriptEnginePlugin.class,
               "Could not create instance of ScriptEngineFactory class: " + factoryClass.getName(), e);
         }
      }

      return manager;
   }

}
