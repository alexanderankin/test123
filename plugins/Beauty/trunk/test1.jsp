<%@ include file="/estore/common/common.jspf" %>
<%@ taglib uri="/WEB-INF/tlds/pageTemplate.tld" prefix="page" %>
<%@ page import="com.avenueme.util.WebKeys" %>
<%@ page import="com.avenueme.beans.UserInfoBean" %>
<%@ page import="com.avenueme.beans.UserInfoFactory" %>
<html>
  <body>
    <p>
    <br>
    <dsp:page xml="true">
      <table>
        <tr>
          <!-- a comment -->
          <td class="ma_mod_pageFrame_pageHeaderTD">
            <%-- insert ma_mod_pageHeader --%>
            <table cellpadding="0" cellspacing="0">
              <tr>
                <td class="ma_mod_pageHeader">
                  <img src="<page:fqdnLink url="/estore/images/cart/ma/ma_mod/pageHeader_myAccount.jpg" isImage="true" 
                  useStaticServer="true" />" />  
                </td> 
                <td class="ma_mod_pageHeader">
                  <img src="<page:fqdnLink url="/estore/images/cart/ma/ma_mod/pageHeader_myAddressBook.jpg" 
                  isImage="true" useStaticServer="true" />" />  
                </td> 
                <td width="100%">
                  &nbsp;  
                </td> 
              </tr> 
            </table> 
          </td> 
        </tr> 
      </table> 
    </dsp:page> 
  </body> 
</html> 
