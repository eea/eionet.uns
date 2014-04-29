<%@ include file="/pages/common/taglibs.jsp"%>

<h:form>
	<htm:h1>
		<h:outputText value="Mail configuration" />
	</htm:h1>

	<htm:fieldset>
		<htm:legend>
			<h:outputText value="SMTP settings" />
		</htm:legend>

		<h:panelGrid columns="2">
			<h:outputLabel value="Host:" for="smtp_host" />
			<h:outputText id="smtp_host" value="#{configBean.configMap['smtpserver/smtp_host'].tempValue}" />

			<h:outputLabel value="Port:" for="smtp_port" />
			<h:outputText id="smtp_port" value="#{configBean.configMap['smtpserver/smtp_port'].tempValue}" />

			<h:outputLabel value="Username:" for="smtp_username" />
			<h:outputText id="smtp_username" value="#{configBean.configMap['smtpserver/smtp_username'].tempValue}" />

			<h:outputLabel value="Password:" for="smtp_password" />
			<h:outputText id="smtp_password" value="#{configBean.configMap['smtpserver/smtp_password'].tempValue}" />

			<h:outputLabel value="Authorization:" for="smtp_useauth" />
			<h:outputText id="smtp_useauth" value="#{configBean.configMap['smtpserver/smtp_useauth'].tempValue}" />
		</h:panelGrid>
	</htm:fieldset>
	<htm:fieldset>
		<htm:legend>
			<h:outputText value="POP3 settings" />
		</htm:legend>

		<h:panelGrid columns="2">
			<h:outputLabel value="Host:" for="pop3_host" />
			<h:outputText id="pop3_host" value="#{configBean.configMap['pop3server/pop3_host'].tempValue}" />

			<h:outputLabel value="Port:" for="pop3_port" />
			<h:outputText id="pop3_port" value="#{configBean.configMap['pop3server/pop3_port'].tempValue}" />

			<h:outputLabel value="Username:" for="pop3_username" />
			<h:outputText id="pop3_username" value="#{configBean.configMap['pop3server/pop3_username'].tempValue}" />

			<h:outputLabel value="Password:" for="pop3_password" />
			<h:outputText id="pop3_password" value="#{configBean.configMap['pop3server/pop3_password'].tempValue}" />

			<h:outputLabel value="Administrator mail:" for="adminmail" />
			<h:outputText id="adminmail" value="#{configBean.configMap['pop3server/adminmail'].tempValue}" />
		</h:panelGrid>
	</htm:fieldset>


	<t:saveState value="#{configBean.configMap}" />


</h:form>
