/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projectbuilder
// imports {{{
import projectbuilder.command.Entry
import projectbuilder.command.ShellRunner
import projectbuilder.utils.ZipUtils

import java.util.*
import java.util.zip.*
import javax.script.ScriptContext
import java.io.File
import javax.swing.*

import org.gjt.sp.jedit.*
import org.gjt.sp.jedit.msg.*
import org.gjt.sp.util.Log
import org.gjt.sp.jedit.jEdit as JEDIT

// import org.apache.tools.ant.Project
// import org.apache.tools.ant.ProjectHelper

import projectviewer.vpt.VPTProject
import projectviewer.vpt.VPTNode
import projectviewer.event.ViewerUpdate
import projectviewer.ProjectViewer
import com.townsfolkdesigns.jedit.plugins.scripting.*
import console.Shell
import console.Console
// }}} imports
/**
 *
 * @author elberry, dradtke
 */
public class ProjectBuilderPlugin extends EditPlugin implements EBComponent {

   public static final String templateDir = getPluginHome(this).getPath()+File.separator+"templates"
   public static final String userTemplateDir = JEDIT.getSettingsDirectory()+File.separator+"project-templates"
   
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
      updateToolbar(null)
      EditBus.addToBus(this)
   }

   @Override
   public void stop() {
   	   for (view in JEDIT.getViews()) {
   	  	  ProjectToolbar.remove(view)
   	  }
   	  EditBus.removeFromBus(this)
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
   
   public void executeCommand(View view, VPTProject proj, Entry.ParsedCommand command) {
   	   String type = command.type()
   	   JEDIT.getAction("error-list-clear").invoke(view)
	   JEDIT.saveSettings()
   	   if (type.equals("SYSTEM")) {
   	   	   // Run in system shell
   	   	   String cmd = command.getProperty("cmd")
   	   	   String[] commands = new String[2]
   	   	   commands[0] = "cd \""+proj.getRootPath()+"\""
   	   	   commands[1] = cmd
   	   	   ShellRunner runner = new ShellRunner(view, "System", commands)
   	   	   runner.start()
   	   }
   	   else if (type.equals("ANT")) {
   	   	   String[] commands = new String[2]
   	   	   String buildfile = command.getProperty("buildfile")
   	   	   if (buildfile == null || buildfile.length() == 0)
				buildfile = proj.getRootPath()+File.separator+"build.xml"
		   commands[0] = "+"+buildfile
   	   	   commands[1] = "!"+command.getProperty("target")
   	   	   ShellRunner runner = new ShellRunner(view, "Ant", commands)
   	   	   runner.start()
   	   }
   }
   
   // TODO: See if there's a way to open the project properties pane with Build/Run Settings selected
   public void editSettings(View view) {}
   
   // Build Project methods {{{
   public void buildProject(View view) {
   	   buildProject(view, projectviewer.ProjectViewer.getActiveProject(view))
   }
                                
   public void buildProject(View view, VPTProject proj) {
   	   if (proj == null) {
	   	   GUIUtilities.error(view, "projectBuilder.msg.no-project", null)
	   	   return
	   }
	   String buildProp = proj.getProperty("projectBuilder.command.build")
	   if (buildProp == null || buildProp.length() == 0) {
	   	   GUIUtilities.error(view, "projectBuilder.msg.no-build-command", null)
	   	   return
	   }
	   Entry entry = new Entry(buildProp)
	   executeCommand(view, proj, entry.parse())
   }
   
   public ArrayList<Entry> getBuildCommands(VPTProject proj) {
   	   ArrayList<Entry> list = new ArrayList<Entry>()
   	   for (int i = 0; true; i++) {
   	   	   String prop = proj.getProperty("projectBuilder.command.build."+i)
   	   	   if (prop == null)
   	   	   	   break
   	   	   list.add(new Entry(prop))
   	   }
   	   return list
   }
   // }}} Build Project methods
   
   // Run Project methods {{{
   public void runProject(View view) {
   	   runProject(view, projectviewer.ProjectViewer.getActiveProject(view))
   }
   
   public void runProject(View view, VPTProject proj) {
   	   if (proj == null) {
	   	   GUIUtilities.error(view, "projectBuilder.msg.no-project", null)
	   	   return
	   }
	   String runProp = proj.getProperty("projectBuilder.command.run")
	   if (runProp == null || runProp.length() == 0) {
	   	   GUIUtilities.error(view, "projectBuilder.msg.no-run-command", null)
	   	   return
	   }
	   Entry entry = new Entry(runProp)
	   executeCommand(view, proj, entry.parse())
   }
   
   public ArrayList<Entry> getRunCommands(VPTProject proj) {
   	   ArrayList<Entry> list = new ArrayList<Entry>()
   	   for (int i = 0; true; i++) {
   	   	   String prop = proj.getProperty("projectBuilder.command.run."+i)
   	   	   if (prop == null)
   	   	   	   break
   	   	   list.add(new Entry(prop))
   	   }
   	   return list
   }
   // }}} Run Project settings
   
   // Toolbar methods {{{
   public void toggleToolbar() {
   	   boolean toolbar = !JEDIT.getBooleanProperty("options.projectBuilder.toolbar.visible")
   	   JEDIT.setBooleanProperty("options.projectBuilder.toolbar.visible", toolbar)
   	   updateToolbar(null)
   }
   
   private void updateToolbar(VPTProject proj) {
   	   boolean toolbar = JEDIT.getBooleanProperty("options.projectBuilder.toolbar.visible")
   	   if (toolbar) {
   	   	   boolean useActive = (proj == null)
   	   	   for (view in JEDIT.getViews()) {
   	   	   	   if (useActive) proj = ProjectViewer.getActiveProject(view)
   	   	   	   if (proj == null) continue
			   ProjectToolbar.create(view, proj)
		   }
	   } else {
	   	   for (view in JEDIT.getViews()) {
	   	   	   ProjectToolbar.remove(view)
	   	   }
	   }
   }
   // }}} Toolbar methods
   
   // Find template dir
   public static String findTemplateDir(String template) {
   	   try {
   	   	   // Try user template dir
		   File dir = new File(userTemplateDir)+File.separator+template
		   if (dir.exists() && dir.isDirectory()) return dir.getPath()+File.separator
		   // Try plugin home
		   dir = new File(templateDir)+File.separator+template
		   if (dir.exists() && dir.isDirectory()) return dir.getPath()+File.separator
		   return null
	   } catch (Exception e) { return null }
   }
   
   // Edit Bus
   public void handleMessage(EBMessage message) {
   	   if (message instanceof ViewUpdate) {
   	   	   ViewUpdate view_message = (ViewUpdate) message
   	   	   boolean toolbar = JEDIT.getBooleanProperty("options.projectBuilder.toolbar.visible")
   	   	   View view = view_message.getView()
   	   	   if (view_message.getWhat() == ViewUpdate.CREATED) {
   	   	   	   if (toolbar) {
   	   	   	   	   VPTProject proj = ProjectViewer.getActiveProject(view)
   	   	   	   	   if (proj != null) ProjectToolbar.create(view, proj)
   	   	   	   }
   	   	   } else if (view_message.getWhat() == ViewUpdate.CLOSED) {
   	   	   	   if (toolbar) ProjectToolbar.remove(view)
   	   	   }
   	   } else if (message instanceof ViewerUpdate) {
   	   	   ViewerUpdate viewer_message = (ViewerUpdate) message;
   	   	   VPTNode new_node = viewer_message.getNode()
   	   	   ProjectToolbar.remove(viewer_message.getView())
   	   	   if (new_node.isProject()) {
   	   	   	   updateToolbar((VPTProject) new_node)
   	   	   }
   	   }
   }
   
}

