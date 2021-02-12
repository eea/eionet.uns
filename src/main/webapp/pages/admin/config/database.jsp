<%@ include file="/pages/common/taglibs.jsp"%>
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
<h:form>
	<htm:h1>
		<h:outputText value="Database configuration" />
	</htm:h1>

	<h:panelGrid columns="2">

		<h:outputLabel value="Host:" for="dbserver_host" />
		<h:outputText id="dbserver_host" value="#{configBean.configMap['dbserver/host'].tempValue}" />

		<h:outputLabel value="Port:" for="dbserver_port" />
		<h:outputText id="dbserver_port" value="#{configBean.configMap['dbserver/port'].tempValue}" />

		<h:outputLabel value="Database:" for="dbserver_database" />
		<h:outputText id="dbserver_database" value="#{configBean.configMap['dbserver/database'].tempValue}" />

		<h:outputLabel value="Username:" for="dbserver_username" />
		<h:outputText id="dbserver_username" value="#{configBean.configMap['dbserver/username'].tempValue}" />

		<h:outputLabel value="Password:" for="dbserver_password" />
		<h:outputText id="dbserver_password" value="#{configBean.configMap['dbserver/password'].tempValue}" />

		<h:outputLabel value="Connection timeout:" for="dbserver_connect_timeout" />
		<h:outputText id="dbserver_connect_timeout" value="#{configBean.configMap['dbserver/connect_timeout'].tempValue}" />

	</h:panelGrid>
</h:form>
</c:if>