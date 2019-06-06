<%@ include file="/pages/common/taglibs.jsp"%>

<f:verbatim>
<div id="tabbedmenu" >

	<c:if  test="${selectedSubMenu == 'general'}" >
		<ul>
			<li id="currenttab"><span style="color: black; text-decoration: none;" title="General configuration">General</span></li>
			<li><a title="Ldap configuration" href="<c:url value='/admin/config/ldap.jsf'/>"   >Ldap</a></li>
			<li><a title="Database configuration" href="<c:url value='/admin/config/database.jsf'/>"   >Database</a></li>
			<li><a title="Mail configuration" href="<c:url value='/admin/config/mail.jsf'/>"   >Mail</a></li>
<%--			<li><a title="Jabber configuration" href="<c:url value='/admin/config/jabber.jsf'/>"   >Jabber</a></li>--%>
		</ul>
	</c:if>
	<c:if  test="${selectedSubMenu == 'ldap'}" >
		<ul>
			<li><a title="General configuration" href="<c:url value='/admin/config/general.jsf'/>"   >General</a></li>
			<li id="currenttab"><span style="color: black; text-decoration: none;" title="Ldap configuration">Ldap</span></li>
			<li><a title="Database configuration" href="<c:url value='/admin/config/database.jsf'/>"   >Database</a></li>
			<li><a title="Mail configuration" href="<c:url value='/admin/config/mail.jsf'/>"   >Mail</a></li>
<%--			<li><a title="Jabber configuration" href="<c:url value='/admin/config/jabber.jsf'/>"   >Jabber</a></li>--%>
		</ul>
	</c:if>
	<c:if  test="${selectedSubMenu == 'database'}" >
		<ul>
			<li><a title="General configuration" href="<c:url value='/admin/config/general.jsf'/>"   >General</a></li>
			<li><a title="Ldap configuration" href="<c:url value='/admin/config/ldap.jsf'/>"   >Ldap</a></li>
			<li id="currenttab"><span style="color: black; text-decoration: none;" title="Database configuration">Database</span></li>
			<li><a title="Mail configuration" href="<c:url value='/admin/config/mail.jsf'/>"   >Mail</a></li>
<%--			<li><a title="Jabber configuration" href="<c:url value='/admin/config/jabber.jsf'/>"   >Jabber</a></li>--%>
		</ul>
	</c:if>
	<c:if  test="${selectedSubMenu == 'mail'}" >
		<ul>
			<li><a title="General configuration" href="<c:url value='/admin/config/general.jsf'/>"   >General</a></li>
			<li><a title="Ldap configuration" href="<c:url value='/admin/config/ldap.jsf'/>"   >Ldap</a></li>
			<li><a title="Database configuration" href="<c:url value='/admin/config/database.jsf'/>"   >Database</a></li>
			<li id="currenttab"><span style="color: black; text-decoration: none;" title="Mail configuration">Mail</span></li>
<%--			<li><a title="Jabber configuration" href="<c:url value='/admin/config/jabber.jsf'/>"   >Jabber</a></li>--%>
		</ul>
	</c:if>
	<c:if  test="${selectedSubMenu == 'jabber'}" >
		<ul>
			<li><a title="General configuration" href="<c:url value='/admin/config/general.jsf'/>"   >General</a></li>
			<li><a title="Ldap configuration" href="<c:url value='/admin/config/ldap.jsf'/>"   >Ldap</a></li>			
			<li><a title="Database configuration" href="<c:url value='/admin/config/database.jsf'/>"   >Database</a></li>
			<li><a title="Mail configuration" href="<c:url value='/admin/config/mail.jsf'/>"   >Mail</a></li>
<%--			<li id="currenttab"><span style="color: black; text-decoration: none;" title="Jabber configuration">Jabber</span></li>--%>
		</ul>
	</c:if>
</div>
</f:verbatim>
