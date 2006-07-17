<%@ include file="/pages/common/taglibs.jsp"%>

<h:form>

	<h:panelGrid columns="2" border="0" columnClasses="vertical_align_top">

		<h:outputLabel rendered="#{ empty rpcChannelBean.channel.title}" for="title" value="#{msg['label.common.title']}:" />
		<h:inputText rendered="#{ empty rpcChannelBean.channel.title}" id="title" size="30" required="true" value="#{rpcChannelBean.channel.title}" />


		<h:outputLabel rendered="#{ not empty rpcChannelBean.channel.title}" for="title_text" value="#{msg['label.common.title']}:" />
		<h:outputText rendered="#{ not empty rpcChannelBean.channel.title}" id="title_text" value="#{rpcChannelBean.channel.title}" />


		<h:outputLabel for="description" value="#{msg['label.common.description']}:" />
		<h:inputTextarea id="description" rows="2" cols="50" value="#{rpcChannelBean.channel.description}" style="width: 48em;" />
		<h:outputLabel for="language" value="#{msg['label.channels.language']}:" />
		<t:selectOneLanguage id="language" value="#{rpcChannelBean.channel.language}" />
	</h:panelGrid>

	<t:div style="text-align:left; padding-top:2em">
		<h:commandButton action="#{rpcChannelBean.save}" value="#{msg['label.save']}" />
		<h:commandButton action="rpcUserChannels" value="#{msg['label.cancel']}" immediate="true"  />
	</t:div>

</h:form>


