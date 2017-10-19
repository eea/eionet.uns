<%@ include file="/pages/common/taglibs.jsp"%>
	<h:outputText value="&nbsp;" escape="false" rendered="#{facesContext.maximumSeverity.ordinal > 0}" /> 

	<t:div styleClass="system-msg" rendered="#{facesContext.maximumSeverity.ordinal == 1}">
		<t:messages showSummary="false"  showDetail="true"  layout="list" />
	</t:div>
	
	<t:div styleClass="caution-msg" rendered="#{facesContext.maximumSeverity.ordinal == 2}">
		<t:htmlTag value="strong">
			<h:outputText value="Warning ..."></h:outputText>		
		</t:htmlTag>
		<t:messages showSummary="false"  showDetail="true"  layout="list" />
	</t:div>	

	<t:div styleClass="warning-msg" rendered="#{facesContext.maximumSeverity.ordinal > 2}">
		<t:htmlTag value="strong">
			<h:outputText value="Errors ..."></h:outputText>		
		</t:htmlTag>
		<t:messages showSummary="false"  showDetail="true"  layout="list" />
	</t:div>	