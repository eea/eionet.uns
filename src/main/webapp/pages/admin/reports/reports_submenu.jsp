<%@ include file="/pages/common/taglibs.jsp"%>

<f:verbatim>
<div id="tabbedmenu">
	<c:if  test="${selectedSubMenu == 'throughput'}">
		<ul>
			<li id="currenttab"><span  title="Notifications throughput">Notifications throughput</span></li>		
			<li><a title="Failed notifications" href="<c:url value='/admin/reports/failed_notifications.jsf' />"  > Failed notifications</a></li>
		</ul>
	</c:if>
	<c:if  test="${selectedSubMenu == 'failed'}" >
		<ul>
			<li><a title="Notifications throughput" href="<c:url value='/admin/reports/report_criteria.jsf'/>"   >Notifications throughput</a></li>		
			<li id="currenttab"><span  title="Failed notifications">Failed notifications</span></li>
		</ul>
	</c:if>
</div>
</f:verbatim>



 