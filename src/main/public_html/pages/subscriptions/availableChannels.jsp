<%@ include file="/pages/common/taglibs.jsp"%>
<t:div id="formInitialization" rendered="#{ not subscriptionsBean.preparedChannels}" />
<h:form>
	<htm:h1>
		<h:outputText value="Subscribe to the channels" />
	</htm:h1>

	<t:div style="width:97%;" rendered="#{ not empty subscriptionsBean.channels}">
		<t:dataTable columnClasses="width30,,textAlignCenter,textAlignCenter" style="width:99%" styleClass="sortable" rowClasses="zebraeven," var="channel" value="#{subscriptionsBean.channels}" preserveDataModel="true" rowId="#{channel.title}" sortColumn="#{subscriptionsBean.st.sort}" sortAscending="#{subscriptionsBean.st.ascending}" preserveSort="true">
			<h:column>
				<f:facet name="header">
					<t:commandSortHeader columnName="title" value="#{msg['label.common.title']}" title="#{'title'!= subscriptionsBean.st.sort ? msg['table.sortable']:( subscriptionsBean.st.ascending?msg['table.sort.asc.az']:msg['table.sort.desc.za'] )}" rel="noflow" arrow="false" immediate="true">
						<f:facet name="descending">
							<h:graphicImage url="/images/sort_desc.gif" />
						</f:facet>
						<f:facet name="ascending">
							<h:graphicImage url="/images/sort_asc.gif" />
						</f:facet>
					</t:commandSortHeader>
				</f:facet>
				<t:commandLink styleClass="subscribe" action="#{subscriptionBean.edit}">
					<h:outputText value="#{channel.title}" />
					<t:updateActionListener property="#{subscriptionBean.subscription.channel}" value="#{channel}" />
				</t:commandLink>
			</h:column>
			<h:column>
				<f:facet name="header">
					<h:outputText value="#{msg['label.common.description']}" title="#{msg['label.table.sort.notSortable']}" />
				</f:facet>
				<h:outputText value="#{channel.description}" />
			</h:column>
			<h:column>
				<f:facet name="header">
					<t:commandSortHeader columnName="lastHarvestDate" value="#{msg['label.channels.lastHarvest']}" title="#{'lastHarvestDate'!= subscriptionsBean.st.sort ? msg['table.sortable']:( subscriptionsBean.st.ascending?msg['table.sort.asc.az']:msg['table.sort.desc.za'] )}" rel="noflow" arrow="false" immediate="true">
						<f:facet name="descending">
							<h:graphicImage url="/images/sort_desc.gif" />
						</f:facet>
						<f:facet name="ascending">
							<h:graphicImage url="/images/sort_asc.gif" />
						</f:facet>
					</t:commandSortHeader>
				</f:facet>
				<h:outputText rendered="#{channel.lastHarvestDate.year > 100}" value="#{channel.lastHarvestDate}">
					<f:converter converterId="ed.DateConverter" />
				</h:outputText>
				<h:outputText rendered="#{channel.lastHarvestDate.year < 100}" value="Not harvested" />
			</h:column>
			<h:column>
				<f:facet name="header">
					<t:commandSortHeader columnName="language" value="#{msg['label.channels.language']}" title="#{'language'!= subscriptionsBean.st.sort ? msg['table.sortable']:( subscriptionsBean.st.ascending?msg['table.sort.asc.az']:msg['table.sort.desc.za'] )}" rel="noflow" arrow="false" immediate="true">
						<f:facet name="descending">
							<h:graphicImage url="/images/sort_desc.gif" />
						</f:facet>
						<f:facet name="ascending">
							<h:graphicImage url="/images/sort_asc.gif" />
						</f:facet>
					</t:commandSortHeader>
				</f:facet>
				<t:selectOneLanguage value="#{channel.language}" displayValueOnly="true" />
			</h:column>
		</t:dataTable>
		<t:saveState value="#{subscriptionsBean.channels}" />
	</t:div>
	<t:div rendered="#{empty subscriptionsBean.channels}">
		<h:outputText value="No avaiable channels for subscription" />
	</t:div>

</h:form>
