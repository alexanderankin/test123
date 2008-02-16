/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.townsfolkdesigns.lucene.jedit;

import java.awt.Container;
import java.net.URL;
import org.gjt.sp.util.Log;
import org.swixml.SwingEngine;

/**
 *
 * @author eberry
 */
public class FindFileView {
   
   private Container view;
   private static final String VIEW_FILE = "/luceneplugin/FindFileView.xml";
   
   public FindFileView() {
      SwingEngine swingEngine = new SwingEngine(this);
      Container view = null;
      try {
         URL viewUrl = getClass().getResource(VIEW_FILE);
         Log.log(Log.DEBUG, this, "Find File View - url: " + viewUrl);
         view = swingEngine.render(viewUrl);
         setView(view);
      } catch(Exception e) {
         Log.log(Log.ERROR, this, "Error rendering Find File View", e);
      }
   }

   public Container getView() {
      return view;
   }

   public void setView(Container view) {
      this.view = view;
   }
}
