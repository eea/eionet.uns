<%@ include file="/pages/common/taglibs.jsp"%>
<t:div id="formInitialization" rendered="#{ not reportBean.preparedFailedNotifications}" />
<h:form >
	<htm:h1><h:outputText value="&nbsp;" escape="false"/></htm:h1>
	<t:div rendered="#{not empty reportBean.notificationsRecords }">
		<t:dataTable  style="width:99%" styleClass="sortable" rowClasses="zebraeven," var="record" value="#{reportBean.notificationsRecords}" preserveDataModel="true" sortColumn="#{reportBean.st1.sort}" sortAscending="#{reportBean.st1.ascending}" preserveSort="true">
			<h:column>
				<f:facet name="header">
					<t:commandSortHeader id="sasa1" columnName="subscription.user.externalId" value="User" title="#{'subscription.user.externalId'!= reportBean.st1.sort ? msg['table.sortable']:( reportBean.st1.ascending?msg['table.sort.asc.az']:msg['table.sort.desc.za'] )}" rel="noflow" arrow="false" immediate="true">
						<f:facet name="descending">
							<h:graphicImage url="/images/sort_desc.gif" />
						</f:facet>
						<f:facet name="ascending">
							<h:graphicImage url="/images/sort_asc.gif" />
						</f:facet>
					</t:commandSortHeader>
				</f:facet>				
				<h:outputText value="#{record.subscription.user.externalId}" />
			</h:column>
			<h:column>
				<f:facet name="header">
					<t:commandSortHeader id="sasa2" columnName="subscription.channel.title" value="Channel" title="#{'subscription.channel.title'!= reportBean.st1.sort ? msg['table.sortable']:( reportBean.st1.ascending?msg['table.sort.asc.az']:msg['table.sort.desc.za'] )}" rel="noflow" arrow="false" immediate="true">
						<f:facet name="descending">
							<h:graphicImage url="/images/sort_desc.gif" />
						</f:facet>
						<f:facet name="ascending">
							<h:graphicImage url="/images/sort_asc.gif" />
						</f:facet>
					</t:commandSortHeader>
				</f:facet>
				<h:outputText value="#{record.subscription.channel.title}" />
			</h:column>
			<h:column>
				<f:facet name="header">
					<t:commandSortHeader columnName="deliveryType" value="Delivery type" title="#{'deliveryType'!= reportBean.st1.sort ? msg['table.sortable']:( reportBean.st1.ascending?msg['table.sort.asc.az']:msg['table.sort.desc.za'] )}" rel="noflow" arrow="false" immediate="true">
						<f:facet name="descending">
							<h:graphicImage url="/images/sort_desc.gif" />
						</f:facet>
						<f:facet name="ascending">
							<h:graphicImage url="/images/sort_asc.gif" />
						</f:facet>
					</t:commandSortHeader>					
				</f:facet>
				<h:outputText value="#{record.deliveryType}" />				
			</h:column>
			<h:column>
				<f:facet name="header">
					<t:commandSortHeader columnName="count" value="Failed Nr" title="#{'count'!= reportBean.st1.sort ? msg['table.sortable']:( reportBean.st1.ascending?msg['table.sort.asc.az']:msg['table.sort.desc.za'] )}" rel="noflow" arrow="false" immediate="true">
						<f:facet name="descending">
							<h:graphicImage url="/images/sort_desc.gif" />
						</f:facet>
						<f:facet name="ascending">
							<h:graphicImage url="/images/sort_asc.gif" />
						</f:facet>
					</t:commandSortHeader>					
				</f:facet>
				<h:outputText value="#{record.count}" />
			</h:column>
			<h:column>
				<f:facet name="header">
					<h:outputText value="Delivery address" title="Delivery address" />
				</f:facet>
				<h:outputText value="#{record.address}" />
			</h:column>
			<h:column>
				<f:facet name="header">
					<h:outputText value="#{msg['label.common.action']}" title="#{msg['label.table.sort.notSortable']}" />
				</f:facet>
				<t:commandLink action="#{reportBean.unsubscribeUser}" actionListener="#{subscriptionsBean.reset}" immediate="true"    onclick="if (!approve('Are you sure you want to unsubscribe user {} from the {} channel',['#{record.subscription.user.externalId}','#{record.subscription.channel.title}'])) return false;">
					<h:graphicImage url="/images/delete.gif" alt="Unsubscribe user from channel" title="Unsubscribe user from channel" />
					<t:updateActionListener property="#{reportBean.subscription}" value="#{record.subscription}"  />
				</t:commandLink>
			</h:column>			
		</t:dataTable>
		<t:saveState value="#{reportBean.notificationsRecords}" />
	</t:div>
	<t:div rendered="#{empty reportBean.notificationsRecords}">
		<h:outputText value="There is no failed notifications in the system" />
	</t:div>

</h:form>
