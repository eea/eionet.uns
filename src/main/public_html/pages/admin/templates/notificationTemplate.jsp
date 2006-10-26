<%@ include file="/pages/common/taglibs.jsp"%>

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
		<t:inputHtml styleClass="unsHtmlEditor" id="html_text" value="#{notificationTemplateBean.notificationTemplate.htmlText}" style="width: 49em;" />
	</h:panelGrid>


	<t:div style="text-align:center">
		<h:commandButton action="#{notificationTemplateBean.save}" value="#{msg['label.save']}" />
		<h:commandButton action="#{notificationTemplateBean.prepareTest}" value="Test" actionListener="#{notificationTemplateBean.changeAfterTest} " />
		<h:commandButton action="notificationTemplates" value="#{msg['label.cancel']}" immediate="true" />
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
					<td>$UNSUSCRIBE_LINK</td>
					<td>Link that user may use to unsubscribe from channel</td>
				</tr>
			</tbody>
		</table>
		</fieldset>
	</f:verbatim>


</h:form>
