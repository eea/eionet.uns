<%@ include file="/pages/common/taglibs.jsp"%>

<h:form id="notificationTemplate">
		<htm:fieldset>
			<htm:legend><h:outputText value="Plain text result" /></htm:legend>
				<h:panelGrid columns="1" style="background-color:white;" border="1" width="99%" >		
					<h:outputText   escape="false" id="plain_text"  value="#{notificationTemplateBean.resultText}"   style="width: 48em;" />
				</h:panelGrid>	
		</htm:fieldset>
		<htm:fieldset>
			<htm:legend><h:outputText value="Html result" /></htm:legend>
				<h:panelGrid columns="1" style="background-color:white;" border="1" width="99%"  >		
					<t:inputHtml displayValueOnly="true" id="html_text" value="#{notificationTemplateBean.resultHtml}"  style="width: 48em;" />		
				</h:panelGrid>	
		</htm:fieldset>
	<t:div styleClass="commandButtons">	
		<h:commandButton action="#{notificationTemplateBean.afterTest}" value="OK"/>
	</t:div>
</h:form>