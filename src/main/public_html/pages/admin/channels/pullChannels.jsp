<%@ include file="/pages/common/taglibs.jsp"%>
<t:div id="formInitialization" rendered="#{ not channelListBean.preparedPullChannels}" />
<h:form>
	<htm:h1><h:outputText value="Pull channels list" /></htm:h1>
	<t:div style="width:97%;" rendered="#{ not empty channelListBean.pullChannels}">
		<t:dataTable columnClasses="width30,,textAlignCenter,textAlignCenter,textAlignCenter,textAlignCenter,textAlignCenter" style="width:97%" styleClass="sortable" rowClasses="zebraeven," var="channel" value="#{channelListBean.pullChannels}" preserveDataModel="true" rowId="#{channel.title}" sortColumn="#{channelListBean.st.sort}" sortAscending="#{channelListBean.st.ascending}" preserveSort="true">
			<h:column>
				<f:facet name="header">
					<t:commandSortHeader columnName="title" value="#{msg['label.common.title']}" title="#{'title'!= channelListBean.st.sort ? msg['table.sortable']:( channelListBean.st.ascending?msg['table.sort.asc.az']:msg['table.sort.desc.za'] )}" rel="noflow" arrow="false" immediate="true">
						<f:facet name="descending">
							<h:graphicImage url="/images/sort_desc.gif" />
						</f:facet>
						<f:facet name="ascending">
							<h:graphicImage url="/images/sort_asc.gif" />
						</f:facet>
					</t:commandSortHeader>
				</f:facet>
				<t:commandLink actionListener="#{channelBean.reset}" action="channelUrl" immediate="true">
					<h:outputText value="#{channel.title}" />
					<t:updateActionListener property="#{channelBean.channel}" value="#{channel}" />
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
					<t:commandSortHeader columnName="creator" value="#{msg['label.table.channel.creator']}" title="#{'creator'!= channelListBean.st.sort ? msg['table.sortable']:( channelListBean.st.ascending?msg['table.sort.asc.az']:msg['table.sort.desc.za'] )}" rel="noflow" arrow="false" immediate="true">
						<f:facet name="descending">
							<h:graphicImage url="/images/sort_desc.gif" />
						</f:facet>
						<f:facet name="ascending">
							<h:graphicImage url="/images/sort_asc.gif" />
						</f:facet>
					</t:commandSortHeader>
				</f:facet>
				<h:outputText value="#{channel.creator.externalId}" />
			</h:column>
			<h:column>
				<f:facet name="header">
					<t:commandSortHeader columnName="lastHarvestDate" value="#{msg['label.channels.lastHarvest']}" title="#{'lastHarvestDate'!= channelListBean.st.sort ? msg['table.sortable']:( channelListBean.st.ascending?msg['table.sort.asc.az']:msg['table.sort.desc.za'] )}" rel="noflow" arrow="false" immediate="true">
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
					<t:commandSortHeader columnName="numberOfSubscriptions" value="Subs" title="Number of sumbscriptions" rel="noflow" arrow="false" immediate="true">
						<f:facet name="descending">
							<h:graphicImage url="/images/sort_desc.gif" />
						</f:facet>
						<f:facet name="ascending">
							<h:graphicImage url="/images/sort_asc.gif" />
						</f:facet>
					</t:commandSortHeader>
				</f:facet>
				<h:outputText value="#{channel.numberOfSubscriptions}" />
			</h:column>
			<h:column>
				<f:facet name="header">
					<t:commandSortHeader columnName="language" value="#{msg['label.channels.language']}" title="#{'language'!= channelListBean.st.sort ? msg['table.sortable']:( channelListBean.st.ascending?msg['table.sort.asc.az']:msg['table.sort.desc.za'] )}" rel="noflow" arrow="false" immediate="true">
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
			<h:column>
				<f:facet name="header">
					<h:outputText value="#{msg['label.common.action']}" title="#{msg['label.table.sort.notSortable']}" />
				</f:facet>
				<t:commandLink action="#{channelBean.remove}" immediate="true" actionListener="#{channelListBean.reset}" onclick="if (!approve('Are you sure you want to remove channel {}',['#{channel.title}'])) return false;">
					<h:graphicImage url="/images/delete.gif" alt="#{msg['label.channel.delete']}" title="#{msg['label.channel.delete']}" />
					<t:updateActionListener property="#{channelBean.channel}" value="#{channel}" />
				</t:commandLink>
				<h:outputText value=" " />
				<t:commandLink action="#{metadataBean.prepareMetadataElements}" immediate="true">
					<h:graphicImage url="/images/properties.gif" alt="Manage channel metadata elements" title="Manage channel metadata elements" />
					<t:updateActionListener property="#{metadataBean.channel}" value="#{channel}" />
				</t:commandLink>
			</h:column>
		</t:dataTable>
		<t:saveState value="#{channelListBean.pullChannels}" />
	</t:div>
	<t:div rendered="#{empty channelListBean.pullChannels}">
		<h:outputText value="There is no pull channels in the system" />
	</t:div>
	<htm:br/>
	<t:commandButton action="#{channelBean.create}" actionListener="#{channelBean.reset}" value="Create" immediate="true" />



</h:form>
