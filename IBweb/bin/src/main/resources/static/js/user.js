var token = null;

$(document).ready(function(){
	
	token = localStorage.getItem("token");
	

	$('#logout').on('click',function(e){
		localStorage.removeItem("token");
		window.location.href = "https://localhost:8443/";
	});
	
	

	$('#download').on('click',function(e){
		var token = localStorage.getItem("token");
		console.log(token);
		
		var xhr = new XMLHttpRequest();
		xhr.open('GET', "api/users/whoami/download", true);
		xhr.setRequestHeader("Authorization", "Bearer " + token);
		xhr.responseType = 'blob';

		xhr.onload = function(e) {
			if (this.status == 200) {
				alert("Download uspesan");
				var blob = this.response;
				console.log(blob);
				var a = document.createElement('a');
				var url = window.URL.createObjectURL(blob);
				a.href = url;
				a.download = xhr.getResponseHeader('fileName');
				a.click();
				window.URL.revokeObjectURL(url);
			}
		};
		xhr.send();
	});

	
/*	$('#download').on('click',function(e){
		var token = localStorage.getItem("token");
		$.ajax({
			headers:{"Authorization" :"Bearer " + token},
			contentType: 'application/json',
			type: 'GET',
			data: 'json',
			crossDomain: true,
			url:'https://localhost:8443/api/users/whoami/download',
			success:function(response){
				
				console.log(response)
				console.log("stastasta")
			},
			error: function (jqXHR, textStatus, errorThrown) { 
				console.log(jqXHR);
				alert(textStatus);
				console.log("JOOJa")

			}
			
		});

	});*/
});