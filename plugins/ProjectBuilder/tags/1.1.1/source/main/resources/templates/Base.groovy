/**
NOTE!! Editing this file will do nothing unless the plugin dir is deleted,
then this plugin is reloaded
*/
// imports {{{
import groovy.swing.SwingBuilder
import java.awt.GridBagConstraints as GBC
import javax.swing.*
import javax.swing.SwingConstants as SC
import javax.swing.WindowConstants as WC
import projectbuilder.ProjectBuilderPlugin
import projectbuilder.builder.ProjectBuilder as PB
import projectbuilder.utils.PVUtils

import org.gjt.sp.jedit.EditPlugin
import org.gjt.sp.jedit.browser.VFSBrowser
import org.gjt.sp.jedit.browser.VFSFileChooserDialog
import org.gjt.sp.jedit.jEdit as JEDIT
import org.gjt.sp.jedit.View
import org.gjt.sp.jedit.io.VFSManager
import org.gjt.sp.jedit.Macros
import org.gjt.sp.jedit.GUIUtilities
import org.gjt.sp.util.Log

import projectviewer.ProjectManager;
import projectviewer.ProjectViewer;
import projectviewer.vpt.VPTProject;
import projectviewer.vpt.VPTGroup;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTRoot;
import projectviewer.importer.RootImporter
// }}}

View view = JEDIT.getActiveView()

// Get project types.
//File pluginHome = EditPlugin.getPluginHome(ProjectBuilderPlugin.class) ?: new File(System.getProperty("user.dir"), "build")
File pluginHome = EditPlugin.getPluginHome(ProjectBuilderPlugin.class)
if(!pluginHome.exists()) {
   pluginHome.mkdirs()
}
File templatesDir = new File(ProjectBuilderPlugin.templateDir)
File userTemplatesDir = new File(ProjectBuilderPlugin.userTemplateDir)
if(!templatesDir.exists()) {
   templatesDir.mkdirs()
}
if (!userTemplatesDir.exists()) {
	userTemplatesDir.mkdir()
}
def templateTypes = []

int i = 0
int selected = 0

templatesDir.eachDir { dir ->
   templateTypes << new TemplateTypeOption(name: dir.name, dir: dir, templatesDir: templatesDir)
   if (dir.name == projectType) selected = i
   i++
}
userTemplatesDir.eachDir { dir ->
	templateTypes << new TemplateTypeOption(name: dir.name, dir: dir, templatesDir: userTemplatesDir)
	if (dir.name == projectType) selected = i
	i++
}

// Create the form
name = ""
workspace = JEDIT.getProperty("projectBuilder.workspace", System.getProperty("user.home")+File.separator+"workspace")

def swing = new SwingBuilder()
def form = swing.panel() {
   gridBagLayout()
   def gbc = swing.gbc(gridx: 0, gridy: 0, weightx: 0.0f, weighty: 0.0f, gridwidth: 1, gridheight: 1, fill: GBC.HORIZONTAL)
	
   // set up the labels.
   gbc.insets = [10, 10, 0, 0]
   label(text: "Name:", horizontalAlignment: SC.RIGHT, constraints: gbc)
   gbc.insets = [5, 10, 0, 0]
   gbc.gridy++
   label(text: "Type:", horizontalAlignment: SC.RIGHT, constraints: gbc)
   gbc.gridy++;
   label(text: "Group:", horizontalAlignment: SC.RIGHT, constraints: gbc)
   gbc.gridy++
   label(text: "Directory:", horizontalAlignment: SC.RIGHT, constraints: gbc)
   gbc.gridy++
   label(text: "*", horizontalAlignment: SC.RIGHT, constraints: gbc)

   // set up fields
   gbc.gridx = 1
   gbc.gridy = 0
   gbc.weightx = 1.0f
   gbc.weighty = 0.0f
   gbc.insets = [10, 5, 0, 0]
   textField(id: "name_field", columns: 30, text: name, constraints: gbc)
   gbc.gridy++
   comboBox(id: "type_field", items: templateTypes, constraints: gbc, selectedIndex: selected)
   gbc.gridy++
   groups = PVUtils.listGroups()
   comboBox(id: "group_field", items: PVUtils.groupNames(), constraints: gbc)
   gbc.gridy++
   textField(id: "directory_field", columns: 30, text: workspace , constraints: gbc)
   gbc.gridy++
   gbc.insets = [10, 5, 10, 0]
   label(text: "<html><small>Project will be created in [Directory]${File.separator}[Name]</small></html>", constraints: gbc)

   // add browse button
   gbc.gridy = 3
   gbc.gridx = 2
   gbc.insets = [10, 5, 0, 10]
   button(text: "...", constraints: gbc, actionPerformed: {
      VFSFileChooserDialog chooser = new VFSFileChooserDialog(view, buffer.directory, VFSBrowser.CHOOSE_DIRECTORY_DIALOG, false, true)
      directory_field.text = chooser.selectedFiles?.getAt(0)
   })
}

