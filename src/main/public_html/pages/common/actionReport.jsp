<%@ include file="/pages/common/taglibs.jsp"%>
	<h:outputText value="&nbsp;" escape="false" rendered="#{facesContext.maximumSeverity.ordinal > 0}" /> 
	<t:div style="border-width: 1px; border-color: #97AF79; border-style: solid; width: 60%; background: #ffffff;" rendered="#{facesContext.maximumSeverity.ordinal == 1}" >
		<h:panelGrid  columns="2" border="0"   >
			<h:graphicImage url="/images/exclamation_info.gif" alt="" title=""/>
			<h:outputText value="INFORMATION ..."></h:outputText>
			<h:graphicImage url="/images/bullet_info.gif" alt="" title=""/>
			<t:messages showSummary="false"  showDetail="true"  layout="table" />
		</h:panelGrid>
	</t:div>
	<t:div style="border-width: 1px; border-color: #97AF79; border-style: solid; width: 60%; background: #ffffff;" rendered="#{facesContext.maximumSeverity.ordinal == 2}" >
		<h:panelGrid  columns="2" border="0"   >
			<h:graphicImage url="/images/exclamation_warning.gif" alt="" title=""/>
			<h:outputText value="WARNING ..."></h:outputText>
			<h:graphicImage url="/images/bullet_warning.gif" alt="" title=""/>
			<t:messages showSummary="false"  showDetail="true"  layout="table" />
		</h:panelGrid>
	</t:div>

	<t:div style="border-width: 1px; border-color: #97AF79; border-style: solid; width: 60%; background: #ffffff;" rendered="#{facesContext.maximumSeverity.ordinal > 2}" >
		<h:panelGrid columns="2" border="0"   >
			<h:graphicImage url="/images/exclamation_error.gif" alt="" title=""/>
			<h:outputText style="color:#FF0000" value="ERRORS ..."></h:outputText>
			<h:graphicImage url="/images/bullet_error.gif" alt="" title=""/>
			<t:messages showSummary="false"  layout="table" showDetail="true"  style="color:#FF0000"/> 
		</h:panelGrid>
	</t:div>	