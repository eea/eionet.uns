<%@ include file="/pages/common/taglibs.jsp"%>

<h:form>
	<t:div rendered="#{reportBean.total == 0}">
		<h:outputText value="There is no such notifications in the system" />
	</t:div>
	<t:div rendered="#{reportBean.total != 0}">

		<h:panelGrid columns="2">

			<h:outputLabel value="Period" for="period" />
			<h:outputText id="period" value="#{reportBean.formatedFromDate} -- #{reportBean.formatedToDate}" />

			<h:outputLabel rendered="#{not empty reportBean.channel}" value="Channel" for="channel" />
			<h:outputText rendered="#{not empty reportBean.channel}" id="channel" value="#{reportBean.channel.title}" />

			<h:outputLabel rendered="#{not empty reportBean.user}" value="User" for="user" />
			<h:outputText rendered="#{not empty reportBean.user}" id="user" value="#{reportBean.user.externalId}" />

			<h:outputLabel value="Total" for="total" />
			<h:outputText id="total" value="#{reportBean.totalSuccess}/#{reportBean.totalFailed}" />

		</h:panelGrid>


		<t:dataTable style="width:99%" styleClass="sortable" rowClasses="zebraeven," var="record" value="#{reportBean.throuthputRecords}" preserveDataModel="true" sortColumn="#{reportBean.st.sort}" sortAscending="#{reportBean.st.ascending}" preserveSort="true">
			<h:column>
				<f:facet name="header">
					<t:commandSortHeader columnName="realDay" value="Day" title="#{'realDay'!= reportBean.st.sort ? msg['table.sortable']:( reportBean.st.ascending?msg['table.sort.asc.az']:msg['table.sort.desc.za'] )}" rel="noflow" arrow="false" immediate="true">
						<f:facet name="descending">
							<h:graphicImage url="/images/sort_desc.gif" />
						</f:facet>
						<f:facet name="ascending">
							<h:graphicImage url="/images/sort_asc.gif" />
						</f:facet>
					</t:commandSortHeader>
				</f:facet>
				<h:outputText value="#{record.day}" />
			</h:column>
			<h:column>
				<f:facet name="header">
					<h:outputText value="EMAIL" title="EMAIL" />
				</f:facet>
				<h:outputText value="#{record.dailyEmailSuccess}" />
				<h:outputText value="/" />
				<h:outputText value="#{record.dailyEmailFailed}" />
			</h:column>
			<h:column>
				<f:facet name="header">
					<h:outputText value="JABBER" title="JABBER" />
				</f:facet>
				<h:outputText value="#{record.dailyJabberSuccess}" />
				<h:outputText value="/" />
				<h:outputText value="#{record.dailyJabberFailed}" />
			</h:column>
			<h:column>
				<f:facet name="header">
					<h:outputText value="All" title="All" />
				</f:facet>
				<h:outputText value="#{record.dailySuccess}" />
				<h:outputText value="/" />
				<h:outputText value="#{record.dailyFailed}" />
			</h:column>
		</t:dataTable>
		<t:saveState value="#{reportBean.throuthputRecords}" />
		<t:saveState value="#{reportBean.totalSuccess}" />
		<t:saveState value="#{reportBean.totalFailed}" />
		<t:saveState value="#{reportBean.fromDate}" />
		<t:saveState value="#{reportBean.toDate}" />
		<t:saveState value="#{reportBean.channel}" />
		<t:saveState value="#{reportBean.user}" />
	</t:div>

</h:form>
