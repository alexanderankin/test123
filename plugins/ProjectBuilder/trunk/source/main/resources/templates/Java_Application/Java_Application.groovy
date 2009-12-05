import projectbuilder.builder.ProjectBuilder as PB
import groovy.swing.SwingBuilder
import javax.swing.*
import org.gjt.sp.jedit.*
import org.gjt.sp.jedit.jEdit as JEDIT
import java.awt.GridBagConstraints as GBC
import javax.swing.SwingConstants as SC

def templateDir = "${templatesDir}/Java_Application"
def dist = "${project.directory.path}/${project.name}/dist"
def templateData = [:]
templateData.name = project.name

def swing = new SwingBuilder()
def form = swing.panel() {
   gridBagLayout()
   def gbc = swing.gbc(gridx: 0, gridy: 0, weightx: 0.0f, weighty: 0.0f, gridwidth: 1, gridheight: 1, fill: GBC.HORIZONTAL)
   gbc.insets = [10, 10, 0, 0]
   // Labels
   label(text: "Main class:", horizontalAlignment: SC.RIGHT, constraints: gbc)
   gbc.gridy++
   label(text: "Author:", horizontalAlignment: SC.RIGHT, constraints: gbc)
   
   // Text fields
   gbc.gridx++
   gbc.gridy = 0
   textField(id: "main_class", columns:20, constraints: gbc)
   gbc.gridy++
   textField(id: "author", columns:20, constraints: gbc)
}
def answer = JOptionPane.showConfirmDialog(view, form, "Java Project Details : ${project.name}", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, new ImageIcon("${templateDir}/icon.png"))

if (answer == JOptionPane.OK_OPTION) {
	def mainClass = swing.main_class.text
	templateData.mainClass = "${mainClass}"
	templateData.author = swing.author.text
	int dot = mainClass.lastIndexOf(".")
	def deep = (dot != -1)
	def mainClassDir = ""
	if (deep) {
		templateData.pkg = "package "+mainClass.substring(0, dot)+";"
	} else {
		templateData.pkg = ""
	}
	PB.build(project.name, project.directory.path) {
		f("build.xml", template: "${templateDir}/JavaBuild.template", templateData: templateData)
		d("build") {}
		d("lib") {}
		d("dist") {}
		d("src") {
			if (deep) {
				// Add the main class file to an arbitrarily deep directory
			} else {
				f("${mainClass}.java", template: "${templateDir}/JavaMainClass.template", templateData: templateData)
				open.add("${project.directory.path}/${project.name}/src/${mainClass}.java")
			}
		}
	}
	project.build = "ANT:all"
	project.run = "java -jar ${dist}/${project.name}.jar"
} else {
	abort = true
}
