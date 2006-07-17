<%@ include file="/pages/common/taglibs.jsp"%>

<h:form>
	<h:panelGrid columns="1" style="width:99%">
		<htm:h1>
			<h:outputText value="LDAP configuration" />
		</htm:h1>
		<h:panelGrid columns="2">
			<h:outputLabel value="LDAP URL" for="ldap_url" />
			<h:inputText id="ldap_url" value="#{configBean.configMap['ldap.url'].tempValue}" size="30" />
			<h:outputLabel value="LDAP Context" for="ldap_context" />
			<h:inputText id="ldap_context" value="#{configBean.configMap['ldap.context'].tempValue}" size="30" />
			<h:outputLabel value="LDAP User Dir" for="ldap_user_dir" />
			<h:inputText id="ldap_user_dir" value="#{configBean.configMap['ldap.user.dir'].tempValue}" size="30" />
			<h:outputLabel value="LDAP User UID" for="ldap_user_uid" />
			<h:inputText id="ldap_user_uid" value="#{configBean.configMap['ldap.attr.uid'].tempValue}" size="30" />
		</h:panelGrid>
	</h:panelGrid>
	<t:saveState value="#{configBean.configMap}" />
	<h:commandButton action="#{configBean.updateLdap}" value="#{msg['label.save']}" />
</h:form>
