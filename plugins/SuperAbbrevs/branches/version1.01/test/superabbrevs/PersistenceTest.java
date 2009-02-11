package superabbrevs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import superabbrevs.model.Abbrev;
import superabbrevs.model.Mode;

public class PersistenceTest {
	private Persistence persistence;
	private File tempFile;
	private Mode mode;
	
	@Before 
	public void setup() throws IOException {
		tempFile = File.createTempFile("persistenceTest", null);
		persistence = new Persistence() {
			@Override
			protected File getModeFileName(String mode) {
				return tempFile;
			}
		};
		
		mode = new Mode("java");
		mode.addAbbreviation(new Abbrev("For-loop", "for", "for (...) {}"));
		mode.addAbbreviation(new Abbrev("While-loop", "while", "while (...) {}"));
		mode.addAbbreviation(new Abbrev("if-statement", "if", "if (...) {}"));
	}
	
	@After
	public void cleanUp() {
		tempFile.delete();
	}
	
	@Test
	public void saveModeTest() throws Exception {
		persistence.saveMode(mode);
		Reader expectedReader = null;
		Reader actualReader = null;
		try {
			InputStream stream = this.getClass().getResourceAsStream("expectedJavaMode.xml");
			expectedReader = new InputStreamReader(stream);
			String expected = IOUtils.toString(expectedReader);
			
			actualReader = new FileReader(tempFile);
			String actual = IOUtils.toString(actualReader);
			
			assertEquals(expected, actual);
		} finally {
			IOUtils.closeQuietly(expectedReader);
			IOUtils.closeQuietly(actualReader);
		}
	}
	
	@Test
	public void loadedAndSavedModesShouldBeEquals() throws Exception {
		persistence.saveMode(mode);
		Mode loadedMode = persistence.loadMode(mode.getName());
		assertEquals(mode.getName(), loadedMode.getName());
		
		Set<Abbrev> expectedAbbrevs = mode.getAbbreviations();
		Set<Abbrev> actualAbbrevs = loadedMode.getAbbreviations();
		
		assertEquals(expectedAbbrevs.size(), actualAbbrevs.size());
		assertTrue(expectedAbbrevs.containsAll(actualAbbrevs));
	}
}
