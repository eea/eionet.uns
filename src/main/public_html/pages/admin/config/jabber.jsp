<%@ include file="/pages/common/taglibs.jsp"%>

<h:form>
	<h:panelGrid columns="1" style="width:99%">
		<htm:h1>
			<h:outputText value="Jabber configuration" />
		</htm:h1>
		<h:panelGrid columns="2">
			<h:outputLabel value="Host" for="jabber_host" />
			<h:inputText id="jabber_host" value="#{configBean.configMap['jabberserver/host'].tempValue}" size="30" />
			<h:outputLabel value="Port" for="jabber_port" />
			<h:inputText id="jabber_port" value="#{configBean.configMap['jabberserver/port'].tempValue}" size="4">
				<f:validateLongRange minimum="1000" maximum="65536" />
			</h:inputText>
			<h:outputLabel value="Username" for="jabber_username" />
			<h:inputText id="jabber_username" value="#{configBean.configMap['jabberserver/username'].tempValue}" size="15" />
			<h:outputLabel value="Password" for="jabber_password" />
			<h:inputText id="jabber_password" value="#{configBean.configMap['jabberserver/password'].tempValue}" size="15" />
			<h:outputLabel value="Use SSL" for="jabber_usessl" />
			<h:selectBooleanCheckbox id="jabber_usessl" value="#{configBean.configMap['jabberserver/usessl'].tempValue}" />
		</h:panelGrid>
	</h:panelGrid>
	<t:saveState value="#{configBean.configMap}" />
	<h:commandButton action="#{configBean.updateJabber}" value="#{msg['label.save']}" />
</h:form>
