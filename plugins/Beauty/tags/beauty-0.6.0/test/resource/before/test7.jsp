<table cellpadding="0" cellspacing="0">
  <tr>
    <td class="ma_mod_pageHeader">
      <c:set var="imgsrc" value="/estore/images/cart/ma/ma_mod/pageHeader_myAccount.jpg"/>
      <img src="${imgsrc}"/>
    </td>
    <td class="ma_mod_pageHeader">
      <img src="/estore/images/cart/ma/ma_mod/pageHeader_myAddressBook.jpg"/>
      <c:forEach var="item" items="${whatever}" begin="0" end="6" step="2">
      some text
      </c:forEach>
      <span>
        some text
      </span>
      <b> ${whatever}</b>
      <strong>
        <c:out value="${title}"/>
      </strong>
    </td>
  </tr>
</table>
