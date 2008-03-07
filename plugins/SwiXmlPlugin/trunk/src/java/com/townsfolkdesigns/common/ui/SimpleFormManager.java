/*
 * Copyright (c) 2008 Eric Berry <elberry@gmail.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.townsfolkdesigns.common.ui;

import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.gjt.sp.util.Log;

/**
 *
 * @author eberry
 */
public class SimpleFormManager implements FormManager {
   
   private static SimpleFormManager instance;
   public static final String FORM_CONTEXT_CLASS_PROPERTY = "form.context.class";
   private FormContext formContext;
   private Map<String, FormController> forms;

   protected FormContext getFormContext() {
      return formContext;
   }

   protected void setFormContext(FormContext formContext) {
      this.formContext = formContext;
   }
   
   protected SimpleFormManager() {
      
   }
   
   public synchronized static SimpleFormManager getInstance() {
      if(instance == null) {
         instance = new SimpleFormManager();
         String formContextClassName = System.getProperty(FORM_CONTEXT_CLASS_PROPERTY);
         Class formContextClass = null;
         if(StringUtils.isNotBlank(formContextClassName)) {
            try {
               formContextClass = Class.forName(formContextClassName);
            } catch(Exception e) {
               Log.log(Log.ERROR, instance, "Error creating instance of given form context, default will be used. Given class: " + formContextClassName, e);
            }
         }
         if(formContextClass == null) {
            formContextClass = SimpleFormContext.class;
         }
         try {
            FormContext formContext = (FormContext)formContextClass.newInstance();
            instance.setFormContext(formContext);
         } catch(Exception e) {
            Log.log(Log.ERROR, instance, "Error creating instance of form context class: " + formContextClass.getName(), e);
         }
      }
      return instance;
   }
   
   public FormContext register(FormController form) {
      return formContext;
   }

}
