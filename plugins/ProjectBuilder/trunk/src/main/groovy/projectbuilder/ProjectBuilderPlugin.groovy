/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package projectbuilder
import org.gjt.sp.jedit.EditPlugin
import org.gjt.sp.util.Log

/**
 *
 * @author elberry
 */
public class ProjectBuilderPlugin extends EditPlugin {

   public void start() {
      Log.log(Log.DEBUG, ProjectBuilderPlugin.class, "Start called")
      File pluginHome = this.pluginHome
      if(!pluginHome.exists()) {
         pluginHome.mkdirs()
      }
      File templatesDir = new File(pluginHome, "templates")
      if(!templatesDir.exists()) {
         templatesDir.mkdirs()
      }

      File templatesFile = EditPlugin.getResourcePath(this, "templates.zip")
      if(templatesFile.exists()) {
         ZipUtils.extract(templatesFile)
      }
   }

   public void stop() {
      
   }
	
}

