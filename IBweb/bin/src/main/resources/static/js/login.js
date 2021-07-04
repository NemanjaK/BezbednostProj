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
			whoAmI(token);
			alert('Login OK.');
		},
		error : function(jqXHR, textStatus, errorThrown) {
			if(jqXHR.status=="401"){
				alert("Wrong email or password.");
			}
		}
	});


	function whoAmI(token) {
		$.ajax({
			headers:{"Authorization" :"Bearer " + token},
			contentType: 'application/json',
			type: 'GET',
			dataType:'json',
			crossDomain: true,
			url:'https://localhost:8443/api/users/whoami',
			success:function(response){
				var role = response.userAuthorities[0];
				if(response.active) {
					if(role === "ROLE_ADMIN") {
					location.href = "https://localhost:8443/admin.html"
					} else if(role === "ROLE_REGULAR") {
						location.href = "https://localhost:8443/user.html"
					}
				} else {
					alert("You can't access this page until administrator activates your account.")
				}				
			},
			error: function (jqXHR, textStatus, errorThrown) { 
				console.log(jqXHR);
				alert(textStatus);
			}
		});
	}
}