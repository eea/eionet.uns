<%@ include file="/pages/common/taglibs.jsp"%>

<h:form >
	<h:panelGrid  columns="2" border="0" columnClasses="vertical_align_top">
	
		<htm:fieldset>
			<htm:legend><h:outputText value="Test channel "/></htm:legend>
	
			<h:selectOneListbox id="testChannels" size="10" style="width:220px;" value="#{notificationTemplateBean.id}" required="true" >
				<f:selectItems value="#{notificationTemplateBean.testEventsItems}"/>
			</h:selectOneListbox>
		</htm:fieldset>
	</h:panelGrid>
	<t:div styleClass="commandButtons">	
		<h:commandButton action="#{notificationTemplateBean.test}" value="Run Test"/>
		<h:commandButton action="#{notificationTemplateBean.afterTest}"  value="#{msg['label.cancel']}" immediate="true" />
	</t:div>
</h:form>
