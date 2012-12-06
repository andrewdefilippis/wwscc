<%inherit file="base.mako" />
<%namespace file="/forms/driverform.mako" import="driverform"/>

<div class='helpbox'>
If you have a profile in this series, please login.
If you have a profile in another active series, use that information and you will be given the option of copying the profile.
Otherwise, create a new profile.
</div>

<table id='loginrow'>
<tr>
<td id='logincell'>
<form id="loginForm" action="${h.url_for(action='checklogin')}" method="post">
<div id='logintable'>
<input type="hidden" name="otherseries" value=""/>
<table class='form'>
<tr><th>First Name</th><td><input type="text" name="firstname" value="" class="required"/></td></tr>
<tr><th>Last Name</th><td><input type="text" name="lastname" value="" class="required"/></td></tr>
<tr><th>Email or Unique Id</th><td><input type="text" name="email" value="" class="required"/></td></tr>
</table>
</div>

<div id='submit'>
<input type="submit" value="Login" id='loginsubmit'/>
</div>
</form>
</td>

<td id='orcell'>
OR
</td>

<td id='othercell'>
<ul>
%for name, creds in c.otherseries.iteritems():
<li><button onclick="copylogin('${creds.firstname}', '${creds.lastname}', '${creds.email}', '${name}')">Copy Profile From ${name.upper()}</button></li>
%endfor
<li><button onclick='editdriver(-1)'>Create New Profile</button></li>
</ul>
</td>

</tr>
</table>

<div class='viewheader'>Events:</div>
<table class='viewtable'>
%for e in c.events:
	<tr>
	<td class='vname'>${e.name}</td>
	<td class='vweekday'>${e.date.strftime('%a')}</td>
	<td class='vmonth'>${e.date.strftime('%b')}</td>
	<td class='vday'>${e.date.strftime('%d')}</td>
	<td><a class='viewbutton' href='${h.url_for(action='view', event=e.id)}' >View</a></td>
%if e.totlimit:
	<td>${e.count}/${e.totlimit}</td>
%else:
	<td>${e.count}</td>
%endif
	</tr>
%endfor
</table>

${driverform(action=h.url_for(action='newprofile'), method='post')}

<script type='text/javascript'>
var drivers = new Array();
$(document).ready(function() {
	$("#loginForm").validate(); 
	$("#loginsubmit").button();
	$("button").button();
	$(".viewbutton").button();
	setupDriverDialog("New Driver");
});

function driveredited()
{
	$("#drivereditor").submit();
}

function copylogin(f, l, e, s)
{
	$("#loginForm [name=firstname]").val(f);
	$("#loginForm [name=lastname]").val(l);
	$("#loginForm [name=email]").val(e);
	$("#loginForm [name=otherseries]").val(s);
	$("#loginForm").submit();
}


</script>
