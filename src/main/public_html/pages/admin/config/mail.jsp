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
			<h:outputLabel value="Host" for="smtp_host" />
			<h:inputText id="smtp_host" value="#{configBean.configMap['smtpserver/smtp_host'].tempValue}" size="30" />
			<h:outputLabel value="Port" for="smtp_port" />
			<h:inputText id="smtp_port" value="#{configBean.configMap['smtpserver/smtp_port'].tempValue}" size="4">
				<f:validateLongRange minimum="1" maximum="65536" />
			</h:inputText>
			<h:outputLabel value="Username" for="smtp_username" />
			<h:inputText id="smtp_username" value="#{configBean.configMap['smtpserver/smtp_username'].tempValue}" size="15" />
			<h:outputLabel value="Password" for="smtp_password" />
			<h:inputText id="smtp_password" value="#{configBean.configMap['smtpserver/smtp_password'].tempValue}" size="15" />
			<h:outputLabel value="Authorization" for="smtp_useauth" />
			<h:selectBooleanCheckbox id="smtp_useauth" value="#{configBean.configMap['smtpserver/smtp_useauth'].tempValue}" />
		</h:panelGrid>
		<h:commandButton action="#{configBean.updateSmtp}" value="#{msg['label.save']}" />
	</htm:fieldset>
	<htm:fieldset>
		<htm:legend>
			<h:outputText value="POP3 settings" />
		</htm:legend>

		<h:panelGrid columns="2">
			<h:outputLabel value="Host" for="pop3_host" />
			<h:inputText id="pop3_host" value="#{configBean.configMap['pop3server/pop3_host'].tempValue}" size="30" />
			<h:outputLabel value="Port" for="pop3_port" />
			<h:inputText id="pop3_port" value="#{configBean.configMap['pop3server/pop3_port'].tempValue}" size="4">
				<f:validateLongRange minimum="1" maximum="65536" />
			</h:inputText>
			<h:outputLabel value="Username" for="pop3_username" />
			<h:inputText id="pop3_username" value="#{configBean.configMap['pop3server/pop3_username'].tempValue}" size="15" />
			<h:outputLabel value="Password" for="pop3_password" />
			<h:inputText id="pop3_password" value="#{configBean.configMap['pop3server/pop3_password'].tempValue}" size="15" />
			<h:outputLabel value="Administrator mail" for="adminmail" />
			<h:inputText id="adminmail" value="#{configBean.configMap['pop3server/adminmail'].tempValue}" size="15" />
		</h:panelGrid>
		<h:commandButton action="#{configBean.updatePop3}" value="#{msg['label.save']}" />
	</htm:fieldset>


	<t:saveState value="#{configBean.configMap}" />


</h:form>
