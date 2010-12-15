#!/usr/bin/env groovy
/* This script copies properties provided as System properties in Hudson into actual properties files used by jEdit's
 * Build process.
 */
 
def props = System.properties
def env = System.env

String workspace = props.get("user.dir")

println "Copying properties over to appropriate directories"
String xsl = props.get("docbook.xsl")
String catalog = props.get("docbook.catalog")
String xsltproc = props.get("xsltproc.executable")
String fopDir = props.get("fop.dir")
String launch4j = props.get("launch4j.dir")
String installDir = props.get("install.dir")
String pluginsDir = props.get("jedit.plugins.dir")
String jeditJarsDir = props.get("jedit.jars.dir")

def jeditProps = """## THIS FILE IS GENERATED BASED ON SYSTEM PROPERTIES SUPPLIED TO HUDSON, AND IS USUALLY REPLACED ON EACH BUILD.
ci.workspace=${workspace}
docbook.xsl=${xsl}
docbook.catalog=${catalog}
xsltproc.executable=${xsltproc}
fop.dir=${fopDir}
launch4j.dir=${launch4j}
"""

def pluginProps = """${jeditProps}
install.dir=${installDir}
jedit.plugins.dir=${pluginsDir}
jedit.jars.dir=${jeditJarsDir}
"""

File jeditPropsFile = new File(workspace, "jedit/build.properties")
// make sure stuff exists.
jeditPropsFile.parentFile.exists() ?: jeditPropsFile.parentFile.mkdirs() ?: {
   System.err.println("'${jeditPropsFile.parentFile}' did not exist, and could not be created. Exiting.")
   System.exit(1)
}()
jeditPropsFile.exists() ?: jeditPropsFile.createNewFile() ?: {
   System.err.println("'${jeditPropsFile}' did not exist, and could not be created. Exiting.")
   System.exit(1)
}()

File pluginPropsFile = new File(workspace, "jedit/jars/build.properties")
// make sure stuff exists.
pluginPropsFile.parentFile.exists() ?: pluginPropsFile.parentFile.mkdirs() ?: {
   System.err.println("'${pluginPropsFile.parentFile}' did not exist, and could not be created. Exiting.")
   System.exit(1)
}()
pluginPropsFile.exists() ?: pluginPropsFile.createNewFile() ?: {
   System.err.println("'${pluginPropsFile}' did not exist, and could not be created. Exiting.")
   System.exit(1)
}()

// Overwrite the existing props
jeditPropsFile.setText(jeditProps)
pluginPropsFile.setText(pluginProps)

/* ::mode=groovy:noTabs=true:maxLineLen=120:wrap=soft:: */
