/*
 * PluginTestCollector.java
 * Copyright (c) 2002 Calvin Yu
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

package junit;

import java.util.Enumeration;
import java.util.Hashtable;
import junit.runner.ClassPathTestCollector;

/**
 * Test collector that uses the jEdit configured class path.
 */
public class PluginTestCollector extends ClassPathTestCollector {
   private String classPath;

   /**
    * Create a new <code>PluginTestCollector</code>.
    */
   public PluginTestCollector(String aClassPath) {
      classPath = aClassPath;
   }

   public Enumeration collectTests() {
      Hashtable result = super.collectFilesInPath(classPath);
      return result.elements();
   }
}

