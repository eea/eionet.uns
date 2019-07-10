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
<f:verbatim>
<div id="tabbedmenu">
	<c:if test="${selectedSubMenu == 'throughput'}">
		<ul>
			<li id="currenttab"><span title="Notifications throughput">Notifications throughput</span></li>
			<li><a title="Failed notifications" href="<c:url value='/admin/reports/failed_notifications.jsf'/>">Failed notifications</a></li>
			<li><a title="Notifications report" href="<c:url value='/admin/reports/report_criteria.jsf'/>">Notifications report</a></li>
		</ul>
	</c:if>
	<c:if test="${selectedSubMenu == 'failed'}" >
		<ul>
			<li><a title="Notifications throughput" href="<c:url value='/admin/reports/throughput_criteria.jsf'/>">Notifications throughput</a></li>
			<li id="currenttab"><span title="Failed notifications">Failed notifications</span></li>
      <li><a title="Notifications report" href="<c:url value='/admin/reports/report_criteria.jsf' />">Notifications report</a></li>
    </ul>
	</c:if>
    <c:if test="${selectedSubMenu == 'notifications'}">
        <ul>
            <li><a title="Notifications throughput" href="<c:url value='/admin/reports/throughput_criteria.jsf'/>">Notifications throughput</a></li>
            <li><a title="Failed notifications" href="<c:url value='/admin/reports/failed_notifications.jsf' />">Failed notifications</a></li>
            <li id="currenttab"><span title="Notifications report">Notifications report</span></li>
        </ul>
    </c:if>
</div>
</f:verbatim>
</c:if>


