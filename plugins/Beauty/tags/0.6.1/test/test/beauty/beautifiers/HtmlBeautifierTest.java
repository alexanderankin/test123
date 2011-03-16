
package beauty.beautifiers;


import org.junit.*;
import static org.junit.Assert.*;

public class HtmlBeautifierTest {
 
    @Test
    public void test1() {
        try {
        // make sure jsp tags aren't converted to lowercase
        String before = 
            "<c:forEach items=\"${suite.bundleMembers}\" var=\"column\" >\n" +
            "  <div class=\"Columns\">\n" +
            "    <%-- TODO: current product needs to be highlighted with <p class=\"HeadingSmall\"> --%>\n" +
            "    <div class=\"ColumnsFour LayoutCellTop LayoutCellLeft\">\n" +
            "      <c:forEach items=\"${column}\" var=\"value\" >\n" +
            "        <p>\n" +
            "          <c:out value=\"${value}\" />\n" +
            "        </p>\n" +
            "      </c:forEach>\n" + 
            "    </div>\n" + 
            "  </div>\n" + 
            "</c:forEach>\n";        
        String answer = 
            "<c:forEach items=\"${suite.bundleMembers}\" var=\"column\" >\n" +
            "  <div class=\"Columns\">\n" +
            "    <%-- TODO: current product needs to be highlighted with <p class=\"HeadingSmall\"> --%>\n" +
            "    <div class=\"ColumnsFour LayoutCellTop LayoutCellLeft\">\n" +
            "      <c:forEach items=\"${column}\" var=\"value\" >\n" +
            "        <p>\n" +
            "          <c:out value=\"${value}\" />\n" +
            "        </p>\n" +
            "      </c:forEach> \n" + 
            "    </div> \n" + 
            "  </div> \n" + 
            "</c:forEach> \n";        
 
        Beautifier beautifier = new HtmlFormat();
        beautifier.setEditMode( "jsp" );
        beautifier.setLineSeparator( "\n" );
        beautifier.setTabWidth( 2 );
        beautifier.setIndentWidth( 2 );
        beautifier.setUseSoftTabs( true );
        beautifier.setWrapMargin( 80 );
        beautifier.setWrapMode( "none" );
        String after = beautifier.beautify(before);
        assertTrue("jsp tag test failed.\nbefore:\n|" + before + "|\n\nafter:\n|" + after + "|", answer.equals(after));
        } catch (Exception e) {
            fail(e.getMessage());   
        }
    }
}