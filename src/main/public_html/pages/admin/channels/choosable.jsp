<%@ include file="/pages/common/taglibs.jsp"%>

<h:form>
	<htm:h1 rendered="#{channelBean.channel.mode != 'PUSH'}">
		<h:outputText value="Step 4 of 4" />
	</htm:h1>
	<htm:h1 rendered="#{channelBean.channel.mode == 'PUSH'}">
		<h:outputText value="Step 3 of 3" />
	</htm:h1>

	<t:div style="width:97%;">
		<t:dataTable rendered="#{ not empty channelBean.channelMetadataElements}" style="width:100%" styleClass="sortable" rowClasses="zebraeven," var="cme" value="#{channelBean.channelMetadataElements}" preserveDataModel="false" rows="10" rowId="#{cme.metadataElement.id}" sortColumn="#{channelBean.st.sort}" sortAscending="#{channelBean.st.ascending}">
			<h:column>
				<f:facet name="header">
					<h:outputText value="#{msg['label.channel.choosable']}" title="#{msg['label.table.sort.notSortable']}" />
				</f:facet>
				<h:selectBooleanCheckbox value="#{cme.filtered}" />
			</h:column>
			<h:column>
				<f:facet name="header">
					<t:commandSortHeader action="#{channelBean.prepareChoosableElements}" columnName="name" value="#{msg['label.channel.metadata.element.local_name']}" title="#{'name'!= channelBean.st.sort ? msg['table.sortable']:( channelBean.st.ascending?msg['table.sort.asc.az']:msg['table.sort.desc.za'] )}" rel="noflow" arrow="false" immediate="false">
						<f:facet name="descending">
							<h:graphicImage url="/images/sort_desc.gif" />
						</f:facet>
						<f:facet name="ascending">
							<h:graphicImage url="/images/sort_asc.gif" />
						</f:facet>
					</t:commandSortHeader>
				</f:facet>
				<h:outputText value="#{cme.metadataElement.localName}" />
			</h:column>
			<h:column>
				<f:facet name="header">
					<h:outputText value="#{msg['label.channel.metadata.element.predicate']}" title="#{msg['label.table.sort.notSortable']}" />
				</f:facet>
				<h:outputText value="#{cme.metadataElement.name}" />
			</h:column>
		</t:dataTable>
		<t:div rendered="#{ empty channelBean.channelMetadataElements}">
			<h:outputText value="There is no metadata elements for this channel" />
		</t:div>

		<t:div styleClass="commandButtons">
			<h:commandButton action="#{channelBean.prepareTemplates}" value="#{msg['label.back']}" />
			<h:commandButton action="#{channelBean.save}" value="#{msg['label.save']}" />
			<h:commandButton action="pushChannels" value="#{msg['label.cancel']}" immediate="true" rendered="#{channelBean.channel.mode == 'PUSH'}" />
			<h:commandButton action="pullChannels" value="#{msg['label.cancel']}" immediate="true" rendered="#{channelBean.channel.mode != 'PUSH'}" />
		</t:div>

	</t:div>

</h:form>
