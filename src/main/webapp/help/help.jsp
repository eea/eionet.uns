<%@ include file="/pages/common/taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Frameset//EN"
		  "http://www.w3.org/TR/REC-html40/frame.dtd">
<html>
<head>
<title>Unified Notification Service Online Help</title>
</head>
<frameset frameborder="0" framespacing="0" border="0" cols="*" rows="32,*" >
	<frame src="<c:url value="/help/heading.html"/>"   marginwidth="0" marginheight="0"   noresize scrolling="no">
	<frameset frameborder="0" framespacing="0" border="0" cols="25%,*" rows="*">
		<% 
		if(request.isUserInRole("admin")){
		%>
			<frame  src="<c:url value="/help/menu-admin.html"/>" marginwidth="5" marginheight="5" src="" name="menu" noresize frameborder="0">
		<% 
		}
		else if(request.isUserInRole("xmlrpc")){
		%>
			<frame  src="<c:url value="/help/menu-rpc.html"/>" marginwidth="5" marginheight="5" src="" name="menu" noresize frameborder="0">		
		<%
		}
		else  { %>
		<frame  src="<c:url value="/help/menu-user.html"/>" marginwidth="5" marginheight="5" src="" name="menu" noresize frameborder="0">
		<%} %>
				
		<frame  src="<c:url value="/help/docs/index.html"/>" marginwidth="5" marginheight="5"  name="text" noresize>
	</frameset>
	<noframes>
	<p>The <code>NOFRAMES</code> element is to be used to give useful content to people with browsers that cannot display frames. One example is Lynx, a text-based browser.</p>
	</noframes>
</frameset>
</html>