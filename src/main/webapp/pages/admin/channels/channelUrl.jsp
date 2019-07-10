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
<h:form>
	<htm:h1>
		<h:outputText value="Step 1 of 4" />
	</htm:h1>
	<h:panelGrid columns="2">
		<h:outputLabel for="feedUrl" value="#{msg['label.channel.url']}" />
		<h:inputText id="feedUrl" size="50" required="true" value="#{channelBean.channel.feedUrl}" valueChangeListener="#{channelBean.channelUrlChange}" />
	</h:panelGrid>
	<htm:br/>
	<h:panelGrid columns="2">
	<h:commandButton action="#{channelBean.edit}" value="#{msg['label.next']}" />
	<h:commandButton action="cancel" value="#{msg['label.cancel']}" immediate="true" />
	</h:panelGrid>
</h:form>
</c:if>