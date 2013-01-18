<%inherit file="base.mako" />

<% def singleline(text): return text.replace('\n', '') %>

<%def name="dorow(ii, cls)" filter="singleline">
<tr>
<td>
	<input type="text" name="clslist-${ii}.code" value="${cls.code}" size="6" />
</td>
<td>
	<input type="text" name="clslist-${ii}.descrip" value="${cls.descrip}" size="50" />
</td>
<td title="Receives trophies at events">
	<input type="checkbox" name="clslist-${ii}.eventtrophy" ${cls.eventtrophy and 'checked="checked"' or ''|n} />
</td>
<td title="Receives trophies for the series">
	<input type="checkbox" name="clslist-${ii}.champtrophy" ${cls.champtrophy and 'checked="checked"' or ''|n} />
</td>
<td title="Cars are individually indexed by index value">
	<input type="checkbox" name="clslist-${ii}.carindexed" ${cls.carindexed and 'checked="checked"' or ''|n} />
</td>
<td title="Entire class is indexed by this index code">
	<select name="clslist-${ii}.classindex">
	%for code in sorted(c.indexlist):
		<option value="${code}" ${cls.classindex==code and "selected" or ""}>${code}</option>
	%endfor
	</select>
</td>
<td class="multcol" title="This multiplier is applied to entire class, i.e. street tire factor">
	<input type="text" name="clslist-${ii}.classmultiplier" value="${"%0.3f" % (cls.classmultiplier or 1.000)}" size="5" />
</td>
<td class="flagcol" title="Require that the car flag is checked for the additional multiplier to be applied">
	<input type="checkbox" name="clslist-${ii}.usecarflag" ${cls.usecarflag and 'checked="checked"' or ''|n} />
</td>
<td title="Limit number of counted runs for this class">
	<input type="text" name="clslist-${ii}.countedruns" value="${cls.countedruns or 0}" size="3" />
</td>
<td>
	<button class="small deleterow">Del</button>
</td>
<td>
	<input type="hidden" name="clslist-${ii}.numorder" value="${cls.numorder}" />
</td>
</tr>
</%def>

<style>
#classtable td { text-align: center; }
#classtable { border-collapse: collapse; }
#doctable th { white-space: nowrap; text-align: right; padding-right: 5px; }
th.multcol, th.flagcol { border-top: 1px solid gray; }
td.multcol, th.multcol { border-left: 1px solid gray; padding-left: 5px; }
td.flagcol, th.flagcol { border-right: 1px solid gray; padding-right: 5px; }
tr:last-child td.multcol, tr:last-child td.flagcol { border-bottom: 1px solid gray; }
</style>

<h2>Class Editor</h2>

<p>
Each class has a short code and a full string description.  The description is optional.  The settings available are:

<table id='doctable'>
<tr><th>Event Trophy</th><td>This class is eligible for trophies at each event and will have a 'T' added in the results</td></tr>
<tr><th>Champ Trophy</th><td>This class is eligible for championship points and will appear in the championship report</td></tr>
<tr><th>Cars Indexed</th><td>Cars in this class will have an index applied based on the index of the car entry</td></tr>
<tr><th>Class Indexed</th><td>Cars in this class will have an index applied based on this specified index code</td></tr>
<tr><th>Addl Multiplier</th><td>Cars in this class will have an additional multipler applied to them, like class indexed but statically assigned to this class.  This is a place for a street tire modifier.</td></tr>
<tr><th>Require Car Flag</th><td>If checked, cars in this class will have their own checkbox for additional tire index and only if that is checked will additional multiplier be applied.</td></tr>
<tr><th>Counted Runs</th><td>Entries in the class will only count a maximum of X runs towards official results.  If all classes are to have the same number of counted runs, you can also set counted runs for each event.  The minimum of the two is used.</td></tr>
</table>

</p>

<p></p>


<form action="${c.action}" method="post">
<table id='classtable'>
<tr>
<th>Code</th>
<th>Description</th>
<th>Event<br/>Trophy</th>
<th>Champ<br/>Trophy</th>
<th>Cars<br/>Indexed</th>
<th>Class<br/>Indexed</th>
<th class='multcol'>Addl<br/>Multiplier</th>
<th class='flagcol'>Require<br/>Car Flag</th>
<th>Counted<br/>Runs</th>
</tr>

%for ii, cls in enumerate(c.classlist):
${dorow(ii, cls)}
%endfor

</table>
<button id='addbutton'>Add</button>
<input type='submit' value="Save">
</form>

<%  from nwrsc.model import Class %>
<script>
var rowstr = '${dorow('xxxxx', Class())}\n';
var ii = ${ii};
$('#addbutton').click(function() {
	$("#classtable > tbody").append(rowstr.replace(/xxxxx/g, ++ii));
	$('#classtable button').button();
	return false;
});
</script>

