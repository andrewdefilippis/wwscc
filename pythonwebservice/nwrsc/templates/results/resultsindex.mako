<%inherit file="/base.mako" />
<%def name="extrahead()">
<style type="text/css">
ul, ol, h3, h2 { text-align: left; }
h3 { color: #111; margin-bottom: 0px; }
ul { margin-top: 0px; list-style: none; }
a { color: #44A; text-decoration: none; }

ul.classlist {
margin: 0;
margin-left: 40px;
padding: 0;
list-style: none;
width: 350px;
}

ul.classlist li {
float: left;
width: 70px;
margin: 0;
padding: 0;
}

</style>
</%def>

<h2>Results for ${c.event.name}</h2>

<h3>Active Classes</h3>
<ul class='classlist'>
%for cls in sorted(c.active, key=lambda x:x.code):
	<li><a href='${h.url_for(action='byclass', list=cls.code)}'>${cls.code}</a></li>
%endfor
</ul>
<br style='clear:both;'/>

<h3>Classes By Run Group</h3>
<ul class='classlist'>
%for ii in range(1,5):
	<li><a href='${h.url_for(action='bygroup', course=1, list=ii)}'>R${ii}</a></li>
%endfor
</ul>
<br style='clear:both;'/>

%if c.event.ispro:
	%for ch in c.challenges:
		<h3>${ch.name}</h3>
		<ul class='classlist'>
		<li><a href='${h.url_for(action='bracket', id=ch.id)}'>Bracket</a></li>
		<li><a href='${h.url_for(action='challenge', id=ch.id)}'>Details</a></li>
		</ul>
		<br style='clear:both'/>
	%endfor
%endif

<h3>Top Times Lists (All Runs)</h3>
<ul class='classlist'>
<li><a href='${h.url_for(action='topindexall')}'>Indexed</a></li>
<li><a href='${h.url_for(action='toprawall')}'>Raw</a></li>
</ul>
<br style='clear:both;'/>


<h3>Top Times Lists (Counted Runs)</h3>
<ul class='classlist'>
<li><a href='${h.url_for(action='topindex')}'>Indexed</a></li>
<li><a href='${h.url_for(action='topraw')}'>Raw</a></li>
</ul>
<br style='clear:both;'/>


%if c.event.getSegmentCount() > 0:
<h3>Other Top Times Lists</h3>
<ul class='classlist'>
<li><a href='${h.url_for(action='topseg')}'>Segment</a></li>
</ul>
<br style='clear:both;'/>
%endif


<h3>Other</h3>
<ul>
%if config['nwrsc.private']:
<li><a href="${h.url_for(controller='live')}">Mobile Live Page</a></li>
<li><a href="${h.url_for(controller='announcer')}">Announcer Panel</a></li>
%endif
<li><a href='${h.url_for(action='all')}'>All Classes</a></li>
<li><a href='${h.url_for(action='post')}'>Event Results For Posting</a></li>
<li><a href='${h.url_for(action='champ')}'>Championship Results for Posting</a></li>
<li><a href='${h.url_for(controller='xml', action='scca')}'>SCCA Report</a></li>
%if c.event.ispro and config['nwrsc.private']:
	<li><a href='${h.url_for(action='grid', order='number')}'>Grid By Number</a></li>
	<li><a href='${h.url_for(action='grid', order='position')}'>Grid By Position</a></li>
%endif
%if c.event.ispro:
	<li><a href='${h.url_for(action='dialins')}'>Dialins By Net</a></li>
	<li><a href='${h.url_for(action='dialins', order='Diff')}'>Dialins By Diff</a></li>
%endif
</ul>
