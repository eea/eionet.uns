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
	<table cellpadding="1" cellspacing="1" border="0" width="99%">
	   <tr>
	      <td style=""/>
	      <td style=""/>
	      <td style="font-size:small;text-align:center;">
	         <a target="_blank" href="http://eea.eu.int/">European Environment Agency</a>
	      </td>
	      <td style=""/>
	      <td style=""/>
	   </tr>
	   <tr>
	      <td style=""/>
	      <td style=""/>
	      <td style="text-align:center;font-style:italic;">Kgs. Nytorv 6, DK-1050 Copenhagen K, Denmark - Phone: +45 3336 7100</td>
	      <td style=""/>
	      <td style=""/>
	   </tr>
	   <tr>
	      <td style=""/>
	      <td style=""/>
	      <td style=""/>
	      <td style=""/>
	      <td style=""/>
	   </tr>
	</table>
</f:verbatim>

	