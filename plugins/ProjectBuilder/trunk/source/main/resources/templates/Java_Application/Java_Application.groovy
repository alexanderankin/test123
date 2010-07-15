// Project-creation script for Java applications

import projectbuilder.builder.ProjectBuilder as PB
import org.gjt.sp.jedit.MiscUtilities

def templateData = [:]
templateData.name = name

PB.build(name, workspace) {
	f("build.xml", template:"${templateDir}/JavaBuild.template", templateData:templateData)
	d("lib") {}
	d("src") {}
}

project.setProperty("java.optionalSourcepath", MiscUtilities.constructPath(root, "src"))
project.setProperty("java.optionalBuildpath", MiscUtilities.constructPath(root, "build"))

return true
