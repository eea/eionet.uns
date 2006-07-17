<%@ include file="/pages/common/taglibs.jsp"%>
<f:verbatim>
	<div id="identification">
		<a href="/">
		        <img src="/UNS/images/eionet/logo.png" alt="Logo" id="logo" border="0" /></a>
		<div class="sitetitle">
			Unified Notification System
		</div>
		<div class="sitetagline">
		        Notifications from Eionet in your mailbox
		</div>
		<a class="skiplink" href="#maincontent" accesskey="2">Skip over navigation</a>
	</div>
	<div class="breadcrumbtrail">
	        <div class="breadcrumbhead">
	                You are here:
	        </div>
	        <div class="breadcrumbitem">
	                <a href="http://www.eionet.europa.eu">EIONET</a>
	        </div>
		</f:verbatim>
			<h:outputText escape="false" value="#{ breadCrumbBean.breadCumbs}" />
		<f:verbatim>
	        <div class="breadcrumbtail">
	        </div>
	</div>	
</f:verbatim>