<%-- one line comment --%>
<table cellpadding="0" cellspacing="0">
  <tr>
    <%--
      three
      line
      comment
    --%>
    <td class="ma_mod_pageHeader">
      <c:set var="imgsrc" value="/estore/images/cart/ma/ma_mod/pageHeader_myAccount.jpg"/>
      <img src="${imgsrc}"/>
    </td>
    <td class="ma_mod_pageHeader">

      <img src="/estore/images/cart/ma/ma_mod/pageHeader_myAddressBook.jpg"/>
      some text
      <span>
        <%-- one line --%>
        some text
      </span>
      <b> ${whatever}</b>
      <%--
        three lines, next line is blank




        lines
      --%>
      <strong>
        <c:out value="${title}"/>
      </strong>
    </td>
  </tr>
</table>
