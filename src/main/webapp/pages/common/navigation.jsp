<%@ include file="/pages/common/taglibs.jsp"%>
<%@ page import="com.eurodyn.uns.web.filters.EionetCASFilter" %>

<%@ page isELIgnored="false" contentType="text/html;charset=UTF-8" language="java"%>
<%
	com.eurodyn.uns.model.User user =  (com.eurodyn.uns.model.User) com.eurodyn.uns.web.jsf.LoginBean.getUser(request);
	String userRole = "";
	String userName = ((user != null) && (user.isLoggedIn())) ? user.getExternalId() : request.getRemoteUser();


	if(request.isUserInRole("admin")){
		userRole = "admin";
	}
	else if(request.isUserInRole("xmlrpc")){
		userRole = "rpc";
	}
	else if(userName != null){
		userRole = "eea";
	}else if(user != null) {
		 userRole = "eeaNotLogged";
	}

	request.setAttribute("userRole",userRole);

%>
	<f:verbatim>
	<c:if test="${userRole != ''}" >
		<ul>
			<c:choose>
			  <c:when test="${userRole== 'admin'}" >
			  		<c:choose>
				  		<c:when test="${sessionScope.user.preferDashboard}" >
							<li class="${selectedMenu == 'dashboard'?'selected':''}"><a href="<c:url value="/dash/${sessionScope.user.externalId}/dashboard.jsf"/>" title="Dashboard"><fmt:message key="label.menu.myDashboard"/> </a></li>
						</c:when>
						<c:otherwise>
							<li class="${selectedMenu == 'dashboard'?'selected':''}"><a href="<c:url value="/rss/${sessionScope.user.externalId}/rssReader.jsf?reset=true"/>" title="Dashboard"><fmt:message key="label.menu.myDashboard"/> </a></li>
						</c:otherwise>
					</c:choose>
					<%  if (request.isUserInRole("xmlrpc")) { %>
							<li class="${selectedMenu == 'channels'?'selected':''}"><a href="<c:url value="/xmlrpc/rpcUserChannels.jsf"/>" title="Your rpc channels">RPC Channels </a></li>
					<%}%>
					<li class="${selectedMenu == 'config'?'selected':''}"><a href="<c:url value="/admin/config/general.jsf"/>" title="Configuration"><fmt:message key="label.menu.config"/> </a></li>
					<li class="${selectedMenu == 'channels'?'selected':''}"><a href="<c:url value="/admin/channels/pullChannels.jsf"/>" title="Channels"><fmt:message key="label.menu.channels"/> </a></li>
					<li class="${selectedMenu == 'templates'?'selected':''}"><a href="<c:url value="/admin/templates/notificationTemplates.jsf"/>" title="Templates"><fmt:message key="label.menu.templates"/> </a></li>
					<li class="${selectedMenu == 'reports'?'selected':''}"><a href="<c:url value="/admin/reports/report_criteria.jsf"/>" title="Reports"><fmt:message key="label.menu.reports"/> </a></li>
					<li class="${selectedMenu == 'subscriptions'?'selected':''}"><a href="<c:url value="/subscriptions/subscriptions.jsf"/>" title="Subscriptions"><fmt:message key="label.menu.subscriptions"/> </a></li>
					<li class="${selectedMenu == 'cleandb'?'selected':''}"><a href="<c:url value="/admin/cleandb.jsf"/>" title="Clean database">Clean database </a></li>
			  </c:when>
			  <c:when test="${userRole== 'eea'}" >
			  		<c:choose>
				  		<c:when test="${sessionScope.user.preferDashboard}" >
							<li class="${selectedMenu == 'dashboard'?'selected':''}"><a href="<c:url value="/dash/${sessionScope.user.externalId}/dashboard.jsf"/>" title="Dashboard"><fmt:message key="label.menu.myDashboard"/> </a></li>
						</c:when>
						<c:otherwise>
							<li class="${selectedMenu == 'dashboard'?'selected':''}"><a href="<c:url value="/rss/${sessionScope.user.externalId}/rssReader.jsf"/>" title="Dashboard"><fmt:message key="label.menu.myDashboard"/> </a></li>
						</c:otherwise>
					</c:choose>
					<li class="${selectedMenu == 'subscriptions'?'selected':''}"><a href="<c:url value="/subscriptions/subscriptions.jsf"/>" title="Subscriptions"><fmt:message key="label.menu.subscriptions"/> </a></li>
					<%  if (request.isUserInRole("reports")) { %>
                            <li class="${selectedMenu == 'reports'?'selected':''}"><a href="<c:url value="/admin/reports/report_criteria.jsf"/>" title="Reports"><fmt:message key="label.menu.reports"/> </a></li>
                    <%}%>
			  </c:when>
			  <c:when test="${userRole == 'eeaNotLogged'}" >
			  		<c:choose>
				  		<c:when test="${sessionScope.user.preferDashboard}" >
							<li class="${selectedMenu == 'dashboard'?'selected':''}"><a href="<c:url value="/dash/${sessionScope.user.externalId}/dashboard.jsf"/>" title="Dashboard"><fmt:message key="label.menu.myDashboard"/> </a></li>
						</c:when>
						<c:otherwise>
							<li class="${selectedMenu == 'dashboard'?'selected':''}"><a href="<c:url value="/rss/${sessionScope.user.externalId}/rssReader.jsf"/>" title="Dashboard"><fmt:message key="label.menu.myDashboard"/> </a></li>
						</c:otherwise>
					</c:choose>
			  </c:when>
			  <c:when test="${userRole == 'rpc'}" >
					<li class="${selectedMenu == 'channels'?'selected':''}"><a href="<c:url value="/xmlrpc/rpcUserChannels.jsf"/>" title="Your rpc channels"><fmt:message key="label.menu.channels"/> </a></li>
			  </c:when>
			</c:choose>
		</ul>
	</c:if>
	</f:verbatim>

	<t:div  rendered="#{(not empty sessionScope.user) and sessionScope.user.loggedIn}" style="text-align:center;">
		<h:graphicImage url="/images/mail-no.gif" rendered="#{ not empty sessionScope['user'] and sessionScope['user'].vacationFlag}" alt="Vacation flag" />
	</t:div>
<h:form id="navigationForm" style="display:none">
</h:form>

