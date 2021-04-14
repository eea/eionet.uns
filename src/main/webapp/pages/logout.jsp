<%@ page import="com.eurodyn.uns.web.filters.EionetCASFilter" %>
<%@ page import="com.eurodyn.uns.web.filters.EULoginCASFilter" %>
<% session.invalidate(); %>
<% if(EULoginCASFilter.checkEULoginCookie(request)) {
    EULoginCASFilter.attachEULoginCookie(response,false);
    response.sendRedirect(EULoginCASFilter.getCASLogoutURL(request));
} else {
    EionetCASFilter.attachEionetLoginCookie(response,false);
    response.sendRedirect(EionetCASFilter.getCASLogoutURL(request));
}%>