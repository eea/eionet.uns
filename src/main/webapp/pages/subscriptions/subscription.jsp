<%@ include file="/pages/common/taglibs.jsp"%>
<t:saveState value="#{subscriptionBean.subscription}" />
<t:saveState value="#{subscriptionBean.filter}" />
<t:saveState value="#{subscriptionBean.editFilterMode}" />
<t:saveState value="#{subscriptionBean.propertyValues}" />
<t:saveState value="#{subscriptionBean.availableProperties}" />
<t:saveState value="#{subscriptionBean.allChoosableStatements}" />
<t:div id="formInitialization" rendered="#{ not subscriptionBean.externalSubscription}"  />
<h:form>
	<htm:h1 rendered="#{empty subscriptionBean.subscription.id}"> <h:outputText  value="Subscribe to the '#{subscriptionBean.subscription.channel.title}' channel "  /></htm:h1>
	<htm:h1 rendered="#{not empty subscriptionBean.subscription.id}"> <h:outputText  value="Edit the '#{subscriptionBean.subscription.channel.title}' channel subscription "  /></htm:h1>


	<h:panelGrid columns="1" style="width:80%;">
	<htm:fieldset>
		<htm:legend>
			<h:outputText value="Your preferences" />
		</htm:legend>
		<h:panelGrid columns="2" border="0" columnClasses="vertical_align_top">
			<h:outputLabel value="#{msg['label.address.email']}" for="email_address" />
			<h:outputText id="email_address" value="#{subscriptionBean.subscriber.deliveryAddresses[applicationScope['EMAIL']].address}" />
