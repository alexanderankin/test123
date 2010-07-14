
package beauty.beautifiers;


import org.junit.*;
import static org.junit.Assert.*;

import org.gjt.sp.jedit.jEdit;

import beauty.BeautyPlugin;
import org.gjt.sp.jedit.testframework.TestUtils;


public class JspBeautifierTest {
 
 
 
    @BeforeClass
    public static void setUpjEdit() {
        TestUtils.beforeClass();
    }

 
 
 
    @AfterClass
    public static void tearDownjEdit() {
        TestUtils.afterClass();
    }

 
 
 
    @Before
    public void beforeTest() {
        jEdit.getPlugin(BeautyPlugin.class.getName()).getPluginJAR().activatePluginIfNecessary();
    }
 
 
 
    @Test
    public void test1() {
        // basic test, collapse blank lines is true
        try {
            jEdit.setBooleanProperty("beauty.jsp.padSlashEnd", false);
            jEdit.setBooleanProperty("beauty.jsp.padTagEnd", false);
            jEdit.setBooleanProperty("beauty.jsp.wrapAttributes", false);
            jEdit.setBooleanProperty("beauty.jsp.collapseBlankLines", true);
            
            StringBuilder before = new StringBuilder();
            before.append("            <table cellpadding=\"0\" cellspacing=\"0\">\n");
            before.append("              <tr><td class=\"ma_mod_pageHeader\"><c:set var=\"imgsrc\" value=\"/estore/images/cart/ma/ma_mod/pageHeader_myAccount.jpg\"/>\n");
            before.append("                  <img src=\"${imgsrc}\"/>\n");
            before.append("                  </td>\n");
            before.append("                <td class=\"ma_mod_pageHeader\">\n");
            before.append("\n"); 
            before.append("                  <img src=\"/estore/images/cart/ma/ma_mod/pageHeader_myAddressBook.jpg\"/>\n");
            before.append("                  some text\n");
            before.append("                  <span>\n");
            before.append("                    some text\n");
            before.append("                  </span>\n");
            before.append("\n"); 
            before.append("\n"); 
            before.append("\n"); 
            before.append("\n"); 
            before.append("                  <strong><c:out value=\"${title}\"/>\n");
            before.append("                  </strong>\n");
            before.append("                </td>\n");
            before.append("</tr>\n");
            before.append("</table>\n"); 
 
            StringBuilder answer = new StringBuilder();
            answer.append("<table cellpadding=\"0\" cellspacing=\"0\">\n");
            answer.append("  <tr>\n");
            answer.append("    <td class=\"ma_mod_pageHeader\">\n");
            answer.append("      <c:set var=\"imgsrc\" value=\"/estore/images/cart/ma/ma_mod/pageHeader_myAccount.jpg\"/>\n");
            answer.append("      <img src=\"${imgsrc}\"/>\n");
            answer.append("    </td>\n");
            answer.append("    <td class=\"ma_mod_pageHeader\">\n");
            answer.append("\n");
            answer.append("      <img src=\"/estore/images/cart/ma/ma_mod/pageHeader_myAddressBook.jpg\"/>\n");
            answer.append("      some text\n");
            answer.append("      <span>\n");
            answer.append("        some text\n");
            answer.append("      </span>\n");
            answer.append("\n");
            answer.append("      <strong>\n");
            answer.append("        <c:out value=\"${title}\"/>\n");
            answer.append("      </strong>\n");
            answer.append("    </td>\n");
            answer.append("  </tr>\n");
            answer.append("</table>\n");
 
            Beautifier beautifier = new JspBeautifier();
            beautifier.setEditMode("jsp");
            beautifier.setLineSeparator("\n");
            beautifier.setTabWidth(2);
            beautifier.setIndentWidth(2);
            beautifier.setUseSoftTabs(true);
            beautifier.setWrapMargin(80);
            beautifier.setWrapMode("none");
            String after = beautifier.beautify(before.toString());
            assertTrue("returned text was null", after != null);
            assertTrue("jsp test failed.\nbefore:\n|" + before + "|\n\nafter:\n|" + after + "|", answer.toString().equals(after));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage()); 
        }
    }
    
    @Test
    public void test2() {
        // in this test, collapse blank lines is false.
        try {
            jEdit.setBooleanProperty("beauty.jsp.padSlashEnd", false);
            jEdit.setBooleanProperty("beauty.jsp.padTagEnd", false);
            jEdit.setBooleanProperty("beauty.jsp.wrapAttributes", false);
            jEdit.setBooleanProperty("beauty.jsp.collapseBlankLines", false);
            
            StringBuilder before = new StringBuilder();
            before.append("            <table cellpadding=\"0\" cellspacing=\"0\">\n");
            before.append("              <tr><td class=\"ma_mod_pageHeader\"><c:set var=\"imgsrc\" value=\"/estore/images/cart/ma/ma_mod/pageHeader_myAccount.jpg\"/>\n");
            before.append("                  <img src=\"${imgsrc}\"/>\n");
            before.append("                  </td>\n");
            before.append("                <td class=\"ma_mod_pageHeader\">\n");
            before.append("\n"); 
            before.append("                  <img src=\"/estore/images/cart/ma/ma_mod/pageHeader_myAddressBook.jpg\"/>\n");
            before.append("                  some text\n");
            before.append("                  <span>\n");
            before.append("                    some text\n");
            before.append("                  </span>\n");
            before.append("\n"); 
            before.append("\n"); 
            before.append("\n"); 
            before.append("\n"); 
            before.append("                  <strong><c:out value=\"${title}\"/>\n");
            before.append("                  </strong>\n");
            before.append("                </td>\n");
            before.append("</tr>\n");
            before.append("</table>\n"); 
 
            StringBuilder answer = new StringBuilder();
            answer.append("<table cellpadding=\"0\" cellspacing=\"0\">\n");
            answer.append("  <tr>\n");
            answer.append("    <td class=\"ma_mod_pageHeader\">\n");
            answer.append("      <c:set var=\"imgsrc\" value=\"/estore/images/cart/ma/ma_mod/pageHeader_myAccount.jpg\"/>\n");
            answer.append("      <img src=\"${imgsrc}\"/>\n");
            answer.append("    </td>\n");
            answer.append("    <td class=\"ma_mod_pageHeader\">\n");
            answer.append("\n");
            answer.append("      <img src=\"/estore/images/cart/ma/ma_mod/pageHeader_myAddressBook.jpg\"/>\n");
            answer.append("      some text\n");
            answer.append("      <span>\n");
            answer.append("        some text\n");
            answer.append("      </span>\n");
            answer.append("\n");
            answer.append("\n");
            answer.append("\n");
            answer.append("\n");
            answer.append("      <strong>\n");
            answer.append("        <c:out value=\"${title}\"/>\n");
            answer.append("      </strong>\n");
            answer.append("    </td>\n");
            answer.append("  </tr>\n");
            answer.append("</table>\n");
 
            Beautifier beautifier = new JspBeautifier();
            beautifier.setEditMode("jsp");
            beautifier.setLineSeparator("\n");
            beautifier.setTabWidth(2);
            beautifier.setIndentWidth(2);
            beautifier.setUseSoftTabs(true);
            beautifier.setWrapMargin(80);
            beautifier.setWrapMode("none");
            String after = beautifier.beautify(before.toString());
            assertTrue("returned text was null", after != null);
            assertTrue("jsp test failed.\nbefore:\n|" + before + "|\n\nafter:\n|" + after + "|", answer.toString().equals(after));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage()); 
        }
    }
    
}