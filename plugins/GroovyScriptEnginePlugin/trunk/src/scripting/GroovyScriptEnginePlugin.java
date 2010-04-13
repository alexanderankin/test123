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

import com.sun.script.groovy.GroovyScriptEngineFactory;

import scripting.ScriptEngineService;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.jEdit;


/**
 * The GroovyScriptEnginePlugin provides the Groovy ScriptEngine to jEdit plugins.
 * @author elberry
 */
public class GroovyScriptEnginePlugin extends EditPlugin implements ScriptEngineService {

   private GroovyScriptEngineFactory factory = new GroovyScriptEngineFactory();

   public Class getEngineFactoryClass() {
      return factory.getClass();
   }

   public Mode getMode() {
      return jEdit.getMode("groovy");
   }


   @Override
   public void start() {
   }

   @Override
   public void stop() {
   }

}
