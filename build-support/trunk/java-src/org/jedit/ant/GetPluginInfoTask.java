/*
 * Definition of a task for jedit build environment.
 * :tabSize=2:indentSize=2:noTabs=true:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2012 Jarek Czekalski
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.jedit.ant;

import java.io.File;
import java.util.Iterator;
import java.util.Properties;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.PropertySet;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.FileResourceIterator;

/** Takes fileset ids as arguments: <code>fsSrc</code> and
    <code>fsExtras</code> and generates plugin information.
    Sets the following properties:<ul>
    <li>plugin.class.name - e.g. projectviewer.ProjectPlugin
    <li>plugin.jar.name - e.g. ProjectViewer
    <li>plugin.jedit.version.full - e.g. 4.4.99.0
    <li>plugin.jedit.version - e.g. 4.4
    <li>plugin.dep.X.class - e.g. CommonControlsPlugin, X=0..n
    <li>plugin.dep.X.version - e.g. 1.3
    <li>plugin.dep.X.jar.name - e.g. CommonControls
    </ul>
    and a property set <code>plugin.props.set</code>.
    */

public class GetPluginInfoTask extends Task
{
  private FileSet fsSrc;
  private FileSet fsExtras;
  private Project p;
  private PropertySet ps;
  
  //{{{ getPluginClassName method 
  /** Gets an <code>Iterator</code> over objects implementing
      <code>toString</code> and discovers the plugin name.
      The strings are treated as filenames and the filename ending
      with <code>Plugin.java</code> denotes the plugin name.
      This is the same as done in
      <code>org.gjt.sp.jedit.PluginJAR.generateCache()</code>.
      @param sBaseDir The base directory will be substracted from plugin
                      filename to get only the part containing the
                      package name. May be <code>null</code>
      @param it The <code>iterator</code> over <code>Object</code>s,
                which implement <code>toString()</code>
      @return <code>null</code> if not a plugin.
      */
  public static String getPluginClassName(String sBaseDir, Iterator it) {
    String sPluginClass = null;
    while (it.hasNext()) {
      String sFile = it.next().toString();
      if (sFile.endsWith("Plugin.java")) {
        sPluginClass = sFile.substring(sBaseDir.length()+1);
        sPluginClass = sPluginClass.replaceFirst("\\.java$", "");
        sPluginClass = sPluginClass.replaceAll("[/\\\\]", ".");
        break;
      }
    }
    return sPluginClass;
  } //}}}

  //{{{ parsePropse method 
  /** Reads dependencies from properties.
    * See <code>PluginJAR.checkDependencies()</code> */
  private void parseProps(String sPluginClass, Properties props)
  {
    int i, iPluginDep;
    i = 0; iPluginDep = 0;
    String sDepPropName = "plugin." + sPluginClass + ".depend.";
    String sDep;
    while((sDep = props.getProperty(sDepPropName + i)) != null) {
      String asDeps[] = sDep.split(" ");
      if (asDeps[0].equals("jedit")) {
        p.setProperty("plugin.jedit.version.full", asDeps[1]);
        String v[] = asDeps[1].split("\\.");
        getProject().setProperty("plugin.jedit.version", v[0] + "." + v[1]);
        ps.appendName("plugin.jedit.version.full");
        ps.appendName("plugin.jedit.version");
      }
      if (asDeps[0].equals("optional")) {
        // ignore the optional keyword, treat as usual plugin dep
        asDeps = java.util.Arrays.copyOfRange(asDeps, 1, asDeps.length);
      }
      if (asDeps[0].equals("plugin")) {
        //print("" + iPluginDep + asDeps[1] + "-" + asDeps[2]);
        String sPref = "plugin.dep." + iPluginDep;
        String sDepPluginClass = asDeps[1];
        p.setProperty(sPref + ".class", sDepPluginClass);
        p.setProperty(sPref + ".version", asDeps[2]);
        ps.appendRegex(sPref + "\\.*");
        String sJarName = GetPluginJarNameTask.getJarName(asDeps[1]);
        p.setProperty(sPref + ".jar.name", sJarName);
        iPluginDep++;
      }
      
      i++;
    }
    p.setProperty("plugin.dep.count", "" + iPluginDep);
  } //}}}
  
  @Override
  public void execute()
  {
    p = getProject();
    if (fsSrc == null) {
      throw new BuildException("fsSrc parameter not specified.");
    }
    if (fsExtras == null) {
      throw new BuildException("fsSrc parameter not specified.");
    }
        
    String sPluginClass = getPluginClassName(fsSrc.getDir().toString(),
                                             fsSrc.iterator());
    getProject().setProperty("plugin.class.name", sPluginClass);
    ps = (PropertySet)getProject().createDataType("propertyset"); 
    getProject().addReference("plugin.props.set", ps); 
    //print("no src files:" + fsSrc.size());
        
    ps.appendName("plugin.class.name");
    ps.appendName("plugin.jar.name");
    ps.appendName("plugin.dep.count");

    // load all props files {{{
    Properties props = new Properties();
    FileResourceIterator it = (FileResourceIterator)fsExtras.iterator();
    while (it.hasNext()) {
      FileResource fr = (FileResource)it.next();
      if (fr.toString().endsWith(".props")) {
        try {
          props.load(fr.getInputStream());
        }
        catch (java.io.IOException e) {
          throw new BuildException(e);
        }
      }
    } // }}}
    
    parseProps(sPluginClass, props);
  }
  
  public void setFsSrc(String sId)
  {
    Object o = getProject().getReference(sId);
    if (o == null || !(o instanceof FileSet)) {
      throw new BuildException("Fileset id fsSrc not correct.");
    }
    fsSrc = (FileSet)o;
  }

  public void setFsExtras(String sId)
  {
    Object o = getProject().getReference(sId);
    if (o == null || !(o instanceof FileSet)) {
      throw new BuildException("Fileset id fsExtras not correct.");
    }
    fsExtras = (FileSet)o;
  }
}