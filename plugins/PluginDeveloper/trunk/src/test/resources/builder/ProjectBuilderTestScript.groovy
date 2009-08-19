import plugindeveloper.builder.ProjectBuilder;
import junit.framework.Assert;

def pb = ProjectBuilder.build("ProjectBuilderTest", System.getProperty("user.dir")) {
    directory("src", id:"srcDir") {
        dir("main") {
            d("java", id:"mainJava") {
                f("ProjectBuilder.java", id:"mainClass")
            }
            d("resources")
        }
        d("test") {
            d("java", id:"testJava") {
                f("ProjectBuilderTest.java")
            }
        }
    }
    file("build.xml")
    f("sample.build.properties")
    f("LICENSE.txt", template:"Test.groovy")
}

Assert.assertNotNull(pb.srcDir)
Assert.assertNotNull(pb.mainJava)
Assert.assertNotNull(pb.mainClass)
Assert.assertNotNull(pb.testJava)
Assert.assertNotNull(pb.projectDirectory)

return pb;