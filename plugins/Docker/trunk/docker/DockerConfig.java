/*
 * DockerConfig.java
 * :tabSize=3:indentSize=3:noTabs=true:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2003 Calvin Yu
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package docker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.gjt.sp.jedit.jEdit;

class DockerConfig {
   private List autoHideOverrides;

   /**
    * Sets whether a given dock has auto hide enabled.
    */
   public void setAutoHideEnabled(String dockName, boolean enabled) {
      jEdit.setBooleanProperty(formatPropertyName(dockName + ".enabled"), enabled);
   }

   /**
    * Returns <code>true</code> if the named dock has auto hide enabled.
    */
   public boolean isAutoHideEnabled(String dockName) {
      return jEdit.getBooleanProperty(formatPropertyName(dockName + ".enabled"), false);
   }

   /**
    * Returns <code>true</code> if overriding should be disabled for the given
    * dockable.
    */
   public boolean isAutoHideOverride(String dockable) {
      loadAutoHideOverrides();
      return autoHideOverrides.contains(dockable);
   }

   /**
    * Returns the list dockables that will override the auto hide feature of
    * docker.
    */
   public List getAutoHideOverrides() {
      loadAutoHideOverrides();
      return autoHideOverrides;
   }

   /**
    * Set the list of dockables that will override the auto hide feature of
    * docker.
    */
   public void setAutoHideOverrides(List dockables) {
      StringBuffer buf = new StringBuffer();
      for (Iterator i = dockables.iterator(); i.hasNext();) {
         String dockable = (String) i.next();
         buf.append(dockable);
         if (i.hasNext()) {
            buf.append(',');
         }
      }
      setProperty("auto-hide-overrides", buf.toString());
      autoHideOverrides = null;
   }

   /**
    * Returns an a docker property.
    */
   public String getProperty(String name) {
      return jEdit.getProperty(formatPropertyName(name));
   }

   /**
    * Returns an a docker property.
    */
   public String getProperty(String name, String def) {
      return jEdit.getProperty(formatPropertyName(name), def);
   }

   /**
    * Set a docker property.
    */
   public void setProperty(String name, String val) {
      jEdit.setProperty(formatPropertyName(name), val);
   }

   /**
    * Format a property name.
    */
   private String formatPropertyName(String name) {
      return "docker." + name;
   }

   /**
    * Load the auto hide overrides if it isn't already loaded.
    */
   private void loadAutoHideOverrides() {
      if (autoHideOverrides == null) {
         autoHideOverrides = new ArrayList();
         StringTokenizer strtok = new StringTokenizer(getProperty("auto-hide-overrides", ""), ",");
         while (strtok.hasMoreTokens()) {
            autoHideOverrides.add(strtok.nextToken());
         }
      }
   }
}

