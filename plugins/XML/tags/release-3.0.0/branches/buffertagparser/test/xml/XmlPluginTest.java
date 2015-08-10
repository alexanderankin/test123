/*
 * XMLPluginTest.java
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2009 Eric Le Lay
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */
package xml;

// {{{ jUnit imports 
import java.util.concurrent.TimeUnit;

import org.junit.*;
import static org.junit.Assert.*;

import org.fest.swing.fixture.*;
import org.fest.swing.core.*;
import org.fest.swing.finder.*;
import org.fest.swing.edt.*;
import org.fest.swing.timing.*;
import org.fest.swing.driver.*;
import org.fest.swing.cell.*;
import static org.fest.assertions.Assertions.*;

import org.gjt.sp.jedit.testframework.Log;

import static xml.XMLTestUtils.*;
import static org.gjt.sp.jedit.testframework.EBFixture.*;
import static org.gjt.sp.jedit.testframework.TestUtils.*;
import org.gjt.sp.jedit.testframework.TestUtils;

// }}}

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.Buffer;

import java.io.*;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import org.gjt.sp.jedit.gui.CompletionPopup;

/**
 * integration tests using test_data
 * $Id$
 */
public class XmlPluginTest{
	private static File testData;
	
    @BeforeClass
    public static void setUpjEdit() throws IOException{
        TestUtils.beforeClass();
        jEdit.setBooleanProperty("firewall.enabled",true);
        jEdit.setProperty("firewall.host","139.10.0.226");
        jEdit.setProperty("firewall.port","8080");
        System.setProperty("http.proxyHost","139.10.0.226");
        System.setProperty("http.proxyPort","8080");
        testData = new File(System.getProperty("test_data")).getCanonicalFile();
        assertTrue(testData.exists());
    }
    
    @AfterClass
    public static void tearDownjEdit() {
        TestUtils.afterClass();
    }
    
    /** ensures that ipo:comment is not proposed in XML Insert */
    @Test
    public void testAbstractSubstitution() throws IOException{
    	File xml = new File(testData,"abstract_substitution/abstract_element_instance.xml");
    	
    	TestUtils.openFile(xml.getPath());
    	parseAndWait();
    	
    	action("xml-insert-float",1);
    	
    	FrameFixture insert = TestUtils.findFrameByTitle("XML Insert");
    	
		// go into the file
		gotoPositionAndWait(530);

		assertThat(xmlListContents(insert.list("elements"))).contains("ipo:shipComment");
		assertThat(xmlListContents(insert.list("elements"))).excludes("ipo:comment").excludes("comment");
		assertThat(xmlListContents(insert.list("elements"))).excludes("ipo:otherComment").excludes("otherComment");
		assertThat(xmlListContents(insert.list("elements"))).contains("ipo:concreteOtherComment");
		
		// go into the customerComment
		gotoPositionAndWait(483);
		
		Pause.pause(500);
		assertThat(xmlListContents(insert.list("elements"))).contains("customerId");

		insert.close();
    }
    
    /** Tests XML Plugin's completion
    * see test_data/attributes_completion 
    */
    @Test
    public void testAttributesCompletion() throws IOException{
    	File xml = new File(testData,"attributes_completion/attributes.xml");
    	
    	TestUtils.openFile(xml.getPath());
    	
    	parseAndWait();
		
		// aa, ab
		gotoPositionAndWait(493);
		
		action("sidekick-complete",1);
		
		JWindowFixture completion = XMLTestUtils.completionPopup();
		
		completion.requireVisible();
		assertThat(xmlListContents(completion.list())).containsOnly("aa","ab");
		completion.list().pressAndReleaseKeys(KeyEvent.VK_ESCAPE);
		
		Pause.pause(500);

		// no popup
		gotoPositionAndWait(540);
		action("sidekick-complete",1);
		try{
			completion = XMLTestUtils.completionPopup();
			fail("shouldn't be there");
		}catch(Throwable e){
			//fine
		}
		
		// aa,ab
		gotoPositionAndWait(657);
		action("sidekick-complete",1);
		completion = XMLTestUtils.completionPopup();
		
 		completion.requireVisible();
		assertThat(xmlListContents(completion.list())).containsOnly("aa","ab");
		completion.list().pressAndReleaseKeys(KeyEvent.VK_ESCAPE);
		Pause.pause(500);

		// aa,ab
		gotoPositionAndWait(771);
		action("sidekick-complete",1);
		completion = XMLTestUtils.completionPopup();

		completion.requireVisible();
		assertThat(xmlListContents(completion.list())).containsOnly("aa","ab");
		completion.list().pressAndReleaseKeys(KeyEvent.VK_ESCAPE);
		Pause.pause(500);

		
		gotoPositionAndWait(834);
		action("sidekick-complete",1);
		try{
			completion = XMLTestUtils.completionPopup();
			fail("shouldn't be there");
		}catch(Throwable e){
			//fine
		}
		
		gotoPositionAndWait(850);
		action("sidekick-complete",1);
		try{
			completion = XMLTestUtils.completionPopup();
			fail("shouldn't be there");
		}catch(Throwable e){
			//fine
		}
	}
	
