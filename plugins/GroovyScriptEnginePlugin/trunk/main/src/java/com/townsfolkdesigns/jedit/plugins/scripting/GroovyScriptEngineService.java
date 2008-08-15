/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.townsfolkdesigns.jedit.plugins.scripting;

import com.sun.script.groovy.GroovyScriptEngineFactory;

import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.jEdit;


/**
 *
 * @author elberry
 */
public class GroovyScriptEngineService implements ScriptEngineService {

   private GroovyScriptEngineFactory factory = new GroovyScriptEngineFactory();

   public Class getEngineFactoryClass() {
      return factory.getClass();
   }

   public String getEngineName() {
      return factory.getEngineName();
   }

   public Mode getMode() {
      return jEdit.getMode("groovy");
   }

}
