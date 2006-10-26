<%@ include file="/pages/common/taglibs.jsp"%>

<h:form id="channelForm5d77" onsubmit="submitChannel(this)">
	<t:inputHidden value="#{channelBean.channel.id}" />
	<htm:h1 rendered="#{channelBean.channel.mode != 'PUSH'}">
		<h:outputText value="Step 2 of 4" />
	</htm:h1>
	<htm:h1 rendered="#{channelBean.channel.mode == 'PUSH'}">
		<h:outputText value="Step 1 of 3" />
	</htm:h1>

	<htm:fieldset>
		<htm:legend>
			<h:outputText value="Channel details" />
		</htm:legend>
		<h:panelGrid columns="2" border="0" columnClasses="">
			<h:outputLabel for="title" value="#{msg['label.common.title']}" />
			<h:inputText id="title" size="30" required="true" value="#{channelBean.channel.title}" style="width: 39.7em;" />

			<h:outputLabel for="description" value="#{msg['label.common.description']}" />
			<h:inputTextarea id="description" rows="2" cols="50" value="#{channelBean.channel.description}" style="width: 48em;" />
			<h:outputLabel for="language" value="#{msg['label.channels.language']}" />
			<t:selectOneLanguage style="margin:0;padding:0;" value="#{channelBean.channel.language}" />
			<h:outputLabel for="refreshDelay" value="Refresh delay" rendered="#{channelBean.channel.mode == 'PULL'}" />
			<h:panelGrid id="refreshDelay" columns="3" rendered="#{channelBean.channel.mode == 'PULL'}">
				<h:outputText value="Days" style="width:0" />
				<h:outputText value="Hours" style="width:0" />
				<h:outputText value="Minutes" style="width:0" />
				<h:inputText id="days" size="3" required="true" value="#{channelBean.refreshDelay.days}" />
				<h:inputText id="hours" size="3" required="true" value="#{channelBean.refreshDelay.hours}" />
				<h:inputText id="minutes" size="3" required="true" value="#{channelBean.refreshDelay.minutes}" />
			</h:panelGrid>
		</h:panelGrid>
	</htm:fieldset>
	<htm:fieldset>
		<htm:legend>
			<h:outputText value="Subscribe Access Rights" />
		</htm:legend>
		<h:panelGrid columns="3">
			<h:outputLabel for="availableRoles" value="All EEA Roles" />
			<h:outputText value="" style="width:0" />
			<h:outputLabel for="channelRoles" value="Allowed Only to" />
			<h:selectManyListbox id="availableRoles" size="15" style="width:220px;">
				<f:selectItems value="#{channelBean.availableRolesItems}" />
			</h:selectManyListbox>
			<h:panelGrid columns="1">
				<t:commandButton type="button" value=">>" onclick="addRoles(this);" />
				<t:commandButton type="button" value="<<"  onclick=" removeRoles(this)"/>
			</h:panelGrid>
			<h:selectManyListbox id="channelRoles" size="15" style="width:220px;">
				<f:selectItems value="#{channelBean.channelRolesItems}" />
			</h:selectManyListbox>
		</h:panelGrid>
	</htm:fieldset>
	<htm:fieldset>
		<htm:legend>
			<h:outputText value="Allowed delivery types" />
		</htm:legend>
		<h:outputLabel for="Delivery_types" value="Delivery types" />
		<h:selectManyCheckbox id="Delivery_types" value="#{channelBean.channel.deliveryTypes}" required="true">
			<f:converter converterId="ed.DeliveryTypes" />
			<f:selectItems value="#{channelBean.deliveryTypesItems}" />
		</h:selectManyCheckbox>
	</htm:fieldset>

	<htm:br/>
	<h:panelGrid columns="4">
		<h:commandButton action="channelUrl" value="#{msg['label.back']}" rendered="#{channelBean.channel.mode != 'PUSH'}" />
		<h:commandButton action="#{channelBean.prepareTemplates}" value="#{msg['label.next']}" />
		<h:commandButton action="#{channelBean.save}" value="#{msg['label.save']}" />
		<h:commandButton action="pushChannels" value="#{msg['label.cancel']}" immediate="true" rendered="#{channelBean.channel.mode == 'PUSH'}" />
		<h:commandButton action="pullChannels" value="#{msg['label.cancel']}" immediate="true" rendered="#{channelBean.channel.mode != 'PUSH'}" />
	</h:panelGrid>
	<t:inputHidden id="currentChannelRoles" value="#{channelBean.currentChannelRoles}" />
</h:form>

