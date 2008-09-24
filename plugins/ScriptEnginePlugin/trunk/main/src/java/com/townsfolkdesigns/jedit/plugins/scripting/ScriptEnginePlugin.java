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

import com.townsfolkdesigns.jedit.plugins.scripting.forms.CreateMacroForm;

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

import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.SimpleScriptContext;

import javax.swing.JOptionPane;


/**
 *
 * @author elberry
 */
public class ScriptEnginePlugin extends EditPlugin {
   public static final String SCRIPT_VALUE_VARIABLE = "SCRIPTVALUE";

   private static Map<Mode, ScriptEngineService> scriptEngineServices =
      new ConcurrentHashMap<Mode, ScriptEngineService>();

   private static final String MACRO_TEMPLATE = "import com.townsfolkdesigns.jedit.plugins.scripting.*;\n" +
      "ScriptEnginePlugin.executeMacro(view, scriptPath, \"%1$s\");";

   public static void createMacro(View view) {
      getScriptEngineManager();

      CreateMacroForm form = new CreateMacroForm();
      form.show(view);

      int dialogValue = form.getDialogValue();

      if (dialogValue == JOptionPane.OK_OPTION) {
         String macroName = form.getMacroName();
         String directory = form.getDirectoryName();
         Mode mode = form.getMode();
         createMacro(macroName, directory, mode);
      }
   }

   public static void createMacroFromBuffer(View view) {

   }

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

         // TODO: Add ability for ScriptEnginePlugins to provide extra context items and an init script.

         try {
            Log.log(Log.DEBUG, ScriptEnginePlugin.class, "Executing Script - content: \n" + script);
            returnVal = engine.eval(script);

            if (engine.get(SCRIPT_VALUE_VARIABLE) != null) {
               returnVal = engine.get(SCRIPT_VALUE_VARIABLE);
            }

            Log.log(Log.DEBUG, ScriptEnginePlugin.class, "Script executed - return val: " + returnVal);
         } catch (Exception e) {
            Log.log(Log.ERROR, ScriptEnginePlugin.class, "Error executing script - content: \n" + script, e);
         }
      } else {
         Macros.message(jEdit.getActiveView(), "Could not find \"" + engineName + "\" Script Engine.");
      }

      return returnVal;
   }

   public static void executeMacro(View view, String scriptPath, String modeStr) {
      Mode mode = jEdit.getMode(modeStr);
      File bshScriptFile = new File(scriptPath);
      String bshScriptName = bshScriptFile.getName();
      int indexOfDot = bshScriptName.lastIndexOf(".");
      bshScriptName = bshScriptName.substring(0, indexOfDot);

      String scriptExtension = getScriptExtension(mode);
      String macroScriptName = bshScriptName + "." + scriptExtension;
      File macroScriptFile = new File(bshScriptFile.getParentFile(), macroScriptName);

      if (macroScriptFile.exists()) {
         ScriptEngine engine = getScriptEngineForMode(mode);

         try {
            engine.eval(new FileReader(macroScriptFile));
         } catch (Exception e) {
            Log.log(Log.ERROR, ScriptEnginePlugin.class, "Error executing script file: " + macroScriptFile.getPath(),
               e);
         }
      }
   }

   public static Collection<Mode> getRegisteredModes() {
      return scriptEngineServices.keySet();
   }

   public static ScriptEngine getScriptEngineForMode(Mode mode) {
      ScriptEngine engine = getScriptEngineForName(mode.getName());;

      if (engine == null) {
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

   private static String cleanMacroName(String macroName) {
      String cleanMacroName = macroName;
      cleanMacroName = cleanMacroName.replaceAll(" ", "_");

      return cleanMacroName;
   }

   private static void createMacro(String macroName, String directory, Mode mode) {
      macroName = cleanMacroName(macroName);

      File macroFile = new File(jEdit.getSettingsDirectory(), "macros");

      if (macroFile.exists()) {
         macroFile = new File(macroFile, directory);

         if (!macroFile.exists()) {
            macroFile.mkdirs();
         }

         File scriptMacroFile = writeScriptMacroTemplate(macroFile, macroName, mode);

         // open new macro file in jEdit.
         jEdit.openFile(jEdit.getActiveView(), scriptMacroFile.getPath());
			Macros.loadMacros();
      }
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

   private static String getScriptExtension(Mode mode) {
      String scriptExtension = null;
      ScriptEngine engine = getScriptEngineForMode(mode);

      if (engine != null) {
         ScriptEngineFactory factory = engine.getFactory();
         List<String> extensions = factory.getExtensions();

         // just pick the first one.
         scriptExtension = extensions.get(0);
      }

      return scriptExtension;
   }

   private static File writeScriptMacroTemplate(File macroFile, String macroName, Mode mode) {
      String bshMacroFileName = macroName + ".bsh";
      String scriptMacroFileName = macroName + "." + getScriptExtension(mode);
      File bshMacroFile = new File(macroFile, bshMacroFileName);
      File scriptMacroFile = new File(macroFile, scriptMacroFileName);
      PrintWriter writer = null;

      try {
         writer = new PrintWriter(bshMacroFile);
         writer.format(MACRO_TEMPLATE, mode);
         writer.flush();
      } catch (Exception e) {
         Log.log(Log.ERROR, ScriptEnginePlugin.class, "Error creating file writer.", e);
      } finally {

         if (writer != null) {

            try {
               writer.close();
            } catch (Exception e) {
               Log.log(Log.ERROR, ScriptEnginePlugin.class, "Error closing file writer.", e);
            }
         }
      }

      try {
         scriptMacroFile.createNewFile();
      } catch (Exception e) {
         Log.log(Log.ERROR, ScriptEnginePlugin.class, "Error creating script macro file: " + scriptMacroFile.getPath(),
            e);
      }

      return scriptMacroFile;
   }

}
