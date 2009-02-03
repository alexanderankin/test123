/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package scripting.engines.groovy.builder

import groovy.swing.SwingBuilder

/**
 *
 * @author eberry
 */
class ProjectBuilder extends FactoryBuilderSupport {

    private projectDirectory
    
    private ProjectBuilder() {
        registerFactory("f", new FileFactory())
        registerFactory("file", new FileFactory())
        registerFactory("d", new DirectoryFactory())
        registerFactory("dir", new DirectoryFactory())
        registerFactory("directory", new DirectoryFactory())

        // ID to Object.
        addAttributeDelegate(SwingBuilder.&objectIDAttributeDelegate)
    }

    public static build(String projectName, String projectRoot, Closure closure) {
        println("Project Name: ${projectName} - root: ${projectRoot}")
        ProjectBuilder builder = new ProjectBuilder()
        builder.projectDirectory = new File(projectRoot, projectName)

        closure.setDelegate(builder)

        // create our initial directories.
        // the project root
        builder.d(projectRoot) {
            // and project directory
            d(projectName) {
                // handle the rest of the closure.
                closure.call(builder)
            }
        }
        return builder
    }
}