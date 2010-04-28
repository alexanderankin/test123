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
project.build.add("Build Jar:ANT[target=build,buildfile=${project.directory.path}/${project.name}/build.xml]")
project.build.add("Docs:ANT[target=docs,buildfile=${project.directory.path}/${project.name}/build.xml]")
project.build.add("Package (Jar & Docs):ANT[target=docs,buildfile=${project.directory.path}/${project.name}/build.xml]")
project.build.add("Compile only:ANT[target=compile,buildfile=${project.directory.path}/${project.name}/build.xml]")
project.build.add("Clean:ANT[target=clean,buildfile=${project.directory.path}/${project.name}/build.xml]")
/* ::mode=groovy:folding=indent:noTabs=true:: */