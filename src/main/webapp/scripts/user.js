/*
function removeProps() {
	var selList = document.forms["channelForm"].elements["visibleElements"];
	var availList = document.forms["channelForm"].elements["availElements"];
	var sortList = document.forms["channelForm"].elements["sortByElements"];
	for (var i = 0; i < selList.options.length; i++){
		var opt = selList.options[i];
		if (opt.selected){
			for (var j = 0; j < sortList.options.length; j++){
				if (sortList.options[j].value == opt.value){
					sortList.options[j] = null;
					break;
				}
			}
			selList.options[i] = null;
			availList.options[availList.options.length] = opt;
			i--;
		}
	}
	selList.selectedIndex = -1;
	availList.selectedIndex = -1;
}

function addProps() {
	var selList = document.forms["channelForm"].elements["visibleElements"];
	var availList = document.forms["channelForm"].elements["availElements"];
	for (var i = 0; i < availList.options.length; i++){
		var opt = availList.options[i];
		if (opt.selected){
			availList.options[i] = null;
			selList.options[selList.options.length] = opt;
			i--;
		}
	}
	selList.selectedIndex = -1;
	availList.selectedIndex = -1;
}

function submitUserChannel() {
	var selList = document.forms["dashboardForm"].elements["dashchannels"];
	var ret = "";
	for (var i = 0; i < selList.options.length; i++){
		ret += selList.options[i].value + ";";
	}
	if(ret.length > 0)	//remove last ;
		ret = ret.substring(0, ret.length-1);	
	document.forms["dashboardForm"].elements["channels"].value = ret;
}

function removeSortBy() {
	var sortList = document.forms["channelForm"].elements["sortByElements"];
	for (var i = 0; i < sortList.options.length; i++){
		var opt = sortList.options[i];
		if (opt.selected){
			sortList.options[i] = null;
			i--;
		}
	}
	sortList.selectedIndex = -1;
}

function addSortBy() {
	var visList = document.forms["channelForm"].elements["visibleElements"];
	var sortList = document.forms["channelForm"].elements["sortByElements"];
	for (var i = 0; i < visList.options.length; i++){
		var opt = visList.options[i];
		if (opt.selected){
			var found = false;
			for (var j = 0; j < sortList.options.length; j++){
				if (sortList.options[j].value == opt.value){
					found = true;
					break;
				}
			}
			if (!found)
				sortList.options[sortList.options.length] = new Option(opt.text, opt.value);
		}
	}
	visList.selectedIndex = -1;
	sortList.selectedIndex = -1;
}

function UpSortBy() {
	var sortList = document.forms["channelForm"].elements["sortByElements"];
	for (var i = 0; i < sortList.options.length; i++){
		var opt = sortList.options[i];
		if (opt.selected && i>0){
			var up = sortList.options[i-1];
			sortList.options[i] = new Option(up.text, up.value);
			sortList.options[i-1] = opt;
		}
	}
}

function DownSortBy() {
	var sortList = document.forms["channelForm"].elements["sortByElements"];
	for (var i = sortList.options.length-1; i >= 0; i--){
		var opt = sortList.options[i];
		if (opt.selected && i<sortList.options.length-1){
			var down = sortList.options[i+1];
			sortList.options[i] = new Option(down.text, down.value);
			sortList.options[i+1] = opt;
		}
	}
}
*/

function openWindow(theURL, winName) {
   var h, w;
   h = screen.height;
   w = screen.width;
   var l, t;
   l = parseInt((w - 795)/2);
   t = parseInt((h - 550)/2);

   WinId = window.open(theURL, 'HelpWindow', 'toolbar=no,menubar=no,location=no,status=no,scrollbars=yes,resizable=yes,width=795,height=550,left=' + l + ',top=' + t);
   WinId.focus();
   WinId.document.location =theURL;
}
