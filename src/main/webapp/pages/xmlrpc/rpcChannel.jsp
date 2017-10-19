<%@ include file="/pages/common/taglibs.jsp"%>

<h:form>
	<htm:h1 rendered="#{empty rpcChannelBean.channel.title}"> <h:outputText  value="Edit channel "  /></htm:h1>
	<htm:h1 rendered="#{not empty rpcChannelBean.channel.title}"> <h:outputText  value="Edit the '#{rpcChannelBean.channel.title}' channel"  /></htm:h1>

	<h:panelGrid columns="2" border="0" >

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
	<t:saveState value="#{rpcChannelBean.channel}" />
</h:form>


