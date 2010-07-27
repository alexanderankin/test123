// imports {{{
import groovy.swing.SwingBuilder
import java.awt.GridBagConstraints as GBC
import java.awt.BorderLayout as BL
import java.awt.CardLayout
import java.awt.Dimension
import java.awt.Component
import java.awt.Font
import java.awt.Color
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
import org.gjt.sp.jedit.MiscUtilities
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
workspace = JEDIT.getProperty("projectbuilder.workspace",
	MiscUtilities.constructPath(System.getProperty("user.home"), "workspace"))

def swing = new SwingBuilder()
def state = 1
def done = false

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
			state = 2
			dialog.dispose()
		})
	}
}

def pv_options = swing.panel() { -> dialog
	boxLayout(axis:BoxLayout.Y_AXIS)
	panel(background:Color.WHITE) {
		label(id:'pv_options_title', text:'', font:new Font("SansSerif", Font.PLAIN, 18))
	}
	separator()
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
		button(text:'Create', icon:GUIUtilities.loadIcon("Save.png"), actionPerformed: {
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
				project.setRootPath(MiscUtilities.constructPath(
					"${dir}", "${name}"))
				def iconPath = MiscUtilities.constructPath(
					"${chosen.dir.getPath()}", "icon.png")
				if (new File(iconPath).exists())
					project.setIconPath(iconPath)
				project.setProperty("project.type", chosen.name)
				project.setProperty("project.template.dir", chosen.dir.getPath());
				binding.setVariable("project", project)
				binding.setVariable("workspace", dir)
				binding.setVariable("name", name)
				dialog.dispose()
			}
		})
	}
}

// TODO: Add the roots of both template dirs here
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
		swing.pv_options_title.text = "New ${chosen.toString()}"
		dialog.setContentPane(pv_options)
		dialog.pack()
		dialog.setLocationRelativeTo(view)
		//dialog.setTitle('New '+chosen.toString())
		dialog.setVisible(true)
	}
	else if (state == 3) {
		// Run the project-creation script
		state = 4
		println("       type: " + chosen.toString())
		println("       name: " + name)
		println("        dir: " + dir)
		println("     script: " + chosen.getScriptPath())
		binding.setVariable("root", MiscUtilities.constructPath(dir, name))
		def script = chosen.getScriptName()
		boolean success = gse.run(script, binding)
		if (success) {
			state = 4
		} else {
			state = 2
			println("Script failed.")
		}
	}
	else if (state == 4) {
		// Transfer properties, run the update script, and import into PV
		state = 0
		def props = new Properties()
		props.load(new FileInputStream(chosen.getProjectPropsPath()))
		for (prop in props.propertyNames()) {
			project.setProperty(prop, props.getProperty(prop))
		}
		ProjectBuilderPlugin.updateProjectConfig(project)
		def viewer = ProjectViewer.getViewer(view)
		manager.addProject(project, groups[group])
		viewer.setRootNode(project)
		new RootImporter(project, viewer, true).doImport()
		VFSManager.waitForRequests()
		for (file in binding.getVariable("open_after")) {
			JEDIT.openFile(view, MiscUtilities.constructPath("${dir}/${name}", file))
		}
		/*
		if (JEDIT.getBooleanProperty("options.projectbuilder.show-toolbar"))
			projectbuilder.actions.BeanshellToolbar.create(view, project)
		*/
	}
}

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
		def iconPath = new File(value.dir.getPath(), "icon.png").toURI().toString();
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
   
   String getScriptPath() {
   	   def scriptFile = new File(dir, "${name}.groovy")
   	   return scriptFile.path
   }

   String getScriptName() {
	   return "${name}/${name}.groovy"
   }
   
   String getProjectPropsPath() {
   	   def propsFile = new File(dir, "${name}.props")
   	   return propsFile.path
   }
   
   String toString() {
      name.replaceAll('_', ' ')
   }
}

