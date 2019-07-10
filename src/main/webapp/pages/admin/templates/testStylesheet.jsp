<%@ include file="/pages/common/taglibs.jsp"%>
<%@ page import="com.eurodyn.uns.web.filters.EionetCASFilter" %>
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
<htm:h1><h:outputText value="&nbsp;" escape="false"/></htm:h1>
<t:div styleClass="visualClear"><h:outputText value=" "/></t:div>

	<t:div styleClass="box">
		<t:div styleClass="boxleft"> 
			<t:div styleClass="boxtop">
				<t:div></t:div>
			</t:div>
			<f:verbatim><h4></f:verbatim><h:outputText value="#{dashTemplateBean.testChannel.title}" /><f:verbatim></h4></f:verbatim>
			<t:div styleClass="boxbuttons"></t:div>
			<t:div styleClass="boxcontent"><h:outputText value="#{dashTemplateBean.testChannel.content}" escape="false"/> </t:div>
			<t:div styleClass="boxbottom">
				<t:div></t:div>
			</t:div>
		</t:div>			
	</t:div>
			
<h:form >
	<htm:br/>
	<t:div style="text-align:center" >
		<h:commandButton action="#{dashTemplateBean.afterTest}" value="OK" />
	</t:div>
</h:form>
</c:if>