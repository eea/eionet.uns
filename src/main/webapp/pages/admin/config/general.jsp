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
<h:form>
    <htm:h1>
        <h:outputText value="General configuration" />
    </htm:h1>

    <h:panelGrid columns="2">

<%--        <h:outputLabel value="Harvester interval (minutes):" for="harvester_interval"/>--%>
<%--        <h:outputText id="harvester_interval" value="#{configBean.configMap['daemons/harvester/interval'].tempValue}"/>--%>

<%--        <h:outputLabel value="Harvester threads:" for="harvester_threads" />--%>
<%--        <h:outputText id="harvester_threads" value="#{configBean.configMap['daemons/harvester/pull_threads'].tempValue}"/>--%>

        <h:outputLabel value="Notificator interval (minutes):" for="notificator_interval" />
        <h:outputText id="notificator_interval" value="#{configBean.configMap['daemons/notificator/interval'].tempValue}"/>

        <h:outputLabel value="Event Updater interval (days):" for="eventupdater_interval"/>
        <h:outputText id="eventupdater_interval" value="7"/>

        <h:outputLabel value="Feed generation interval (days):" for="feed_interval" />
        <h:outputText id="feed_interval" value="#{configBean.configMap['feed/events_feed_age'].tempValue}" />

    </h:panelGrid>
</h:form>
</c:if>

