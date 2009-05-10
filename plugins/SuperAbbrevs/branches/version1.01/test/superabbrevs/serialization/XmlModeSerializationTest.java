package superabbrevs.serialization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import superabbrevs.model.Abbreviation;
import superabbrevs.model.Mode;

public class XmlModeSerializationTest {
	private ModeSerializer modeSerializer;
	private ByteArrayOutputStream output;
		
	private Mode mode;
	
	@Before
	public void before() {
		modeSerializer = new XmlModeSerializer();
		output = new ByteArrayOutputStream();
		
		mode = new Mode("java");
		mode.addAbbreviation(new Abbreviation("For-loop", "for", "for (...) {}"));
		mode.addAbbreviation(new Abbreviation("While-loop", "while", "while (...) {}"));
		mode.addAbbreviation(new Abbreviation("If-statement", "if", "if (...) {}"));
	}
	
	@Test
	public void serializingAModeToXml() throws Exception {
		String expected = getResourceAsString("expectedJavaMode.xml");
		modeSerializer.serialize(output, mode);
		
		assertEquals(expected, output.toString());
	}
	
	@Test
	public void loadedAndSavedModesShouldBeEquals() throws Exception {
		modeSerializer.serialize(output, mode);
		InputStream input = new ByteArrayInputStream(output.toByteArray());
		
		Mode loadedMode = modeSerializer.deserialize(input);
		assertEquals(mode.getName(), loadedMode.getName());
		
		Set<Abbreviation> expectedAbbrevs = mode.getAbbreviations();
		Set<Abbreviation> actualAbbrevs = loadedMode.getAbbreviations();
		
		assertEquals(expectedAbbrevs.size(), actualAbbrevs.size());
		assertTrue(expectedAbbrevs.containsAll(actualAbbrevs));
		assertTrue(actualAbbrevs.containsAll(expectedAbbrevs));
	}

	private String getResourceAsString(String resourceName) throws IOException {
		InputStream stream = null;
		try {
			stream = this.getClass().getResourceAsStream("expectedJavaMode.xml");
			return IOUtils.toString(stream);
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}
}
