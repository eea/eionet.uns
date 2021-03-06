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
	<htm:h1><h:outputText value="Test stylesheet" escape="false"/></htm:h1>
	<htm:fieldset>
		<htm:legend>
			<h:outputText value="Test channel " />
		</htm:legend>
		<h:panelGrid columns="2" border="0" columnClasses="vertical_align_top">
			<h:outputLabel for="feed_url" value="#{msg['label.xsl.test.inputurl']}" />
			<h:inputText id="feed_url" size="50"  required="false" value="#{dashTemplateBean.testChannel.feedUrl}" />

			<h:outputLabel for="feed_url" value="#{msg['label.xsl.test.channels']}" />
			<h:selectOneListbox id="testChannels" size="15" style="width:320px;" value="#{dashTemplateBean.id}">
				<f:selectItems value="#{dashTemplateBean.testChannelsItems}" />
			</h:selectOneListbox>
		</h:panelGrid>
	</htm:fieldset>
	<htm:br/>
	<h:panelGrid columns="2">
		<h:commandButton action="#{dashTemplateBean.testChannel}" value="#{msg['label.xsl.test.run']}" />
		<h:commandButton action="#{dashTemplateBean.afterTest}"  value="#{msg['label.cancel']}" immediate="true" />
	</h:panelGrid>
</h:form>
</c:if>