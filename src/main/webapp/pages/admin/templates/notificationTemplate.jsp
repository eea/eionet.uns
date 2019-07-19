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
<script type="text/javascript"></script>
</f:verbatim>
<h:form id="notificationTemplate">
	<htm:h1><h:outputText value="&nbsp;" escape="false"/></htm:h1>
	<h:panelGrid columns="2">
		<h:outputLabel for="name" value="#{msg['label.template.notification.name']}" />
		<h:inputText id="name" size="60" required="true" value="#{notificationTemplateBean.notificationTemplate.name}" />

		<h:outputLabel for="subject" value="#{msg['label.template.notification.notifSubject']}" />
		<h:inputText id="subject" size="60" required="true" value="#{notificationTemplateBean.notificationTemplate.subject}" />


		<h:outputLabel for="plain_text" value="#{msg['label.template.notification.plainText']}" />
		<h:inputTextarea id="plain_text" rows="10" cols="50" value="#{notificationTemplateBean.notificationTemplate.plainText}" required="true" style="width: 48em;" />
		<h:outputLabel for="html_text" value="#{msg['label.template.notification.html']}" />
		<h:inputTextarea id="html_text" rows="15" cols="50" value="#{notificationTemplateBean.notificationTemplate.htmlText}" style="width: 48em;" />
	</h:panelGrid>

	<htm:br/>
	<t:div style="text-align:center;"  >	
			<h:commandButton action="#{notificationTemplateBean.save}" value="#{msg['label.save']}"  />			
			<h:commandButton action="#{notificationTemplateBean.prepareTest}" value="Test" actionListener="#{notificationTemplateBean.changeAfterTest} " style="margin-left:10px"  />
			<h:commandButton action="notificationTemplates" value="#{msg['label.cancel']}" immediate="true" style="margin-left:10px" />
	</t:div>

	<f:verbatim>
		<br />
		<fieldset><legend>Supported placeholders</legend>
		<table>
			<tbody>
				<tr>
					<td>$USER</td>
					<td>Full name of the user subscribed to receive notifications</td>
				</tr>
				<tr>

					<td>$EVENT</td>
					<td>Harvested Event (with all its data)</td>
				</tr>
				<tr>
					<td>$EVENT.TITLE</td>
					<td>Title of the received event</td>
				</tr>

				<tr>
					<td>$EVENT.DATE</td>
					<td>Datetime when the Unified notification service received event</td>
				</tr>
				<tr>
					<td>$EVENT.CHANNEL</td>
					<td>Name of the channels used for harvesting event</td>

				</tr>
				<tr>
					<td>$UNSUBSCRIBE_LINK</td>
					<td>Link that user may use to unsubscribe from channel</td>
				</tr>
			</tbody>
		</table>
		</fieldset>
	</f:verbatim>
</h:form>
</c:if>