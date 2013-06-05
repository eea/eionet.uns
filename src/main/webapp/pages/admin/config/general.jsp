<%@ include file="/pages/common/taglibs.jsp"%>
<h:form>
	<htm:h1>
		<h:outputText value="General configuration" />
	</htm:h1>

	<h:panelGrid columns="2">
		<h:outputLabel value="Harvester interval (minutes)" for="harvester_interval" />
		<h:inputText id="harvester_interval" value="#{configBean.configMap['daemons/harvester/interval'].tempValue}" size="4">
			<f:validateLongRange minimum="1" />
		</h:inputText>
		<h:outputLabel value="Harvester threads" for="harvester_threads" />
		<h:inputText id="harvester_threads" value="#{configBean.configMap['daemons/harvester/pull_threads'].tempValue}" size="4">
			<f:validateLongRange minimum="1" maximum="10" />
		</h:inputText>
		<h:outputLabel value="Notificator interval (minutes)" for="notificator_interval" />
		<h:inputText id="notificator_interval" value="#{configBean.configMap['daemons/notificator/interval'].tempValue}" size="4">
			<f:validateLongRange minimum="1" />
		</h:inputText>
		<h:outputLabel value="Feed generation interval (days)" for="feed_interval" />
		<h:inputText id="feed_interval" value="#{configBean.configMap['feed/events_feed_age'].tempValue}" size="4">
			<f:validateLongRange minimum="1" />
		</h:inputText>
	</h:panelGrid>

	<t:saveState value="#{configBean.configMap}" />
	<htm:br/>
	<h:commandButton action="#{configBean.updateGeneral}" value="#{msg['label.save']}" />
</h:form>

