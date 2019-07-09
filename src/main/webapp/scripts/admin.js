JSF_SEPARATOR_CHAR=":"


function approve(message ,parameters)
{	
	if(parameters)
	{				
		for (i =0 ; i < parameters.length; i++)
		{
			message = message.replace("{}",'"' +parameters[i] + '"')											
		}
	}
	return confirm(message)
}			 

function getFormName(element)
{
	id = element.id?element.id:element.name
	formName = id.substring(0,id.lastIndexOf(':'));
	return formName;
}

function removeEmpty(list){
	for (var i = 0; i < list.options.length; i++){
		var opt = list.options[i];
		if (opt.value == -1){
			list.options[i] = null;
		}
	}
	list.selectedIndex = -1;
}

function moveList(fromList, toList){
	for (var i = 0; i < fromList.options.length; i++){
		var opt = fromList.options[i];
		if (opt.selected){
			fromList.options[i] = null;
			toList.options[toList.options.length] = opt;
			i--;
		}
	}
	toList.selectedIndex = -1;
	fromList.selectedIndex = -1;
}

function getAllInList(list, separator){
	var ret = "";
	for (var i = 0; i < list.options.length; i++){
		ret += list.options[i].value + separator;
	}
	if(ret.length > 0)	//remove last separator
		ret = ret.substring(0, ret.length-1);
	return ret;
}

/*
function selectComboBox(combo, value){
	for(var i=0; i<combo.options.length; i++){
		var opt = combo.options[i];
		if(opt.value == value){
			opt.selected = true;
			break;
		}
	}
}
*/

/*
*	Channels
*/
function channelDelete(deletecell, row) {
	title = findTableRowTitle(deletecell, row);
	if(confirm('Are you sure you want delete channel "' + title + '"')){
		return true;
	}
	else {
		return false;
	}
}

function channelEnable(chcell, row) {
	title = findTableRowTitle(chcell, row);
	if(confirm('Are you sure you want to enable channel "' + title + '"')){
		return true;
	}
	else {
		return false;
	}
}

function channelDisable(chcell, row) {
	title = findTableRowTitle(chcell, row);
	if(confirm('Are you sure you want to disable channel "' + title + '"')){
		return true;
	}
	else {
		return false;
	}
}

function xslDelete(deletecell) {
	title = findTableRowTitle(deletecell, 1);
	if(confirm('Are you sure you want delete stylesheet "' + title + '"')){
		return true;
	}
	else {
		return false;
	}
}

function ChannelModeChanged(){
	var mode = document.forms[0].elements["mode"];
	var value = "";
	for(var i=0; i< mode.length; i++){
		if (mode[i].checked){
			value = mode[i].value;
			break;
		}
	}
	switch(value){
		case "PULL":
			document.forms[0].elements["url"].disabled = false;
			document.forms[0].elements["refresh"].disabled = false;
			break;
		case "PUSH":
			document.forms[0].elements["url"].disabled = true;
			document.forms[0].elements["refresh"].disabled = true;
			break;
	}
}



function trimAll(){
	inputs = document.getElementsByTagName('input');
	for(var i=0; i<inputs.length; i++) { inputs[i].value = inputs[i].value.replace(/^\s*|\s*$/g,"");}
}


function submitChannel(form) {

	trimAll();	
	var rolesList = form.elements[form.id+JSF_SEPARATOR_CHAR + "channelRoles"];
	form.elements[form.id + JSF_SEPARATOR_CHAR + "currentChannelRoles"].value = getAllInList(rolesList, ";");
	//alert(form.elements[form.id + JSF_SEPARATOR_CHAR + "currentChannelRoles"].value);
}


function submitChannelTemplates(form) {
	var visibleList = form.elements[form.id+JSF_SEPARATOR_CHAR + "channelVisibleElements"];
	var fromList = document.forms[JSF_FORM].elements[JSF_FORM+JSF_SEPARATOR_CHAR+"availableElements"];
	form.elements[form.id+JSF_SEPARATOR_CHAR +"visibleElements"].value = getAllInList(visibleList, ";");
	//alert(form.elements[form.id + JSF_SEPARATOR_CHAR + "visibleElements"].value);	
	for (var i = 0; i < visibleList.length; i++){visibleList.options[i].selected = 0;}
	for (var i = 0; i < fromList.length; i++){fromList.options[i].selected = 0;}
}


