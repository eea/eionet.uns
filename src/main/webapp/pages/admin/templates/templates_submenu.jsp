<%@ include file="/pages/common/taglibs.jsp"%>
<%@ page import="com.eurodyn.uns.web.filters.EionetCASFilter" %>
<%
	com.eurodyn.uns.model.User user =  (com.eurodyn.uns.model.User) com.eurodyn.uns.web.jsf.LoginBean.getUser(request);
	String userRole = "";
	String userName = ((user != null) && (user.isLoggedIn())) ? user.getExternalId() : request.getRemoteUser();


	if(request.isUserInRole("admin")){
		userRole = "admin";
	}

	request.setAttribute("userRole",userRole);

%>
<c:if test="${userRole == 'admin'}" >
<f:verbatim>
<div id="tabbedmenu">
	<c:if  test="${selectedSubMenu == 'notificationTemplates'}">
		<ul>
			<li id="currenttab"><span  title="Existing notification templates list">Notification templates</span></li>		
			<li><a title="Existing stylesheets list" href="<c:url value='/admin/templates/dashTemplates.jsf' />"  >Stylesheets</a></li>
		</ul>
	</c:if>
	<c:if  test="${selectedSubMenu == 'dashTemplates'}" >
		<ul>
			<li><a title="Existing notification templates list" href="<c:url value='/admin/templates/notificationTemplates.jsf'/>"   >Notification templates</a></li>		
			<li id="currenttab"><span  title="Existing stylesheets templates list">Stylesheets</span></li>
		</ul>
	</c:if>
</div>
</f:verbatim>
</c:if>