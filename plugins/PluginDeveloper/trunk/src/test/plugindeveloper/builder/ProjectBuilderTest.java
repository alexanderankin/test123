package plugindeveloper.builder;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import java.io.File;
import java.net.URL;
import junit.framework.Assert;
import org.junit.Test;

public class ProjectBuilderTest {

    @Test
    public void scriptTest() throws Exception {
        URL scriptUrl = getClass().getResource("/builder/ProjectBuilderTestScript.groovy");
        Assert.assertNotNull("Script URL was null", scriptUrl);

        Binding binding = new Binding();
        GroovyShell shell = new GroovyShell(getClass().getClassLoader(), binding);
        ProjectBuilder builder = (ProjectBuilder) shell.evaluate(new File(scriptUrl.toURI()));
        Assert.assertNotNull(builder.getProjectDirectory());
    }
}
