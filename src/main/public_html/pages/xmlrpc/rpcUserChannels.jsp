<%@ include file="/pages/common/taglibs.jsp"%>

<h:form id="pullChannelsForm007">
	<t:div style="width:97%;" >
		<t:dataTable  rendered="#{ not empty rpcUserChannelsBean.channels}" style="width:100%" styleClass="sortable" rowClasses="zebraeven," var="channel" value="#{rpcUserChannelsBean.channels}" preserveDataModel="false" rows="10" rowId="#{channel.title}" sortColumn="#{rpcUserChannelsBean.st.sort}" sortAscending="#{channelListBean.st.ascending}" preserveSort="true">
			<h:column>
				<f:facet name="header">
					<t:commandSortHeader columnName="secondaryId" value="#{msg['label.channels.identifier']}" title="#{'title'!= channelListBean.st.sort ? msg['table.sortable']:( channelListBean.st.ascending?msg['table.sort.asc.az']:msg['table.sort.desc.za'] )}" rel="noflow" arrow="false" immediate="true">
						<f:facet name="descending">
							<h:graphicImage url="/images/sort_desc.gif" />
						</f:facet>
						<f:facet name="ascending">
							<h:graphicImage url="/images/sort_asc.gif" />
						</f:facet>
					</t:commandSortHeader>
				</f:facet>
				<h:outputText value="#{channel.secondaryId}" />
			</h:column>
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
				<t:commandLink  action="#{rpcChannelBean.edit}" immediate="true">
					<h:outputText value="#{channel.title}" />
					<t:updateActionListener property="#{rpcChannelBean.channel.id}" value="#{channel.id}" />
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
				<h:outputText value="#{channel.creator.fullName}" />
			</h:column>
			<h:column>
				<f:facet name="header">
					<t:commandSortHeader columnName="numberOfSubscriptions" value="#{msg['label.channels.numberOfSubscriptions']}" title="#{'numberOfSubscriptions'!= channelListBean.st.sort ? msg['table.sortable']:( channelListBean.st.ascending?msg['table.sort.asc.az']:msg['table.sort.desc.za'] )}" rel="noflow" arrow="false" immediate="true">
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
					<t:commandSortHeader columnName="status" value="#{msg['label.channels.status']}" title="#{'status'!= channelListBean.st.sort ? msg['table.sortable']:( channelListBean.st.ascending?msg['table.sort.asc.az']:msg['table.sort.desc.za'] )}" rel="noflow" arrow="false" immediate="true">
						<f:facet name="descending">
							<h:graphicImage url="/images/sort_desc.gif" />
						</f:facet>
						<f:facet name="ascending">
							<h:graphicImage url="/images/sort_asc.gif" />
						</f:facet>
					</t:commandSortHeader>
				</f:facet>
				<h:outputText rendered="#{ channel.status == 0}"	value="Disabled"   />
				<h:outputText rendered="#{ channel.status == 1}"	value="Enabled"   />
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
		</t:dataTable>
		<h:outputText rendered="#{empty rpcUserChannelsBean.channels}" value="You don't have rpc channels in the UNS applicaton" />
		<f:verbatim><br/><br/></f:verbatim>
		<t:commandButton action="editRpcChannel" actionListener="#{channelBean.reset}" value="#{msg['label.add.channel']}" immediate="true"/>
	</t:div>
</h:form>
