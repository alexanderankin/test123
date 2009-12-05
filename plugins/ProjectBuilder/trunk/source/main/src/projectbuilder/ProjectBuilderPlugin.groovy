/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projectbuilder

import java.util.zip.*
import javax.script.ScriptContext
import javax.swing.*

import org.gjt.sp.jedit.*
import org.gjt.sp.jedit.msg.*
import org.gjt.sp.util.Log
import org.gjt.sp.jedit.jEdit as JEDIT

import org.apache.tools.ant.Project
import org.apache.tools.ant.ProjectHelper

import projectbuilder.utils.ZipUtils
import projectviewer.vpt.VPTProject
import com.townsfolkdesigns.jedit.plugins.scripting.*
import console.Shell

/**
 *
 * @author elberry
 */
public class ProjectBuilderPlugin extends EditPlugin {

   private Project building = null; // The currently-building project
   
   @Override
   public void start() {
      Log.log(Log.DEBUG, ProjectBuilderPlugin.class, "Start called")
      File pluginHome = this.pluginHome
      if (!pluginHome.exists()) {
         pluginHome.mkdirs()
      }
      File templatesDir = new File(pluginHome, "templates")
      if (!templatesDir.exists()) {
         templatesDir.mkdirs()
      }
      File templatesZipFile = new File(pluginHome, "templates.zip")
      if(!templatesZipFile.exists()) {

         PluginJAR pluginJar = getPluginJAR()
         ZipFile pluginZip = null
         try {
            pluginZip = pluginJar.getZipFile()
            ZipEntry templatesEntry = pluginZip.getEntry("templates.zip")
            if (templatesEntry) {
               Log.log(Log.DEBUG, ProjectBuilderPlugin.class, "Attempting to extract templates.zip from PluginJar to: ${templatesZipFile.path}")
               ZipUtils.copyStream(pluginZip.getInputStream(templatesEntry), templatesZipFile.newOutputStream())
            }
         } catch (Exception e) {
            Log.log(Log.ERROR, ProjectBuilderPlugin.class, "Error getting plugin zip from plugin jar", e)
         }


         File templatesFile = EditPlugin.getResourcePath(this, "templates.zip")
         Log.log(Log.DEBUG, ProjectBuilderPlugin.class, "Attempting to extract templates.zip - null: ${(templatesFile == null)} | exists: ${templatesFile.exists()} | path: ${templatesFile.path}")
         if (templatesFile?.exists()) {
            try {
               ZipUtils.extract(templatesFile, templatesDir)
            } catch (Exception e) {
               Log.log(Log.ERROR, ProjectBuilderPlugin.class, "Error extracting ${templatesFile.name} to: ${templatesDir.path}", e)
            }
         }
      }
   }

   @Override
   public void stop() {
   	  
   }

   public void createNewProject(View view, String projectType) {
      Log.log(Log.DEBUG, ProjectBuilderPlugin.class, "Create new project: ${ScriptEnginePlugin.class.getCanonicalName()}")
      ScriptEnginePlugin plugin = JEDIT.getPlugin(ScriptEnginePlugin.class.getCanonicalName())
      ScriptExecutionDelegate delegate = plugin.getScriptExecutionDelegate()

      ScriptContext scriptContext = ScriptEngineUtilities.getDefaultScriptContext(view)
      scriptContext.setAttribute("projectType", projectType, ScriptContext.ENGINE_SCOPE)
      
      File baseTemplateFile = EditPlugin.getResourcePath(this, "templates/Base.groovy")
      if(baseTemplateFile) {
         delegate.evaluateString(baseTemplateFile.getText(), "groovy", scriptContext)
      }
   }
   
   public void createNewProject(View view) {
   	   createNewProject(view, "")
   }
   
   public void buildProject(View view) {
   	   buildProject(view, projectviewer.ProjectViewer.getActiveProject(view))
   }
   
   public void buildProject(View view, VPTProject proj) {
   	   if (building != null) return
   	   String cmd = null
	   if (proj == null) {
	   	   GUIUtilities.error(view, "projectBuilder.msg.no-project", null)
	    } else {
	    	cmd = proj.getProperty("projectBuilder.command.build")
	    	if (cmd == null || cmd.length() == 0) {
	    		GUIUtilities.error(view, "projectBuilder.msg.no-build-command", null)
	    	} else {
	    		// Run the build command
	    		if (cmd.startsWith("ANT:")) {
	    			if (MiscUtilities.isToolsJarAvailable()) {
						try {
							JComponent console = view.getDockableWindowManager().getDockable("console")
							String target = cmd.substring("ANT:".length(), cmd.length())
							String buildfile = proj.getRootPath()+"/build.xml"
							Shell ant = Shell.getShell("Ant");
							console.clear()
							console.run(ant, "+"+buildfile)
							console.getShell().waitFor(console)
							console.run(ant, "!"+target)
							view.getDockableWindowManager().showDockableWindow("console")
							new BuildWatcher(console).start()
						} catch (Exception e) {
							Log.log(Log.ERROR, ProjectBuilderPlugin.class, e.toString()+": "+e.getMessage())
						} finally {
							building = null
						}
					} else {
						GUIUtilities.error(view, "projectBuilder.msg.no-tools-jar", null)
					}
	    		}
	    		else {
	    			// Run in system shell
	    			view.getDockableWindowManager().addDockableWindow("console")
	    			JComponent console = view.getDockableWindowManager().getDockable("console")
	    			console.setShell("System");
	    			Shell system = Shell.getShell("System")
	    			String cd = "cd \""+proj.getRootPath()+"\""
	    			system.execute(console, null, console.getShellState(system), null, cd)
	    			system.waitFor(console)
	    			system.execute(console, null, console.getShellState(system), null, cmd)
	    			view.getDockableWindowManager().showDockableWindow("console")
	    		}
	    	}
	   }
   }
   
   public void runProject(View view) {
   	   runProject(view, projectviewer.ProjectViewer.getActiveProject(view))
   }
   
   public void runProject(View view, VPTProject proj) {
   	   String cmd = null
	   if (proj == null) {
	   	   GUIUtilities.error(view, "projectBuilder.msg.no-project", null)
	    } else {
	    	cmd = proj.getProperty("projectBuilder.command.run")
	    	if (cmd == null || cmd.length() == 0) {
	    		GUIUtilities.error(view, "projectBuilder.msg.no-run-command", null)
	    	} else {
	    		// Run the run command
	    		view.getDockableWindowManager().addDockableWindow("console")
				JComponent console = view.getDockableWindowManager().getDockable("console")
				String cd = "cd \""+proj.getRootPath()+"\""
				Shell system = Shell.getShell("System")
				console.run(system, cd)
				system.waitFor(console)
				console.run(system, cmd)
				console.clear()
	    	}
	    }
   }
}