<%--			<h:outputLabel for="jabber_address_1"  value="#{msg['label.address.jabber']}"  />--%>
<%--			<h:inputText id="jabber_address_1" rendered="#{empty subscriptionBean.subscriber.deliveryAddresses[applicationScope['JABBER']].address}" value="#{subscriptionBean.subscriber.deliveryAddresses[applicationScope['JABBER']].address}" size="30" >--%>
<%--				<t:validateEmail />--%>
<%--			</h:inputText>--%>
<%--			<h:outputText rendered="#{not empty subscriptionBean.subscriber.deliveryAddresses[applicationScope['JABBER']].address}" value="#{subscriptionBean.subscriber.deliveryAddresses[applicationScope['JABBER']].address}" />--%>
		</h:panelGrid>		
	</htm:fieldset>

	<htm:fieldset>
		<htm:legend>
			<h:outputText value="Subscriptions details" />
		</htm:legend>		
		<h:panelGrid columns="2" border="0">
			<h:outputLabel for="title" value="#{msg['label.common.title']}" />
			<h:outputText id="title" value="#{subscriptionBean.subscription.channel.title}" />
			<h:outputLabel for="description" value="#{msg['label.common.description']}" />
			<h:outputText id="description" value="#{subscriptionBean.subscription.channel.description}" />
			<h:outputLabel for="subs_Delivery_types" value="Notify me through:"  />
			<h:selectManyCheckbox id="subs_Delivery_types" value="#{subscriptionBean.subscription.deliveryTypes}" required="true" >
				<f:converter converterId="ed.DeliveryTypes"/>
				<f:selectItems value="#{subscriptionBean.deliveryTypesItems}" />
			</h:selectManyCheckbox>	
		</h:panelGrid>	
	</htm:fieldset>



	<%--	list of the user filters begin --%>
	<htm:fieldset rendered="#{ not empty subscriptionBean.allChoosableStatements}" >
		<htm:legend>
			<h:outputText value="Filters" />
		</htm:legend>

		<t:dataTable rendered="#{ not empty subscriptionBean.subscription.filters }" var="filter" value="#{subscriptionBean.subscription.filters}" styleClass="sortable" rowClasses="zebraeven," rowCountVar="rowCount" rowIndexVar="index" style="width:99%;"  preserveDataModel="true">
			<h:column>
				<f:facet name="header">
					<h:outputText escape="false" value="&nbsp;" />
				</f:facet>
				<t:commandLink action="#{subscriptionBean.removeFilter}" >
					<h:graphicImage url="/images/delete.gif" alt="Delete filter" title="Delete filter" />
					<t:updateActionListener property="#{subscriptionBean.filter}" value="#{filter}" />
				</t:commandLink>
			</h:column>
			<h:column>
				<f:facet name="header">
					<h:outputText value="Nr." />
				</f:facet>
				<t:commandLink action="#{subscriptionBean.toEditFilterMode}"  value="#{index +1 }">
					<t:updateActionListener property="#{subscriptionBean.filter}" value="#{filter}" />
					<t:updateActionListener property="#{subscriptionBean.filter.editMode}" value="true" />
				</t:commandLink>
			</h:column>
			<h:column>
				<f:facet name="header">
					<h:outputText value="Statements" />
				</f:facet>
				<t:dataTable var="statement" value="#{filter.statements}">
					<h:column>
						<h:outputText value="#{statement.metadataElement.localName}" />
					</h:column>
					<h:column>
						<h:outputText value=" = " />
					</h:column>
					<h:column>
						<h:outputText value="#{statement.value}" />
					</h:column>
				</t:dataTable>
			</h:column>
			<h:column>
				<f:facet name="header">
					<h:outputText escape="false" value="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" />
				</f:facet>
				<h:outputText rendered="#{index + 1 < rowCount}" value="OR" />
			</h:column>
		</t:dataTable>
		<%--	list of the user filters end --%>


		<t:div style="text-align:left; padding-top:2em" rendered="#{ not empty subscriptionBean.allChoosableStatements and not subscriptionBean.editFilterMode}">
			<h:panelGrid columns="1" rendered="#{empty subscriptionBean.subscription.filters }" >
				 <h:outputText value="No filters - all notifications included. " />
				 <h:panelGroup>
					 <h:outputText value="Click " />
					 <h:commandButton action="#{subscriptionBean.toEditFilterMode}" value="Create filter" />
					 <h:outputText value=" to create a filter." />
				</h:panelGroup>
			</h:panelGrid>
			<h:commandButton action="#{subscriptionBean.toEditFilterMode}" value="Create filter"  rendered="#{ not empty subscriptionBean.subscription.filters }"   />
		</t:div>

		<%--	statements and properties begin --%>
		<t:div style="text-align:left; padding-top:2em" rendered="#{ subscriptionBean.editFilterMode}">

			<t:dataTable rendered="#{ not empty subscriptionBean.filter.statements }" var="statement" value="#{subscriptionBean.filter.statements}" styleClass="sortable" rowClasses="zebraeven," rowIndexVar="index" rowCountVar="rowCount" style="width:60%;">
				<h:column>
					<t:commandLink action="#{subscriptionBean.removeStatement}" immediate="false">
						<h:graphicImage url="/images/delete.gif" alt="#{msg['label.channel.delete']}" title="#{msg['label.channel.delete']}" />
						<t:updateActionListener property="#{subscriptionBean.statement}" value="#{statement}" />
					</t:commandLink>
				</h:column>
				<h:column>
					<h:outputText value="#{statement.metadataElement.localName}" />
				</h:column>
				<h:column>
					<h:outputText value=" = " />
				</h:column>
				<h:column>
					<h:outputText value="#{statement.value}" />
				</h:column>
				<h:column>
					<h:outputText rendered="#{index + 1 < rowCount}" value="AND" />
				</h:column>
			</t:dataTable>

			<t:div rendered="#{ not empty subscriptionBean.availableProperties}">
				<h:panelGrid columns="3" style="text-align:center;">
					<h:outputText value="Properties"  />
					<h:outputText value="" />
					<h:outputText value="Values" />
 					<h:selectOneMenu id="property" style="width:220px;" value="#{subscriptionBean.property}" valueChangeListener="#{subscriptionBean.propertyChanged}" immediate="false"	onchange="submit();">
						<f:selectItems value="#{subscriptionBean.propertiesItems}" />
					</h:selectOneMenu>
					<h:outputText value="" />
					<h:selectOneMenu id="stylesheetId" style="width:220px;" value="#{subscriptionBean.value}">
						<f:selectItems value="#{subscriptionBean.propertyValuesItems}" />
					</h:selectOneMenu>					
				</h:panelGrid>
			</t:div>
			<h:panelGrid columns="2">
				<h:commandButton rendered="#{not empty subscriptionBean.filter.statements }" action="#{subscriptionBean.addFilter}" value="Save Filter" />
				<h:commandButton  rendered="#{ not empty subscriptionBean.availableProperties}" action="#{subscriptionBean.addStatement}" value="Add condition"  />				
				<h:outputText  value="" rendered="#{empty subscriptionBean.availableProperties}" />
			</h:panelGrid>
		</t:div>
		<%--	statements and properties end --%>


	</htm:fieldset>

	<htm:br />
	<h:panelGrid columns="3">
		<h:commandButton action="#{subscriptionBean.save}" value="#{msg['label.save']}" />
		<h:commandButton action="avChannels" value="#{msg['label.cancel']}" immediate="true" rendered="#{empty subscriptionBean.subscription.id}" />
		<h:commandButton action="subscriptions" value="#{msg['label.cancel']}" immediate="true" rendered="#{not empty subscriptionBean.subscription.id}" />
	</h:panelGrid>

	</h:panelGrid>
</h:form>
