<%@ include file="/pages/common/taglibs.jsp"%>
<%@page import="com.eurodyn.uns.service.facades.ChannelFacade"%>
<%@page import="java.util.*"%>
<%@page import="com.eurodyn.uns.model.Channel"%>
<%@ page import="com.eurodyn.uns.web.filters.EionetCASFilter" %>
<%@ page import="com.eurodyn.uns.web.filters.EULoginCASFilter" %>

	<h:panelGroup rendered="true">
		<f:verbatim>
		<br />
		<div style="width:99%;">
			<div class="box">
				<div class="boxleft">
					<div class="boxtop">
						<div></div>
					</div>
					<h4 style="font-size: 120%; background: #00446A; border-top: 0px; color: white">Unified Notification Service</h4>
					<div class="boxcontent">
						<ul>
							<li>
								<p style="font-size: 120%">
								The Unified Notification System is part of the ReportNet architecture.
								It provides key stakeholders in international environmental reporting
								with notifications presenting summary information harvested from ReportNet modules,
								as well as from other Agency systems based on predefined profiles.
								</p>
								<br />
							</li>
							<li>
								<p style="font-size: 120%">
								In order to define desired notification profile and set up personal preferences you need to login.
								</p>
							</li>
						</ul>
					</div>
					<div class="boxbottom">
						<div></div>
					</div>
				</div>
			</div>
		</div>
		<br/>
		</f:verbatim>
	</h:panelGroup>

	<h:panelGroup rendered="#{(empty sessionScope.user) or not sessionScope.user.loggedIn }">
		<f:verbatim>
		<div style="width:99%;">
				<div class="box">
					<div class="boxleft">
						<div class="boxtop">
							<div></div>
						</div>
						<h4 style="font-size: 120%; background: #00446A; border-top: 0px; color: white">Login</h4>
						<div class="boxcontent" style="height: 210px;">
							<p>Please login with an user authorised to access the resources you need</p>
							<div style="width: 45%; float: left; text-align: center;">
								<a href="<%=EULoginCASFilter.getCASLoginURL(request)%>" title="EU Login"><img src="../images/login/eu_logo.jpg" alt="EU Login"/></a>
								<br/>
								<h5>EU Login</h5>
							</div>
							<div style="width: 45%; float: right; text-align: center;">
								<a href="<%=EionetCASFilter.getCASLoginURL(request)%>" title="EIONET Login"><img src="../images/login/logo_eionet.png" alt="EIONET Login"/></a>
								<br/>
								<h5>EIONET Login</h5>
							</div>
						</div>
						<div class="boxbottom">
							<div></div>
						</div>
					</div>
				</div>
			</div>
			<br/>
		</f:verbatim>
	</h:panelGroup>
	
	<div id="table" >
		<div class="dashcolumn" style="width:49%; float:left;">
			<div id="channelid_1" class="box"> 
				<div class="boxleft"> 
					<div class="boxtop"><div></div></div> 
					<h4 style="font-size: 120%; background: #00446A; border-top: 0px; color: white">The most popular UNS channels</h4> 
					<div class="boxcontent" style="height:200px;">
					<%
					int i=1;
					ChannelFacade cf=new ChannelFacade();
					List l= (List) cf.getSortedChannels("numberOfSubscriptions","desc").get("list");
					for (Iterator iter = l.iterator(); iter.hasNext();) {
						Channel element = (Channel) iter.next();
						out.println("<p style=\"font-weight:bold;\">" + i + ". "+element.getTitle()+"</p>");
						out.println("Number of subscriptions " + element.getNumberOfSubscriptions());
						out.println("<br/>");
						++i;
						if (i>3) break;
					}
					%>
				</div> 
					<div class="boxbottom"><div></div></div> 
				</div> 
			</div>
		</div>
	
		<div class="dashcolumn" style="width:49%; float:left;">
			<div id="channelid_2" class="box"> 
				<div class="boxleft"> 
					<div class="boxtop"><div></div></div> 
					<h4 style="font-size: 120%; background: #00446A; border-top: 0px; color: white">Support</h4> 
					<div class="boxcontent" style="height:200px;">
						<p style="font-size: 100%">
						If you experience any problem using Unified Notification System, please let the Eionet Service Desk know immediately.
						You can email the Service Desk at any time:
						<a href="mailto:ServiceDesk@eea.europa.eu">ServiceDesk@eea.europa.eu</a>.
						</p>
						<p style="font-size: 100%">
						Do not hesitate, we are here to help!
						</p>
					</div> 
					<div class="boxbottom"><div></div></div> 
				</div> 
			</div>
		</div>
	</div>