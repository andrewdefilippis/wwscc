<%inherit file="base.mako" />
<%namespace file="/forms/carform.mako" import="carform"/>
<%namespace file="/forms/driverform.mako" import="driverform"/>
<%namespace file="events.mako" import="eventdisplay"/>
<%namespace file="cars.mako" import="carlist"/>
<%namespace file="profile.mako" import="profile"/>
<%
	import datetime
	accordindex = 0;
	today = datetime.date.today();
 %>

<div id='events'>
<h2>Events</h2>
<div id='eventsinner'>

%for ii, ev in enumerate(sorted(c.events, key=lambda obj: obj.date)):
	<%
		if ev.date < today:
			accordindex = ii + 1
	%>
			
	<h3 class='${ev.closed and "eventclosed" or "eventopen"}'>
	<a>
	<span class='eventday'>${ev.date.strftime('%a')}</span>
	<span class='eventmonth'>${ev.date.strftime('%b')}</span>
	<span class='eventdate'>${ev.date.strftime('%d')}</span>
	<span class='eventname'>${ev.name}</span>
	</a>
	</h3>

	<div id='event${ev.id}' class='eventholder'>
	${eventdisplay(ev)}
	</div>
%endfor

</div>
</div>


<div id='rightcol'>

<div id='profile'>
<h2>Profile</h2>
<div id='profilewrapper'>
${profile()}
</div>
<input id='editprofile' type='button' value='Edit' onclick='editdriver(${c.driver.id})'/>
<input id='logout' type='button' value='Logout' onclick='document.location.href="${h.url_for(action="logout")}"'/>
</div>


<div id='cars'>
<h2>Cars</h2>
<div id='carswrapper'>
${carlist()}
</div>
<input id='createcar' type='button' name='create' value='Create New Car' onclick='editcar(${c.driver.id}, -1);'/>
</div>

</div>

${driverform()}
${carform(True)}


<script>

$(document).ready(function() {
	$.ajaxSetup({ cache: false });
	$("#eventsinner").accordion({ active: ${accordindex}});
	$("input[type='button']").button();
	setupCarDialog(true);
	setupDriverDialog("Edit Profile");
});
		

function updateEvent(eventid)
{
	$.getJSON('${h.url_for(action='getevent')}', {eventid:eventid}, function(json) {
		$("#event"+eventid).html(json.data);
	});
}

function updateCars()
{
	$.getJSON('${h.url_for(action='getcars')}', function(json) { 
		$("#carswrapper").html(json.data);
	});
}

function driveredited()
{
	$.post('${h.url_for(action='editdriver')}', $("#drivereditor").serialize(), function() {
		$.getJSON('${h.url_for(action='getprofile')}', function(json) {
			$("#profilewrapper").html(json.data)
		});
	});
}

function caredited()
{
	$.post('${h.url_for(action='editcar')}', $("#careditor").serialize(), function() {
		updateCars();
		%for ev in c.events:
		%if not ev.closed:
			updateEvent(${ev.id});
		%endif
		%endfor
	});
}

function deletecar(carid)
{
	$.post('${h.url_for(action='deletecar')}', {carid:carid}, function() {
		updateCars();
		%for ev in c.events:
		%if not ev.closed:
			updateEvent(${ev.id});
		%endif
		%endfor
	});
}

function registerCar(s, eventid)
{
	var carid = s.options[s.selectedIndex].value;
	$("#event"+eventid+" select").attr("disabled", "disabled");
	$.post('${h.url_for(action='registercar')}', {eventid:eventid, carid:carid}, function() {
		updateEvent(eventid);
		updateCars();
	});
}

function reRegisterCar(s, eventid, regid)
{
	var carid = s.options[s.selectedIndex].value;
	$("#event"+eventid+" select").attr("disabled", "disabled");
	$.post('${h.url_for(action='registercar')}', {regid:regid, carid:carid}, function() {
		updateEvent(eventid);
		updateCars();
	});
}

function unregisterCar(eventid, regid)
{
	$("#event"+eventid+" select").attr("disabled", "disabled");
	$.post('${h.url_for(action='registercar')}', {regid:regid, carid:-1}, function() {
		updateEvent(eventid); 
		updateCars();
	});
}

</script>

