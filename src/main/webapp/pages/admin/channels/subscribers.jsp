<%@ include file="/pages/common/taglibs.jsp"%>
<%@ page import="com.eurodyn.uns.web.filters.EionetCASFilter" %>
<htm:h1>
	<h:outputText value="Subscribers" />
</htm:h1>
<h:form >
	<t:div style="width:97%;" rendered="#{ not empty channelBean.channel.subscriptions}">
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
		<htm:fieldset>
			<htm:legend>
				<h:outputText value="Add new subscriber" />
			</htm:legend>		
			<h:panelGrid columns="2" border="0">
				<h:outputLabel value="#{msg['label.channel.subscribers.username']}" for="user_name" />
				<h:inputText id="user_name" value="#{channelBean.newSubscriber}" size="30"/>
				<h:outputLabel for="subs_Delivery_types" value="Notify through:"  />
				<h:selectManyCheckbox id="subs_Delivery_types" value="#{channelBean.subscription.deliveryTypes}" required="true" >
					<f:converter converterId="ed.DeliveryTypes"/>
					<f:selectItems value="#{channelBean.deliveryTypesItems}" />
				</h:selectManyCheckbox>	
				<h:commandButton action="#{channelBean.saveSubscriber}" value="#{msg['label.channel.subscribers.add']}" />
			</h:panelGrid>	
		</htm:fieldset>
	</c:if> 
	<t:dataTable columnClasses=",,textAlignCenter" style="width:100%" styleClass="sortable" rowClasses="zebraeven," var="subscriber" value="#{channelBean.channel.subscriptions}">
		<h:column>
			<f:facet name="header">
				<h:outputText value="#{msg['label.channel.subscribers.user']}" title="#{msg['label.table.sort.notSortable']}" />
			</f:facet>
			<h:outputText value="#{subscriber.user.fullName}" />
		</h:column>
		<h:column>
			<f:facet name="header">
				<h:outputText value="#{msg['label.channel.subscribers.methods']}" title="#{msg['label.table.sort.notSortable']}" />
			</f:facet>
			<t:dataList value="#{subscriber.deliveryTypes}" var="dtype" layout="simple" rowCountVar="rowCount" rowIndexVar="rowIndex">
				<h:outputText value="#{dtype.name}"/>
				<h:outputText value=", " rendered="#{rowIndex + 1 < rowCount}" />
			</t:dataList>
		</h:column>
		<h:column>
			<f:facet name="header">
				<h:outputText value="#{msg['label.common.action']}" title="#{msg['label.table.sort.notSortable']}" />
			</f:facet>
			<c:if test="${userRole == 'admin'}" >
				<t:commandLink action="#{channelBean.removeSubscriber}" immediate="true" onclick="if (!approve('Are you sure you want to unsubscribe user from the {} channel',['#{subscriber.channel.title}'])) return false;">
					<h:graphicImage url="/images/delete.gif" alt="Unsubscribe " title="Unsubscribe" />
					<t:updateActionListener property="#{channelBean.subscription}" value="#{subscriber}" />
				</t:commandLink>
			</c:if> 
		</h:column>
	</t:dataTable>
	</t:div>
</h:form>