	/** an error in an online schema : navigate to it via Error List*/
	@Test
	public void testBrokenOnlineSchema(){
    	File xml = new File(testData,"broken_online_schema/actions.xml");
    	
		// accept downloading
		ClickT clickT = new ClickT(Option.YES,30000);
		clickT.start();

    	TestUtils.openFile(xml.getPath());
    	
    	action("sidekick-parse",1);

		clickT.waitForClick();
    	
		// wait for end of parsing
		simplyWaitForMessageOfClass(sidekick.SideKickUpdate.class,10000);
		
		action("error-list-show",1);
		
    	FrameFixture errorlist = TestUtils.findFrameByTitle("Error List");
		
		errorlist.tree().selectRow(1);
		
		// wait for end of parsing of actions.xsd
		simplyWaitForMessageOfClass(sidekick.SideKickUpdate.class,10000);
		// wait until new buffer is displayed and ready :-(
		Pause.pause(2000);
		
		String selected = TestUtils.view().getTextArea().getSelectedText();
		assertTrue("got '"+selected+"'",selected.startsWith("<xs:borkedschema"));
		assertEquals("actions.xsd",TestUtils.view().getBuffer().getName());
		assertEquals("https://jedit.svn.sourceforge.net/svnroot/jedit/plugins/XML/trunk/test_data/dir%20with%20space/",
			TestUtils.view().getBuffer().getDirectory());
		
		errorlist.close();
	}
	
	/** test xml.root, test XML Insert offering ids from other documents */
	@Test
	public void testCompoundDocuments(){
    	File xml = new File(testData,"compound_documents/fragment1.xml");
    	
    	TestUtils.openFile(xml.getPath());
    	
		// wait for end of parsing
    	parseAndWait();

    	action("xml-insert-float",1);
    	
    	
    	FrameFixture insert = TestUtils.findFrameByTitle("XML Insert");
    	
		// go into the file
		gotoPositionAndWait(341);
		
		// entities declared in the root document appear here
		assertThat(xmlListContents(insert.list("entities"))).contains("frag2");
		// ids declared in other fragment appear heare
		assertThat(xmlListContents(insert.list("ids"))).contains("testing [element: <chapter>]");
		
		insert.list("ids").item("testing [element: <chapter>]").click(MouseButton.RIGHT_BUTTON);
		
		
		Pause.pause(1000);
		
		assertEquals("fragment2.xml",TestUtils.view().getBuffer().getName());

		insert.close();
		
	}
	
	/** an error in a directory with spaces : navigate to it via Error List*/
	@Test
	public void testDirWithSpace(){
    	File xml = new File(testData,"dir with space/actions.xml");
    	
    	TestUtils.openFile(xml.getPath());
    	

    	action("sidekick-parse",1);
		simplyWaitForMessageOfClass(sidekick.SideKickUpdate.class,10000);
		
		action("error-list-show",1);
		
    	FrameFixture errorlist = TestUtils.findFrameByTitle("Error List");
		
		errorlist.tree().selectRow(1);
		
    	action("sidekick-parse",1);
		simplyWaitForMessageOfClass(sidekick.SideKickUpdate.class,10000);
		
		String selected = TestUtils.view().getTextArea().getSelectedText();
		assertTrue("got '"+selected+"'",selected.startsWith("<xs:borkedschema"));
		assertEquals("actions.xsd",TestUtils.view().getBuffer().getName());
		// on windows, it ends with antislash
		assertTrue(TestUtils.view().getBuffer().getDirectory().matches(".*dir with space(/|\\\\)"));
		
		errorlist.close();
	}
	
