package xml.parser;

import static org.gjt.sp.jedit.testframework.TestUtils.close;
import static org.gjt.sp.jedit.testframework.TestUtils.view;

import java.io.File;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.testframework.JEditRunner;
import org.gjt.sp.jedit.testframework.TestData;
import org.gjt.sp.jedit.testframework.TestUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import errorlist.DefaultErrorSource;

@RunWith(JEditRunner.class)
public class ValidationTest {
    @Rule
    public TestData testData = new TestData();

	@Test
	public void includedEntities(){
		/* book_refguide.docbook can't be parsed on its own:
		   it references the java entity declared in the root document.
		   Still, one wants to reparse it to get the tree.
		   See PB #1872 XML: entity references in child documents break tree parsing and xref linkend= navigation
		 */
		File xml = new File(testData.get(),"entities/book_refguide.docbook");

		final Buffer b = TestUtils.openFile(xml.getPath());

		XercesParserImpl parser = new XercesParserImpl();

		DefaultErrorSource es = new DefaultErrorSource("HELLO", TestUtils.view());

		parser.parse(b, es);

		Assert.assertNull(es.getAllErrors());

		close(view(),b);
	}

}
