package elementmatcher.elements.file;

import elementmatcher.AbstractElementProviderTest;
import org.junit.Test;
import org.junit.Before;
import java.io.File;

public class FileElementProviderTest extends AbstractElementProviderTest {

    private FileElementProvider provider;

    @Before
    public void setUp() throws Exception {
        provider = new FileElementProvider();
    }

    @Test
    public void testTwoCloseFileNames() {
        final String home = System.getProperty("user.home");
        testLine(provider,
                home + ";" + home,
                new File(home), new File(home));
    }

//    @Test
//    public void test1() {
//        testLine(provider,
//                "parsing buildfile D:\\Java\\JEditCustomization\\build-support\\plugin-build.xml with URI = file:/D:/Java/JEditCustomization/build-support/plugin-build.xml",
//                new File("D:\\Java\\JEditCustomization\\build-support\\plugin-build.xml"),
//                new File("D:/Java/JEditCustomization/build-support/plugin-build.xml"));
//    }
//
//    @Test
//    public void test2() {
//        testLine(provider,
//                "[copy] collections-generic-4.01.jar omitted as C:\\Program Files\\jEdit\\jars\\collections-generic-4.01.jar is up to date.",
//                new File("C:\\Program Files\\jEdit\\jars\\collections-generic-4.01.jar"));
//    }
//
}