	/** refering to actions.dtd */
	@Test
	public void testBuiltInCatalog(){
    	File xml = new File(testData,"dtd/actions.xml");
    	
    	TestUtils.openFile(xml.getPath());
    	
    	action("sidekick-parse",1);
		// wait for end of parsing
		simplyWaitForMessageOfClass(sidekick.SideKickUpdate.class,10000);
		

		action("error-list-show",1);
    	FrameFixture errorlist = TestUtils.findFrameByTitle("Error List");
    	requireEmpty(errorlist.tree());
		errorlist.close();
		
		// inside ACTIONS
		gotoPositionAndWait(151);
    	action("xml-insert-float",1);
    	FrameFixture insert = TestUtils.findFrameByTitle("XML Insert");
		assertThat(xmlListContents(insert.list("elements"))).containsOnly("ACTION");
		insert.close();
		
		gotoPositionAndWait(244);
		
		// inside CODE
    	action("xml-insert-float",1);
    	insert = TestUtils.findFrameByTitle("XML Insert");
		assertThat(xmlListContents(insert.list("elements"))).isEmpty();
		insert.close();
	}
	
	
	@Test
	public void testNamespacesOff(){
    	File xml = new File(testData,"namespaces_off/wallispage.xml");
    	
    	TestUtils.openFile(xml.getPath());
    	
    	action("sidekick-parse",1);
		simplyWaitForMessageOfClass(sidekick.SideKickUpdate.class,10000);
		

		action("error-list-show",1);
    	FrameFixture errorlist = TestUtils.findFrameByTitle("Error List");
    	requireEmpty(errorlist.tree());
		errorlist.close();
		
		TestUtils.close(TestUtils.view(), TestUtils.view().getBuffer());
		
    	xml = new File(testData,"namespaces_off/wallispage_error.xml");
    	
    	TestUtils.openFile(xml.getPath());
    	
		// wait for end of parsing
    	action("sidekick-parse",1);
		simplyWaitForMessageOfClass(sidekick.SideKickUpdate.class,10000);
		

		action("error-list-show",1);
    	errorlist = TestUtils.findFrameByTitle("Error List");
    	errorlist.tree().selectRow(1);
		errorlist.close();
		
	}
	
	/** schemas.xml pointing to a RNG schema and completion from an RNG schema */
	@Test
	public void testRelaxNG(){
    	File xml = new File(testData,"relax_ng/actions.xml");
    	
    	TestUtils.openFile(xml.getPath());
    	
		// wait for end of parsing
    	action("sidekick-parse",1);
		simplyWaitForMessageOfClass(sidekick.SideKickUpdate.class,10000);
		
		action("error-list-show",1);
		
    	FrameFixture errorlist = TestUtils.findFrameByTitle("Error List");
		
		errorlist.tree().selectRow(1);
		
		String selected = TestUtils.view().getTextArea().getSelectedText();
		assertTrue("got '"+selected+"'",selected.contains("<CODDE"));
		
		// inside ACTIONS
		gotoPositionAndWait(219);

    	action("xml-insert-float",1);
    	FrameFixture insert = TestUtils.findFrameByTitle("XML Insert");
		assertThat(xmlListContents(insert.list("elements"))).containsOnly("ACTION");
		insert.close();
		
		gotoPositionAndWait(286);

		// inside CODE
    	action("xml-insert-float",1);
    	insert = TestUtils.findFrameByTitle("XML Insert");
		assertThat(xmlListContents(insert.list("elements"))).isEmpty();
		insert.close();
		
		xml = new File(testData,"relax_ng/actions_valid.xml");
    	
    	TestUtils.openFile(xml.getPath());
    	
		// wait for end of parsing
    	action("sidekick-parse",1);
		simplyWaitForMessageOfClass(sidekick.SideKickUpdate.class,10000);
		
		action("error-list-show",1);
		requireEmpty(errorlist.tree());
		errorlist.close();
	}

	/** a simple test with a schema using compact Relax NG syntax */
	@Test
	public void testRelaxNGCompact(){
    	File xml = new File(testData,"rnc/actions.xml");
    	
    	TestUtils.openFile(xml.getPath());
    	
		// wait for end of parsing
    	action("sidekick-parse",1);
		simplyWaitForMessageOfClass(sidekick.SideKickUpdate.class,10000);
		
		action("error-list-show",1);
		
    	FrameFixture errorlist = TestUtils.findFrameByTitle("Error List");
		
		errorlist.tree().selectRow(1);
		
		String selected = TestUtils.view().getTextArea().getSelectedText();
		assertTrue("got '"+selected+"'",selected.contains("<CODDE"));
		
		// inside ACTIONS
		gotoPositionAndWait(157);

    	action("xml-insert-float",1);
    	FrameFixture insert = TestUtils.findFrameByTitle("XML Insert");
		assertThat(xmlListContents(insert.list("elements"))).containsOnly("ACTION");
		insert.close();
	}
	
