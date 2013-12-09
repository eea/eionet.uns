<%@ include file="/pages/common/taglibs.jsp"%>

<h:form>
    <htm:h1><h:outputText value="Notifications report" /></htm:h1>
    <h:panelGrid columns="2" cellpadding="5" cellspacing="5">
        <h:outputLabel value="Notification subject" for="notificationSubject" />
        <h:inputText id="notificationSubject" value="#{reportBean.notification.subject}"/>
        <h:outputLabel value="Delivery period" for="period" />
        <h:panelGroup id="period">
            <t:inputDate value="#{reportBean.fromDate}" />
            <h:outputText value=" -- " />
            <t:inputDate id="toDate" value="#{reportBean.toDate}" />
        </h:panelGroup>
        <h:outputLabel value="User id" for="user" />
        <h:inputText id="user" value="#{reportBean.user.externalId}"/>
    </h:panelGrid>
    <htm:br />
    <h:commandButton action="#{reportBean.createNotificationsReport}" value="Generate Report" />
</h:form>

<t:div rendered="#{not empty reportBean.notificationsRecords}">
    <t:dataTable style="width:99%" styleClass="sortable"
                 rowClasses="zebraeven,"
                 var="notification" value="#{reportBean.notificationsRecords}">
        <h:column>
            <f:facet name="header">
                <h:outputText value="Receiver" title="Receiver" />
            </f:facet>
            <h:outputText value="#{notification.user.fullName}" />
        </h:column>
        <h:column>
            <f:facet name="header">
                <h:outputText value="Subject" title="Subject" />
            </f:facet>
            <h:outputText value="#{notification.subject}" />
        </h:column>
        <h:column>
            <f:facet name="header">
                <h:outputText value="Deliveries" title="Deliveries" />
            </f:facet>
            <h:dataTable var="delivery" value="#{notification.deliveriesList}">
                <h:column>
                    <f:facet name="header">
                        <h:outputText value="Deliver to" title="Deliver to" />
                    </f:facet>
                    <h:outputText value="#{delivery.deliveryType.name}" />
                </h:column>
                <h:column>
                    <f:facet name="header">
                        <h:outputText value="Date" title="Date" />
                    </f:facet>
                    <h:outputText value="#{delivery.deliveryTime}">
                        <f:converter converterId="ed.DateConverter" />
                    </h:outputText>
                </h:column>
            </h:dataTable>
        </h:column>
    </t:dataTable>
</t:div>
