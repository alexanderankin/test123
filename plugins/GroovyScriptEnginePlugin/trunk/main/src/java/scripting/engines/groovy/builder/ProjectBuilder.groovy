/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package scripting.engines.groovy.builder

/**
 *
 * @author eberry
 */
class ProjectBuilder extends FactoryBuilderSupport {

    public static build(String projectName, String projectRootDir) {
        println("Project Name: ${projectName} - root: ${projectRootDir}")
    }
	
}

