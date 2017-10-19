<%@ page import="com.eurodyn.uns.web.filters.EionetCASFilter" %>
<% session.invalidate(); %>
<% EionetCASFilter.attachEionetLoginCookie(response,false); %>
<% response.sendRedirect(EionetCASFilter.getCASLogoutURL(request)); %>
