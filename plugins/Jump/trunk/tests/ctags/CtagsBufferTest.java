package tests.ctags;

import java.util.Vector;

import jump.ctags.CtagsBuffer;
import jump.ctags.CtagsEntry;
import jump.ctags.CtagsParser;
import junit.framework.TestCase;

public class CtagsBufferTest extends TestCase {
	
	private static final String LINE1 = "MailUtilities\t" +
	".\\includes\\misc\\MailUtilities.inc.php\t" +
	"/^class MailUtilities$/;\"\tc";
	
	private static final String LINE2 = "send\t" +
	".\\includes\\misc\\MailUtilities.inc.php\t" +
	"/^class MailUtilities$/;\"\tf";
	
	private static final String LINE3 = "store\t" +
	".\\includes\\misc\\PersistenceUtilities.inc.php\t" +
	"/^class PersistenceUtilities$/;\"\tf";

	private CtagsBuffer buf;
	
	
	public void testAddRemoveFile() {
		assertEquals(2, buf.getFileNames().size());
		buf.removeFile(".\\includes\\misc\\PersistenceUtilities.inc.php");
		assertEquals(1, buf.getFileNames().size());
		assertEquals(2, buf.getTagsByFile(".\\includes\\misc\\MailUtilities.inc.php").size());
	}
	
	public void testGetEntry() {
		assertEquals("class MailUtilities", ((CtagsEntry)buf.getEntry("send").get(0)).getExCmd());
		assertEquals("f", ((CtagsEntry)buf.getEntry("store").get(0)).getExtensionFields());
	}
	
	public void testGetEntresByStartPrefix() {
		Vector v = buf.getEntriesByStartPrefix("se");
		assertEquals(1, v.size());
		CtagsEntry en = (CtagsEntry)v.get(0);
		assertEquals("class MailUtilities", en.getExCmd());
		
		v = buf.getEntriesByStartPrefix("s");
		assertEquals(2, v.size());
	}
	
	public void testGetTagsByFile() {
		Vector v = buf.getTagsByFile(".\\includes\\misc\\PersistenceUtilities.inc.php");
		assertEquals(1, v.size());
		CtagsEntry en = (CtagsEntry)v.get(0);
		assertEquals("store", en.getTagName());
		
		v = buf.getTagsByFile(".\\includes\\misc\\MailUtilities.inc.php");
		assertEquals(2, v.size());
	}
	
	public void testAdd() {
		CtagsEntry en = new CtagsEntry("NewsPage\t.\\includes\\pages\\NewsPage.inc.php\t/^\tfunction NewsPage($title) {$/;\"\tf");
		buf.add(en);
		assertEquals(4, buf.size());
	}

	protected void setUp() throws Exception {
		super.setUp();
		buf = new CtagsBuffer(new CtagsParser());
		buf.add(new CtagsEntry(LINE1));
		buf.add(new CtagsEntry(LINE2));
		buf.add(new CtagsEntry(LINE3));
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		buf = null;
	}
}
