<%@ include file="/pages/common/taglibs.jsp"%>

<h:form>
<htm:h1><h:outputText value="&nbsp;" escape="false"/></htm:h1>
	<t:div style="width:97%;" rendered="#{ not empty metadataBean.channel.metadataElements}">
		<t:dataTable style="width:99%" styleClass="sortable" rowClasses="zebraeven," var="cme" value="#{metadataBean.channelMetadataElements}" preserveDataModel="true" sortColumn="#{metadataBean.st.sort}" sortAscending="#{metadataBean.st.ascending}" preserveSort="true">
			<h:column>
				<f:facet name="header">
					<t:commandSortHeader columnName="metadataElement.localName" value="#{msg['label.channel.metadata.element.local_name']}" title="#{'metadataElement.localName'!= metadataBean.st.sort ? msg['table.sortable']:( metadataBean.st.ascending?msg['table.sort.asc.az']:msg['table.sort.desc.za'] )}" rel="noflow" arrow="false" immediate="true">
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
			<h:column>
				<f:facet name="header">
					<h:outputText value="Actiion" title="Action" />
				</f:facet>
				<t:commandLink action="#{metadataBean.removeChannelMetadataElement}" immediate="false" onclick="if (!approve('Are you sure you want to delete metadata element {} ',['#{cme.metadataElement.name}'])) return false;"  >
					<h:graphicImage url="/images/delete.gif" alt="#{msg['label.channel.delete']}" title="#{msg['label.channel.delete']}" />
					<t:updateActionListener property="#{metadataBean.channelMetadataElement}" value="#{cme}" />
				</t:commandLink>

			</h:column>
		</t:dataTable>

		<f:verbatim>
			<br />
			<br />
		</f:verbatim>
		<t:div>
			<h:outputLabel value="Metadata value" for="searchValue" />
			<h:inputText id="searchValue" value="#{metadataBean.searchValue}" size="30" />
			<h:selectOneMenu value="#{metadataBean.searchMetadataElement}">
				<f:selectItems value="#{metadataBean.channelMetadataElementsItems}" />
			</h:selectOneMenu>
			<h:commandButton action="#{metadataBean.searchMetadataElementValues}" value="Search " />
		</t:div>
		<t:div rendered="#{not empty  metadataBean.metadataElementValues}">
			<t:dataTable id="propertyValues" style="width:99%" styleClass="sortable" rows="10" rowClasses="zebraeven," var="em" value="#{metadataBean.metadataElementValues}" preserveDataModel="true" sortColumn="#{metadataBean.st1.sort}" sortAscending="#{metadataBean.st1.ascending}" rowCountVar="valuesRowCount">
				<h:column>
					<f:facet name="header">
						<t:commandSortHeader action="#{metadataBean.searchMetadataElementValues}" columnName="value" value="Values" title="#{'value'!= metadataBean.st1.sort ? msg['table.sortable']:( metadataBean.st1.ascending?msg['table.sort.asc.az']:msg['table.sort.desc.za'] )}" rel="noflow" arrow="false" immediate="false" actionListener="#{metadataBean.reset}">
							<f:facet name="descending">
								<h:graphicImage url="/images/sort_desc.gif" />
							</f:facet>
							<f:facet name="ascending">
								<h:graphicImage url="/images/sort_asc.gif" />
							</f:facet>
						</t:commandSortHeader>
					</f:facet>
					<h:outputText value="#{em.value}" />
				</h:column>
				<h:column>
					<f:facet name="header">
						<h:outputText value="Action" title="Action" />
					</f:facet>
					<t:commandLink action="#{metadataBean.removeEventMetadataValue}" immediate="false"  onclick="if (!approve('Are you sure you want to delete value {} ',['#{em.value}'])) return false;" >
						<h:graphicImage url="/images/delete.gif" alt="#{msg['label.channel.delete']}" title="#{msg['label.channel.delete']}" />
						<t:updateActionListener property="#{metadataBean.eventMetadataValue}" value="#{em}" />
					</t:commandLink>
				</h:column>
			</t:dataTable>

			<h:panelGrid rendered="#{empty requestScope.valuesRowCount}" columns="1" styleClass="scrollerTable2" columnClasses="standardTable_ColumnCentered">
				<t:dataScroller id="scroll_1" fastStep="10" for="propertyValues" pageCountVar="pageCount" pageIndexVar="pageIndex" styleClass="scroller" paginator="true" paginatorMaxPages="9" paginatorTableClass="paginator" paginatorActiveColumnStyle="font-weight:bold;" renderFacetsIfSinglePage="false">
					<f:facet name="first">
						<t:graphicImage url="/images/arrow-first.gif" border="1" />
					</f:facet>
					<f:facet name="last">
						<t:graphicImage url="/images/arrow-last.gif" border="1" />
					</f:facet>
					<f:facet name="previous">
						<t:graphicImage url="/images/arrow-previous.gif" border="1" />
					</f:facet>
					<f:facet name="next">
						<t:graphicImage url="/images/arrow-next.gif" border="1" />
					</f:facet>
					<f:facet name="fastforward">
						<t:graphicImage url="/images/arrow-ff.gif" border="1" />
					</f:facet>
					<f:facet name="fastrewind">
						<t:graphicImage url="/images/arrow-fr.gif" border="1" />
					</f:facet>
				</t:dataScroller>
			</h:panelGrid>
		</t:div>
		<t:saveState value="#{metadataBean.metadataElementValues}" />
		<t:saveState value="#{metadataBean.channel}" />
		<t:saveState value="#{metadataBean.channelMetadataElements}" />
	</t:div>

	<t:div style="width:97%;" rendered="#{empty metadataBean.channel.metadataElements}">
		<h:outputText value="No metadata elements for channel #{metadataBean.channel.title} " />
	</t:div>
</h:form>
