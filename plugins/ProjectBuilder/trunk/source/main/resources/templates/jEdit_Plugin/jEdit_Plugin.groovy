import projectbuilder.builder.ProjectBuilder as PB

def pluginPackage = project.name.toLowerCase()
def pluginClass = "${project.name}Plugin"
def templateDir = "${templatesDir}/jEdit_Plugin"

def templateData = [:]
templateData.pluginPackage = pluginPackage
templateData.pluginClass = pluginClass
templateData.project = project
templateData.userName = System.getProperty("user.name")

PB.build(project.name, project.directory.path) {
   d("conf") {
      f("${project.name}.props", template: "${templateDir}/PluginProps.template", templateData: templateData)
      f("actions.xml")
      f("dockables.xml")
      f("services.xml")
   }
   d("docs") {
      f("${project.name}.html")
   }
   d("src") {
      d("main") {
         d(pluginPackage) {
            f("${pluginClass}.java", template: "${templateDir}/PluginClass.template", templateData: templateData)
         }
      }
      d("test") {
         d(pluginPackage) {
            f("${pluginClass}Tests.java")
         }
      }
   }
   f("build.xml", template: "${templateDir}/PluginBuild.template", templateData: templateData)
   f("sample.build.properties")
}