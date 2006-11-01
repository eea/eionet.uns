<%@ include file="/pages/common/taglibs.jsp"%>
<%@ page isELIgnored="false" contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="com.eurodyn.uns.web.filters.EionetCASFilter" %>

<%
String a=request.getContextPath(); 
session.setAttribute("webRoot",a==null?"":a);

response.setHeader("Pragma", "No-cache");
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Cache-Control","no-store");
response.setDateHeader("Expires", 0);
%>
<tiles:importAttribute scope="request"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>Unified Notification System</title>

		<%
			String url = request.getRequestURL().toString();
			if ((url.indexOf("/dash") != -1) || (url.indexOf("/rss") != -1)  ){
				com.eurodyn.uns.model.User user = (com.eurodyn.uns.model.User) session.getAttribute("user");
				if (user != null) {
				%>
					<meta http-equiv="refresh" content="<%= user.getPageRefreshDelay().intValue() %>" />
				<%
				}
			}
		%>
		<style type="text/css" media="screen">
			<!-- @import url(<c:url value="http://www.eionet.europa.eu/styles/eea2006/layout-screen.css"/>); -->
		</style>
		<style type="text/css" media="screen">
			<!-- @import url(<c:url value="/css/portlet.css"/>); -->
		</style>								 


		<link type="text/css" media="print" href="<c:url value="/css/print.css"/>" rel="stylesheet"></link>
		<!--[if IE]>
		<style type="text/css" media="screen">
			@import url(<c:url value="/css/portlet-ie.css" />);
		</style>
		<link type="text/css" media="print" href="/css/print-ie.css" rel="stylesheet"></link>
		<![endif]-->
		<!--[if IE 5.0]>
		<style type="text/css" media="screen">
			@import url(/css/portlet-ie5.css);
		</style>
		<![endif]-->
	    <link rel="shortcut icon" href="<c:url value="/favicon.ico"/>" type="image/x-icon" />
	
	
		<script src="<c:url value="/tiny_mce/tiny_mce.js"/>" type="text/javascript"></script>

	
	
		<script type="text/javascript" src="<c:url value="/scripts/mm.js"/>"></script>
		<script type="text/javascript" src="<c:url value="/scripts/admin.js"/>"></script>
		<script type="text/javascript" src="<c:url value="/scripts/user.js"/>"></script>
		<script type="text/javascript">
			parentLocation='<%=request.getRequestURI()%>';
		   	applicationRoot='<%=request.getContextPath()%>';
		</script>
		<% if (session.getAttribute(EionetCASFilter.CAS_FILTER_USER) == null )  {%>
		<script type="text/javascript" >
				function get_cookie( cookie_name )
				{
				  var results = document.cookie.match ( cookie_name + '=(.*?)(;|$)' );				
				  if ( results )
				    return ( unescape ( results[1] ) );
				  else
				    return null;
				}
				eionetLoginCookieValue = get_cookie("<%= EionetCASFilter.EIONET_LOGIN_COOKIE_NAME %>");
				if (eionetLoginCookieValue != null && eionetLoginCookieValue == "loggedIn"){	
					window.location="<%=EionetCASFilter.getEionetCookieCASLoginURL(request) %>";
				}
		</script>
		<%}%>
		
		<link rel="alternate" type="application/rss+xml" title="Unified Notification Service RSS" href="http://uns.eionet.europa.eu/events" />
		
	</head>
	<f:loadBundle basename="labels.ApplicationResources" var="msg"/>
	<f:view>
		<body>
			<div id="pagehead">
				<f:subview id="header">
					<tiles:insert attribute="header" flush="false" />
				</f:subview>			
			</div><!-- page head -->

			<f:subview id="menu" >
				<tiles:insert attribute="menu" flush="false" />
			</f:subview>
			</div>
			<div id="workarea">
				<f:subview id="submenu">
					<tiles:insert attribute="submenu" flush="false" />
				</f:subview>																			
				<f:subview id="actionReport">
					<tiles:insert attribute="actionReport" flush="false" />
				</f:subview>
				<f:subview id="content">
					<tiles:insert attribute="body" flush="false" />
				</f:subview>	
			</div><!-- workarea --> 
			<br clear="left" />
			<div id="pagefoot" >
				<f:subview id="footer">
					<tiles:insert attribute="footer" flush="false" />
				</f:subview>			
			</div><!-- pagefoot --> 
		</body>
	</f:view>
</html>

