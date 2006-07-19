<%@ include file="/pages/common/taglibs.jsp"%>

<h:form enctype="multipart/form-data">
	<htm:h1><h:outputText value="&nbsp;" escape="false"/></htm:h1>
	<h:panelGrid columns="2" border="0" columnClasses="vertical_align_top">
		<h:outputLabel for="name" value="#{msg['label.common.name']}" />
		<h:inputText id="name" size="20" maxlength="30" required="true" value="#{dashTemplateBean.stylesheet.name}" />

		<h:outputLabel for="description" value="#{msg['label.common.description']}" />
		<h:inputTextarea id="description" rows="2" cols="50" value="#{dashTemplateBean.stylesheet.description}" style="width: 48em;" />

		<h:outputLabel for="fileupload" value="#{msg['label.common.upload']}" />
		<h:panelGroup>
			<t:inputFileUpload id="fileupload" value="#{dashTemplateBean.upFile}" storage="file" style="margin-right:1em"/>
			<h:commandButton value="#{msg['label.common.upload.short']}" action="#{dashTemplateBean.upload}" />
		</h:panelGroup>

		<h:outputLabel for="content" value="#{msg['label.table.xsl.xsl']}" />
		<h:inputTextarea id="content" required="false" value="#{dashTemplateBean.stylesheet.content}" cols="128" rows="300" style="height: 30em; width: 48em; white-space: pre;" />
	</h:panelGrid>
	<t:div styleClass="commandButtons " style="text-align:center;">
		<h:commandButton action="#{dashTemplateBean.save}" value="#{msg['label.save']}" />
		<h:commandButton action="#{dashTemplateBean.prepareTest}" value="Test" actionListener="#{dashTemplateBean.changeAfterTest} " />
		<h:commandButton action="dashTemplates" value="#{msg['label.cancel']}" immediate="true" />
	</t:div>

</h:form>
