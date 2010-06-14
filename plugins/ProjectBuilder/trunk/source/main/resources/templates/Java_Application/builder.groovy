import projectbuilder.builder.ProjectBuilder as PB
import org.gjt.sp.jedit.Macros
import org.gjt.sp.jedit.jEdit
import org.gjt.sp.jedit.MiscUtilities
import org.gjt.sp.jedit.GUIUtilities
import org.gjt.sp.jedit.io.VFSManager
import org.gjt.sp.jedit.io.CopyFileWorker

def view = jEdit.getActiveView()
def status = view.getStatus()

def mainClass = swing.class_field.text
if (mainClass.trim().length() == 0) {
	GUIUtilities.error(view, "projectbuilder.cannot.build", "No main class specified.")
	return false
}
def mainClassName = mainClass

def templateData = [:]
templateData.name = project.getName()
templateData.mainClass = mainClass
templateData.mainClassName = mainClassName

int dot = mainClass.lastIndexOf(".")
def deep = (dot != -1)
def mainClassDir = ""
if (deep) {
	def pkg = mainClass.substring(0, dot)
	templateData.pkg = "package ${pkg};"
	mainClassDir = pkg.replace(".", File.separator)
	mainClassName = mainClass.substring(dot+1)
	templateData.mainClass = mainClass
	templateData.mainClassName = mainClassName
} else {
	templateData.pkg = ""
}

def optionalClasspath = ""

// dependencies
templateData.dependencies = ""
for (depend in dependencies) {
	templateData.dependencies += "<pathelement path=\"${depend}\" />\n\t\t"
	optionalClasspath += "${depend}${File.pathSeparator}"
}

status.setMessage("Building file structure...")
PB.build(name, workspace) {
	f("build.xml", template:"${templateDir}/JavaBuild.template", templateData:templateData)
	//d("build") {}
	d("lib") {}
	d("src/${mainClassDir}") {
		f("${mainClassName}.java", template:"${templateDir}/JavaMainClass.template", templateData:templateData)
	}
}

// Copy lib jars
for (jar in lib_jars) {
	
	status.setMessage("Copying ${jar}...")
	def jarname = new File(jar).getName()
	def target = "${workspace}/${name}/lib"
	/*
	def istream = new FileInputStream(jar)
	def ostream = new FileOutputStream("${workspace}/${name}/lib/${jarname}")
	projectbuilder.utils.ZipUtils.copyStream(istream, ostream)
	*/
	VFSManager.runInWorkThread(new CopyFileWorker(view, jar, target))
	optionalClasspath += "${jar}${File.pathSeparator}"
}

// Copy extras
for (extra in extras) {
	status.setMessage("Copying ${extra}...")
	def extrafile = new File(extra)
	def extraname = extrafile.getName()
	if (extrafile.isFile()) {
		def target = "${workspace}/${name}/src"
		/*
		def istream = new FileInputStream(extra)
		def ostream = new FileOutputStream("${workspace}/${name}/src/${extraname}")
		projectbuilder.utils.ZipUtils.copyStream(istream, ostream)
		*/
		VFSManager.runInWorkThread(new CopyFileWorker(view, extra, target))
	}
	else if (extrafile.isDirectory()) {
		def root = extrafile.getParent()
		extrafile.eachFileRecurse { f ->
			if (f.isFile()) {
				def dir = f.getParent()
				def local = dir.substring(root.length()+1)
				def flocal = new File(MiscUtilities.constructPath("${workspace}/${name}/src", local))
				flocal.mkdirs()
				def fname = f.getName()
				status.setMessage("Copying ${fname}...")
				VFSManager.runInWorkThread(new CopyFileWorker(view, f.getPath(), flocal.getPath()))
				/*
				def istream = new FileInputStream(f.getPath())
				def ostream = new FileOutputStream(MiscUtilities.constructPath(flocal.getPath(), fname))
				projectbuilder.utils.ZipUtils.copyStream(istream, ostream)
				*/
			}
		}
	}
}

if (optionalClasspath.length() > 0)
	optionalClasspath = optionalClasspath.substring(0, optionalClasspath.length()-1)
// Set some properties for JavaSideKick, if it's installed
project.setProperty("java.optionalClasspath", optionalClasspath)
project.setProperty("java.optionalSourcepath", MiscUtilities.constructPath("${workspace}/${name}", "src"))
project.setProperty("java.optionalBuildpath", MiscUtilities.constructPath("${workspace}/${name}", "build"))

open_after = ["src/${mainClassDir}/${mainClassName}.java"]

status.setMessageAndClear("Done")

return true
