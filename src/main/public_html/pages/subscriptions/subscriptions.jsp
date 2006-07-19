<%@ include file="/pages/common/taglibs.jsp"%>
<t:div id="formInitialization" rendered="#{ not subscriptionsBean.preparedSubscriptions}" />
<h:form>
	<htm:h1>
		<h:outputText value="Existing subscriptions list" />
	</htm:h1>

	<t:div style="width:97%;" rendered="#{ not empty subscriptionsBean.subscriptions}">
		<t:dataTable columnClasses="width30,,textAlignCenter,textAlignCenter,textAlignCenter" style="width:90%" styleClass="sortable" rowClasses="zebraeven," var="subscription" value="#{subscriptionsBean.subscriptions}" preserveDataModel="true" sortColumn="#{subscriptionsBean.st.sort}" sortAscending="#{subscriptionsBean.st.ascending}" preserveSort="true">
			<h:column>
				<f:facet name="header">
					<t:commandSortHeader columnName="channel.title" value="#{msg['label.common.title']}" title="#{'title'!= subscriptionsBean.st.sort ? msg['table.sortable']:( subscriptionsBean.st.ascending?msg['table.sort.asc.az']:msg['table.sort.desc.za'] )}" rel="noflow" arrow="false" immediate="true">
						<f:facet name="descending">
							<h:graphicImage url="/images/sort_desc.gif" />
						</f:facet>
						<f:facet name="ascending">
							<h:graphicImage url="/images/sort_asc.gif" />
						</f:facet>
					</t:commandSortHeader>
				</f:facet>
				<t:commandLink action="#{subscriptionBean.edit}" immediate="true">
					<h:outputText value="#{subscription.channel.title}" />
					<t:updateActionListener property="#{subscriptionBean.subscription}" value="#{subscription}" />
				</t:commandLink>
			</h:column>
			<h:column>
				<f:facet name="header">
					<h:outputText value="#{msg['label.common.description']}" title="#{msg['label.table.sort.notSortable']}" />
				</f:facet>
				<h:outputText value="#{subscription.channel.description}" />
			</h:column>
			<h:column>
				<f:facet name="header">
					<t:commandSortHeader columnName="creationDate" value="Subscription date" title="#{'creationDate'!= subscriptionsBean.st.sort ? msg['table.sortable']:( subscriptionsBean.st.ascending?msg['table.sort.asc.az']:msg['table.sort.desc.za'] )}" rel="noflow" arrow="false" immediate="true">
						<f:facet name="descending">
							<h:graphicImage url="/images/sort_desc.gif" />
						</f:facet>
						<f:facet name="ascending">
							<h:graphicImage url="/images/sort_asc.gif" />
						</f:facet>
					</t:commandSortHeader>
				</f:facet>
				<h:outputText value="#{subscription.creationDate}">
					<f:converter converterId="ed.DateConverter" />
				</h:outputText>
			</h:column>
			<h:column>
				<f:facet name="header">
					<t:commandSortHeader columnName="channel.lastHarvestDate" value="#{msg['label.channels.lastHarvest']}" title="#{'lastHarvestDate'!= subscriptionsBean.st.sort ? msg['table.sortable']:( subscriptionsBean.st.ascending?msg['table.sort.asc.az']:msg['table.sort.desc.za'] )}" rel="noflow" arrow="false" immediate="true">
						<f:facet name="descending">
							<h:graphicImage url="/images/sort_desc.gif" />
						</f:facet>
						<f:facet name="ascending">
							<h:graphicImage url="/images/sort_asc.gif" />
						</f:facet>
					</t:commandSortHeader>
				</f:facet>
				<h:outputText rendered="#{subscription.channel.lastHarvestDate.year > 100}" value="#{subscription.channel.lastHarvestDate}">
					<f:converter converterId="ed.DateConverter" />
				</h:outputText>
				<h:outputText rendered="#{subscription.channel.lastHarvestDate.year < 100}" value="Not harvested" />
			</h:column>
			<h:column>
				<f:facet name="header">
					<h:outputText value="#{msg['label.common.action']}" title="#{msg['label.table.sort.notSortable']}" />
				</f:facet>
				<t:commandLink action="#{subscriptionBean.remove}" actionListener="#{subscriptionsBean.reset}" immediate="true" onclick="if (!approve('Are you sure you want to unsubscribe from the {} channel',['#{subscription.channel.title}'])) return false;">
					<h:graphicImage url="/images/delete.gif" alt="Unsubscribe " title="Unsubscribe" />
					<t:updateActionListener property="#{subscriptionBean.subscription}" value="#{subscription}" />
				</t:commandLink>
			</h:column>
		</t:dataTable>
		<t:saveState value="#{subscriptionsBean.subscriptions}" />
	</t:div>
	<t:div rendered="#{empty subscriptionsBean.subscriptions}">
		<h:panelGrid columns="1">
			<h:outputText value="You have no subscriptions yet. " />
			<h:panelGroup>
				<h:outputText value="Click " />
				<h:commandButton value="Subscribe" action="avChannels" />
				<h:outputText value=" to add subscriptions." />
			</h:panelGroup>
		</h:panelGrid>
	</t:div>

</h:form>
