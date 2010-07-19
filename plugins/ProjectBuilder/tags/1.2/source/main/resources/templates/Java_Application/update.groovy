// Update config script for Java Application

//{{{ imports
import org.gjt.sp.jedit.jEdit
import org.gjt.sp.util.Log
import javax.swing.JOptionPane
import projectviewer.importer.RootImporter
import projectbuilder.builder.ProjectBuilder as PB
import projectbuilder.ProjectBuilderPlugin
//}}}

def buildfile = "${root}/build.xml";
if (!new File(buildfile).exists()) {
	buildfile = "${root}/trunk/build.xml";
	if (!new File(buildfile).exists()) {
		return;
	}
}

def templateData = [:]

// NOTE: If this method is going to be used, it can't be nested within if statements
// an error about java.io.File.call() not being applicable will come up
// Instead, start creating it here and then determine what to create via if-statements inside
def new_files = false
PB.build(name, workspace) {
	d("src") {
		def mainclass = project.getProperty("project.config.mainclass")
		if (mainclass == null) mainclass = ""
		def tag = "<attribute name=\"Main-Class\" value=\"${mainclass}\" />"
		PB.mark(buildfile, "<!-- mark:mainclass -->", "<!-- /mark:mainclass -->", tag)
		if (mainclass.length() > 0) {
			def classToPath = mainclass.replace('.', File.separator)
			def testFile = new File("${root}/src", "${classToPath}.java")
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
					f("${classToPath}.java", template:"${templateDir}/templates/JavaMainClass.template", templateData:templateData)
					jEdit.openFile(view, testFile.getPath())
					new_files = true
				}
			}
		}
	}
}

def manifest = project.getProperty("project.config.manifest")
def manifest_tags = ""
if (manifest != null) {
	def tokenizer = new StringTokenizer(manifest, "\n")
	while (tokenizer.hasMoreTokens()) {
		def token = tokenizer.nextToken()
		def colon = token.indexOf(":")
		if (colon == -1)
			continue
		def name = token.substring(0, colon).trim()
		def value = token.substring(colon+1).trim()
		manifest_tags += "\n\t\t\t\t<attribute name=\"${name}\" value=\"${value}\" />"
	}
} else project.setProperty("project.config.manifest", "")
manifest_tags += "\n\t\t\t\t"
PB.mark(buildfile, "<!-- mark:manifest -->", "<!-- /mark:manifest -->", manifest_tags)

def classpath = project.getProperty("project.config.classpath")
def classpath_tags = ""
if (classpath != null) {
	def tokenizer = new StringTokenizer(classpath, File.pathSeparator)
	while (tokenizer.hasMoreTokens()) {
		def token = tokenizer.nextToken()
		classpath_tags += "\n\t\t<pathelement path=\"${token}\" />"
	}
} else project.setProperty("project.config.classpath", "")
classpath_tags += "\n\t\t"
PB.mark(buildfile, "<!-- mark:dependencies -->", "<!-- /mark:dependencies -->", classpath_tags)

if (new_files) {
	new RootImporter(project, viewer, true).doImport()
}
