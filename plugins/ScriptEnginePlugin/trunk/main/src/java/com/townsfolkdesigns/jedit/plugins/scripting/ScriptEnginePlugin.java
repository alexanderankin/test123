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

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.JARClassLoader;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.ServiceManager;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;


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

      return evaluateString(bufferText, bufferMode.getName());
   }

   public static Object evaluateSelection(View view) {
      String bufferText = view.getTextArea().getText();
      Mode bufferMode = view.getBuffer().getMode();

      return evaluateString(bufferText, bufferMode.getName());
   }

   public static Object evaluateString(String script, String engineName) {
      Object returnVal = null;

      ScriptEngine engine = getScriptEngineForName(engineName);

      if (engine != null) {

         try {
            Log.log(Log.DEBUG, ScriptEnginePlugin.class, "Executing Script - content: \n" + script);
            returnVal = engine.eval(script);
            Log.log(Log.DEBUG, ScriptEnginePlugin.class, "Script executed - return val: " + returnVal);
         } catch (Exception e) {
            Log.log(Log.ERROR, ScriptEnginePlugin.class, "Error executing script - content: \n" + script, e);
         }
      }

      return returnVal;
   }

   public static ScriptEngine getScriptEngineForMode(Mode mode) {
      ScriptEngine engine = null;

      if (scriptEngineServices.containsKey(mode)) {
         ScriptEngineService service = scriptEngineServices.get(mode);
         engine = getScriptEngineForName(service.getEngineName());
      } else {
         Log.log(Log.ERROR, ScriptEnginePlugin.class, "No ScriptEngine registered for mode: " + mode.getName());
      }

      return engine;
   }

   public static ScriptEngine getScriptEngineForName(String engineName) {
      ScriptEngine engine = null;
      ScriptEngineManager manager = getScriptEngineManager();

      Log.log(Log.DEBUG, ScriptEnginePlugin.class,
         "ScriptEngineManager - factories: " + manager.getEngineFactories().size());

      for (ScriptEngineFactory factory : manager.getEngineFactories()) {
         Log.log(Log.DEBUG, ScriptEnginePlugin.class,
            "ScriptEngineFactory - name: " + factory.getEngineName() + " | language name: " +
            factory.getLanguageName());
      }

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
      Log.log(Log.DEBUG, ScriptEnginePlugin.class, "Searching for ScriptEngine Services.");

      String[] scriptEngineServiceNames = ServiceManager.getServiceNames(ScriptEngineService.class.getName());
      ScriptEngineService service = null;

      for (String serviceName : scriptEngineServiceNames) {
         service = (ScriptEngineService) ServiceManager.getService(ScriptEngineService.class.getName(), serviceName);
         scriptEngineServices.put(service.getMode(), service);
         Log.log(Log.DEBUG, ScriptEnginePlugin.class,
            "ScriptEngine Service found - mode: " + service.getMode().getName() + " | engine class: " +
            service.getEngineFactoryClass());
      }

   }

   @Override
   public void stop() {
   }

   private static ScriptEngineManager getScriptEngineManager() {
      ScriptEngineManager manager = new ScriptEngineManager(new JARClassLoader());

      for (ScriptEngineService service : scriptEngineServices.values()) {
         Class factoryClass = service.getEngineFactoryClass();

         try {
            ScriptEngineFactory factory = (ScriptEngineFactory) factoryClass.newInstance();
            Log.log(Log.DEBUG, ScriptEnginePlugin.class,
               "Registering ScriptEngineFactory with manager - name: " + factory.getEngineName() + " | class: " +
               factoryClass);
            manager.registerEngineName(factory.getEngineName(), factory);
         } catch (Exception e) {
            Log.log(Log.ERROR, ScriptEnginePlugin.class,
               "Could not create instance of ScriptEngineFactory class: " + factoryClass.getName(), e);
         }

      }

      return manager;
   }

}
