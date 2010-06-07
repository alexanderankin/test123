// imports {{{
import groovy.swing.SwingBuilder
import java.awt.GridBagConstraints as GBC
import java.awt.BorderLayout as BL
import java.awt.CardLayout
import java.awt.Dimension
import java.awt.Component
import javax.swing.*
import javax.swing.SwingConstants as SC
import javax.swing.WindowConstants as WC
import javax.script.ScriptContext
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

import com.townsfolkdesigns.jedit.plugins.scripting.*
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

templatesDir.eachDir { dir ->
   templateTypes << new TemplateTypeOption(name: dir.name, dir: dir, templatesDir: templatesDir)
}
userTemplatesDir.eachDir { dir ->
	templateTypes << new TemplateTypeOption(name: dir.name, dir: dir, templatesDir: userTemplatesDir)
}

// Create the form
name = ""
workspace = "${JEDIT.getProperty("projectbuilder.workspace", System.getProperty("user.home"))}${File.separator}workspace"

def swing = new SwingBuilder()
def state = 1
def done = false
/*
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
   comboBox(id: "type_field", items: templateTypes, constraints: gbc)
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
}*/

def dialog = new JDialog(view, 'Create a new Project', true)
dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE)

def chosen = null
def name = null
def dir = null
def group = null

def manager = ProjectManager.getInstance()
def project
def groups

// Binding object for the project script
def binding = new Binding()
binding.setVariable("project", project)
binding.setVariable("view", view)
binding.setVariable("buffer", buffer)
binding.setVariable("editPane", editPane)
binding.setVariable("textArea", textArea)
binding.setVariable("wm", wm)
binding.setVariable("open_after", [])

// Binding object for the gui script
def gui_binding = new Binding()
gui_binding.setVariable("project", project)
gui_binding.setVariable("view", view)
gui_binding.setVariable("buffer", buffer)
gui_binding.setVariable("editPane", editPane)
gui_binding.setVariable("textArea", textArea)
gui_binding.setVariable("wm", wm)
gui_binding.setVariable("swing", new SwingBuilder())

def project_type = swing.panel() { -> dialog
	boxLayout(axis:BoxLayout.Y_AXIS)
	emptyBorder([6, 6, 6, 6], parent:true)
	def type_rend = new TypeRenderer()
	comboBox(id:'type_field', items:templateTypes, renderer:type_rend)
	rigidArea(width:0, height:5)
	separator()
	rigidArea(width:0, height:5)
	panel() { -> dialog
		boxLayout(axis:BoxLayout.X_AXIS)
		hglue()
		button(text:'Next', icon:GUIUtilities.loadIcon('ArrowR.png'), actionPerformed: {
			chosen = type_field.selectedItem;
			binding.setVariable("templateDir", "${chosen.dir.getPath()}")
			gui_binding.setVariable("templateDir", "${chosen.dir.getPath()}")
			state = 2
			dialog.dispose()
		})
	}
}

def pv_options = swing.panel() { -> dialog
	boxLayout(axis:BoxLayout.Y_AXIS)
	panel() {
		gridBagLayout()
		def gbc = swing.gbc(gridx: 0, gridy: 0, weightx: 0.0f, weighty: 0.0f, gridwidth: 1, gridheight: 1, fill: GBC.HORIZONTAL)
		// set up the labels.
		gbc.insets = [10, 10, 0, 0]
		label(text: "Name:", horizontalAlignment: SC.RIGHT, constraints: gbc)
		gbc.insets = [5, 10, 0, 0]
		gbc.gridy++
		//label(text: "Type:", horizontalAlignment: SC.RIGHT, constraints: gbc)
		//gbc.gridy++;
		label(text: "Directory:", horizontalAlignment: SC.RIGHT, constraints: gbc)
		gbc.gridy++
		label(text: "PV Group:", horizontalAlignment: SC.RIGHT, constraints: gbc)
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
		//comboBox(id: "type_field", items: templateTypes, constraints: gbc)
		//gbc.gridy++
		groups = PVUtils.listGroups()
		textField(id: "directory_field", columns: 30, text: workspace , constraints: gbc)
		gbc.gridy++
		comboBox(id: "group_field", items: PVUtils.groupNames(), constraints: gbc)
		gbc.gridy++
		gbc.insets = [10, 5, 10, 0]
		label(text: "<html><small>Project will be created in [Directory]${File.separator}[Name]</small></html>", constraints: gbc)
		// add browse button
		gbc.gridy = 1
		gbc.gridx = 2
		gbc.insets = [10, 5, 0, 10]
		button(text: "...", constraints: gbc, actionPerformed: {
			VFSFileChooserDialog chooser = new VFSFileChooserDialog(view, buffer.directory, VFSBrowser.CHOOSE_DIRECTORY_DIALOG, false, true)
			directory_field.text = chooser.selectedFiles?.getAt(0)
		})
	}
	separator()
	panel() { -> dialog
		boxLayout(axis:BoxLayout.X_AXIS)
		hglue()
		button(text:'Back', icon:GUIUtilities.loadIcon("ArrowL.png"), actionPerformed: {
			state = 1
			dialog.dispose()
		})
		button(text:'Next', icon:GUIUtilities.loadIcon("ArrowR.png"), actionPerformed: {
			def _name = name_field.text.trim()
			if (_name.length() == 0) {
				GUIUtilities.error(dialog, "projectbuilder.msg.no-name", null)
				return
			}
			def _dir = directory_field.text.trim()
			if (_dir.length() == 0) {
				GUIUtilities.error(dialog, "projectbuilder.msg.no-directory", null)
				return
			}
			try {
				def _proj = manager.getProject(_name)
				if (_proj == null)
					throw new NullPointerException()
				GUIUtilities.error(view, "projectbuilder.msg.project-exists", null)
			} catch (NullPointerException e) {
				name = _name
				dir = _dir
				group = group_field.selectedItem
				state = 3
				project = new VPTProject(name)
				project.setRootPath("${dir}${File.separator}${name}")
				project.setIconPath("${chosen.dir.getPath()}${File.separator}menu-icon.png")
				project.setProperty("project.type", chosen.toString())
				project.setProperty("project.template.dir", chosen.dir.getPath());
				binding.setVariable("project", project)
				binding.setVariable("workspace", dir)
				binding.setVariable("name", name)
				gui_binding.setVariable("project", project)
				gui_binding.setVariable("workspace", dir)
				gui_binding.setVariable("name", name)
				dialog.dispose()
			}
		})
	}
}

