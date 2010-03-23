/*
* 16:57:45 23/10/99
*

* XInsertPlugin.java - Insert pane based on XML
* Original version Copyright (C) 1999 Romain Guy -  powerteam@chez.com
* This version Copyright (C) 2000 Dominic Stolerman - dominic@sspd.org.uk
* www.chez.com/powerteam
* Changes (c) 2005 by Martin Raspe - hertzhaft@biblhertz.it
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

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.OptionsDialog;
import org.gjt.sp.util.Log;

import java.io.*;

import java.util.Properties;
import java.util.Date;
import java.util.Enumeration;
import java.text.DateFormat;


/**
*  Description of the Class
*

* @author     Dominic Stolerman

 * The <code>Plugin</code> implementation for the XInsert window
**/

public class XInsertPlugin extends EditPlugin {

  private static Properties variables;

/**
  * plugin name
  */
  public final static String NAME = "XInsert";

  public String getName() {
    return NAME;
  }

/**
  * Starts the plugin, loads variables.
  */
  public void start() {
    String sep = System.getProperty("file.separator");
    if(jEdit.getProperty("xinsert.inserts-directory") == null 
    || jEdit.getProperty("xinsert.inserts-directory").equals("")) {
      String defDir = jEdit.getSettingsDirectory() + sep + "xinsert" + sep;
      jEdit.setProperty("xinsert.inserts-directory", defDir);
      Log.log(Log.MESSAGE, this, ("XInsert Inserts Directory set to: " + defDir));
      }
    variables = new Properties();
    try {
      InputStream is = jEdit.class.getResourceAsStream("net.sourceforge.jedit.xinsert.variables");
      if(is != null) {
        loadVariables(is);
        }
      }
    catch(IOException e) {
      Log.log(Log.ERROR, XInsertPlugin.class, "Error loading system variables");
      Log.log(Log.ERROR, XInsertPlugin.class, e);
      }
    try {
      File glVars = new File(MiscUtilities.constructPath(jEdit.getSettingsDirectory(), "xinsert" + sep + "variables"));
      if(glVars.exists() && glVars.isFile()) {
        BufferedInputStream buf = new BufferedInputStream(new FileInputStream(glVars));
        loadVariables(buf);
        }
      }
    catch(IOException e) {
      Log.log(Log.ERROR, XInsertPlugin.class, "Error loading user defined global variables");
      Log.log(Log.ERROR, XInsertPlugin.class, e);
      }
    }

/**
  * Stops the plugin, saves variables
  */
  public void stop() {
    File glVars;
    try {
      File gldir = new File(jEdit.getSettingsDirectory(), "xinsert");
      gldir.mkdirs();
      glVars = new File(gldir, "variables");
      glVars.createNewFile();
      BufferedOutputStream buf = new BufferedOutputStream(new FileOutputStream(glVars));
      saveVariables(buf);
      }
    catch(IOException e) {
      Log.log(Log.ERROR, XInsertPlugin.class, "Error saving user defined global variables");
      Log.log(Log.ERROR, XInsertPlugin.class, e);
      }
    }

  public static void setVariable(String name, String value) {
    variables.put(name, value);
    }

  public static String getVariable(String name) {
    return (String)variables.get(name);
    }

  public static Enumeration getVariables() {
    return variables.propertyNames();
    }

  public static int getVariablesSize() {
    return variables.size();
    }

  public static String getViewSpecificVariable(View view, String key) {
    Buffer buffer = view.getBuffer();
    if(key.equals("path")) {
      return Utilities.replace(buffer.getPath(), "\\", "\\\\");
      }
    else if(key.equals("name")) {
      return Utilities.replace(buffer.getName(), "\\", "\\\\");
      }
    else if(key.equals("filename")) {
      String path = buffer.getPath();
      if(path != null) return (new File(path)).getName();
      }
    else if(key.equals("directory")) {
      File path = new File(buffer.getPath());
      if(path != null) return Utilities.replace(path.getParent(), "\\", "\\\\");
      }
    else if(key.equals("date")) {
      DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);
      return df.format(new Date(System.currentTimeMillis()));
      }
    else if(key.equals("time")) {
      DateFormat df = DateFormat.getTimeInstance(DateFormat.LONG);
      return df.format(new Date(System.currentTimeMillis()));
      }
    else if(key.equals("datetime")) {
      DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
      return df.format(new Date(System.currentTimeMillis()));
      }
    else if(key.equals("selected")) {
      return view.getTextArea().getSelectedText();
      }
    return null;
    }

  public static boolean containsVariable(String name) {
    return variables.containsKey(name);
    }

  public static void loadVariables(InputStream in) throws IOException {
    variables.load(in);
    }

  public static void saveVariables(OutputStream out) {
    try {
      variables.store(out, "XInsert Global Variables File");
      }
    catch(IOException e) {
      Log.log(Log.ERROR, XInsertPlugin.class,
         "Failure to write XInsert variables to disk.");
      Log.log(Log.ERROR, XInsertPlugin.class, e);
      }
    }

  public static void clearVariables() {
    variables.clear();
    }


  }