	/** grammar including another one */
	@Test
	public void testRelaxNGInclude(){
    	File xml = new File(testData,"parentRef/instance.xml");
    	
    	TestUtils.openFile(xml.getPath());
    	
		// wait for end of parsing
    	parseAndWait();
		
		// inside doc
		gotoPositionAndWait(115);

    	action("xml-insert-float",1);
    	FrameFixture insert = TestUtils.findFrameByTitle("XML Insert");
		assertThat(xmlListContents(insert.list("elements"))).containsOnly("p","table");
		insert.close();
		
		// inside td
		gotoPositionAndWait(217);

    	action("xml-insert-float",1);
    	insert = TestUtils.findFrameByTitle("XML Insert");
		assertThat(xmlListContents(insert.list("elements"))).containsOnly("em");
		insert.close();
	}

	@Test
	public void testSchemaLoader(){
    	File xml = new File(testData,"schema_loader/actions.xml");
    	
    	TestUtils.openFile(xml.getPath());
    	
    	parseAndWait();
		
		action("error-list-show",1);
		
    	FrameFixture errorlist = TestUtils.findFrameByTitle("Error List");
		
		errorlist.tree().selectRow(1);
		
		String selected = TestUtils.view().getTextArea().getSelectedText();
		assertTrue("got '"+selected+"'",selected.contains("<ACTIONN"));
				
		errorlist.close();

    	action("xml-insert-float",1);
    	FrameFixture insert = TestUtils.findFrameByTitle("XML Insert");
    	

		// inside body
		gotoPositionAndWait(166);
		
		assertThat(xmlListContents(insert.list("elements"))).contains("ACTION");
		insert.close();
	}
	
	
	@Test
	public void testXinclude(){
		//already tested by XMLTagTest
	}
	
