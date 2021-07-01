var token = null;

$(document).ready(function(){

	token = localStorage.getItem("token");

	$('#logout').on('click',function(e){
		localStorage.removeItem("token");
		window.location.href = "https://localhost:8443/";
	});
	
	$('#download').on('click',function(e){
		var token = localStorage.getItem("token");
		$.ajax({
			headers:{"Authorization" :"Bearer " + token},
			contentType: 'application/json',
			type: 'GET',
			dataType:'json',
			crossDomain: true,
			url:'https://localhost:8443/api/users/whoami/download',
			success:function(response){

				console.log(response)

			},
			error: function (jqXHR, textStatus, errorThrown) { 
				console.log(jqXHR);
				alert(textStatus);
			}
		});
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
});