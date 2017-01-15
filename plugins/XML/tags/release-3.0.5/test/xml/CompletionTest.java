/*
 * CompletionTest.java
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2016 Eric Le Lay
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */
package xml;

// {{{ imports
import static org.fest.assertions.Assertions.assertThat;
import static org.gjt.sp.jedit.testframework.TestUtils.action;
import static org.junit.Assert.fail;
import static xml.XMLTestUtils.gotoPositionAndWait;
import static xml.XMLTestUtils.openParseAndWait;
import static xml.XmlPluginTest.xmlListContents;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import org.fest.swing.exception.ComponentLookupException;
import org.fest.swing.timing.Pause;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.testframework.JEditRunner;
import org.gjt.sp.jedit.testframework.TestData;
import org.gjt.sp.jedit.testframework.TestUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import xml.XMLTestUtils.JWindowFixture;
//}}}



@RunWith(JEditRunner.class)
public class CompletionTest {
    @Rule
    public TestData testData = new TestData();

    /** Tests XML Plugin's completion
     * see test_data/attributes_completion
     */
    @Test
    public void testAttributesCompletion() throws IOException{
        File xml = new File(testData.get(), "attributes_completion/attributes.xml");

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

    /** Tests XML Plugin's attribute value completion
     * see test_data/attributes_completion
     */
    @Test
    public void testAttributesValueCompletion() throws IOException{
        File xml = new File(testData.get(), "attributes_completion/value_completion.xml");

        Buffer buffer = openParseAndWait(xml.getPath());

        // after aa=
        gotoPositionAndWait(409);

        TestUtils.insertInEDT(buffer, 409, "value=\"");

        action("sidekick-complete",1);

        JWindowFixture completion = XMLTestUtils.completionPopup();

        completion.requireVisible();
        assertThat(xmlListContents(completion.list())).containsOnly("a","b","c");
        completion.list().pressAndReleaseKeys(KeyEvent.VK_ESCAPE);

        Pause.pause(500);

    }

}
