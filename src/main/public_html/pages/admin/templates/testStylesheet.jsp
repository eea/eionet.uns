<%@ include file="/pages/common/taglibs.jsp"%>


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
	<t:div styleClass="commandButtons">
		<h:commandButton action="#{dashTemplateBean.afterTest}" value="OK" />
	</t:div>
</h:form>
