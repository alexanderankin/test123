/*
 * Definition of an ant type for jedit build environment.
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

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.PropertySet;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.FileResourceIterator;

/** Takes filesets as nested arguments: <code>fsSrc</code> and
    <code>fsExtras</code> and retrieves plugin information.
    Alternatively <code>jar</code> attibute may be given to parse the
    jar contents instead of filesets.
    */

public class PluginInfoType extends DataType
{
  // input parameters
  private FileSet fsSrc;
  private FileSet fsExtras;
  private String sJarIn;
  
  // plugin info
  private String sClass;
  private String sJar;
  private String sJeditVersionShort;
  private String sJeditVersionFull;
  private ArrayList<Dep> deps = new ArrayList<Dep>();
  
  /** Whether the info is already filled. */
  private boolean bFilled;
  private Project p;
  
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
      if (sFile.endsWith("Plugin.java") ||
          sFile.endsWith("Plugin.class") ) {
        sPluginClass = sFile;
        if (sBaseDir != null) {
          sPluginClass = sPluginClass.substring(sBaseDir.length()+1);
        }
        sPluginClass = sPluginClass.replaceFirst("\\.((java)|(class))$", "");
        sPluginClass = sPluginClass.replaceAll("[/\\\\]", ".");
        break;
      }
    }
    return sPluginClass;
  } //}}}

  //{{{ fill() method
  public void fill()
  {
    if (isReference()) { getRef().fill(); return; }
    if (bFilled) { return; }
    Iterator itSrc, itExtras;
    String sBaseDir;
    ZipFile zip = null;
    p = getProject();
    if (sJarIn != null) {
      // process jar file to get the info
      try {
        zip = new ZipFile(sJarIn);
        sBaseDir = null;
        itSrc = Collections.list(zip.entries()).iterator();
        itExtras = itSrc;
      } catch (java.io.IOException ioe) {
        throw new BuildException(ioe);
      }
    } else {
      // filesets given as source for the info
      if (fsSrc == null) {
        throw new BuildException("fsSrc parameter not specified.");
      }
      if (fsExtras == null) {
        throw new BuildException("fsExtras parameter not specified.");
      }
      sBaseDir = fsSrc.getDir().toString(); 
      itSrc = fsSrc.iterator();
      itExtras = fsExtras.iterator();
    }
    sClass = getPluginClassName(sBaseDir, itSrc);
    sJar = GetPluginJarNameTask.getJarName(sClass);

    // load all props files {{{
    Properties props = new Properties();
    while (itExtras.hasNext()) {
      Object entry = itExtras.next();
      if (entry.toString().endsWith(".props")) {
        try {
          if (sJarIn != null) {
            ZipEntry zipEntry = (ZipEntry)entry;
            props.load(zip.getInputStream(zipEntry));
          } else {
            FileResource fr = (FileResource)entry;
            props.load(fr.getInputStream());
          }
        } catch (java.io.IOException e) {
          throw new BuildException(e);
        }
      }
    } // }}}

    parseProps(sClass, props);
    bFilled = true;
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
        sJeditVersionFull = asDeps[1];
        String v[] = sJeditVersionFull.split("\\.");
        sJeditVersionShort = v[0] + "." + v[1];
      }
      if (asDeps[0].equals("optional")) {
        // ignore the optional keyword, treat as usual plugin dep
        asDeps = Arrays.copyOfRange(asDeps, 1, asDeps.length);
      }
      if (asDeps[0].equals("plugin")) {
        Dep dep = new Dep();
        //print("" + iPluginDep + asDeps[1] + "-" + asDeps[2]);
        String sPref = "plugin.dep." + iPluginDep;
        dep.sClass = asDeps[1];
        dep.sVersion = asDeps[2];
        dep.sJar = GetPluginJarNameTask.getJarName(asDeps[1]);
        deps.add(dep);
        iPluginDep++;
      }
      
      i++;
    }
  } //}}}

  public void setJar(String s)
  {
    sJarIn = s;
    checkAttr();
  }

  public void addFsSrc(FileSet fs)
  {
    fsSrc = fs;
    checkAttr();
  }

  public void addFsExtras(FileSet fs)
  {
    fsExtras = fs;
    checkAttr();
  }

  private void checkAttr()
  {
    p = getProject();
    if (sJarIn != null && (fsSrc != null || fsExtras != null)) {
      throw new BuildException("jar and fsSrc/Extras " +
                               "are mutually exclusive."); 
    }
  }
  
  protected PluginInfoType getRef() {
    return (PluginInfoType) getCheckedRef(PluginInfoType.class,
                                          "plugininfotype");
  }

  public String toString()
  {
    if (isReference()) { return getRef().toString(); }
    String s;
    if (!bFilled) { fill(); }
    s = "Plugin class name: " + sClass + ", jar name: " + sJar + "\n";
    s += "jedit version: " + sJeditVersionFull;
    s += ", dependencies count: " + deps.size() + "\n";
    for (Dep dep: deps) {
      s += "dependency: " + dep.sJar + " " + dep.sVersion + "\n";
    }
    return s; 
  }
  
  //{{{ setProjectProperties() method
  /** Stores plugin info in project properties. For details see
      {@link GetPluginInfoTask}.
      @param sPref Prefix added to the properties. May not be
             <code>null</code>
  */
  public void setProjectProperties(String sPref)
  {
    if (isReference()) { getRef().setProjectProperties(sPref); return; }
    fill();
    PropertySet ps = (PropertySet)p.createDataType("propertyset"); 
    p.addReference(sPref + "plugin.props.set", ps); 
    p.setProperty(sPref + "plugin.class.name", sClass);
    p.setProperty(sPref + "plugin.jar.name", sJar);
    p.setProperty(sPref + "plugin.jedit.version.full", sJeditVersionFull);
    p.setProperty(sPref + "plugin.jedit.version", sJeditVersionShort);
    p.setProperty(sPref + "plugin.dep.count", "" + deps.size());
    ps.appendName(sPref + "plugin.class.name");
    ps.appendName(sPref + "plugin.jar.name");
    ps.appendName(sPref + "plugin.jedit.version.full");
    ps.appendName(sPref + "plugin.jedit.version");
    ps.appendName(sPref + "plugin.dep.count");
    for (int i=0; i<deps.size(); i++) {
      Dep dep = deps.get(i);
      String sDepPref = sPref + "plugin.dep." + i;
      p.setProperty(sDepPref + ".class", dep.sClass);
      p.setProperty(sDepPref + ".version", dep.sVersion);
      p.setProperty(sDepPref + ".jar.name", dep.sJar);
      ps.appendRegex(sDepPref + "\\.*");
    }
  } //}}}
  
  //{{{ Dep class
  /** Plugin dependency information */
  public static class Dep
  {
    String sClass;
    String sVersion;
    String sJar;
  } //}}}
}