String[] roots = [templatesDir.path]
GroovyScriptEngine gse = new GroovyScriptEngine(roots)

while (state > 0) {
	if (state == 1) {
		// Choose a project type
		state = 0
		chosen = null
		dialog.setContentPane(project_type)
		dialog.pack()
		dialog.setSize(new Dimension(200, dialog.getHeight()))
		dialog.setLocationRelativeTo(view)
		dialog.setTitle('Create a new Project')
		dialog.setVisible(true)
		
		if (chosen == null)
			return
	}
	else if (state == 2) {
		// Set project options (name, directory, and group)
		state = 0
		dialog.setContentPane(pv_options)
		dialog.pack()
		dialog.setLocationRelativeTo(view)
		dialog.setTitle('New '+chosen.toString())
		dialog.setVisible(true)
	}
	else if (state == 3) {
		// Display the config dialog, if any
		def gui_script = new File(chosen.getGuiScriptPath())
		if (!gui_script.exists()) {
			state = 4
		} else {
			state = 0
			// set bindings
			def pane = gse.run(gui_script.getPath(), gui_binding)
			def config_dialog = swing.panel() {
				boxLayout(axis:BoxLayout.Y_AXIS)
				panel(pane)
				separator()
				panel() {
					boxLayout(axis:BoxLayout.X_AXIS)
					hglue()
					button(text:'Back', icon:GUIUtilities.loadIcon('ArrowL.png'), actionPerformed: {
						state = 2
						dialog.dispose()
					})
					button(text:'Finish', icon:GUIUtilities.loadIcon('Save.png'), actionPerformed: {
						state = 4
						dialog.dispose()
					})
				}
			}
			/*
			ScriptContext scriptContext = ScriptEngineUtilities.getDefaultScriptContext(JEDIT.getActiveView())
			ScriptEnginePlugin plugin = JEDIT.getPlugin(ScriptEnginePlugin.class.getCanonicalName())
			ScriptExecutionDelegate delegate = plugin.getScriptExecutionDelegate()
			def gui_script = new File("${chosen.dir.getPath()}${File.separator}gui.groovy")
			def pane = delegate.evaluateString(gui_script.getText(), "groovy", scriptContext)
			*/
			dialog.setContentPane(config_dialog)
			dialog.pack()
			dialog.setLocationRelativeTo(view)
			dialog.setTitle("New "+chosen.toString())
			dialog.setVisible(true)
			// Copy over defined variables to the script binding
			binding = new Binding(gui_binding.getVariables())
		}
	}
	else if (state == 4) {
		// Run the project-creation script
		state = 5
		println("       type: " + chosen.toString())
		println("       name: " + name)
		println("        dir: " + dir)
		println("     script: " + chosen.getBuilderScriptPath())
		def script = chosen.getBuilderScriptPath()
		gse.run(script, binding)
		//Macros.message(view, "Congratulations. You've just created a project.")
	}
	else if (state == 5) {
		// Import the project into project viewer
		state = 0
		def props = new Properties()
		props.load(new FileInputStream("${chosen.dir.getPath()}/project.props"))
		for (prop in props.propertyNames()) {
			project.setProperty(prop, props.getProperty(prop))
		}
		def viewer = ProjectViewer.getViewer(view)
		manager.addProject(project, groups[group])
		viewer.setRootNode(project)
		new RootImporter(project, viewer, true).doImport()
		VFSManager.waitForRequests()
		for (file in binding.getVariable("open_after")) {
			JEDIT.openFile(view, "${dir}/${name}/${file}")
		}
	}
}

