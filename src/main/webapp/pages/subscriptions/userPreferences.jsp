<%@ include file="/pages/common/taglibs.jsp"%>
<t:saveState value="#{subscriptionBean.subscriber}" />
<h:form >
	<htm:h1><h:outputText value="My preferences" /></htm:h1>
	<h:panelGrid columns="1" style="width:80%;">
	<htm:fieldset>
		<htm:legend>
			<h:outputText value="Notifications" />
		</htm:legend>

		<h:panelGrid columns="2">
			<h:outputLabel value="#{msg['label.address.email']}" for="email_address" />
			<h:outputText id="email_address" value="#{subscriptionBean.subscriber.deliveryAddresses[applicationScope['EMAIL']].address}" />
			<h:outputLabel value="Send e-mail as " for="preferHtml" />

			<h:selectOneRadio id="preferHtml" value="#{subscriptionBean.subscriber.preferHtml}">
				<f:selectItem itemValue="false" itemLabel="Plain text" />
				<f:selectItem itemValue="true" itemLabel="HTML" />
			</h:selectOneRadio>

			<h:outputLabel value="#{msg['label.address.jabber']}" for="jabber_address_1" />
			<h:inputText id="jabber_address_1" value="#{subscriptionBean.subscriber.deliveryAddresses[applicationScope['JABBER']].address}" size="30" >
				<t:validateEmail  />
			</h:inputText>
		</h:panelGrid>
		<h:panelGrid columns="3"  cellpadding="1" cellspacing="1"  columnClasses=".verticalAlignMIddle, verticalAlignTop, verticalAlignTop"  >
			<h:outputText  value="Dashboard layout" />
			<h:selectOneRadio onclick="document.forms[getFormName(this)].submit();" style="margin:0px;padding:0px;" id="preferDashboard" layout="pageDirection" value="#{subscriptionBean.subscriber.preferDashboard}">
				<f:selectItem itemValue="true" itemLabel="Portlets based" />
				<f:selectItem itemValue="false" itemLabel="Rss Reader based" />
			</h:selectOneRadio>
			<h:panelGrid columns="2" >				
				<h:outputLabel value="#{msg['label.profile.numcols']}" for="number_of_columns_2" />
				<h:selectOneMenu id="number_of_columns_2" value="#{subscriptionBean.subscriber.numberOfColumns}" disabled="#{ not subscriptionBean.subscriber.preferDashboard}">
					<f:selectItem itemValue="1" itemLabel="One" />
					<f:selectItem itemValue="2" itemLabel="Two" />
					<f:selectItem itemValue="3" itemLabel="Three" />
				</h:selectOneMenu>
			</h:panelGrid>
		</h:panelGrid>
		<h:panelGrid columns="3">
			<h:outputLabel value="Dashboard refresh delay"  for="refreshDelay" />
			<h:inputText id="refreshDelay" value="#{subscriptionBean.subscriber.pageRefreshDelay}"  size="4" >
				<f:validateLongRange minimum="10" />
			</h:inputText>
			<h:outputText  value=" in seconds "/>
		</h:panelGrid>
		<htm:br/>
		<h:commandButton action="#{subscriptionBean.saveUserPreferences}" value="#{msg['label.save']}" />

	</htm:fieldset>
	</h:panelGrid>
</h:form>



<h:form >
	<h:panelGrid columns="1" style="width:80%;">
	<htm:fieldset>
		<htm:legend>
			<h:outputText value="Vacation flag" />
		</htm:legend>

		<h:outputText rendered="#{ sessionScope['user'].vacationFlag}" value="Status:ENABLED " />
		<htm:br rendered="#{ sessionScope['user'].vacationFlag}" />
		<h:outputText rendered="#{ not sessionScope['user'].vacationFlag}" value="Status:DISABLED" />
		<h:panelGrid columns="1" rendered="#{ not sessionScope['user'].vacationFlag}">
			<h:panelGroup>
				<h:outputText value="Automatically disable on " />
				<h:inputText id="Vacation-Date" value="#{sessionScope['user'].vacationExpiration}" size="10">
					<f:convertDateTime pattern="yyyy-MM-dd" />
				</h:inputText>
				<h:outputText value=" (yyyy-MM-dd)" />
			</h:panelGroup>
			<h:outputText value="(Leave empty if you want to disable vacation flag manually)" />
		</h:panelGrid>
		<h:panelGrid columns="1" rendered="#{ sessionScope['user'].vacationFlag and not empty sessionScope['user'].vacationExpiration }">
			<h:panelGroup>
				<h:outputText value="Automatically disabled on " />
				<h:outputText value="#{sessionScope['user'].vacationExpiration}">
					<f:convertDateTime pattern="yyyy-MM-dd" />
				</h:outputText>
			</h:panelGroup>
		</h:panelGrid>
		<htm:br/>
		<h:commandButton action="#{subscriptionBean.saveVacationFlag}" value="Enable vacation flag" rendered="#{ not sessionScope['user'].vacationFlag}">
			<t:updateActionListener property="#{sessionScope['user'].vacationFlag}" value="true" />
		</h:commandButton>
		<h:commandButton action="#{subscriptionBean.saveVacationFlag}" value="Disable vacation flag" rendered="#{ sessionScope['user'].vacationFlag}">
			<t:updateActionListener property="#{sessionScope['user'].vacationFlag}" value="false" />
		</h:commandButton>

	</htm:fieldset>
	</h:panelGrid>
</h:form>