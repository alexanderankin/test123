import projectbuilder.builder.ProjectBuilder as PB
import org.gjt.sp.jedit.jEdit as JEDIT

def pluginPackage = project.name.toLowerCase()
def pluginClass = "${project.name}Plugin"

def home = JEDIT.getJEditHome()
def settings = JEDIT.getSettingsDirectory()

def templateData = [:]
templateData.pluginPackage = pluginPackage
templateData.pluginClass = pluginClass
templateData.project = project
templateData.userName = System.getProperty("user.name")
templateData.jeditInstallDir = home
templateData.installDir = ((settings != null) ? settings : home)
templateData.buildSupport = swing.build_support_field.text
templateData.docbookXSL = swing.docbook_xsl_field.text
templateData.docbookCatalog = swing.docbook_catalog.text

// Save values chosen for these fields
JEDIT.setProperty("projectbuilder.jEdit_Plugin.build-support", swing.build_support_field.text)
JEDIT.setProperty("projectbuilder.jEdit_Plugin.docbook-xsl", swing.docbook_xsl_field.text)
JEDIT.setProperty("projectbuilder.jEdit_Plugin.docbook-catalog", swing.docbook_catalog_field.text)

PB.build(name, workspace) {
   d("trunk") {
       d("docs") {
          f("${project.name}.html")
       }
       d("lib")
       d("src") {
          d(pluginPackage) {
             f("${pluginClass}.java", template:"${templateDir}/PluginClass.template", templateData:templateData)
          }
       }
       d("test") {
          d(pluginPackage) {
             f("${pluginClass}Tests.java")
          }
       }
       f("actions.xml")
       f("dockables.xml")
       f("services.xml")
       f("${project.name}.props", template:"${templateDir}/PluginProps.template", templateData:templateData)
       f("build.xml", template:"${templateDir}/PluginBuild.template", templateData:templateData)
       f("description.html", template:"${templateDir}/PluginDescription.template", templateData:templateData)
       f("build.properties", template:"${templateDir}/BuildProperties.template", templateData:templateData)
   }
}

return true

/* ::mode=groovy:folding=indent:noTabs=true:: */