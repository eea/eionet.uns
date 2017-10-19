<%@ include file="/pages/common/taglibs.jsp"%>
<h:form>
	<htm:h1>
		<h:outputText value="Step 1 of 4" />
	</htm:h1>
	<h:panelGrid columns="2">
		<h:outputLabel for="feedUrl" value="#{msg['label.channel.url']}" />
		<h:inputText id="feedUrl" size="50" required="true" value="#{channelBean.channel.feedUrl}" valueChangeListener="#{channelBean.channelUrlChange}" />
	</h:panelGrid>
	<htm:br/>
	<h:panelGrid columns="2">
	<h:commandButton action="#{channelBean.edit}" value="#{msg['label.next']}" />
	<h:commandButton action="cancel" value="#{msg['label.cancel']}" immediate="true" />
	</h:panelGrid>
</h:form>
