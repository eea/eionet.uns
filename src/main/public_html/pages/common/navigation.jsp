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

<h:form id="navigationForm">
	<f:verbatim><div id="globalnav"><h2>Contents</h2></f:verbatim>
	<f:verbatim>
	<c:if test="${userRole != ''}" > 
		<ul>
			<c:choose> 
			  <c:when test="${userRole== 'admin'}" > 
			  		<c:choose>
				  		<c:when test="${sessionScope.user.preferDashboard}" > 
							<li class="${selectedMenu == 'dashboard'?'selected':''}"><a href="<c:url value="/dash/${sessionScope.user.externalId}/dashboard.jsf"/>" title="Dashboard"><fmt:message key="label.menu.myDashboard"/></a></li>
						</c:when>
						<c:otherwise> 	
							<li class="${selectedMenu == 'dashboard'?'selected':''}"><a href="<c:url value="/rss/${sessionScope.user.externalId}/rssReader.jsf?reset=true"/>" title="Dashboard"><fmt:message key="label.menu.myDashboard"/></a></li>
						</c:otherwise>
					</c:choose>
					<%  if (request.isUserInRole("xmlrpc")) { %> 
							<li class="${selectedMenu == 'channels'?'selected':''}"><a href="<c:url value="/xmlrpc/rpcUserChannels.jsf"/>" title="Your rpc channels">RPC Channels</a></li>
					<%}%>												
					<li class="${selectedMenu == 'config'?'selected':''}"><a href="<c:url value="/admin/config/general.jsf"/>" title="Configuration"><fmt:message key="label.menu.config"/></a></li>
					<li class="${selectedMenu == 'channels'?'selected':''}"><a href="<c:url value="/admin/channels/pullChannels.jsf"/>" title="Channels"><fmt:message key="label.menu.channels"/></a></li>
					<li class="${selectedMenu == 'templates'?'selected':''}"><a href="<c:url value="/admin/templates/notificationTemplates.jsf"/>" title="Templates"><fmt:message key="label.menu.templates"/></a></li>
					<li class="${selectedMenu == 'reports'?'selected':''}"><a href="<c:url value="/admin/reports/report_criteria.jsf"/>" title="Reports"><fmt:message key="label.menu.reports"/></a></li>
					<li class="${selectedMenu == 'subscriptions'?'selected':''}"><a href="<c:url value="/subscriptions/subscriptions.jsf"/>" title="Subscriptions"><fmt:message key="label.menu.subscriptions"/></a></li>
					<li><a href="#" onclick="javascript:openWindow('<c:url value="/help/help.jsp"/>','onlinehelp');" title="Help"><fmt:message key="label.menu.help"/></a></li>
			  </c:when> 
			  <c:when test="${userRole== 'eea'}" > 
			  		<c:choose>
				  		<c:when test="${sessionScope.user.preferDashboard}" > 
							<li class="${selectedMenu == 'dashboard'?'selected':''}"><a href="<c:url value="/dash/${sessionScope.user.externalId}/dashboard.jsf"/>" title="Dashboard"><fmt:message key="label.menu.myDashboard"/></a></li>
						</c:when>
						<c:otherwise> 	
							<li class="${selectedMenu == 'dashboard'?'selected':''}"><a href="<c:url value="/rss/${sessionScope.user.externalId}/rssReader.jsf"/>" title="Dashboard"><fmt:message key="label.menu.myDashboard"/></a></li>
						</c:otherwise>
					</c:choose>		
					<li class="${selectedMenu == 'subscriptions'?'selected':''}"><a href="<c:url value="/subscriptions/subscriptions.jsf"/>" title="Subscriptions"><fmt:message key="label.menu.subscriptions"/></a></li>
					<li><a href="#" onclick="javascript:openWindow('<c:url value="/help/help.jsp"/>','onlinehelp');" title="Help"><fmt:message key="label.menu.help"/></a></li>
			  </c:when> 
			  <c:when test="${userRole == 'eeaNotLogged'}" > 
			  		<c:choose>
				  		<c:when test="${sessionScope.user.preferDashboard}" > 
							<li class="${selectedMenu == 'dashboard'?'selected':''}"><a href="<c:url value="/dash/${sessionScope.user.externalId}/dashboard.jsf"/>" title="Dashboard"><fmt:message key="label.menu.myDashboard"/></a></li>
						</c:when>
						<c:otherwise> 	
							<li class="${selectedMenu == 'dashboard'?'selected':''}"><a href="<c:url value="/rss/${sessionScope.user.externalId}/rssReader.jsf"/>" title="Dashboard"><fmt:message key="label.menu.myDashboard"/></a></li>
						</c:otherwise>
					</c:choose>		
					<li><a href="#" onclick="javascript:openWindow('<c:url value="/help/help.jsp"/>','onlinehelp');" title="Help"><fmt:message key="label.menu.help"/></a></li>
			  </c:when> 
			  <c:when test="${userRole == 'rpc'}" > 
					<li class="${selectedMenu == 'channels'?'selected':''}"><a href="<c:url value="/xmlrpc/rpcUserChannels.jsf"/>" title="Your rpc channels"><fmt:message key="label.menu.channels"/></a></li>
					<li><a href="#" onclick="javascript:openWindow('<c:url value="/help/help.jsp"/>','onlinehelp');" title="Help"><fmt:message key="label.menu.help"/></a></li>
			  </c:when> 
			</c:choose>  						
		</ul>
	</c:if> 	
	</f:verbatim>		

	<h:panelGroup rendered="#{(empty sessionScope.user) or not sessionScope.user.loggedIn }">
		<f:verbatim>
			<h2>
				<fmt:message key="label.menu.notlogged"/>
			</h2>								
			<ul>														
				<li><a href="<%=EionetCASFilter.getCASLoginURL(request)%>" title="login">Login</a></li>
			</ul>
		</f:verbatim>	
	</h:panelGroup>
			
	<h:panelGroup rendered="#{(not empty sessionScope.user) and sessionScope.user.loggedIn}" >
		<f:verbatim>
			<h2>
				<fmt:message key="label.menu.logged"/>
				<br/>
				</f:verbatim>
					<h:outputText value="#{sessionScope.user.externalId}"/>
				<f:verbatim>
			</h2>
			<ul>
				<li><a href="<c:url value="/pages/logout.jsp" />" title="login">Logout</a></li>
			</ul>
		</f:verbatim>	
	</h:panelGroup>
	<t:div  rendered="#{(not empty sessionScope.user) and sessionScope.user.loggedIn}" style="text-align:center;">
		<h:graphicImage url="/images/mail-no.gif" rendered="#{ not empty sessionScope['user'] and sessionScope['user'].vacationFlag}" alt="Vacation flag" />
	</t:div>	
	<f:verbatim>
		<h2>Reportnet </h2>
		<ul>
			<li><a title="Reporting Obligations" href="http://rod.eionet.europa.eu/">ROD Obligations</a></li>
			<li><a title="Central Data Repository" href="http://cdr.eionet.europa.eu/">CDR Repository</a></li>
			<li><a title="Data Dictionary" href="http://dd.eionet.europa.eu/">Data Dictionary</a></li>
			<li><a title="Content Registry" href="http://cr.eionet.europa.eu/">Content Registry</a></li>
		</ul>
	</f:verbatim>
    <f:verbatim>
   </div>
  </f:verbatim>
</h:form>

