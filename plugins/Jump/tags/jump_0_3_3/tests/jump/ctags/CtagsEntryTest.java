package tests.jump.ctags;

import jump.ctags.CtagsEntry;
import junit.framework.TestCase;

public class CtagsEntryTest extends TestCase {
	
	private static final String VALID_CTAGS_LINE = "MailUtilities\t" +
			".\\includes\\misc\\MailUtilities.inc.php\t" +
			"/^class MailUtilities$/;\"\tc";
	
	public void testConstructEntry() {
		CtagsEntry en = new CtagsEntry(VALID_CTAGS_LINE);
		assertEquals("MailUtilities", en.getTagName());
		assertEquals("MailUtilities", en.toString());
		assertEquals(".\\includes\\misc\\MailUtilities.inc.php", en.getFileName());
		assertEquals("c", en.getExtensionFields());
		assertEquals("class MailUtilities", en.getExCmd());
	}
	
	public void testIsTagNameStartsWith() {
		CtagsEntry en = new CtagsEntry(VALID_CTAGS_LINE);
		assertTrue("MailUtilities", en.isTagNameStartsWith("Ma"));
		assertFalse("MailUtilities", en.isTagNameStartsWith("Foo"));
	}
}
