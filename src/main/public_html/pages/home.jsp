<%@ include file="/pages/common/taglibs.jsp"%>
<f:verbatim >
	<br />
	
	<div style="width:99%;">
		<div class="box"> 
			<div class="boxleft"> 
				<div class="boxtop"><div></div></div> 
					<h4 style="font-size: 150%">Welcome</h4> 
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
				<div class="boxbottom"><div></div></div> 
			</div>
		</div>
	</div>
	
	<br/>
	
	<div id="table" >

		<div class="dashcolumn" style="width:59%;">
			<div id="channelid_1" class="box"> 
				<div class="boxleft"> 
					<div class="boxtop"><div></div></div> 
					<h4 style="font-size: 120%">ROD Obligations statistic</h4> 
					<div class="boxcontent" style="height:200px;">
					<div style="overflow:auto; width: 100%;height:200px">
						<img src="<%= request.getContextPath() %>/svg.unsvg?subs_id=0" alt="Obligations number per year graph"/>
					</div>
					</div> 
					<div class="boxbottom"><div></div></div> 
				</div> 
			</div>
		</div>
	
		<div class="dashcolumn" style="width:40%;">
			<div id="channelid_2" class="box"> 
				<div class="boxleft"> 
					<div class="boxtop"><div></div></div> 
					<h4 style="font-size: 120%">Support</h4> 
					<div class="boxcontent" style="height:200px;">
						<p style="font-size: 100%">
						If you experience any problem using Unified Notification System, please let the Eionet Helpdesk know immediately. 
						The Helpdesk can be reached by phone on +32 2 714 87 87 from Monday through Friday 9:00 to 17:00 CET.
						</p>
						<p style="font-size: 100%">
						You can also email the helpdesk at any time:
						<a href="mailto:helpdesk@eionet.eu.int">helpdesk@eionet.eu.int</a>.
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
</f:verbatim>





	

