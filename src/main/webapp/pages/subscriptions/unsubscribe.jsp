<%@ include file="/pages/common/taglibs.jsp"%>
<%@ page import="com.eurodyn.uns.web.filters.EionetCASFilter" %>
<%
	com.eurodyn.uns.model.User user =  (com.eurodyn.uns.model.User) com.eurodyn.uns.web.jsf.LoginBean.getUser(request);
	String userRole = "";
	String userName = ((user != null) && (user.isLoggedIn())) ? user.getExternalId() : request.getRemoteUser();

	if(request.isUserInRole("admin")){
		userRole = "admin";
	}
	else if(userName != null){
		userRole = "eea";
	}

	request.setAttribute("userRole",userRole);

%>
<c:if test="${userRole == 'admin' || userRole == 'eea'}" >
<t:div id="formInitialization" rendered="#{subscriptionBean.preparedUnsubscribe}" />
<h:form rendered="#{not empty subscriptionBean.subscription}">
	<htm:h1><h:outputText value="&nbsp;" escape="false"/></htm:h1>
	<h:outputText value="Are you sure you want to unsubscribe from the channel \" #{subscriptionBean.subscription.channel.title}\"" />
	<f:verbatim>
		<br />
		<br />
	</f:verbatim>
	<h:commandButton value="Yes" action="#{subscriptionBean.remove}" immediate="false" />
	<h:commandButton value="No" action="subscriptions" immediate="true" />
	<t:saveState value="#{subscriptionBean.subscription}" />
	<f:verbatim>
		<br />
		<br />
	</f:verbatim>
</h:form>

<h:form rendered="#{empty subscriptionBean.subscription}">
	<h:outputText value="Your subscription is alredy deleted" />
	<f:verbatim>
		<br />
		<br />
	</f:verbatim>
</h:form>
</c:if>