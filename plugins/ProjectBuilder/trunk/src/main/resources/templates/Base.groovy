import groovy.swing.SwingBuilder
import javax.swing.SwingConstants as SC
import javax.swing.WindowConstants as WC
import projectbuilder.builder.ProjectBuilder as PB
import java.awt.GridBagConstraints as GBC
import org.gjt.sp.jedit.EditPlugin
import projectbuilder.ProjectBuilderPlugin
import javax.swing.*

// Get project types.
File pluginHome = EditPlugin.getPluginHome(ProjectBuilderPlugin.class) ?: new File(System.getProperty("user.dir"), "build")
if(!pluginHome.exists()) {
   pluginHome.mkdirs()
}
File templatesDir = new File(pluginHome as File, "templates")
if(!templatesDir.exists()) {
   templatesDir.mkdirs()
}
def templateTypes = []

templatesDir.eachDir { dir ->
   templateTypes << new TemplateTypeOption(name: dir.name, dir: dir, templatesDir: templatesDir)
}

// Create the form
def swing = new SwingBuilder()
def form = swing.panel() {
   gridBagLayout()
   def gbc = swing.gbc(gridx: 0, gridy: 0, weightx: 0.0f, weighty: 0.0f, gridwidth: 1, gridheight: 1, fill: GBC.HORIZONTAL)
	
   // set up the labels.
   gbc.insets = [10, 10, 0, 0]
   label(text: "Type:", horizontalAlignment: SC.RIGHT, constraints: gbc)
   gbc.insets = [5, 10, 0, 0]
   gbc.gridy++
   label(text: "Name:", horizontalAlignment: SC.RIGHT, constraints: gbc)
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
   comboBox(id: "type_field", items: templateTypes, constraints: gbc)
   gbc.gridy++
   textField(id: "name_field", columns: 40, constraints: gbc)
   gbc.gridy++
   textField(id: "directory_field", columns: 40, constraints: gbc)
   gbc.gridy++
   gbc.insets = [10, 5, 10, 0]
   label(text: "<html><small>Project will be created in [Directory]${File.separator}[Name]</small></html>", constraints: gbc)

   // add browse button
   gbc.gridy = 2
   gbc.gridx = 2
   gbc.insets = [10, 5, 0, 10]
   button(text: "...", constraints: gbc)
}

def answer = JOptionPane.showConfirmDialog(null, form, "Create a new Project", JOptionPane.OK_CANCEL_OPTION)

if(answer == JOptionPane.OK_OPTION) {
   def templateType = swing.type_field.selectedItem
   def projectName = swing.name_field.text
   def projectDir = new File(swing.directory_field.text)
   def project = new Project(name: swing.name_field.text, directory: projectDir)
   String[] roots = [templatesDir.path]
   
   println("       type: " + templateType)
   println("       name: " + projectName)
   println("        dir: " + projectDir)
   println("     script: " + templateType.scriptPath)

   Binding binding = new Binding()
   GroovyScriptEngine gse = new GroovyScriptEngine(roots)
   binding.setVariable("project", project)
   binding.setVariable("templatesDir", templatesDir)
   gse.run(templateType.scriptPath, binding)
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
}