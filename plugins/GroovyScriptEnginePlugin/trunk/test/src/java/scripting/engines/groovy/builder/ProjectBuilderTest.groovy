/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package scripting.engines.groovy.builder

def pb = ProjectBuilder.build("PluginDeveloper", "/Users/eberry/development/projects/jedit/plugins") {
    directory("src", id:"srcDir") {
        dir("main") {
            d("java", id:"mainJava") {
                f("PluginDeveloper.java", id:"mainClass")
            }
            d("resources")
        }
        d("test") {
            d("java", id:"testJava") {
                f("PluginDeveloperTestSuite.java")
            }
        }
    }
    file("build.xml")
    f("sample.build.properties")
    f("LICENSE.txt", template:"Test.groovy")
}

println("Source Dir: ${pb.srcDir.path}");
println("Main Java Dir: ${pb.mainJava.path}");
println("   Main Class: ${pb.mainClass.path}");
println("Test Java Dir: ${pb.testJava.path}");