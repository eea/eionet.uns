<%@ include file="/pages/common/taglibs.jsp"%>
<t:div id="formInitialization" rendered="#{ not reportBean.preparedForm}" />
<h:form>
	<h:panelGrid columns="2" cellpadding="5" cellspacing="5">
		<h:outputLabel value="Period" for="period" />
		<h:panelGroup id="period">
			<t:inputDate value="#{reportBean.fromDate}" />
			<h:outputText value=" -- " />
			<t:inputDate id="toDate" value="#{reportBean.toDate}" />		
		</h:panelGroup>
		<h:outputLabel value="Channels" for="channels" />
		<h:selectOneMenu id="channels" style="width:220px;" value="#{reportBean.channel.id}">
			<f:selectItem itemLabel="All" itemValue="-1" />
			<f:selectItems value="#{reportBean.channelsItems}" />
		</h:selectOneMenu>
		<h:outputLabel value="Users" for="users" />
		<h:selectOneMenu id="users" style="width:220px;" value="#{reportBean.user.id}">
			<f:selectItem itemLabel="All" itemValue="-1" />
			<f:selectItems value="#{reportBean.usersItems}" />
		</h:selectOneMenu>
	</h:panelGrid>
	<t:div styleClass="commandButtons">
		<h:commandButton action="#{reportBean.generateTrouthputReport}" value="Generate Report" />
	</t:div>
	<t:saveState value="#{reportBean.channels}" />
	<t:saveState value="#{reportBean.users}" />
</h:form>
