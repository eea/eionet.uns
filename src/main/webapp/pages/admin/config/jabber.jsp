<%@ include file="/pages/common/taglibs.jsp"%>

<h:form>
	<htm:h1>
		<h:outputText value="Jabber configuration" />
	</htm:h1>

	<h:panelGrid columns="2">

		<h:outputLabel value="Host:" for="jabber_host" />
		<h:outputText id="jabber_host" value="#{configBean.configMap['jabberserver/host'].tempValue}" />

		<h:outputLabel value="Port:" for="jabber_port" />
		<h:outputText id="jabber_port" value="#{configBean.configMap['jabberserver/port'].tempValue}" />

		<h:outputLabel value="Username:" for="jabber_username" />
		<h:outputText id="jabber_username" value="#{configBean.configMap['jabberserver/username'].tempValue}" />

		<h:outputLabel value="Password:" for="jabber_password" />
		<h:outputText id="jabber_password" value="#{configBean.configMap['jabberserver/password'].tempValue}" />

		<h:outputLabel value="Use SSL:" for="jabber_usessl" />
		<h:outputText id="jabber_usessl" value="#{configBean.configMap['jabberserver/usessl'].tempValue}" />

	</h:panelGrid>
</h:form>
