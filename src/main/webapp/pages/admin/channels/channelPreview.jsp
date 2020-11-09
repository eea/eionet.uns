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
<htm:h1><h:outputText value="&nbsp;" escape="false"/></htm:h1>
<t:div styleClass="success" style="text-align:center;"><h:outputText value="Preview" /></t:div>
<t:div styleClass="visualClear"><h:outputText value=" "/></t:div>

	<t:div styleClass="box">
		<t:div styleClass="boxleft"> 
			<t:div styleClass="boxtop">
				<t:div></t:div>
			</t:div>
			<f:verbatim><h4></f:verbatim><h:outputText value="#{channelBean.channel.title}" /><f:verbatim></h4></f:verbatim>
			<t:div styleClass="boxbuttons"></t:div>
			<t:div styleClass="boxcontent"><h:outputText value="#{channelBean.transformedContent}" escape="false"/> </t:div>
			<t:div styleClass="boxbottom">
				<t:div></t:div>
			</t:div>
		</t:div>			
	</t:div>
			
<h:form id="chprew001">
	<t:div styleClass="commandButtons" style="text-align:center;" >
		<h:commandButton action="#{channelBean.prepareTemplates}" value="Back to Wizard" />
	</t:div>
</h:form>
</c:if>