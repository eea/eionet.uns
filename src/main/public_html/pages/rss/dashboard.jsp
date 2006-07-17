<%@ include file="/pages/common/taglibs.jsp"%>
	<f:verbatim><br/><br/></f:verbatim>
	<t:div id="formInitialization" rendered="#{ not dashboardBean.preparedForm}"  />
	<h:form   rendered="#{not empty dashboardBean.channels}"  >
		<t:div>	
			<t:dataTable  value="#{dashboardBean.rows}" var="xxx" style="width:98%"  cellpadding="0" cellspacing="0" >
				<t:columns  value="#{dashboardBean.channels}" var="dashColumn" style="vertical-align:top;width:33%"  >
					<t:div  styleClass="dashcolumn" >						
						<t:dataTable  value="#{dashColumn}" var="subs"  rendered="#{not empty dashColumn}" style="width:100%;margin-right:0;border-rigth:0;padding-right:0" cellpadding="0"  cellspacing="0" >
							<t:column  >
						    	<t:div styleClass="box"  >
						    		<t:div styleClass="boxleft" >
							    		<t:div styleClass="boxtop" >
							    			<t:div></t:div>
							    		</t:div>	
										<f:verbatim><h4></f:verbatim><h:outputText value="#{subs.channel.title}" /><f:verbatim></h4></f:verbatim>
							    		<t:div styleClass="boxbuttons" >
											<t:commandLink  action="#{dashboardBean.close}"  immediate="true" onmouseout="MM_swapImgRestore()"  onmouseover="MM_swapImage(this.childNodes[0],'',this.childNodes[0].src.replace('off','on') ,1)"  onclick="if (!approve('Are you sure you want to remove  the {} channel from dashboard',['#{subs.channel.title}'])) return false;" >
												<t:graphicImage  url="/images/close_off.png" alt="close" title="Close channel"  width="15" height="15"  />
												<t:updateActionListener property="#{dashboardBean.id}" value="#{subs.channel.id}" />
											</t:commandLink>
											<t:commandLink rendered="#{subs.channel.right}" action="#{dashboardBean.moveChannel}" immediate="true" onmouseout="MM_swapImgRestore()"  onmouseover="MM_swapImage(this.childNodes[0],'',this.childNodes[0].src.replace('off','on') ,1)" >
												<t:graphicImage  url="/images/right_off.png" alt="right" title="Move right"  width="15" height="15"  />
												<t:updateActionListener property="#{dashboardBean.id}" value="#{subs.channel.id}" />
												<t:updateActionListener property="#{dashboardBean.direction}" value="right" />
											</t:commandLink>	    		
											<t:commandLink rendered="#{subs.channel.down}" action="#{dashboardBean.moveChannel}" immediate="true" onmouseout="MM_swapImgRestore()"  onmouseover="MM_swapImage(this.childNodes[0],'',this.childNodes[0].src.replace('off','on') ,1)" >
												<t:graphicImage  url="/images/down_off.png" alt="down" title="Move down"  width="15" height="15"  />
												<t:updateActionListener property="#{dashboardBean.id}" value="#{subs.channel.id}" />
												<t:updateActionListener property="#{dashboardBean.direction}" value="down" />											
											</t:commandLink>	    		
											<t:commandLink rendered="#{subs.channel.up}" action="#{dashboardBean.moveChannel}" immediate="true" onmouseout="MM_swapImgRestore()"  onmouseover="MM_swapImage(this.childNodes[0],'',this.childNodes[0].src.replace('off','on') ,1)" >
												<t:graphicImage  url="/images/up_off.png" alt="up" title="Move up"  width="15" height="15"  />
												<t:updateActionListener property="#{dashboardBean.id}" value="#{subs.channel.id}" />
												<t:updateActionListener property="#{dashboardBean.direction}" value="up" />	
											</t:commandLink>	    		
											<t:commandLink rendered="#{subs.channel.left}" action="#{dashboardBean.moveChannel}" immediate="true" onmouseout="MM_swapImgRestore()"  onmouseover="MM_swapImage(this.childNodes[0],'',this.childNodes[0].src.replace('off','on') ,1)" >
												<t:graphicImage  url="/images/left_off.png" alt="left" title="Move left"  width="15" height="15"  />
												<t:updateActionListener property="#{dashboardBean.id}" value="#{subs.channel.id}" />
												<t:updateActionListener property="#{dashboardBean.direction}" value="left" />													
											</t:commandLink>	    		
							    		</t:div>
										<t:div styleClass="boxcontent">
											<h:outputText escape="false" value="#{subs.channel.content}" />
										</t:div>
										<t:div styleClass="boxbottom">
											<t:div></t:div>
										</t:div> 
									</t:div>
						    	</t:div>
							</t:column>
						</t:dataTable>
						<h:outputText value=" "  rendered="#{empty dashColumn}" />
					</t:div>
				</t:columns>
			</t:dataTable>
		</t:div>
	</h:form>
	
	<t:div id="condition001" rendered="#{ empty dashboardBean.channels}" style="text-align:center;font-size: medium" ><h:outputText  escape="false" value="#{msg['dashboard.noDashboard']}" /></t:div>
	<t:saveState value="#{dashboardBean.channels}" />
	<t:saveState value="#{dashboardBean.portletExist}" />	
	