function addRoles(element){
	JSF_FORM = getFormName(element)
	var toList = document.forms[JSF_FORM].elements[JSF_FORM+JSF_SEPARATOR_CHAR+"channelRoles"];
	var fromList = document.forms[JSF_FORM].elements[JSF_FORM+JSF_SEPARATOR_CHAR+"availableRoles"];
	moveList(fromList, toList);
}

function removeRoles(element){
	JSF_FORM = getFormName(element)
	var fromList = document.forms[JSF_FORM].elements[JSF_FORM+JSF_SEPARATOR_CHAR+"channelRoles"];
	var toList = document.forms[JSF_FORM].elements[JSF_FORM+JSF_SEPARATOR_CHAR+"availableRoles"];
	moveList(fromList, toList);
}

function addProps(element) {
	JSF_FORM = getFormName(element)
	var toList = document.forms[JSF_FORM].elements[JSF_FORM+JSF_SEPARATOR_CHAR+"channelVisibleElements"];
	var fromList = document.forms[JSF_FORM].elements[JSF_FORM+JSF_SEPARATOR_CHAR+"availableElements"];
	moveList(fromList, toList);
}

function removeProps(element) {
	JSF_FORM = getFormName(element)
	var fromList = document.forms[JSF_FORM].elements[JSF_FORM+JSF_SEPARATOR_CHAR+"channelVisibleElements"];
	var toList = document.forms[JSF_FORM].elements[JSF_FORM+JSF_SEPARATOR_CHAR+"availableElements"];
	moveList(fromList, toList);
}

function upVisible(element){
	JSF_FORM = getFormName(element)
	var list = document.forms[JSF_FORM].elements[JSF_FORM+JSF_SEPARATOR_CHAR+"channelVisibleElements"];
	for (var i = 0; i < list.options.length; i++){
		var opt = list.options[i];
		if (opt.selected && i>0){
			var up = list.options[i-1];
			list.options[i] = new Option(up.text, up.value);
			list.options[i-1] = opt;
		}
	}
}

function downVisible(element){
	JSF_FORM = getFormName(element)
	var list = document.forms[JSF_FORM].elements[JSF_FORM+JSF_SEPARATOR_CHAR+"channelVisibleElements"];
	for (var i = list.options.length-1; i >= 0; i--){
		var opt = list.options[i];
		if (opt.selected && i<list.options.length-1){
			var down = list.options[i+1];
			list.options[i] = new Option(down.text, down.value);
			list.options[i+1] = opt;
		}
	}
}



/*
function SelectStylesheet(value){
	var combo = document.forms[0].elements["stylesheetId"];
	selectComboBox(combo, value);
}
*/

function haveStylesheetChanged(element){
	JSF_FORM = getFormName(element)
	//var mode = document.forms[0].elements["haveStylesheet"];
	var mode = document.forms[JSF_FORM].elements[JSF_FORM+JSF_SEPARATOR_CHAR+"isStylesheetSelected"];

	var value = "";
	for(var i=0; i< mode.length; i++){
		if (mode[i].checked){
			value = mode[i].value;
			break;
		}
	}
	
	prefix = JSF_FORM+JSF_SEPARATOR_CHAR;
	switch(value){
		case "true":
		    document.getElementById(prefix+"VisibleElementsDiv").style.display = "none";
		    document.getElementById(prefix+"StylesheetDiv").style.display = "block";
			//document.forms[0].elements[prefix+"stylesheetId"].disabled = false;
			//document.forms[0].elements[prefix+"availableElements"].disabled = true;
			//document.forms[0].elements[prefix+"channelVisibleElements"].disabled = true;
			//document.forms[0].elements[prefix+"visibleElements"].disabled = true;
			break;
		case "false":
		    document.getElementById(prefix+"VisibleElementsDiv").style.display = "block";
		    document.getElementById(prefix+"StylesheetDiv").style.display = "none";
			//document.forms[0].elements[prefix+"stylesheetId"].disabled = true;
			//document.forms[0].elements[prefix+"availableElements"].disabled = false;
			//document.forms[0].elements[prefix+"channelVisibleElements"].disabled = false;
			//document.forms[0].elements[prefix+"visibleElements"].disabled = false;
			break;
	}
}



/*
*	Dashboards
*/
function removeSelected() {
	var selList = document.forms["dashboardForm"].elements["dashchannels"];
	var availList = document.forms["dashboardForm"].elements["availchannels"];
	moveList(selList, availList);
}

function addAvailable() {
	var selList = document.forms["dashboardForm"].elements["dashchannels"];
	var availList = document.forms["dashboardForm"].elements["availchannels"];
	moveList(availList, selList);
}

