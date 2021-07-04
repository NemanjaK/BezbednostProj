$(document).ready(function(){

	var submit = $('#submit').click(function(e){
		register();
		e.preventDefault();
		return false;
	});
	
});

function register(){
	var email = $('#email').val().trim();
	var password = $('#password').val().trim();
	var password_confirm = $('#password_confirm').val().trim();
	
	if(email=="" || password=="" || password_confirm==""){
		alert("All fields must be filled out.")
		return;
	}
	
	// provera da li je uneta lozinka ista
	if (password != password_confirm) {
		$('#password-error').show();
		return false;
	}

	var data = {
		'email':email,
		'password':password
	}

	$.ajax({
		type: 'POST',
        contentType: 'application/json',
        url: 'https://localhost:8443/api/users/register',
        data: JSON.stringify(data),
        dataType: 'json',
        crossDomain: true,
		cache: false,
		processData: false,
		success:function(response){
			console.log(response);
			alert("You have registered successfully. An administrator needs to activate your account first for you to be able to login.")
			window.location.href = "index.html";
		},
		error: function (jqXHR, textStatus, errorThrown) {  
			if(jqXHR.status=="403"){
				alert("There's already a used registered under this email.");
			}
		}
	});
}