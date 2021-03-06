<%@ include file="/pages/common/taglibs.jsp"%>
<%@ page import="com.eurodyn.uns.web.filters.EionetCASFilter" %>
<%
    com.eurodyn.uns.model.User user =  (com.eurodyn.uns.model.User) com.eurodyn.uns.web.jsf.LoginBean.getUser(request);
    String userRole = "";
    String userName = ((user != null) && (user.isLoggedIn())) ? user.getExternalId() : request.getRemoteUser();


    if(request.isUserInRole("admin")){
        userRole = "admin";
    }
    else if(userName != null){
        if(request.isUserInRole("reports")){
            userRole = "eea";
        };
    }

    request.setAttribute("userRole",userRole);

%>
<c:if test="${userRole == 'admin' || userRole == 'eea'}" >
<t:div id="formInitialization" rendered="#{not reportBean.preparedForm}" />

<h:form>
    <htm:h1><h:outputText value="Notifications report" /></htm:h1>
    <h:panelGrid columns="2" cellpadding="5" cellspacing="5">
        <h:outputLabel value="Notification subject" for="notificationSubject" />
        <h:inputText id="notificationSubject" value="#{reportBean.notification.subject}" size="100"/>
        <h:outputLabel value="Delivery period" for="period" />
        <h:panelGroup id="period">
            <t:inputDate value="#{reportBean.fromDate}" />
            <h:outputText value=" -- " />
            <t:inputDate id="toDate" value="#{reportBean.toDate}" />
        </h:panelGroup>
        <h:outputLabel value="Users" for="users" />
        <h:selectOneMenu id="users" style="width:220px;" value="#{reportBean.user.id}">
          <f:selectItem itemLabel="Select a user" itemValue="-1" />
          <f:selectItems value="#{reportBean.usersItems}" />
        </h:selectOneMenu>
        <h:outputLabel value="Channels" for="channels" />
        <h:selectOneMenu id="channels" style="width:220px;" value="#{reportBean.channel.id}">
          <f:selectItem itemLabel="All" itemValue="-1" />
          <f:selectItems value="#{reportBean.channelsItems}" />
        </h:selectOneMenu>
    </h:panelGrid>
    <htm:br />
    <h:commandButton action="#{reportBean.generateNotificationsReport}" value="Generate Report" />

    <t:saveState value="#{reportBean.channels}" />
    <t:saveState value="#{reportBean.users}" />
</h:form>
</c:if>