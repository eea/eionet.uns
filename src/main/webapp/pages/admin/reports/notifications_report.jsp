<%@ include file="/pages/common/taglibs.jsp"%>

<t:div rendered="#{empty reportBean.notificationsRecords}">
    <h:outputText value="There are no notifications in the system that matched your search criteria" />
</t:div>
<t:div rendered="#{not empty reportBean.notificationsRecords}">
    <t:dataTable id="notifications" style="width:99%" styleClass="sortable"
                 rowClasses="zebraeven," rows="20"
                 var="notification" value="#{reportBean.notificationsRecords}"
                 preserveDataModel="true" sortColumn="#{reportBean.notificationsSortTable.sort}" sortAscending="#{reportBean.notificationsSortTable.ascending}" preserveSort="true">
        <t:column>
            <f:facet name="header">
                <f:facet name="header">
                    <t:commandSortHeader columnName="user.fullName" value="Receiver" title="#{'user.fullName'!= reportBean.notificationsSortTable.sort ? msg['table.sortable']:( reportBean.notificationsSortTable.ascending?msg['table.sort.asc.az']:msg['table.sort.desc.za'] )}" rel="noflow" arrow="false" immediate="true">
                        <f:facet name="descending">
                            <h:graphicImage url="/images/sort_desc.gif" />
                        </f:facet>
                        <f:facet name="ascending">
                            <h:graphicImage url="/images/sort_asc.gif" />
                        </f:facet>
                    </t:commandSortHeader>
                </f:facet>
            </f:facet>
            <h:outputText value="#{notification.user.fullName}" />
        </t:column>
        <h:column>
            <f:facet name="header">
                <t:commandSortHeader columnName="subject" value="Subject" title="#{'subject'!= reportBean.notificationsSortTable.sort ? msg['table.sortable']:( reportBean.notificationsSortTable.ascending?msg['table.sort.asc.az']:msg['table.sort.desc.za'] )}" rel="noflow" arrow="false" immediate="true">
                    <f:facet name="descending">
                        <h:graphicImage url="/images/sort_desc.gif" />
                    </f:facet>
                    <f:facet name="ascending">
                        <h:graphicImage url="/images/sort_asc.gif" />
                    </f:facet>
                </t:commandSortHeader>
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
    <t:dataScroller for="notifications" fastStep="10"
                    pageIndexVar="pageIndex" renderFacetsIfSinglePage="false"
                    pageCountVar="pageCount" paginator="true" paginatorMaxPages="9"
                    immediate="true">
        <f:facet name="first">
            <t:outputText value="First"/>
        </f:facet>
        <f:facet name="last">
            <t:outputText value="Last"/>
        </f:facet>
        <f:facet name="previous">
            <t:outputText value="Previous"/>
        </f:facet>
        <f:facet name="next">
            <t:outputText value="Next"/>
        </f:facet>
    </t:dataScroller>
    <t:saveState value="#{reportBean.notificationsRecords}" />
    <t:saveState value="#{reportBean.totalSuccess}" />
    <t:saveState value="#{reportBean.totalFailed}" />
    <t:saveState value="#{reportBean.fromDate}" />
    <t:saveState value="#{reportBean.toDate}" />
    <t:saveState value="#{reportBean.channel}" />
    <t:saveState value="#{reportBean.user}" />
</t:div>
