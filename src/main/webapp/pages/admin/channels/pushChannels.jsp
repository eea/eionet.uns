<%@ include file="/pages/common/taglibs.jsp"%>
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
<t:div id="formInitialization" rendered="#{ not channelListBean.preparedPushChannels}" />
<h:form>
	<htm:h1><h:outputText value="Push channels list" /></htm:h1>
	<t:div style="width:97%;" rendered="#{ not empty channelListBean.pushChannels}">
		<t:dataTable style="width:100%" columnClasses=",width20,,,textAlignCenter,textAlignCenter,textAlignCenter,width10Center" styleClass="sortable" rowClasses="zebraeven," var="channel" value="#{channelListBean.pushChannels}" preserveDataModel="true"  rowId="#{channel.title}" sortColumn="#{channelListBean.st.sort}" sortAscending="#{channelListBean.st.ascending}" preserveSort="true">
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
				<t:commandLink actionListener="#{channelBean.reset}" action="#{channelBean.edit}" immediate="true">
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
					<t:commandSortHeader columnName="numberOfSubscriptions" value="Subs"   title="Number of sumbscriptions" rel="noflow" arrow="false" immediate="true">
						<f:facet name="descending">
							<h:graphicImage url="/images/sort_desc.gif" />
						</f:facet>
						<f:facet name="ascending">
							<h:graphicImage url="/images/sort_asc.gif" />
						</f:facet>
					</t:commandSortHeader>
				</f:facet>
				<t:commandLink action="#{channelBean.subscribers}" immediate="true">
					<h:outputText value="#{channel.numberOfSubscriptions}" />
					<t:updateActionListener property="#{channelBean.channel}" value="#{channel}" />
				</t:commandLink>
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
				<h:outputText rendered="#{ channel.status == 0}" value="Disabled" />
				<h:outputText rendered="#{ channel.status == 1}" value="Enabled" />
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
				<t:commandLink action="#{channelBean.remove}" immediate="true" actionListener="#{channelListBean.reset}"  onclick="if (!approve('Are you sure you want to remove channel {}',['#{channel.title}'])) return false;">
					<h:graphicImage url="/images/delete.gif" alt="#{msg['label.channel.delete']}" title="#{msg['label.channel.delete']}"  /> 
					<t:updateActionListener property="#{channelBean.channel}" value="#{channel}" />
				</t:commandLink>
				<h:outputText value=" " />
				<t:commandLink rendered="#{ channel.status == 0}" action="#{channelBean.changeStatus}" immediate="true" actionListener="#{channelListBean.reset}" >
					<h:graphicImage url="/images/check.gif" alt="Enable channel" title="Enable channel" />
					<t:updateActionListener property="#{channelBean.channel}" value="#{channel}" />
				</t:commandLink>
				<t:commandLink rendered="#{ channel.status == 1}" action="#{channelBean.changeStatus}" immediate="true" actionListener="#{channelListBean.reset}"  onclick="if (!approve('Are you sure you want to disable channel {}',['#{channel.title}'])) return false;">
					<h:graphicImage url="/images/disable.gif" alt="Disable channel" title="Disable channel" />
					<t:updateActionListener property="#{channelBean.channel}" value="#{channel}" />
				</t:commandLink>
				<h:outputText value=" " />				
				<t:commandLink action="#{metadataBean.prepareMetadataElements}" immediate="true">
					<h:graphicImage url="/images/properties.gif" alt="Manage channel metadata elements" title="Manage channel metadata elements"  />
					<t:updateActionListener property="#{metadataBean.channel}" value="#{channel}" />
				</t:commandLink>
			</h:column>
		</t:dataTable>
		<t:saveState value="#{channelListBean.pushChannels}" />
	</t:div>
	<t:div rendered="#{empty channelListBean.pushChannels}">
		<h:outputText value="There is no push channels in the system" />
	</t:div>
</h:form>
</c:if>