	@Test
	public void testMultipleName(){
    	File xml = new File(testData,"multiple_name/instance.xml");
    	
    	final Buffer b = TestUtils.openFile(xml.getPath());
    	
    	action("xml-insert-float",1);
    	FrameFixture insert = TestUtils.findFrameByTitle("XML Insert");
    	
    	action("sidekick-parse",1);
		// wait for end of parsing
		simplyWaitForMessageOfClass(sidekick.SideKickUpdate.class,10000);
		

		action("error-list-show",1);
    	FrameFixture errorlist = TestUtils.findFrameByTitle("Error List");
    	requireEmpty(errorlist.tree());
		errorlist.close();
		
		// inside body
		gotoPositionAndWait(208);
		
		assertThat(xmlListContents(insert.list("elements"))).isEmpty();
		
		// inside second comment (succeeds now !)
		gotoPositionAndWait(276);
		
		assertThat(xmlListContents(insert.list("elements"))).contains("p");

		// demonstrate a limitation of local scope when Sidekick tree becomes
		// out of sync : no completion is available because the parent <comment>
		// is not in the SideKick Tree
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					b.insert(367, "<comment>");
				}
		});
		
		gotoPositionAndWait(376);
		
		assertThat(xmlListContents(insert.list("elements"))).isEmpty();
		
		
		insert.close();
		close(view(),b);
	}

	@Test
	public void testImportSchema(){
		
    	File xml = new File(testData,"import_schema/instance.xml");
    	
    	final Buffer b = TestUtils.openFile(xml.getPath());
    	
    	action("xml-insert-float",1);
    	FrameFixture insert = TestUtils.findFrameByTitle("XML Insert");
    	
		// wait for end of parsing
    	action("sidekick-parse",1);
		simplyWaitForMessageOfClass(sidekick.SideKickUpdate.class,10000);
		

		action("error-list-show",1);
		
    	FrameFixture errorlist = TestUtils.findFrameByTitle("Error List");
		
    	
 		errorlist.tree().selectRow(1);
		String selected = TestUtils.view().getTextArea().getSelectedText();
		assertTrue("got '"+selected+"'",selected.startsWith("  <ipo:comment>"));
		errorlist.close();
		
		// inside comment
		gotoPositionAndWait(391);
		
		// fails for the moment
		assertThat(xmlListContents(insert.list("elements"))).contains("ipo:comment");
		
		gotoPositionAndWait(472);
		
		// inside ipo:comment
		assertThat(xmlListContents(insert.list("elements"))).isEmpty();

		insert.close();
		close(view(),b);
	}
	
	@Test
	public void testImportSchemaRNG(){
    	File xml = new File(testData,"import_schema/relax_ng/instance.xml");
    	
    	final Buffer b = TestUtils.openFile(xml.getPath());
    	
    	parseAndWait();
    	
    	action("xml-insert-float",1);
    	FrameFixture insert = TestUtils.findFrameByTitle("XML Insert");
    	
		action("error-list-show",1);
		
    	FrameFixture errorlist = TestUtils.findFrameByTitle("Error List");
		
    	
 		errorlist.tree().selectRow(1);
		String selected = TestUtils.view().getTextArea().getSelectedText();
		assertTrue("got '"+selected+"'",selected.startsWith("  <ipo:comment>"));
		errorlist.close();
		
		// inside comment
		gotoPositionAndWait(320);
		
		// fails for the moment
		assertThat(xmlListContents(insert.list("elements"))).contains("ipo:comment");
		
		gotoPositionAndWait(391);
		
		// inside ipo:comment
		assertThat(xmlListContents(insert.list("elements"))).isEmpty();

		insert.close();
		
		close(view(),b);
	}
	
	@Test
	public void testMalformedInstanceDocument(){
		
    	File xml = new File(testData,"malformed/actions.xml");
    	
    	final Buffer b = TestUtils.openFile(xml.getPath());
    	
    	// XmlPlugin is not activated for some reason ??
		action("sidekick.parser.xml-switch");
		parseAndWait();

		// inside ACTIONS
		gotoPositionAndWait(731);
		
    	action("xml-insert-float",1);
    	FrameFixture insert = TestUtils.findFrameByTitle("XML Insert");

		assertThat(xmlListContents(insert.list("elements"))).contains("ACTION");
		
		insert.close();

		close(view(),b);
    	FrameFixture sidekick = TestUtils.findFrameByTitle("Sidekick");
    	sidekick.close();
	
	}
	
	@Test
	public void testDTDWithXSD(){
    	File xml = new File(testData,"dtd/dtdwithxsd.xml");
    	
    	final Buffer b = TestUtils.openFile(xml.getPath());
    	
    	action("xml-insert-float",1);
    	FrameFixture insert = TestUtils.findFrameByTitle("XML Insert");
    	
    	// will be prompted to download docbook.xsd
    	ClickT clickT = new ClickT(Option.NO);
    	clickT.start();
    	
    	parseAndWait();
		
    	clickT.waitForClick();
		// inside ACTIONS
		gotoPositionAndWait(783);
		
		assertThat(xmlListContents(insert.list("entities"))).contains("ecirc");
		assertThat(xmlListContents(insert.list("entities"))).excludes("ang");
		
		insert.close();
		
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					b.insert(538, "\n%isoamso;");
				}
		});
		
		parseAndWait();
		gotoPositionAndWait(795);

    	action("xml-insert-float",1);
    	insert = TestUtils.findFrameByTitle("XML Insert");
    	
		assertThat(xmlListContents(insert.list("entities"))).contains("ang");
		
		insert.close();
		close(view(),b);
	}
	
	/** test that predefined entities are proposed even with no completion info */
	@Test
	public void testNoCompletionInfo(){
    	File xml = new File(testData,"tagparser/test.xml");
    	
    	final Buffer b = TestUtils.openFile(xml.getPath());
    	
    	action("xml-insert-float",1);
    	FrameFixture insert = TestUtils.findFrameByTitle("XML Insert");
    	
    	parseAndWait();
		
		// inside p:p
		gotoPositionAndWait(45);
		
		assertThat(xmlListContents(insert.list("entities"))).contains("amp");
		
		insert.close();
		
		close(view(),b);
	}

	private static final JListCellReader instance = new BasicJListCellReader(
		new CellRendererReader(){
			public  String 	valueFrom(Component c) {
				if(c instanceof XmlListCellRenderer){
					return ((XmlListCellRenderer)c).getMainText();
				}else return null;
			}
		});
	
	public static JListCellReader xmlListCellReader(){
		return instance;
	}
	
	public static String[] xmlListContents(JListFixture list){
		return list.cellReader(xmlListCellReader()).contents();
	}
}
