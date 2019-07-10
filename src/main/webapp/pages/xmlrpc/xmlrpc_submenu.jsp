<%@ include file="/pages/common/taglibs.jsp"%>
<%@ page import="com.eurodyn.uns.web.filters.EionetCASFilter" %>
<%
	com.eurodyn.uns.model.User user =  (com.eurodyn.uns.model.User) com.eurodyn.uns.web.jsf.LoginBean.getUser(request);
	String userRole = "";
	String userName = ((user != null) && (user.isLoggedIn())) ? user.getExternalId() : request.getRemoteUser();


	if(request.isUserInRole("admin")){
		if(request.isUserInRole("xmlrpc")){
			userRole = "admin";
		}
	}
	else if(request.isUserInRole("xmlrpc")){
		userRole = "rpc";
	}


	request.setAttribute("userRole",userRole);

%>
<c:if test="${userRole == 'admin' || userRole == 'rpc'}" >
<f:verbatim>
<div id="tabbedmenu">
		<ul>
			<li id="currenttab"><span  title="RPC Channels">RPC Channels</span></li>		
		</ul>
</div>
</f:verbatim>
</c:if>

 