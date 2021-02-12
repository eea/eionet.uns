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
<f:verbatim>
<div id="tabbedmenu" >

	<c:if  test="${selectedSubMenu == ''}" >
		<c:if  test="${not empty requestScope.metadataBean}" >
			<c:set  var="selectedSubMenu" value="${requestScope.metadataBean.subMenu}" />
		</c:if>
		<c:if  test="${empty requestScope.metadataBean}" >
			<c:set  var="selectedSubMenu" value="${sessionScope.channelBean.subMenu}" />
		</c:if>
	</c:if>

<%--	<c:if  test="${selectedSubMenu == 'pullChannels'}" >--%>
<%--		<ul>--%>
<%--			<li id="currenttab"><span style="color: black; text-decoration: none;" title="Pull channels">Pull channels</span></li>--%>
<%--			<li><a title="Push channels" href="<c:url value='/admin/channels/pushChannels.jsf'/>"   >Push channels</a></li>--%>
<%--		</ul>--%>
<%--	</c:if>--%>
	<c:if  test="${selectedSubMenu == 'pushChannels'}">
		<ul>
<%--			<li><a title="Pull channels" href="<c:url value='/admin/channels/pullChannels.jsf' />"  >Pull channels</a></li>--%>
			<li id="currenttab"><span  title="Push channels">Push channels</span></li>
		</ul>
	</c:if>
</div>
</f:verbatim>
</c:if>