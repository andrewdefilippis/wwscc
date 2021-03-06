<%inherit file="base.mako" />

<style>
.Xcleanlist { list-style: none; }
</style>

<div class='emenu'>
<h2 style='margin-bottom:5px;'>${c.event.name} - ${c.event.date.strftime('%B %d, %Y')}</h2>

<div style='margin-left: 20px;'>

<h3>General Admin</h3>
<ul class='cleanlist'>
<li><a href='${h.url_for(action='editevent')}'>Edit Event Details</a></li>
<li><a href='${h.url_for(action='list')}'>Entry Admin</a></li>
<li><a href='${h.url_for(action='rungroups')}'>Grid Order</a></li>
<li><a href='${h.url_for(action='deleteevent')}' onclick='return confirm("Do you really want to delete this event and and its registered entries?");'>Delete Event</a></li>
</ul>

<h3>Before The Event</h3>
<ul class='cleanlist'>
<li>Pregistered Timing Cards (<a href='${h.url_for(action='printhelp')}' target='_blank'>Printing Help</a>):
	<ul>
	<li>8x5 Index Cards</li>
		<ul>
		<li><a href='${h.url_for(action='printcards', type='lastname', page='card')}'>By Last Name</a></li>
		<li><a href='${h.url_for(action='printcards', type='classnumber', page='card')}'>By Class then Number</a></li>
		</ul>
	<li>8x11 Letter Paper</li>
		<ul>
		<li><a href='${h.url_for(action='printcards', type='lastname', page='letter')}'>By Last Name</a></li>
		<li><a href='${h.url_for(action='printcards', type='classnumber', page='letter')}'>By Class then Number</a></li>
		</ul>
	<li>CSV Data</li>
		<ul>
		<li><a href='${h.url_for(action='printcards', type='lastname', page='csv')}'>By Last Name</a></li>
		<li><a href='${h.url_for(action='printcards', type='classnumber', page='csv')}'>By Class then Number</a></li>
		</ul>
	</ul>
</li>

<li>Blank Timing Cards
	<ul>
	<li><a href='${h.url_for(action='printcards', type='blank', page='card')}'>8x5 Index Cards</a></li>
	<li><a href='${h.url_for(action='printcards', type='blank', page='letter')}'>8x11 Letter Paper</a></li>
	</ul>
</li>
</ul>

<h3>Reports</h3>
<ul class='cleanlist'>
<li><a href='${h.url_for(action='numbers')}' target='_blank'>Used Car Number List</a></li>
<li><a href='${h.url_for(action='paid')}' target='_blank'>List of Fess Already Paid</a></li>
<li><a href='${h.url_for(action='paypal')}' target='_blank'>Paypal Transaction List</a></li>
<li><a href='${h.url_for(action='contactlist')}'>Contact List</a></li>
</ul>

<h3>After The Event</h3>
<ul class='cleanlist'>
<li><a href='${h.url_for(action='newentrants')}' target='_blank'>Collected Fee List</a></li>
</ul>

</div>

</div>
