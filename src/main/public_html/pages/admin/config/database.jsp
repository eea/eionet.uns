<%@ include file="/pages/common/taglibs.jsp"%>

<h:form>
	<htm:h1>
		<h:outputText value="Database configuration" />
	</htm:h1>
	<h:panelGrid columns="1" style="width:99%">
		<h:panelGrid columns="2">
			<h:outputLabel value="Host" for="dbserver_host" />
			<h:inputText id="dbserver_host" value="#{configBean.configMap['dbserver/host'].tempValue}" size="30" />
			<h:outputLabel value="Port" for="dbserver_port" />
			<h:inputText id="dbserver_port" value="#{configBean.configMap['dbserver/port'].tempValue}" size="4">
				<f:validateLongRange minimum="1000" maximum="65536" />
			</h:inputText>
			<h:outputLabel value="Database" for="dbserver_database" />
			<h:inputText id="dbserver_database" value="#{configBean.configMap['dbserver/database'].tempValue}" size="15" />
			<h:outputLabel value="Username" for="dbserver_username" />
			<h:inputText id="dbserver_username" value="#{configBean.configMap['dbserver/username'].tempValue}" size="15" />
			<h:outputLabel value="Password" for="dbserver_password" />
			<h:inputText id="dbserver_password" value="#{configBean.configMap['dbserver/password'].tempValue}" size="15" />
			<h:outputLabel value="Connection timeout" for="dbserver_connect_timeout" />
			<h:inputText id="dbserver_connect_timeout" value="#{configBean.configMap['dbserver/connect_timeout'].tempValue}" size="4">
				<f:validateLongRange minimum="1" maximum="100" />
			</h:inputText>

		</h:panelGrid>
	</h:panelGrid>
	<t:saveState value="#{configBean.configMap}" />
	<h:commandButton action="#{configBean.updateDatabase}" value="#{msg['label.save']}" />
</h:form>
