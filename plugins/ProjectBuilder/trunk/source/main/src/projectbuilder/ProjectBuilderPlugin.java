/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projectbuilder;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.PluginJAR;
import org.gjt.sp.util.Log;
import projectbuilder.utils.ZipUtils;

/**
 *
 * @author elberry
 */
public class ProjectBuilderPlugin extends EditPlugin {

   @Override
   public void start() {
      Log.log(Log.DEBUG, ProjectBuilderPlugin.class, "Start called");
      File pluginHome = this.getPluginHome();
      if (!pluginHome.exists()) {
         pluginHome.mkdirs();
      }
      File templatesDir = new File(pluginHome, "templates");
      if (!templatesDir.exists()) {
         templatesDir.mkdirs();
      }
      File templatesZipFile = new File(pluginHome, "templates.zip");
      if(!templatesZipFile.exists()) {

         PluginJAR pluginJar = getPluginJAR();
         ZipFile pluginZip = null;
         try {
            pluginZip = pluginJar.getZipFile();
            ZipEntry templatesEntry = pluginZip.getEntry("templates.zip");
            if (templatesEntry != null) {
               Log.log(Log.DEBUG, ProjectBuilderPlugin.class, "Attempting to extract templates.zip from PluginJar to: " + templatesZipFile.getPath());
               ZipUtils.copyStream(pluginZip.getInputStream(templatesEntry), new BufferedOutputStream(new FileOutputStream(templatesZipFile)));
            }
         } catch (Exception e) {
            Log.log(Log.ERROR, ProjectBuilderPlugin.class, "Error getting plugin zip from plugin jar", e);
         }


         File templatesFile = EditPlugin.getResourcePath(this, "templates.zip");
         Log.log(Log.DEBUG, ProjectBuilderPlugin.class, "Attempting to extract templates.zip - null: " + (templatesFile == null) + " | exists: " + templatesFile.exists() + " | path: " + templatesFile.getPath());
         if (templatesFile != null && templatesFile.exists()) {
            try {
               ZipUtils.extract(templatesFile, templatesDir);
            } catch (Exception e) {
               Log.log(Log.ERROR, ProjectBuilderPlugin.class, "Error extracting " + templatesFile.getName() + " to: " + templatesDir.getPath(), e);
            }
         }
      }
   }

   @Override
   public void stop() {
   }
}

