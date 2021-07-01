$(document).ready(function() {

	var submit = $('#submit').click(function(e) {
		login();
		e.preventDefault();
		return false;
	});

});

function login() {
	var email = $('#email').val().trim();
	var password = $('#password').val().trim();
	var token = '';

	var data = {
		'username' : email,
		'password' : password
	}

	$.ajax({
		type : 'POST',
		contentType : 'application/json',
		url : 'https://localhost:8443/api/auth/login',
		data : JSON.stringify(data),
		dataType : 'json',
		crossDomain : true,
		cache : false,
		processData : false,
		success : function(response) {
			var token = response.access_token;
			console.log(token);
			localStorage.setItem("token", token);
			alert('Login OK.');
		},
		error : function(jqXHR, textStatus, errorThrown) {
			if(jqXHR.status=="401"){
				alert("Wrong email or password.");
			}
		}
	});
}