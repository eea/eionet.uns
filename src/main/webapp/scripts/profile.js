function submitTab(tab)
{
	document.forms["profileForm"].action = (tab.pathname.indexOf("/") != 0 ? "/" : "") + tab.pathname;
	document.forms["profileForm"].submit();
	return false;
}

function setIgnoreChannels(val){
	document.forms["profileForm"].elements["ignoreChannels"].value = val;
}

function profileChanged(){
	setIgnoreChannels("true");
}
