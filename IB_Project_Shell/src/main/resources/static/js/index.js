$(document).ready(function(){
	
	$('#loginSubmit').on('click', function(event) {
		event.preventDefault();
		var emailInput = $('#emailInput');
		var passwordInput = $('#passwordInput');
		
		var email = emailInput.val();
		var password = passwordInput.val();
		
		if($('#emailInput').val() == "" || $('#passwordInput').val() == ""){
            alert('Niste uneli sve potrebne informacije!');
            return;
        }
		
		$.post('api/users/user/login', {'email': email, 'password': password},
			function(response){
				console.log(response);
				if(response == 'Admin'){
					window.location.href = 'admin.html';
				}else {
					window.location.href = 'user.html';
				}
		}).fail(function(){
			console.log("error")
		});
	});
});