<%@ include file="/pages/common/taglibs.jsp"%>
<t:div id="formInitialization" rendered="#{ not templatesBean.preparedNotificationTemplates}" />
<h:form>
	<htm:h1>
		<h:outputText value="Templates for generating notifications" />
	</htm:h1>
	<t:div style="width:97%;">
		<t:dataTable style="width:100%" styleClass="sortable" rowClasses="zebraeven," columnClasses="width30,,textAlignCenter" var="template" value="#{templatesBean.notificationTemplates}" preserveDataModel="true" sortColumn="#{templatesBean.st.sort}" sortAscending="#{templatesBean.st.ascending}" preserveSort="true">
			<h:column>
				<f:facet name="header">
					<t:commandSortHeader value="#{msg['label.common.name']}" title="#{'name'!= templatesBean.st.sort ? msg['table.sortable']:( templatesBean.st.ascending?msg['table.sort.asc.az']:msg['table.sort.desc.za'] )}" rel="noflow" columnName="name" arrow="false" immediate="true">
						<f:facet name="descending">
							<h:graphicImage url="/images/sort_desc.gif" />
						</f:facet>
						<f:facet name="ascending">
							<h:graphicImage url="/images/sort_asc.gif" />
						</f:facet>
					</t:commandSortHeader>
				</f:facet>
				<t:commandLink action="#{notificationTemplateBean.edit}" immediate="true">
					<h:outputText value="#{template.name}" />
					<t:updateActionListener property="#{notificationTemplateBean.notificationTemplate}" value="#{template}" />
				</t:commandLink>
			</h:column>
			<h:column>
				<f:facet name="header">
					<h:outputText value="#{msg['label.template.notification.subject']}" title="#{msg['label.table.sort.notSortable']}" />
				</f:facet>
				<h:outputText value="#{template.subject}" />
			</h:column>
			<h:column>
				<f:facet name="header">
					<h:outputText value="#{msg['label.common.action']}" title="#{msg['label.table.sort.notSortable']}" />
				</f:facet>
				<t:commandLink action="#{notificationTemplateBean.prepareTest}" immediate="true">
					<h:graphicImage url="/images/test.gif" alt="Test notification template" title="Test notification template" />
					<t:updateActionListener property="#{notificationTemplateBean.notificationTemplate}" value="#{template}" />
				</t:commandLink>
				<t:commandLink rendered="#{not template.editOnly}" action="#{notificationTemplateBean.remove}" actionListener="#{templatesBean.reset}" immediate="true" onclick="if (!approve('Are you sure you want to remove the {} template',['#{template.name}'])) return false;">
					<h:graphicImage url="/images/delete.gif" alt="Delete notification template" title="Delete notification template " />
					<t:updateActionListener property="#{notificationTemplateBean.notificationTemplate}" value="#{template}" />
				</t:commandLink>
			</h:column>
		</t:dataTable>

		<t:div styleClass="commandButtons">
			<t:commandButton value="#{msg['label.common.create']}" action="#{notificationTemplateBean.edit}" actionListener="#{notificationTemplateBean.reset}" />
		</t:div>
		<t:saveState value="#{templatesBean.notificationTemplates}" />
	</t:div>

</h:form>
