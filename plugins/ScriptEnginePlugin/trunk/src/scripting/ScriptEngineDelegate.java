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

//import console.Shell;

import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.ServiceManager;
import org.gjt.sp.jedit.View;
import org.gjt.sp.util.Log;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;


/**
 *
 * @author eberry
 */
public class ScriptEngineDelegate {

   private static Map<Mode, ScriptEngineService> scriptEngineServices =
      new ConcurrentHashMap<Mode, ScriptEngineService>();

   public ScriptEngineDelegate() {

   }

   public Collection<Mode> getRegisteredModes() {
      return scriptEngineServices.keySet();
   }

   public ScriptEngine getScriptEngineForMode(Mode mode) {
      ScriptEngine engine = getScriptEngineForName(mode.getName());

      if (engine == null) {
         Log.log(Log.ERROR, ScriptEngineDelegate.class, "No ScriptEngine registered for mode: " + mode.getName());
      }

      return engine;
   }

   public ScriptEngine getScriptEngineForName(String engineName) {
      ScriptEngine engine = null;
      ScriptEngineManager manager = getScriptEngineManager();

      try {
         engine = manager.getEngineByName(engineName);
      } catch (Exception e) {
         Log.log(Log.ERROR, ScriptEngineDelegate.class, "Could not locate script engine named: " + engineName, e);
      }

      if (engine == null) {
         Log.log(Log.DEBUG, ScriptEngineDelegate.class, "Could not locate script engine named: " + engineName);
      }

      return engine;
   }

   public ScriptEngine getScriptEngineForView(View view) {
      Mode bufferMode = view.getBuffer().getMode();
      ScriptEngine engine = getScriptEngineForMode(bufferMode);

      return engine;
   }

   public ScriptEngineManager getScriptEngineManager() {
      ScriptEngineManager manager = new ScriptEngineManager();

      Log.log(Log.DEBUG, ScriptEngineDelegate.class, "Searching for ScriptEngine Services.");

      String[] scriptEngineServiceNames = ServiceManager.getServiceNames(ScriptEngineService.class.getName());
      ScriptEngineService service = null;

      Log.log(Log.DEBUG, ScriptEngineDelegate.class,
         "ScriptEngineService's registered: " + scriptEngineServiceNames.length);

      Mode serviceMode = null;

      for (String serviceName : scriptEngineServiceNames) {
         service = (ScriptEngineService) ServiceManager.getService(ScriptEngineService.class.getName(), serviceName);
         serviceMode = service.getMode();
         scriptEngineServices.put(serviceMode, service);
         Log.log(Log.DEBUG, ScriptEngineDelegate.class,
            "ScriptEngine Service found - mode: " + serviceMode.getName() + " | engine class: " +
            service.getEngineFactoryClass());

         Class factoryClass = service.getEngineFactoryClass();

         try {
            ScriptEngineFactory factory = (ScriptEngineFactory) factoryClass.newInstance();
            Log.log(Log.DEBUG, ScriptEngineDelegate.class,
               "Registering ScriptEngineFactory with manager - name: " + factory.getEngineName() + " | mode: " +
               serviceMode.getName() + " | class: " + factoryClass);

            // Use the Mode as the ScriptEngine name for consistency. Some engine's are named not for their languages.
            // EG. The Javascript Engine used, is called "Mozilla Rhino" and not "javascript".
            manager.registerEngineName(serviceMode.getName(), factory);
            ScriptEngine engine = manager.getEngineByName(serviceMode.getName());
            //Shell shell = new ScriptEngineShell("scriptengine." + serviceMode.getName(), engine);
            //Shell.registerShell(shell);


            Log.log(Log.DEBUG, ScriptEngineDelegate.class,
               "\"" + serviceMode.getName() + "\" ScriptEngine is registered: " +
               (engine != null));


         } catch (Exception e) {
            Log.log(Log.ERROR, ScriptEngineDelegate.class,
               "Could not create instance of ScriptEngineFactory class: " + factoryClass.getName(), e);
         }
      }

      return manager;
   }
}
