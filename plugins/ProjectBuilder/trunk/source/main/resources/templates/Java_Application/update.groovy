// Update config script for Java Application

//{{{ imports
import org.gjt.sp.jedit.jEdit
import org.gjt.sp.util.Log
import javax.swing.JOptionPane
import projectviewer.importer.RootImporter
import projectbuilder.builder.ProjectBuilder as PB
import projectbuilder.ProjectBuilderPlugin
//}}}

def templateData = [:]

// NOTE: If this method is going to be used, it can't be nested within if statements
// an error about java.io.File.call() not being applicable will come up
// Instead, start creating it here and then determine what to create via if-statements inside
def new_files = false
PB.build(name, workspace) {
	d("src") {
		def mainclass = project.getProperty("project.config.mainclass").trim()
		if (mainclass.length() > 0) {
			def classToPath = mainclass.replace('.', File.separator)
			def testFile = new File("${workspace}/${name}/src", "${classToPath}.java")
			if (!testFile.exists()) {
				def pkg = ""
				def classname = mainclass
				def dot = mainclass.lastIndexOf('.')
				if (dot != -1) {
					pkg = "package ${mainclass.substring(0, dot)};"
					classname = mainclass.substring(dot+1)
				}
				templateData.mainClassName = classname
				templateData.pkg = pkg
				def result = JOptionPane.showConfirmDialog(jEdit.getActiveView(),
					"Class '${mainclass}' does not exist. Create it?", "Java Application", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)
				if (result == JOptionPane.YES_OPTION) {
					f("${classToPath}.java", template:"${templateDir}/JavaMainClass.template", templateData:templateData)
					jEdit.openFile(view, testFile.getPath())
					new_files = true
					def tag = "<attribute name=\"Main-Class\" value=\"${mainclass}\" />"
					def buildfile = new File("${workspace}/${name}/build.xml")
					def start = buildfile.text.indexOf("<!-- mark:mainclass -->")
					if (start == -1) {
						Log.log(Log.ERROR, ProjectBuilderPlugin.class, "Mark 'mainclass' not found in buildfile. Main-Class attribute will not be set.")
					} else {
						def end = buildfile.text.indexOf("<!-- /mark:mainclass -->")
						buildfile.text = buildfile.text.substring(0, start+"<!-- mark:mainclass -->".length())+tag+
							buildfile.text.substring(end)
					}
				}
			}
		}
	}
}

// TODO: Add dependencies and manifest to build.xml

if (new_files) {
	new RootImporter(project, viewer, true).doImport()
}
