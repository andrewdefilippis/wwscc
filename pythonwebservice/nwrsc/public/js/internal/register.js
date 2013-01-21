
(function( $ ){

  var methods = {

	loadEvent: function(eventid) {
		this.load(url_for("getevent"), {eventid:eventid});
	},

	loadCars: function() {
		this.load(url_for('getcars'));
	}

  };

  $.fn.nwr = function( method ) {
    if ( methods[method] ) {
      return methods[method].apply( this, Array.prototype.slice.call( arguments, 1 ));
    } else {
      $.error( 'Method ' +  method + ' does not exist on nwr' );
    }    
  };

})( jQuery );


function url_for(action)
{
	return window.location.href.substring(0, window.location.href.lastIndexOf('/')+1) + action
}

function updateEvent(eventid)
{
	$.getJSON(url_for("getevent"), {eventid:eventid}, function(json) {
		$("#event"+eventid).html(json.data);
	});
}

function updateCars()
{
	$.getJSON(url_for('getcars'), function(json) { 
		$("#carswrapper").html(json.data);
	});
}

function driveredited()
{
	$.post(url_for('editdriver'), $("#drivereditor").serialize(), function() {
		$.getJSON(url_for('getprofile'), function(json) {
			$("#profilewrapper").html(json.data)
		});
	});
}

function disableBox(id)
{
	$(id+" select").attr("disabled", "disabled");
	$(id+" button").replaceWith("");
}


function registerCars()
{
	$.post(url_for('registercars'), $("#registereventform").serialize(), function() {
		updateCars();
		$('#registereventform input:checked').each(function() {
			updateEvent($(this).prop('name'));
		});
	});
}

function registerCar(s, eventid)
{
	var carid = s.options[s.selectedIndex].value;
	$(s).replaceWith("<div class='notifier'>registering...</div>");
	$.post(url_for('registercar'), {eventid:eventid, carid:carid}, function() {
		updateEvent(eventid);
		updateCars();
	});
}

function reRegisterCar(s, eventid, regid)
{
	var carid = s.options[s.selectedIndex].value;
	$(s).replaceWith("<div class='notifier'>updating registration ...</div>");
	disableBox("#event"+eventid);
	$.post(url_for('registercar'), {regid:regid, carid:carid}, function() {
		updateEvent(eventid);
		updateCars();
	});
}

function unregisterCar(domelem, eventid, regid)
{
	$(domelem).parent().replaceWith("<div class='notifier'>unregistering...</div>");
	$.post(url_for('registercar'), {regid:regid, carid:-1}, function() {
		$('#carswrapper').nwr('loadCars');
		$('#event'+eventid).nwr('loadEvent', eventid);
	});
}

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

function finishregedit() 
{
	$('#carswrapper').nwr('loadCars');
	$('#registereventform input:checked').each(function() { 
		var eventid = $(this).prop('name');
		$('#event'+eventid).nwr('loadEvent', eventid);
	});
}

