// Project-creation script for jEdit plugins

import projectbuilder.builder.ProjectBuilder as PB
import org.gjt.sp.jedit.jEdit as JEDIT
import org.gjt.sp.jedit.MiscUtilities

def pluginPackage = name.toLowerCase()
def pluginClass = "${name}Plugin"

def home = JEDIT.getJEditHome()
def settings = JEDIT.getSettingsDirectory()

def templateData = [:]
templateData.pluginPackage = pluginPackage
templateData.pluginClass = pluginClass
templateData.project = project
templateData.userName = System.getProperty("user.name")
templateData.jeditInstallDir = home
templateData.installDir = ((settings != null) ? settings : home)

PB.build(name, workspace) {
    d("trunk") {
        d("docs") {
           f("${name}.html")
        }
        d("lib")
        d("src") {
           d(pluginPackage) {
              f("${pluginClass}.java", template:"${templateDir}/templates/PluginClass.template", templateData:templateData)
           }
        }
        d("test") {
           d(pluginPackage) {
              f("${pluginClass}Tests.java")
           }
        }
	    /*
        f("actions.xml")
        f("dockables.xml")
        f("services.xml")
	    */
        f("${name}.props", template:"${templateDir}/templates/PluginProps.template", templateData:templateData)
        f("build.xml", template:"${templateDir}/templates/PluginBuild.template", templateData:templateData)
        f("description.html", template:"${templateDir}/templates/PluginDescription.template", templateData:templateData)
        //f("build.properties", template:"${templateDir}/BuildProperties.template", templateData:templateData)
    }
}

project.setProperty("java.optionalSourcepath", MiscUtilities.constructPath(root, "trunk/src"))
project.setProperty("java.optionalBuildpath", MiscUtilities.constructPath(root, "trunk/build/classes"))

return true

/* ::mode=groovy:folding=indent:noTabs=true:: */
