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
				// This code is essentially a workaround to be able to have the folder structure
				// replicate the given package. There should hopefully be a better way to do this
				def pkg = mainClass.substring(0, dot)
				def dir = pkg.replace(".", File.separator)
				def cls = mainClass.substring(dot+1, mainClass.length())
				def file = cls+".java"
				new File("${project.directory.path}/${project.name}/src/${dir}").mkdirs()
				PrintWriter pw = new PrintWriter("${project.directory.path}/${project.name}/src/${dir}/${file}")
				pw.println("package ${pkg};")
				pw.println("public class ${cls} {")
				pw.println("	public static void main(String[] args) {")
				pw.println("		")
				pw.println("	}")
				pw.println("}")
				pw.close()
				open.add("${project.directory.path}/${project.name}/src/${dir}/${file}")
			} else {
				f("${mainClass}.java", template: "${templateDir}/JavaMainClass.template", templateData: templateData)
				open.add("${project.directory.path}/${project.name}/src/${mainClass}.java")
			}
		}
	}
	// Add build and clean commands
	project.build.add("Build Executable:ANT[target=,buildfile=${project.directory.path}/${project.name}/build.xml]")
	project.build.add("Clean:ANT[target=clean,buildfile=${project.directory.path}/${project.name}/build.xml]")
	// Add a system command to the project's run settings
	project.run.add("Run Executable:SYSTEM[cmd=java -jar ${dist}/${project.name}.jar]")
} else {
	abort = true
}
