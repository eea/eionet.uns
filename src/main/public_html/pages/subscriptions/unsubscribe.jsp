<%@ include file="/pages/common/taglibs.jsp"%>

<t:div id="formInitialization" rendered="#{ subscriptionBean.preparedUnsubscribe}"  />
<h:form rendered="#{not empty subscriptionBean.subscription}" >
	
	<h:outputText value="Are you sure you want to unsubscribe from the channel \"#{subscriptionBean.subscription.channel.title}\"" /> 
	<f:verbatim> <br /> <br /> </f:verbatim>
	<h:commandButton value="Yes"  action="#{subscriptionBean.remove}" immediate="false"  />
	<h:commandButton value="No"   action="subscriptions"  immediate="true"  />
	<t:saveState value="#{subscriptionBean.subscription}" />	
	<f:verbatim> <br /> <br /> </f:verbatim>
</h:form>

<h:form rendered="#{empty subscriptionBean.subscription}" >
	<h:outputText value="Your subscription is alredy deleted" /> 
	<f:verbatim> <br /> <br /> </f:verbatim>
</h:form>