// QUESTION: Should we find a custom icon for this? I like this better than the generic question mark
retry = true
while (retry) {
	retry = false
	def answer = JOptionPane.showConfirmDialog(view, form, "Create a new Project",
		JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
		GUIUtilities.loadIcon("22x22/actions/application-run.png"))
	
	if(answer == JOptionPane.OK_OPTION) {
	   def templateType = swing.type_field.selectedItem
	   def projectGroup = groups[swing.group_field.selectedIndex]
	   def projectName = swing.name_field.text
	   def projectDir = new File(swing.directory_field.text)
	   def project = new Project(name: swing.name_field.text, directory: projectDir, build: [], run: [])
	   String[] roots = [templatesDir.path]
	   
	   if (projectName.trim().length() == 0) {
		   // ERROR: Enter project name
		   GUIUtilities.error(JEDIT.getActiveView(), "projectBuilder.msg.field-missing", [ "name" ])
		   name = projectName
		   workspace = projectDir.getPath()
		   retry = true
		   continue
	   } else if (!projectDir.exists()) {
		   // ERROR: Choose a directory
		   GUIUtilities.error(JEDIT.getActiveView(), "projectBuilder.msg.field-missing", [ "directory" ])
		   name = projectName
		   workspace = projectDir.getPath()
		   retry = true
		   continue
	   }
	   
	   // Make sure a project with the chosen name doesn't already exist   
	   ProjectManager manager = ProjectManager.getInstance()
	   try {
		   if (manager.getProject(projectName) != null) {
			   GUIUtilities.error(view, "projectBuilder.msg.project-exists", null)
			   return
		   }
	   } catch (Exception e) {
		   // Assume this means that it's null. Do nothing
	   }
	   
	   println("       type: " + templateType)
	   println("       name: " + projectName)
	   println("        dir: " + projectDir)
	   println("     script: " + templateType.scriptPath)
	   
	   JEDIT.setProperty("projectBuilder.workspace", projectDir.getPath())
	
	   Binding binding = new Binding()
	   GroovyScriptEngine gse = new GroovyScriptEngine(roots)
	   binding.setVariable("project", project)
	   binding.setVariable("templatesDir", templatesDir)
	   binding.setVariable("view", view)
	   binding.setVariable("buffer", buffer)
	   binding.setVariable("editPane", editPane)
	   binding.setVariable("textArea", textArea)
	   binding.setVariable("wm", wm)
	   binding.setVariable("scriptPath", templateType.scriptPath);
	   binding.setVariable("abort", false)
	   binding.setVariable("open", [])
	   gse.run(templateType.scriptPath, binding)
	   
	   // If abort wasn't set to true, create the project in ProjectViewer
	   if (!binding.getVariable("abort")) {
		   try {
			   view.getDockableWindowManager().addDockableWindow("projectviewer")
			   VPTProject proj = new VPTProject(project.name)
			   proj.setRootPath(project.directory.getPath()+File.separator+project.name)
			   for (int j = 0; j<project.build.size(); j++) {
				   if (j == 0) proj.setProperty("projectBuilder.command.build", project.build.get(j))
				   proj.setProperty("projectBuilder.command.build."+j, project.build.get(j))
			   }
			   for (int k = 0; k<project.run.size(); k++) {
				   if (k == 0) proj.setProperty("projectBuilder.command.run", project.run.get(k))
				   proj.setProperty("projectBuilder.command.run."+k, project.run.get(k))
			   }
			   proj.setProperty("projectBuilder.template-dir", templateType.templatesDir.getPath())
			   proj.setProperty("projectBuilder.template-name", templateType.name)
			   ProjectViewer viewer = ProjectViewer.getViewer(view)
			   manager.addProject(proj, projectGroup)
			   viewer.setRootNode(proj)
			   new RootImporter(proj, viewer, true).doImport()
			   VFSManager.waitForRequests()
			   for (String s : binding.getVariable("open")) {
				   JEDIT.openFile(view, s)
			   }
		   } catch (Exception e) {
			   Log.log(Log.ERROR, ProjectBuilderPlugin.class, e.toString()+": "+e.getMessage())
			   e.printStackTrace()
		   }
	   }
	}
}

class TemplateTypeOption {
   String name
   File dir
   File templatesDir
   
   String getScriptPath() {
      File scriptFile = new File(dir, "${name}.groovy")
      return scriptFile.path - "${templatesDir.path}${File.separator}"
   }

   String toString() {
      name.replaceAll('_', ' ')
   }
}

class Project {
   String name
   File directory
   ArrayList build
   ArrayList run
}