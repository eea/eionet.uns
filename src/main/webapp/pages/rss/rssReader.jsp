<%@ include file="/pages/common/taglibs.jsp"%>
<f:verbatim >
	<br/>
</f:verbatim>
<t:div id="formInitialization" rendered="#{ not rssReaderBean.preparedForm}"  />
<t:div rendered="#{not empty rssReaderBean.channels}" >
	<t:div styleClass="box">
		<t:div styleClass="boxleft">
			<t:div styleClass="boxtop">
				<t:div></t:div>
			</t:div>
			<f:verbatim>
				<h4>
			</f:verbatim>
			<h:outputText value="UNS RSS Reader" />
			<f:verbatim>
				</h4>
			</f:verbatim>
			<t:div styleClass="boxcontent" style="height:auto;">
				<h:form>
					<h:panelGrid columns="2" border="0" cellspacing="0" cellpadding="0" style="width:99%; table-layout:fixed;" columnClasses="rssColumn1,rssColumn2">
						<t:dataTable style="width:99%" var="channel" value="#{rssReaderBean.channels}" rowId="channelRow#{channel.id}" >
							<h:column>
								<f:facet name="header">
									<h:outputText value="Channels" />
								</f:facet>
								<t:commandLink action="#{rssReaderBean.changeChannel}"  immediate="true" style="text-decoration:none">
									<h:outputText value="#{channel.title}" />
									<t:updateActionListener property="#{rssReaderBean.channel}" value="#{channel}" />
								</t:commandLink>
							</h:column>
						</t:dataTable>

						<h:panelGrid columns="1" border="0" cellspacing="0" cellpadding="0" style="width:98%;">
							<f:facet name="header">							
								<h:panelGrid columns="2" style="width:99%;margin:0;font-weight:bold;" border="0" cellspacing="0" cellpadding="0" columnClasses="eventsHeader1,eventsHeader2">
									<h:outputText value="Title" />
									<h:outputText escape="false" value="&nbsp;&nbsp;Received date" />
								</h:panelGrid>
							</f:facet>
							<t:div style="height:150px;overflow: scroll">
								<t:dataTable border="0" style="width:97%;table-layout:fixed;" cellspacing="0" cellpadding="2" var="rdfThing" value="#{rssReaderBean.events}" onkeydown=" setNextEvent(event) "  columnClasses="eventsColumn1,eventsColumn2"    rowIndexVar="index" rowId="eventRow#{index}" >
									<h:column>
										<t:commandLink action="#" immediate="true" 	style="text-decoration:none"	onclick="colorRow(this); setCurrentEvent('#{rdfThing.eventId}') ; return false;">
											<h:outputText value="#{rdfThing.title}" />
											<t:updateActionListener property="#{rssReaderBean.eventId}" value="#{rdfThing.eventId}" />
										</t:commandLink>
									</h:column>
									<h:column>
										<h:outputText value="#{rdfThing.receivedDate}">
											<f:converter converterId="ed.DateConverter" />
										</h:outputText>
									</h:column>
								</t:dataTable>
							</t:div>
							<h:outputText escape="false" value="#{rssReaderBean.renderedEvents}" />
						</h:panelGrid>
					</h:panelGrid>
				</h:form>
			</t:div>
			<t:div styleClass="boxbottom">
				<t:div></t:div>
			</t:div>
		</t:div>
	</t:div>
	<f:verbatim>
		<script type="text/javascript">
		<!--
			firstEventId = <%= "'" + session.getAttribute("currentEvent") + "'" %> ;
			channelId = <%= "'" + session.getAttribute("rrChanelId") + "'" %> ;
			document.getElementById('channelRow' + channelId).style.backgroundColor = "#BEE0FC";
			selectedRow = document.getElementById('eventRow0');
			if(selectedRow){
				selectedRow.style.backgroundColor = "#BEE0FC";
				selectedRow.childNodes[0].childNodes[0].focus();			
			}
		//-->
		</script>
		<br/>
	</f:verbatim>
	
</t:div>

<t:div rendered="#{ empty rssReaderBean.channels}" style="text-align:center;font-size: medium">
	<h:outputText escape="false" value="#{msg['dashboard.noDashboard']}" />
</t:div>
<t:saveState value="#{rssReaderBean.channels}" />