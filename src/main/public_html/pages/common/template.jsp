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
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
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
		<link rel="stylesheet" type="text/css" href="http://www.eionet.europa.eu/styles/eionet2007/print.css" media="print" />
		<link rel="stylesheet" type="text/css" href="http://www.eionet.europa.eu/styles/eionet2007/handheld.css" media="handheld" />		
		<link rel="stylesheet" type="text/css" href="http://www.eionet.europa.eu/styles/eionet2007/screen.css" media="screen" title="Eionet 2007 style" />
		<link rel="stylesheet" type="text/css" href="<c:url value="/css/portlet.css"/>" media="screen" title="Eionet 2007 style" />
		<link type="text/css" media="print" href="<c:url value="/css/print.css"/>" rel="stylesheet"></link>
		<link rel="shortcut icon" href="<c:url value="/favicon.ico"/>" type="image/x-icon" />

		<!--[if IE]>
		<link type="text/css" media="screen" href="<c:url value="/css/portlet-ie.css" />" rel="stylesheet"></link>
		<link type="text/css" media="print" href="/css/print-ie.css" rel="stylesheet"></link>
		<![endif]-->
		<!--[if IE 5.0]>
		<link type="text/css" media="screen" href="/css/portlet-ie5.css" rel="stylesheet"></link>
		<![endif]-->

	
		<script src="<c:url value="/tiny_mce/tiny_mce.js"/>" type="text/javascript"></script>
	
		<script type="text/javascript" src="<c:url value="/scripts/mm.js"/>"></script>
		<script type="text/javascript" src="<c:url value="/scripts/admin.js"/>"></script>
		<script type="text/javascript" src="<c:url value="/scripts/user.js"/>"></script>
		<script type="text/javascript" src="<c:url value="/scripts/pageops.js"/>"></script>
		<script type="text/javascript">
			parentLocation='<%=request.getRequestURI()%>';
		   	applicationRoot='<%=request.getContextPath()%>';
		</script>
		<% if (session.getAttribute(EionetCASFilter.CAS_FILTER_USER) == null )  {%>
		<script type="text/javascript" >
			//<![CDATA[
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
			//]]>
		</script>
		<%}%>
		
		<link rel="alternate" type="application/rss+xml" title="Unified Notification Service RSS" href="http://uns.eionet.europa.eu/events" />
		
	</head>
	<f:loadBundle basename="labels.ApplicationResources" var="msg"/>
	<f:view>
	<body>
		<div id="container">
			<div id="toolribbon">
				<div id="lefttools">
			      <a id="eealink" href="http://www.eea.europa.eu/">EEA</a>
			      <a id="ewlink" href="http://www.ewindows.eu.org/">EnviroWindows</a>
			    </div>
			    <div id="righttools">    
			    	<h:panelGroup rendered="#{(not empty sessionScope.user) and sessionScope.user.loggedIn}" >
			    		<f:verbatim>
							<a id="logoutlink" href="<c:url value="/pages/logout.jsp" />" title="Logout">Logout 
						</f:verbatim>
							<h:outputText value="#{sessionScope.user.externalId}"/>
						<f:verbatim>
							</a>
						</f:verbatim>
					</h:panelGroup>
					<h:panelGroup rendered="#{(empty sessionScope.user) or not sessionScope.user.loggedIn }">
						<f:verbatim>
							<a id="loginlink" href="<%=EionetCASFilter.getCASLoginURL(request)%>" title="Login">Login</a>
						</f:verbatim>
					</h:panelGroup>
					<a href="#" id="pagehelplink" onclick="javascript:openWindow('<c:url value="/help/help.jsp"/>','onlinehelp');" title="Help"><span><fmt:message key="label.menu.help"/></span></a>
					<a id="printlink" title="Print this page" href="javascript:this.print();"><span>Print</span></a>
			        <a id="fullscreenlink" href="javascript:toggleFullScreenMode()" title="Switch to/from full screen mode"><span>Switch to/from full screen mode</span></a>
			        <a id="acronymlink" href="http://www.eionet.europa.eu/acronyms" title="Look up acronyms"><span>Acronyms</span></a>
			        <form action="http://search.eionet.europa.eu/search.jsp" method="get">
						<div id="freesrchform"><label for="freesrchfld">Search</label>
							<input type="text" id="freesrchfld" name="query"/>
							<input id="freesrchbtn" type="image" src="<c:url value="/images/button_go.gif" />" alt="Go"/>
						</div>
					</form>
			    </div>
			</div>
			<div id="pagehead">
				<f:subview id="header">
					<tiles:insert attribute="header" flush="false" />
				</f:subview>			
			</div><!-- page head -->
			<div id="menuribbon">
				<%@ include file="dropdownmenus.txt" %>
			</div>
			<f:verbatim>
			<div class="breadcrumbtrail">
				<div class="breadcrumbhead">
					You are here:
				</div>
				<div class="breadcrumbitem">
					<a href="http://www.eionet.europa.eu">EIONET</a>
				</div>
				</f:verbatim>
					<h:outputText escape="false" value="#{ breadCrumbBean.breadCumbs}" />
				<f:verbatim>
			        <div class="breadcrumbtail">
			        </div>
			</div>	
			</f:verbatim>
			<div id="leftcolumn" class="localnav">
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
		</div>
	</body>
	</f:view>
</html>

