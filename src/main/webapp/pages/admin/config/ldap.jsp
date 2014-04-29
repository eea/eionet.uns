<%@ include file="/pages/common/taglibs.jsp"%>

<h:form>
	<htm:h1>
		<h:outputText value="LDAP configuration" />
	</htm:h1>

	<h:panelGrid columns="2">

		<h:outputLabel value="LDAP URL:" for="ldap_url" />
		<h:outputText id="ldap_url" value="#{configBean.configMap['ldap.url'].tempValue}" />

		<h:outputLabel value="LDAP Context:" for="ldap_context" />
		<h:outputText id="ldap_context" value="#{configBean.configMap['ldap.context'].tempValue}" />

		<h:outputLabel value="LDAP User Dir:" for="ldap_user_dir" />
		<h:outputText id="ldap_user_dir" value="#{configBean.configMap['ldap.user.dir'].tempValue}" />

		<h:outputLabel value="LDAP User UID:" for="ldap_user_uid" />
		<h:outputText id="ldap_user_uid" value="#{configBean.configMap['ldap.attr.uid'].tempValue}" />

	</h:panelGrid>
</h:form>
