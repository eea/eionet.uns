<%@ include file="/pages/common/taglibs.jsp"%>
<h:form>
	<h:panelGrid columns="1">
		<htm:h1> <h:outputText value="Step 1 of 4"  /></htm:h1>
		<h:panelGroup>
			<h:outputLabel for="feedUrl" value="#{msg['label.channel.url']}" />
			<h:inputText id="feedUrl" size="50" required="true" value="#{channelBean.channel.feedUrl}" valueChangeListener="#{channelBean.channelUrlChange}" />
			<t:div styleClass="commandButtons">
				<h:commandButton action="#{channelBean.edit}" value="#{msg['label.next']}" />
				<h:commandButton action="cancel" value="#{msg['label.cancel']}" immediate="true" />
			</t:div>
		</h:panelGroup>
	</h:panelGrid>
</h:form>
