/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projectbuilder
// imports {{{
import projectbuilder.utils.ZipUtils
import projectbuilder.actions.BeanshellToolbar

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
      BeanShell.getNameSpace().addCommandPath("/bsh", getClass());
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
            Log.log(Log.DEBUG, ProjectBuilderPlugin.class, "templatesEntry = "+templatesEntry);
            if (templatesEntry) {
               Log.log(Log.DEBUG, ProjectBuilderPlugin.class, "Attempting to extract templates.zip from PluginJar to: ${templatesZipFile.path}")
               Log.log(Log.DEBUG, ProjectBuilderPlugin.class, "input stream = "+pluginZip.getInputStream(templatesEntry));
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
      for (view in JEDIT.getViews()) {
      	  def viewer = ProjectViewer.getViewer(view)
      	  def project
      	  if (JEDIT.getBooleanProperty("options.projectbuilder.show-toolbar") && viewer != null && (project = viewer.getActiveProject(view)) != null) {
      	  	  BeanshellToolbar.create(view, project)
      	  }
      }
      EditBus.addToBus(this)
   }

   @Override
   public void stop() {
   	  EditBus.removeFromBus(this)
   	  for (view in JEDIT.getViews()) {
   	  	  println("view = ${view}")
   	  	  BeanshellToolbar.remove(view)
   	  }
   }

   public void createNewProject(View view, String projectType) {
      Log.log(Log.DEBUG, ProjectBuilderPlugin.class, "Create new project: ${ScriptEnginePlugin.class.getCanonicalName()}")
      ScriptEnginePlugin plugin = JEDIT.getPlugin(ScriptEnginePlugin.class.getCanonicalName())
      ScriptExecutionDelegate delegate = plugin.getScriptExecutionDelegate()

      ScriptContext scriptContext = ScriptEngineUtilities.getDefaultScriptContext(view)
      //scriptContext.setAttribute("projectType", projectType, ScriptContext.ENGINE_SCOPE)
      
      File baseTemplateFile = EditPlugin.getResourcePath(this, "templates/Base.groovy")
      if(baseTemplateFile) {
         delegate.evaluateString(baseTemplateFile.getText(), "groovy", scriptContext)
      }
   }
   
   public void createNewProject(View view) {
   	   createNewProject(view, "")
   }
   
   public static String getTemplatePathForProject(VPTProject project) {
   	   return findTemplateDir(project.getProperty("project.type"))
   }
   
   // Find template dir
   public static String findTemplateDir(String template) {
   	   try {
   	   	   // Try user template dir
		   File dir = new File(userTemplateDir+File.separator+template)
		   if (dir.exists() && dir.isDirectory()) return dir.getPath()
		   // Try plugin home
		   dir = new File(templateDir+File.separator+template)
		   if (dir.exists() && dir.isDirectory()) return dir.getPath()
		   return null
	   } catch (Exception e) { return null }
   }
   
   public static ArrayList getTemplateNames() {
   	   def list = []
   	   File template_dir = new File(userTemplateDir)
   	   if (template_dir.exists()) {
		   template_dir.eachDir { dir ->
			   list << dir.name
		   }
	   }
   	   template_dir = new File(templateDir)
   	   if (template_dir.exists()) {
   	   	   template_dir.eachDir { dir ->
   	   	   	   list << dir.name
   	   	   }
   	   }
   	   return list
   }
   
   public static ArrayList<ArrayList<String>> getBeanshellScripts(VPTProject project) {
		def list = new ArrayList<String[]>()
		def scripts = project.getProperty("project.bsh")
		if (scripts == null) {
			list.add(["No scripts found", null])
			return list
		}
		def tokenizer = new StringTokenizer(scripts)
		while (tokenizer.hasMoreTokens()) {
			def token = tokenizer.nextToken()
			if (token.equals("-")) {
				list.add(["-", null])
				continue
			}
			def prefix = "project.bsh."+token
			def label = project.getProperty(prefix+".label")
			def script = project.getProperty(prefix+".script")
			if (label != null && script != null)
				list.add([label, script])
		}
		return list
	}
	
	public static void toggleToolbar() {
		def visible = !JEDIT.getBooleanProperty("options.projectbuilder.show-toolbar")
		JEDIT.setBooleanProperty("options.projectbuilder.show-toolbar", visible)
		if (visible) {
			for (view in JEDIT.getViews()) {
				def viewer = ProjectViewer.getViewer(view)
				def project = viewer.getActiveProject(view)
				if (project != null) {
					BeanshellToolbar.create(view, project)
				}
			}
		} else {
			for (view in JEDIT.getViews()) {
				println("removing: ${view}")
				BeanshellToolbar.remove(view)
			}
		}
	}
	
	public static void updateProjectConfig(VPTProject project) {
		/*
		ScriptEnginePlugin plugin = JEDIT.getPlugin(ScriptEnginePlugin.class.getCanonicalName())
		ScriptExecutionDelegate delegate = plugin.getScriptExecutionDelegate()
		ScriptContext scriptContext = ScriptEngineUtilities.getDefaultScriptContext(JEDIT.getActiveView())
		def templateDir = getTemplatePathForProject(project)
		
		scriptContext.setAttribute("project", project, ScriptContext.ENGINE_SCOPE)
		scriptContext.setAttribute("name", project.getName(), ScriptContext.ENGINE_SCOPE)
		scriptContext.setAttribute("workspace", new File(project.getRootPath()).getParent(), ScriptContext.ENGINE_SCOPE)
		scriptContext.setAttribute("templateDir", templateDir, ScriptContext.ENGINE_SCOPE)
		*/
		
		def templateDir = getTemplatePathForProject(project)
		String[] roots = [templateDir]
		GroovyScriptEngine gse = new GroovyScriptEngine(roots)
		Binding binding = new Binding()
		binding.setVariable("project", project)
		binding.setVariable("name", project.getName())
		binding.setVariable("workspace", new File(project.getRootPath()).getParent())
		binding.setVariable("templateDir", templateDir)
		def view = JEDIT.getActiveView()
		binding.setVariable("view", view)
		binding.setVariable("viewer", ProjectViewer.getViewer(view))

		File updateScript = new File(templateDir, "update.groovy")
		if (updateScript.exists()) {
			//delegate.evaluateString(updateScript.getText(), "groovy", scriptContext)
			gse.run(updateScript.getPath(), binding)
		}
	}
   
   // Edit Bus
   public void handleMessage(EBMessage message) {
   	   if (message instanceof ViewUpdate) {
   	   	   def update = (ViewUpdate) message
   	   	   if (update.getWhat() == ViewUpdate.CLOSED) {
   	   	   	   BeanshellToolbar.remove(update.getView())
   	   	   } else if (update.getWhat() == ViewUpdate.CREATED) {
   	   	   	   if (JEDIT.getBooleanProperty("options.projectbuilder.show-toolbar")) {
   	   	   	   	   def view = update.getView()
   	   	   	   	   def viewer = ProjectViewer.getViewer(view)
   	   	   	   	   def project
   	   	   	   	   if (viewer != null && (project = viewer.getActiveProject(view)) != null) {
   	   	   	   	   	   BeanshellToolbar.create(view, project)
   	   	   	   	   }
   	   	   	   }
   	   	   }
   	   	   return
   	   } else if (message instanceof ViewerUpdate) {
   	   	   def update = (ViewerUpdate) message
   	   	   def view = update.getView()
   	   	   if (update.getType() == ViewerUpdate.Type.PROJECT_LOADED && JEDIT.getBooleanProperty("options.projectbuilder.show-toolbar")) {
   	   	   	   BeanshellToolbar.create(view, (VPTProject) update.getNode())
   	   	   } else if (update.getType() == ViewerUpdate.Type.GROUP_ACTIVATED) {
   	   	   	   BeanshellToolbar.remove(view)
   	   	   }
   	   	   return
   	   }
   	   /*
   	   def viewer = ProjectViewer.getViewer(view)
   	   def project = null
   	   if (viewer != null)
   	   	   project = viewer.getActiveProject(view)
   	   if (message instanceof ViewerUpdate) {
   	   	   def update = (ViewerUpdate) message
   	   	   if (update.getType() == ViewerUpdate.Type.PROJECT_LOADED) {
   	   	   	   project = (VPTProject) update.getNode()
   	   	   	   println("Project Loaded: ${project}")
   	   	   }
   	   }
   	   if () {
   	   	   if (project != null)
   	   	   	   BeanshellToolbar.create(view, project)
   	   	   else if (BeanshellToolbar.exists(view) && project == null)
   	   	   	   BeanshellToolbar.remove(view)
   	   } else if (!JEDIT.getBooleanProperty("options.projectbuilder.show-toolbar") && BeanshellToolbar.exists(view)) {
   	   	   BeanshellToolbar.remove(view)
   	   }
   	   */
   }
   
}

