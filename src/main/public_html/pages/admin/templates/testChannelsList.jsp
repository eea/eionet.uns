<%@ include file="/pages/common/taglibs.jsp"%>

<h:form>
	<htm:h1><h:outputText value="&nbsp;" escape="false"/></htm:h1>
	<htm:fieldset>
		<htm:legend>
			<h:outputText value="Test channel " />
		</htm:legend>
		<h:panelGrid columns="2" border="0" columnClasses="vertical_align_top">
			<h:outputLabel for="feed_url" value="#{msg['label.xsl.test.inputurl']}" />
			<h:inputText id="feed_url" size="50"  required="false" value="#{dashTemplateBean.testChannel.feedUrl}" />

			<h:outputLabel for="feed_url" value="#{msg['label.xsl.test.channels']}" />
			<h:selectOneListbox id="testChannels" size="15" style="width:320px;" value="#{dashTemplateBean.id}">
				<f:selectItems value="#{dashTemplateBean.testChannelsItems}" />
			</h:selectOneListbox>
		</h:panelGrid>
	</htm:fieldset>
	<htm:br/>
	<h:panelGrid columns="2">
		<h:commandButton action="#{dashTemplateBean.testChannel}" value="#{msg['label.xsl.test.run']}" />
		<h:commandButton action="#{dashTemplateBean.afterTest}"  value="#{msg['label.cancel']}" immediate="true" />
	</h:panelGrid>
</h:form>
