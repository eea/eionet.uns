<%@ include file="/pages/common/taglibs.jsp"%>
<h:form id="channelTemplates22" onsubmit="submitChannelTemplates(this)">
	<h:panelGrid columns="1">
	<htm:h1 rendered="#{channelBean.channel.mode != 'PUSH'}" > <h:outputText  value="Step 3 of 4"  /></htm:h1>
	<htm:h1 rendered="#{channelBean.channel.mode == 'PUSH'}" > <h:outputText  value="Step 2 of 3"  /></htm:h1>
	
	<htm:fieldset>
		<htm:legend>
			<h:outputText value="Notifications template" />
		</htm:legend>
		<h:panelGrid columns="1">
			<h:selectOneListbox id="notificationTemplates" size="10" style="width:330px;" value="#{channelBean.channel.notificationTemplate.id}">
				<f:selectItems value="#{channelBean.notificationTemplatesItems}" />
			</h:selectOneListbox>
		</h:panelGrid>
	</htm:fieldset>
		<%-- 
		<h:selectOneRadio  id="isStylesheetSelected" value="#{channelBean.channel.stylesheetSelected}"  onchange="haveStylesheetChanged(this)" onclick="haveStylesheetChanged(this)">
 				<f:selectItem itemValue="true" itemLabel="#{msg['label.channel.selstylesheet']}" />
 				<f:selectItem  itemValue="false" itemLabel="#{msg['label.channel.selviselements']}" />
		</h:selectOneRadio>
		--%>
	<htm:fieldset>
		<htm:legend>
			<h:outputText value="Channel rendering" />
		</htm:legend>		
		<h:selectOneRadio id="isStylesheetSelected" value="#{channelBean.stylesheetSelected}" onchange="haveStylesheetChanged(this)" onclick="haveStylesheetChanged(this)">
			<f:selectItem itemValue="true" itemLabel="Stylesheet based" />
			<f:selectItem itemValue="false" itemLabel="Based on manually selected metadata" />
		</h:selectOneRadio>

		<f:verbatim>
			<BR />
		</f:verbatim>
		<t:div id="StylesheetDiv" style="display:#{channelBean.stylesheetSelected?'display':'none'}">
			<h:selectOneMenu id="stylesheetId" style="width:330px;" value="#{channelBean.channel.transformation.id}">
				<f:selectItems value="#{channelBean.stylesheetsItems}" />
			</h:selectOneMenu>
		</t:div>
		<t:div id="VisibleElementsDiv" style="display:#{channelBean.stylesheetSelected?'none':'display'}">
			<h:panelGrid columns="4">
				<h:outputLabel for="availableElements" value="Available metadata" />
				<h:outputText value="" />
				<h:outputLabel for="channelVisibleElements" value="Visible metadata" />
				<h:outputText value="" />
				<h:selectManyListbox id="availableElements" size="10" style="width:220px;">
					<f:selectItems value="#{channelBean.availableElementsItems}" />
				</h:selectManyListbox>
				<h:panelGrid columns="1" >
					<t:commandButton type="button" value=">>" onclick="addProps(this)" />
					<t:commandButton type="button"  value="<<"  onclick=" removeProps(this)"/>
				</h:panelGrid>
				<h:selectManyListbox id="channelVisibleElements" size="10" style="width:220px;">
					<f:selectItems value="#{channelBean.visibleElementsItems}" />
				</h:selectManyListbox>
				<h:panelGroup>
					<h:panelGrid columns="1">
						<t:commandButton type="button" styleClass="button" value="/\\" onclick="upVisible(this)" />
						<t:commandButton type="button" styleClass="button" value="\\/" onclick="downVisible(this)" />
					</h:panelGrid>
				</h:panelGroup>
			</h:panelGrid>
		</t:div>
		<t:div  styleClass="commandButtons" style="text-align:center;" >
			<h:commandButton action="#{channelBean.preview}" value="Channel preview" styleClass="button"  />
		</t:div>		
	</htm:fieldset>
	</h:panelGrid>
	<t:div styleClass="commandButtons">
		<h:commandButton action="#{channelBean.edit}" value="#{msg['label.back']}" />
		<h:commandButton action="#{channelBean.prepareChoosableElements}" value="#{msg['label.next']}" />
		<h:commandButton action="#{channelBean.save}" value="#{msg['label.save']}" />
		<h:commandButton action="pushChannels" value="#{msg['label.cancel']}" immediate="true" rendered="#{channelBean.channel.mode == 'PUSH'}" />
		<h:commandButton action="pullChannels" value="#{msg['label.cancel']}" immediate="true" rendered="#{channelBean.channel.mode != 'PUSH'}"  />
	</t:div>

		<t:inputHidden id="visibleElements" value="#{channelBean.visibleElements}" />
</h:form>
