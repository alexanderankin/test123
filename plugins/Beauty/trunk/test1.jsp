<%@ include file="/estore/common/common.jspf"%>
<%@ taglib uri="/WEB-INF/tlds/pageTemplate.tld" prefix="page"%>
<%@ page import="com.avenueme.util.WebKeys"%>
<%@ page import="com.avenueme.beans.UserInfoBean"%>
<%@ page import="com.avenueme.beans.UserInfoFactory"%>
<html>
  <head>
    <style>
      li, p {
        background-color: lime
      }
      address {
        background-color: lime
      }
      * {
        color: lime
      }
      ul, p {
        color: red
      }
      *.t1 {
        color: lime
      }
      #foo {
        background-color: lime
      }
      p {
        background-color: red
      }
      p {
        background-color: red
      }
      p[title] {
        background-color: lime
      }
      address {
        background-color: red
      }
      address[title = "foo"] {
        background-color: lime
      }
      span[title = "a"] {
        background-color: red
      }
    </style>
  </head>
  <body>
    <script type="text/javascript">
			var x; 
			var mycars = new Array( ); 
			mycars[ 0 ] = "Saab"; 
			mycars[ 1 ] = "Volvo"; 
			mycars[ 2 ] = "BMW"; 
			for( x in mycars ) {
			    document.write( mycars[ x ] + "<br />" );
			} 
    </script>
    <dsp:page xml="true">
      <%
				add(start);
				writeln();
				++ token_source.level;
				try {
				    if (content != null && content.image.trim().length() > 0) {
				        Beautifier beautifier = new DefaultBeautifier("java");
				        beautifier.setIndentWidth(indentWidth);
				        beautifier.setTabWidth(tabSize);
				        beautifier.setUseSoftTabs(useSoftTabs);
				        beautifier.setInitialIndentLevel(token_source.level);
				        String java = beautifier.beautify(content.image.trim());
				        writePre(java);
				    } 
				    List < String > list = new ArrayList < String >();
				    for (String s : list) {
				      %>
				      <c:out value="${s}"/>   
				      <%
				   }
				} 
				catch (ParserException pe) {
				    // TODO: handle this
				} 
				-- token_source.level;
				add(end);
				writeln();
      %>
      <table>
        <tr>
          <!-- a comment -->
          <td class="ma_mod_pageFrame_pageHeaderTD">
            <%-- insert ma_mod_pageHeader --%>
            <table cellpadding="0" cellspacing="0">
              <tr>
                <td class="ma_mod_pageHeader">
                  <c:set var="imgsrc" value="/estore/images/cart/ma/ma_mod/pageHeader_myAccount.jpg"/>
                  <img src="${imgsrc}"/>
                </td>
                <td class="ma_mod_pageHeader">
                  <img src="/estore/images/cart/ma/ma_mod/pageHeader_myAddressBook.jpg"/>
                </td>
                <td width="100%">
                  &nbsp;<br/>
                </td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
    </dsp:page>
  </body>
</html>

