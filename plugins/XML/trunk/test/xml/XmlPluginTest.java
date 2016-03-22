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
import static org.fest.assertions.Assertions.assertThat;
import static org.gjt.sp.jedit.testframework.EBFixture.simplyWaitForMessageOfClass;
import static org.gjt.sp.jedit.testframework.TestUtils.action;
import static org.gjt.sp.jedit.testframework.TestUtils.close;
import static org.gjt.sp.jedit.testframework.TestUtils.requireEmpty;
import static org.gjt.sp.jedit.testframework.TestUtils.view;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static xml.XMLTestUtils.gotoPositionAndWait;
import static xml.XMLTestUtils.openParseAndWait;
import static xml.XMLTestUtils.parseAndWait;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.SwingUtilities;

import org.fest.assertions.ObjectArrayAssert;
import org.fest.swing.cell.JListCellReader;
import org.fest.swing.core.MouseButton;
import org.fest.swing.driver.BasicJListCellReader;
import org.fest.swing.driver.CellRendererReader;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiTask;
import org.fest.swing.exception.ComponentLookupException;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JListFixture;
import org.fest.swing.timing.Pause;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.testframework.TestUtils;
import org.gjt.sp.jedit.testframework.TestUtils.ClickT;
import org.gjt.sp.jedit.testframework.TestUtils.Option;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import xml.XMLTestUtils.JWindowFixture;
// }}}

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
    	
    	final Buffer b = openParseAndWait(xml.getPath());
    	
    	try{
	    	gotoPositionAndWait(348);
			GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					view().getTextArea().setSelectedText("<");
				}
			});
			
			action("sidekick-complete",1);
			
			JWindowFixture completion;
			
			completion = XMLTestUtils.completionPopup();
			completion.requireVisible();
			ObjectArrayAssert al = assertThat(xmlListContents(completion.list()));
			
			// reportTitle is in the report namespace. It's proposed without prefix
			al.contains("reportTitle");
			// comment is abstract: it's not proposed
			al.excludes("ns0:comment");
			// some concrete comments are proposed, in a generated prefix (ns0)
			al.contains("ns0:customerComment");
			JListFixture list = completion.list().cellReader(xmlListCellReader());
			
			list.selectItem("reportTitle");
			
			// inserted, without IPO namespace
			assertTrue(b.getText().contains("<reportTitle></reportTitle>"));
			assertFalse(b.getText(348,b.getLength()-348).contains("http://www.example.com/IPO"));
			
			action("undo",1);
			
			// now, try to insert element with generated prefix
			GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					view().getTextArea().setSelectedText("<");
				}
			});
			
			action("sidekick-complete",1);

			completion = XMLTestUtils.completionPopup();
			completion.requireVisible();
			assertThat(xmlListContents(completion.list())).contains("ns0:customerComment");
			
			list = completion.list().cellReader(xmlListCellReader());

			int iRT = list.item("ns0:customerComment").index();
			for(int i=0;i<iRT;i++){
				list.pressAndReleaseKeys(KeyEvent.VK_DOWN);
			}
			list.pressAndReleaseKeys(KeyEvent.VK_SPACE);
			
			assertTrue(b.getText().contains("<ns0:customerComment xmlns:ns0=\"http://www.example.com/IPO\""));

			action("undo",1);
			
			// now, try to insert element with chosen prefix
			GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					view().getTextArea().setSelectedText("<ipo:");
				}
			});
			
			action("sidekick-complete",1);

			completion = XMLTestUtils.completionPopup();
			completion.requireVisible();
			// reportTitle can't be there because we chose a new prefix
			assertThat(xmlListContents(completion.list())).contains("ipo:customerComment").excludes("reportTitle");
			
			list = completion.list().cellReader(xmlListCellReader());

			list.selectItem("ipo:customerComment");
			
			assertTrue(b.getText().contains("<ipo:customerComment xmlns:ipo=\"http://www.example.com/IPO\""));
			
			parseAndWait();
			
			Pause.pause(1000);
			// try to insert customerId 
			// now, try to insert element with chosen prefix
			GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					view().getTextArea().setSelectedText("<");
				}
			});
			
			action("sidekick-complete",1);

			completion = XMLTestUtils.completionPopup();
			completion.requireVisible();
			assertThat(xmlListContents(completion.list())).contains("customerId");
			
			
    	}finally{
    		// discard changes
    		TestUtils.close(view(), b);
    	}
    }
    
    /** Tests XML Plugin's completion
    * see test_data/attributes_completion 
    */
    @Test
    public void testAttributesCompletion() throws IOException{
    	File xml = new File(testData,"attributes_completion/attributes.xml");
    	
    	openParseAndWait(xml.getPath());
    	
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
		}catch(ComponentLookupException e){
			// it's OK
		}
		
		Pause.pause(500);

		// aa, ab
		gotoPositionAndWait(653);
		
		action("sidekick-complete",1);
		
		completion = XMLTestUtils.completionPopup();
		
		completion.requireVisible();
		assertThat(xmlListContents(completion.list())).containsOnly("aa","ab");
		completion.list().pressAndReleaseKeys(KeyEvent.VK_ESCAPE);
		
		Pause.pause(500);

		// aa,ab
		gotoPositionAndWait(860);
		action("sidekick-complete",1);
		completion = XMLTestUtils.completionPopup();
		
 		completion.requireVisible();
		assertThat(xmlListContents(completion.list())).containsOnly("aa","ab");
		completion.list().pressAndReleaseKeys(KeyEvent.VK_ESCAPE);
		Pause.pause(500);

		// aa,ab
		gotoPositionAndWait(974);
		action("sidekick-complete",1);
		completion = XMLTestUtils.completionPopup();

		completion.requireVisible();
		assertThat(xmlListContents(completion.list())).containsOnly("aa","ab");
		completion.list().pressAndReleaseKeys(KeyEvent.VK_ESCAPE);
		Pause.pause(500);

		
		gotoPositionAndWait(1037);
		action("sidekick-complete",1);
		try{
			completion = XMLTestUtils.completionPopup();
			fail("shouldn't be there");
		}catch(ComponentLookupException e){
			// it's OK
		}
		
		gotoPositionAndWait(1052);
		action("sidekick-complete",1);
		try{
			completion = XMLTestUtils.completionPopup();
			fail("shouldn't be there");
		}catch(ComponentLookupException e){
			// it's OK
		}

		// P bug #3533666 : should be nothing, is aa, ab
		gotoPositionAndWait(573);
		
		action("sidekick-complete",1);
		Pause.pause(500);
		
		try{
			completion = XMLTestUtils.completionPopup();
			fail("shouldn't be there");
		}catch(ComponentLookupException e){
			// it's OK
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
    	
    	openParseAndWait(xml.getPath());
    	
    	action("xml-insert-float",1);
    	
    	
    	FrameFixture insert = TestUtils.findFrameByTitle("XML Insert");
    	
		// go into the file
		gotoPositionAndWait(341);
		
		// entities declared in the root document appear here
		assertThat(xmlListContents(insert.list("entities"))).contains("frag2");
		// ids declared in other fragment appear heare
		assertThat(xmlListContents(insert.list("ids"))).contains("testing [element: <chapter>]");
		
		insert.list("ids").cellReader(xmlListCellReader()).item("testing [element: <chapter>]").click(MouseButton.RIGHT_BUTTON);
		
		
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
		
		Pause.pause(1000);
		errorlist.tree().selectRow(1);
		// the line is deselected when I go to actions.xsd
		Pause.pause(1000);
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
		
		xml = new File(testData,"relax_ng/valid_actions.xml");
    	
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
    	
    	final Buffer b = openParseAndWait(xml.getPath());
    	
    	action("xml-insert-float",1);
    	FrameFixture insert = TestUtils.findFrameByTitle("XML Insert");
    	
		action("error-list-show",1);
		
    	FrameFixture errorlist = TestUtils.findFrameByTitle("Error List");
		
    	
 		errorlist.tree().selectRow(1);
		String selected = TestUtils.view().getTextArea().getSelectedText();
		assertTrue("got '"+selected+"'",selected.startsWith("  <ipo:comment>"));
		errorlist.close();
		
		// inside comment
		gotoPositionAndWait(731);
		
		assertThat(xmlListContents(insert.list("elements"))).contains("ipo:comment");
		
		gotoPositionAndWait(802);
		
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
				}else return "argh";
			}
		});
	
	public static JListCellReader xmlListCellReader(){
		return instance;
	}
	
	public static String[] xmlListContents(JListFixture list){
		return list.cellReader(xmlListCellReader()).contents();
	}
}