/*
options = [ "Choice 1", "Choice 2" ]
pane = new JOptionPane(form, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION,
	null, options.toArray(), options[0])
dialog = pane.createDialog(view, "Create a new Project")
dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE)
dialog.setModal(true)
dialog.setVisible(true)
System.out.println("Wants input = "+pane.getWantsInput())
*/
/*
retry = true
while (retry) {
	retry = false
	def answer = JOptionPane.showConfirmDialog(view, form, "Create a new Project",
		JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE)
		//GUIUtilities.loadIcon("22x22/actions/application-run.png"))
	
	if(answer == JOptionPane.OK_OPTION) {
	   def templateType = swing.type_field.selectedItem
	   def projectGroup = groups[swing.group_field.selectedIndex]
	   def projectName = swing.name_field.text
	   def projectDir = new File(swing.directory_field.text)
	   def project = new Project(name: swing.name_field.text, directory: projectDir, build: [], run: [])
	   String[] roots = [templatesDir.path]
	   
	   if (projectName.trim().length() == 0) {
		   // ERROR: Enter project name
		   GUIUtilities.error(JEDIT.getActiveView(), "projectbuilder.msg.field-missing", [ "name" ])
		   name = projectName
		   workspace = projectDir.getPath()
		   retry = true
		   continue
	   } else if (!projectDir.exists()) {
		   // ERROR: Choose a directory
		   GUIUtilities.error(JEDIT.getActiveView(), "projectbuilder.msg.field-missing", [ "directory" ])
		   name = projectName
		   workspace = projectDir.getPath()
		   retry = true
		   continue
	   }
	   
	   // Make sure a project with the chosen name doesn't already exist   
	   ProjectManager manager = ProjectManager.getInstance()
	   try {
		   if (manager.getProject(projectName) != null) {
			   GUIUtilities.error(view, "projectbuilder.msg.project-exists", null)
			   return
		   }
	   } catch (Exception e) {
		   // Assume this means that it's null. Do nothing
	   }
	   
	   println("       type: " + templateType)
	   println("       name: " + projectName)
	   println("        dir: " + projectDir)
	   println("     script: " + templateType.scriptPath)
	   
	   JEDIT.setProperty("projectbuilder.workspace", projectDir.getPath())
	
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
			   proj.setIcon("file://"+templateType.templatesDir.getPath()+"/"+templateType.name+"/menu-icon.png")
			   proj.setRootPath(project.directory.getPath()+File.separator+project.name)
			   for (int j = 0; j<project.build.size(); j++) {
				   if (j == 0) proj.setProperty("projectbuilder.command.build", project.build.get(j))
				   proj.setProperty("projectbuilder.command.build."+j, project.build.get(j))
			   }
			   for (int k = 0; k<project.run.size(); k++) {
				   if (k == 0) proj.setProperty("projectbuilder.command.run", project.run.get(k))
				   proj.setProperty("projectbuilder.command.run."+k, project.run.get(k))
			   }
			   proj.setProperty("projectbuilder.template-dir", templateType.templatesDir.getPath())
			   proj.setProperty("projectbuilder.template-name", templateType.name)
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
}*/

class TypeRenderer extends JLabel implements ListCellRenderer {
	public TypeRenderer() {
		setOpaque(true)
	}
	public Component getListCellRendererComponent(JList list, Object value, int index,
	boolean isSelected, boolean cellHasFocus) {
		if (isSelected) {
			setBackground(list.getSelectionBackground())
			setForeground(list.getSelectionForeground())
		} else {
			setBackground(list.getBackground())
			setForeground(list.getForeground())
		}
		String iconPath = "file://${value.dir.getPath()}/menu-icon.png"
		setIcon(GUIUtilities.loadIcon(iconPath))
		setText(value.toString())
		setFont(list.getFont())
		//def item = new JLabel(value.toString(), GUIUtilities.loadIcon(iconPath), javax.swing.SwingConstants.LEFT)
		return this
	}
}

class TemplateTypeOption {
   String name
   File dir
   File templatesDir
   
   String getBuilderScriptPath() {
      File scriptFile = new File(dir, "builder.groovy")
      return scriptFile.path
   }
   
   String getGuiScriptPath() {
      File scriptFile = new File(dir, "gui.groovy")
      return scriptFile.path
   }

   String toString() {
      name.replaceAll('_', ' ')
   }
}

