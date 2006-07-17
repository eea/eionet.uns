<%@ include file="/pages/common/taglibs.jsp"%>
<f:verbatim>
	<br />
</f:verbatim>

<t:div styleClass="box" style="margin-left:auto;margin-right:auto;width:300px;" >
	<t:div styleClass="boxleft">
		<t:div styleClass="boxtop">
			<t:div></t:div>
		</t:div>
		<f:verbatim>
			<h4>
		</f:verbatim>
		<h:outputText value="Please identify yourself" />
		<f:verbatim>
			</h4>
		</f:verbatim>
		<t:div styleClass="boxcontent" style="height:auto;">

			<h:form>
				<h:inputHidden value="#{loginBean.afterLogin}" />

				<h:panelGrid columns="2" border="0" cellpadding="5" cellspacing="3">
					<h:outputLabel for="userNameInput" value="#{msg['label.login.username']}" />
					<h:inputText id="userNameInput" size="20" required="true" value="#{loginBean.username}">
						<f:validateLength minimum="2" maximum="100" />
					</h:inputText>

					<h:outputLabel for="passwordInput">
						<h:outputText value="#{msg['label.login.password']}" />
					</h:outputLabel>
					<h:inputSecret id="passwordInput" size="20" required="true" value="#{loginBean.password}">
						<f:validateLength minimum="2" maximum="100" />
					</h:inputSecret>
				</h:panelGrid>

				<t:div styleClass="commandButtons" style="text-align:center;">
					<h:commandButton action="#{loginBean.login}" value="#{msg['label.login.submit']}" />
				</t:div>
			</h:form>
		</t:div>
		<t:div styleClass="boxbottom">
			<t:div></t:div>
		</t:div>
	</t:div>
</t:div>

<f:verbatim>
	<br />
</f:verbatim>
