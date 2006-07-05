/*
 * JEditTestCaseClassLoader.java
 * :tabSize=4:indentSize=4:noTabs=true:
 * Copyright (c) 2001, 2002 Andre Kaplan
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
 
 import junit.runner.TestCaseClassLoader;
 import org.gjt.sp.jedit.JARClassLoader;
 import org.gjt.sp.util.Log;
 
 public class JEditTestCaseClassLoader extends TestCaseClassLoader {
     
     public JEditTestCaseClassLoader() {
         super();
     }
     
     public JEditTestCaseClassLoader(String classPath) {
         super(classPath);
     }
     
     public synchronized Class loadClass(String name, boolean resolve)
     throws ClassNotFoundException {
         try {
             return super.loadClass(name, resolve);
         } catch (ClassNotFoundException e) {}
         
         if (isExcluded(name)) {
             try {
                 return ((JARClassLoader) junit.JUnitPlugin.class.getClassLoader())
                 .loadClass(name, resolve);
             } catch (ClassNotFoundException e) {}
         }
         throw new ClassNotFoundException(name);
     }
 }
