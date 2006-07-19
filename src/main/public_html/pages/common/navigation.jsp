<%@ include file="/pages/common/taglibs.jsp"%>


<%@ page isELIgnored="false" contentType="text/html;charset=UTF-8" language="java"%>
<% 
	com.eurodyn.uns.model.User user =  (com.eurodyn.uns.model.User) com.eurodyn.uns.web.jsf.LoginBean.getUser(request);
	String userRole = "";
	if(request.isUserInRole("admin")){
		userRole = "admin";
	}
	else if(request.isUserInRole("xmlrpc")){
		userRole = "rpc";
	}
	else if(request.getRemoteUser() != null){
		userRole = "eea";
	}else if(user != null) {			
		 userRole = "eeaNotLogged";
	}
	
	request.setAttribute("userRole",userRole);

%>

<h:form id="navigationForm">
	<f:verbatim>
		<div class="portlet" style="text-align: center;">
			<h5>Navigation</h5>	
			<div class="portletBody">
				<div class="portletContent odd">
					<div>
						<ul class="portal-subnav">
							<c:choose> 
							  <c:when test="${userRole== 'admin'}" > 
							  		<c:choose>
								  		<c:when test="${sessionScope.user.preferDashboard}" > 
											<li class="${selectedMenu == 'dashboard'?'selected':''}"><a href="<c:url value="/dash/${sessionScope.user.externalId}/dashboard.jsf"/>" title="Dashboard"><fmt:message key="label.menu.myDashboard"/></a></li>
										</c:when>
										<c:otherwise> 	
											<li class="${selectedMenu == 'dashboard'?'selected':''}"><a href="<c:url value="/rss/${sessionScope.user.externalId}/rssReader.jsf"/>" title="Dashboard"><fmt:message key="label.menu.myDashboard"/></a></li>
										</c:otherwise>
									</c:choose>		
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
									<li><a href="#" onclick="javascript:openWindow(applicationRoot+'/help/index.jsp','onlinehelp');" title="Help"><fmt:message key="label.menu.help"/></a></li>
							  </c:when> 
							  <c:when test="${userRole == 'rpc'}" > 
									<li class="${selectedMenu == 'channels'?'selected':''}"><a href="<c:url value="/xmlrpc/rpcUserChannels.jsf"/>" title="Your rpc channels"><fmt:message key="label.menu.channels"/></a></li>
									<li><a href="#" onclick="javascript:openWindow('<c:url value="/help/help.jsp"/>','onlinehelp');" title="Help"><fmt:message key="label.menu.help"/></a></li>
							  </c:when> 
							</c:choose>  						
						</ul>
					</div>
				</div>
			</div>
		</div>
	</f:verbatim>		
	<f:verbatim><div class="visualClear"> </div></f:verbatim>

	<h:panelGroup rendered="#{(empty sessionScope.user) or not sessionScope.user.loggedIn }">
		<f:verbatim>
			<div class="portlet" style="text-align: center;">
				<div>
					<h5>
					<span><fmt:message key="label.menu.notlogged"/></span>
					</h5>								
					<div class="portletBody">
						<div class="portletContent odd">									
							<ul class="portal-subnav">
																		
								<li><a href=" <%= application.getInitParameter("edu.yale.its.tp.cas.client.filter.loginUrl")+ "?service="  + (application.getInitParameter("edu.yale.its.tp.cas.client.filter.serverName").startsWith("http")? ((application.getInitParameter("edu.yale.its.tp.cas.client.filter.serverName") + request.getContextPath() + "/login/home.jsf")): (( "http://" + application.getInitParameter("edu.yale.its.tp.cas.client.filter.serverName") + request.getContextPath() + "/login/home.jsf")) )%>" title="login">Login</a></li>
							</ul>
						</div>
					</div>
				</div>
			</div>						
		</f:verbatim>	
	</h:panelGroup>
			
	<h:panelGroup rendered="#{(not empty sessionScope.user) and sessionScope.user.loggedIn}" >
		<f:verbatim>
			<div class="portlet" style="text-align: center;">
				<div>
					<h5>
					<span><fmt:message key="label.menu.logged"/></span>
					<br/>
					<span>
						</f:verbatim>
							<h:outputText value="#{sessionScope.user.externalId}"/>
						<f:verbatim>
					</span>
					</h5>
					<div class="portletBody">
						<div class="portletContent odd">									
							<ul class="portal-subnav">
								<li>
									<li><a href="<c:url value="/pages/logout.jsp" />" title="login">Logout</a></li>								
								<li>
							</ul>
						</div>
					</div>
				</div>
			</div>
		</f:verbatim>	
	</h:panelGroup>
	<t:div  rendered="#{(not empty sessionScope.user) and sessionScope.user.loggedIn}" style="text-align:center;">
		<h:graphicImage url="/images/mail-no.gif" rendered="#{ not empty sessionScope['user'] and sessionScope['user'].vacationFlag}" />
	</t:div>	
	<f:verbatim>
		<div class="visualClear"> </div>
		<div>
			<div class="portlet" style="text-align: center;">
				<h5>Reportnet </h5>
				<div class="portletBody">
					<div class="portletContent odd">
						<ul class="portal-subnav">
							<li><a title="Reporting Obligations" href="http://rod.eionet.eu.int/">ROD</a></li>
							<li><a title="Central Data Repository" href="http://cdr.eionet.eu.int/">CDR</a></li>
							<li><a title="Data Dictionary" href="http://dd.eionet.eu.int/">DD</a></li>
							<li><a title="Content Registry" href="http://cr.eionet.eu.int/">CR</a></li>
						</ul>
					</div>
				</div>
			</div>
		</div>
	</f:verbatim>

</h:form>

