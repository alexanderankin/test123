import projectbuilder.builder.ProjectBuilder as PB

def pluginPackage = project.name.toLowerCase()
def pluginClass = "${project.name}Plugin"

def templateData = [:]
templateData.pluginPackage = pluginPackage
templateData.pluginClass = pluginClass
templateData.project = project
templateData.userName = System.getProperty("user.name")

PB.build(name, workspace) {
   d("trunk") {
       d("docs") {
          f("${project.name}.html")
       }
       d("lib")
       d("src") {
          d(pluginPackage) {
             f("${pluginClass}.java", template: "${templateDir}/PluginClass.template", templateData: templateData)
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
       f("${project.name}.props", template: "${templateDir}/PluginProps.template", templateData: templateData)
       f("build.xml", template: "${templateDir}/PluginBuild.template", templateData: templateData)
       f("description.html", template: "${templateDir}/PluginDescription.template", templateData: templateData)
       f("sample.build.properties")
   }
}
/* ::mode=groovy:folding=indent:noTabs=true:: */