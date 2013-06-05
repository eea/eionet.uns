<%@ include file="/pages/common/taglibs.jsp"%>
<h:panelGrid columns="2" border="0"  rendered="#{facesContext.maximumSeverity.ordinal == 4}" >
	<f:verbatim>
		<script type="text/javascript" >
			headerDiv = document.getElementById('identification');
			headerDiv.style.background = "red";
			headerDiv.style.color = "white";
			headerDiv.innerHTML = 'UNS System error: Please contact the system administrator if problems persist';
		</script>
	</f:verbatim> 
</h:panelGrid>
<f:verbatim>
		<a href="http://eea.europa.eu/">European Environment Agency</a><br/>
		Kgs. Nytorv 6, DK-1050 Copenhagen K, Denmark - Phone: +45 3336 7100
</f:verbatim>

	
