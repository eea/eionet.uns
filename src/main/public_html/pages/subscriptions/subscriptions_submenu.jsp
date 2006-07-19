<%@ include file="/pages/common/taglibs.jsp"%>

<f:verbatim>
<div id="tabbedmenu">

	<c:if  test="${selectedSubMenu == ''}" >
		<c:set  var="selectedSubMenu" value="${subscriptionBean.subMenu}" />
	</c:if>

	<c:if  test="${selectedSubMenu == 'subscriptions'}" >
		<ul>
			<li id="currenttab"><span  title="Existing subscriptions list">My subscriptions</span></li>
			<li><a title="Subscribe" href="<c:url value='/subscriptions/availableChannels.jsf'/>"     >Subscribe</a></li>
			<li><a title="Your preferences" href="<c:url value='/subscriptions/userPreferences.jsf'/>"     >My preferences</a></li>
		</ul>
	</c:if>
	<c:if  test="${selectedSubMenu == 'avChannels'}" >
		<ul>
			<li><a title="Existing subscriptions list" href="<c:url value='/subscriptions/subscriptions.jsf'/>"     >My subscriptions</a></li>
			<li id="currenttab"><span  title="Subscribe">Subscribe</span></li>
			<li><a title="Your preferences" href="<c:url value='/subscriptions/userPreferences.jsf'/>"     >My preferences</a></li>
		</ul>
	</c:if>
	<c:if  test="${selectedSubMenu == 'preferences'}" >
		<ul>
			<li><a title="Existing subscriptions list" href="<c:url value='/subscriptions/subscriptions.jsf'/>"     >My subscriptions</a></li>
			<li><a title="Subscribe" href="<c:url value='/subscriptions/availableChannels.jsf'/>"     >Subscribe</a></li>
			<li id="currenttab"><span  title="Your preferences">My preferences</span></li>
		</ul>
	</c:if>	
</div>
</f:verbatim>

