<!DOCTYPE html>
<html lang="en">
<head>
<title>${c.title}</title>
<meta http-equiv='Content-Type' content='text/html; charset=utf-8' />
<meta http-equiv="X-UA-Compatible" content="IE=9"/>
%for style in c.stylesheets:
<link href="${style}" rel="stylesheet" type="text/css" />
%endfor
%for js in c.javascript:
${h.javascript_link(js)}
%endfor
<script type='text/javascript'>
<%
try:
	context.write('$.nwr.urlbase = "%s";' % h.url_for(action=''));
except:
	pass
%>
</script>
<%def name="extrahead()"></%def>
${self.extrahead()}
</head>
<body>
${c.header|n}
${next.body()}
</body>
</html>