function findTableRowTitle(deletecell, whichrow){
	var row = deletecell.parentNode.parentNode;
	var title="";
	for(var i=0; i<row.childNodes.length; i++){
		var cell = row.childNodes[i];
		if(cell.nodeName == "TD"){
			whichrow--;
			if (whichrow != 0)
				continue;
			if(cell.textContent)
				title = cell.textContent;
			else if(cell.innerText)
				title = cell.innerText;
			title = title.replace(/^\s*|\s*$/g,"");//trim spaces
			title = title.replace(/&quot;/g,"\"");//replace quot
			title = title.replace(/&amp;/g,"&");//replace amp
			break;
		}
	}
	return title;
}

function dashboardDelete(deletecell) {
	title = findTableRowTitle(deletecell, 1);
	if(confirm('Do you want to delete dashboard "' + title + '"')){
		return true;
	}
	else {
		return false;
	}
}

function submitDashboard() {
	var selList = document.forms["dashboardForm"].elements["dashchannels"];

	var ret = getAllInList(selList, ";");
	/*
	var ret = "";
	for (var i = 0; i < selList.options.length; i++){
		ret += selList.options[i].value + ";";
	}
	if(ret.length > 0)	//remove last ;
		ret = ret.substring(0, ret.length-1);	
   */
	document.forms["dashboardForm"].elements["channels"].value = ret;
}

function submitUpload(){
	document.forms[0].action = 'upload';
	document.forms[0].submit();
	return false;
}

function sortTableBy(prop){
	var orderProperty = document.forms["form"].elements["dto(orderProperty)"];
	var order = document.forms["form"].elements["dto(order)"];
	if(orderProperty.value == prop){
		if(order.value == "DESC")
			order.value = "ASC";
		else
			order.value = "DESC";
	}
	else{
		orderProperty.value = prop;
		order.value = "ASC";
	}
	document.forms['form'].submit();
}



var selectedRow = null
function colorRow(curElement) {
	if(selectedRow != null) selectedRow.style.backgroundColor = "";
	while (curElement && !(curElement.tagName == "TR")) {
		curElement = curElement.parentNode;
	}
	curElement.style.backgroundColor = "#BEE0FC";
	selectedRow = curElement;
}


var currentEvent = null
var firstEventId = null


function setCurrentEvent(eventId) {
	if(firstEventId != null) {
	   document.getElementById(firstEventId).style.display = "none";
	   firstEventId = null;
	}else if(currentEvent != null) {
		currentEvent.style.display = "none";
	} 
	divEvent = document.getElementById(eventId);
	divEvent.style.display = "block";
	currentEvent = divEvent;
	
}




function setNextEvent(event) {
	switch (event.keyCode) {
	      case 38: // up
	          nextEvent('up');
		  break;
	      case 39: // right
	      case 40: // down
		   nextEvent('down');	
	           break;
	      case 33: // page up
	      case 36: // home
	      case 34: // page down
	      case 35: // end
	    } 
  
 } 

function nextEvent(where){

	curRow = selectedRow;

	while (curRow && !(curRow.tagName == "TR")) {
		curRow = curRow.parentNode;
	}
	if (where == 'down'){
	   sibling = curRow.nextSibling;
	   while (sibling && sibling.nodeType != 1) {
	     sibling = sibling.nextSibling;
	   }	
	}
	if (where == 'up'){
	   sibling = curRow.previousSibling;
	   while (sibling && sibling.nodeType != 1) {
	     sibling = sibling.previousSibling;
	   }	
	}

	if (!sibling || sibling.tagName != "TR"){
		return;
	}

	if(currentEvent != null) {
		currentEvent.style.display = "none";
	}
	
	if(selectedRow != null) selectedRow.style.backgroundColor = "";
		
	for (i=0; i < sibling.childNodes.length; i++){
		if ( sibling.childNodes[i].tagName == "TD"){		
			td = sibling.childNodes[i];
			if (td.childNodes[0].tagName == "A")
				td.childNodes[0].onclick();
		}	
	}
	selectedRow = sibling;	
}

function eventUpdaterMessage(element) {

	var historic = document.getElementsByClassName("system-msg").item(0);
	if (historic!==null)
		historic.remove();

	var newElement = document.createElement("div");
	newElement.className = 'system-msg';

	var textMessage = document.createTextNode("EVENT UPDATER PROCESS STARTED...");
	newElement.appendChild(textMessage);

	var parentElement = document.getElementById("workarea");
	parentElement.insertBefore(newElement, parentElement.childNodes[0]);

}
