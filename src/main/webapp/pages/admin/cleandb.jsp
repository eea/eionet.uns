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
    <htm:h1>
		<h:outputText value="Clean database" />
	</htm:h1>
	<t:div styleClass="caution-msg">
		<t:htmlTag value="strong">
			<h:outputText value="Warning ..."></h:outputText>		
		</t:htmlTag>
		<t:htmlTag value="p">
			<h:outputText value='Pressing "Delete old events" button will erase all events older than 60 days. Also deletes related notifications and deliveries.'/>
		</t:htmlTag>
	</t:div>
	<t:div style="vertical-align: bottom">
		<h:form>
			<h:commandButton onclick="eventUpdaterMessage(this)" action="#{cleandbBean.clean}" value="Delete old events"/>
		</h:form>
	</t:div>
</c:if>





	

