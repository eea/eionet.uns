<%@ include file="/pages/common/taglibs.jsp"%>
<%@ page import="com.eurodyn.uns.web.filters.EionetCASFilter" %>
<%
	com.eurodyn.uns.model.User user =  (com.eurodyn.uns.model.User) com.eurodyn.uns.web.jsf.LoginBean.getUser(request);
	String userRole = "";
	String userName = ((user != null) && (user.isLoggedIn())) ? user.getExternalId() : request.getRemoteUser();


	if(request.isUserInRole("admin")){
		userRole = "admin";
	}

	request.setAttribute("userRole",userRole);

%>
<c:if test="${userRole == 'admin'}" >
<t:div id="formInitialization" rendered="#{ not templatesBean.preparedStylesheets}" />
<h:form>
	<htm:h1>
		<h:outputText value="Stylesheets for rendering channles content" />
	</htm:h1>


	<t:div style="width:97%;" rendered="#{ not empty templatesBean.stylesheets}">
		<t:dataTable style="width:99%" columnClasses="width30,width30,textAlignCenter," styleClass="sortable" rowClasses="zebraeven," var="stylesheet" value="#{templatesBean.stylesheets}" preserveDataModel="true" sortColumn="#{templatesBean.st.sort}" sortAscending="#{templatesBean.st.ascending}" preserveSort="true">
			<h:column>
				<f:facet name="header">
					<t:commandSortHeader value="#{msg['label.table.xsl.xsl']}" title="#{'name'!= templatesBean.st.sort ? msg['table.sortable']:( templatesBean.st.ascending?msg['table.sort.asc.az']:msg['table.sort.desc.za'] )}" rel="noflow" columnName="name" arrow="false" immediate="true">
						<f:facet name="descending">
							<h:graphicImage url="/images/sort_desc.gif" />
						</f:facet>
						<f:facet name="ascending">
							<h:graphicImage url="/images/sort_asc.gif" />
						</f:facet>
					</t:commandSortHeader>
				</f:facet>
				<t:commandLink action="#{dashTemplateBean.edit}" immediate="false">
					<h:outputText value="#{stylesheet.name}" />
					<t:updateActionListener property="#{dashTemplateBean.stylesheet}" value="#{stylesheet}" />
				</t:commandLink>
			</h:column>
			<h:column>
				<f:facet name="header">
					<h:outputText value="#{msg['label.common.description']}" title="#{msg['label.table.sort.notSortable']}" />
				</f:facet>
				<h:outputText value="#{stylesheet.description}" />
			</h:column>
			<h:column>
				<f:facet name="header">
					<t:commandSortHeader columnName="channelsCount" value="#{msg['label.table.xsl.channelsnum']}" title="#{'channelsCount'!= templatesBean.st.sort ? msg['table.sortable']:( templatesBean.st.ascending?msg['table.sort.asc']:msg['table.sort.desc'] )}" rel="noflow" arrow="false" immediate="true">
						<f:facet name="descending">
							<h:graphicImage url="/images/sort_desc.gif" />
						</f:facet>
						<f:facet name="ascending">
							<h:graphicImage url="/images/sort_asc.gif" />
						</f:facet>
					</t:commandSortHeader>
				</f:facet>
				<h:outputText value="#{stylesheet.channelsCount}" />
			</h:column>
			<h:column>
				<f:facet name="header">
					<h:outputText value="#{msg['label.common.action']}" title="#{msg['label.table.sort.notSortable']}" />
				</f:facet>
				<t:commandLink action="#{dashTemplateBean.download}" immediate="true">
					<h:graphicImage url="/images/download.gif" alt="Download stylesheet" title="Download stylesheet" />
					<t:updateActionListener property="#{dashTemplateBean.stylesheet}" value="#{stylesheet}" />
				</t:commandLink>
				<t:commandLink action="#{dashTemplateBean.prepareTest}" immediate="true">
					<h:graphicImage url="/images/test.gif" alt="Test stylesheet" title="Test stylesheet" />
					<t:updateActionListener property="#{dashTemplateBean.stylesheet}" value="#{stylesheet}" />
				</t:commandLink>
				<t:commandLink rendered="#{stylesheet.channelsCount  == 0 }" action="#{dashTemplateBean.remove}" immediate="true" actionListener="#{templatesBean.reset}" onclick="if (!approve('Are you sure you want to remove the {} stylesheet',['#{stylesheet.name}'])) return false;">
					<h:graphicImage url="/images/delete.gif" alt="Delete stylesheet" title="Delete stylesheet" />
					<t:updateActionListener property="#{dashTemplateBean.stylesheet}" value="#{stylesheet}" />
				</t:commandLink>
			</h:column>
		</t:dataTable>
		<t:saveState value="#{templatesBean.stylesheets}" />
	</t:div>
	<t:div rendered="#{empty templatesBean.stylesheets}">
		<h:outputText value="There is no stylesheetes in the system" />
	</t:div>
	<htm:br/>
	<t:commandButton value="#{msg['label.common.create']}" action="#{dashTemplateBean.edit}" actionListener="#{dashTemplateBean.reset}" />
</h:form>
</c:if>