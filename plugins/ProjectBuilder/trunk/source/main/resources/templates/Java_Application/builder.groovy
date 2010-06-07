import projectbuilder.builder.ProjectBuilder as PB
import org.gjt.sp.jedit.Macros
import org.gjt.sp.jedit.jEdit

def mainClass = swing.class_field.text
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

PB.build(name, workspace) {
	d("trunk") {
		f("build.xml", template:"${templateDir}/JavaBuild.template", templateData:templateData)
		d("build") {}
		d("lib") {}
		d("dist") {}
		d("src${File.separator}${mainClassDir}") {
			f("${mainClassName}.java", template:"${templateDir}/JavaMainClass.template", templateData:templateData)
		}
	}
}

open_after = ["trunk/src/${mainClassDir}/${mainClassName}.java"]

/*
project.setProperty("projectbuilder.bsh.menu", "build-dist - run");
project.setProperty("projectbuilder.bsh.0", "${templateDir}/bsh/build-dist.bsh")
project.setProperty("projectbuilder.bsh.0.label", "Build Application")
project.setProperty("projectbuilder.bsh.1", "${templateDir}/bsh/run.bsh")
project.setProperty("projectbuilder.bsh.1.label", "Run Application")
*/
