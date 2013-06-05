<%@ include file="/pages/common/taglibs.jsp"%>

<h:form >
	<htm:h1><h:outputText value="Test notification template" escape="false"/></htm:h1>
	<h:panelGrid  columns="2" border="0" columnClasses="vertical_align_top">
	
		<htm:fieldset>
			<htm:legend><h:outputText value="Test channel "/></htm:legend>
	
			<h:selectOneListbox id="testChannels" size="15" style="width:320px;" value="#{notificationTemplateBean.id}" required="true" >
				<f:selectItems value="#{notificationTemplateBean.testEventsItems}"/>
			</h:selectOneListbox>
		</htm:fieldset>
	</h:panelGrid>
	<htm:br/>
	<h:panelGrid columns="2">
		<h:commandButton action="#{notificationTemplateBean.test}" value="Run Test"/>
		<h:commandButton action="#{notificationTemplateBean.afterTest}"  value="#{msg['label.cancel']}" immediate="true" />
	</h:panelGrid>
</h:form>
