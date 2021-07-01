var token = null;

$(document).ready(function(){
	
	token = localStorage.getItem("token");
	
	// preuzmi sve korisnike
	getAllUsers();
	
	$('#logout').on('click',function(e){
		localStorage.removeItem("token");
		window.location.href = "https://localhost:8443/";
	});
	
	$(document).on('click', '#activate', function(){
		activateUser($(this).data('id'));
	});
	
});

function getAllUsers(token){
	$.ajax({
			headers:{"Authorization" :"Bearer " + token},
			contentType: 'application/json',
			type: 'GET',
			dataType:'json',
			crossDomain: true,
			url:'https://localhost:8443/api/users',
			success:function(response){
				
				var table = $('#users tbody');
				
				// ubaci redove korisnika
				for(var i=0; i<response.length; i++) {
					user = response[i];
					//console.log(user);
					
					var row = '<tr data-id="'+user.id+'">'+
							'<td>'+user.id+'</td>'+
							'<td>'+user.email+'</td>'+
							'<td>'+user.active+'</td>';
					// za neaktivne korisnike se ubacuje dugme za aktivaciju
					if (!user.active)
						var activationBtn = '<button id="activate" data-id="'+user.id+'">Aktiviraj</button>';
					else
						var activationBtn = '';
					row += '<td>' + activationBtn + '</td>'+
					'</tr>';
					
					table.append(row);
				}
			},
			error: function (jqXHR, textStatus, errorThrown) { 
				console.log(jqXHR);
				alert(textStatus);
			}
		});
}

function activateUser(id){
	$.ajax({
		type: 'POST',
		contentType: 'application/json',
		headers:{"Authorization" :"Bearer " + token},
		url: 'https://localhost:8443/api/users/activate/' + id,
		dataType: 'json',
		crossDomain: true,
		cache: false,
		processData: false,
		success:function(response){
			location.reload();
		},
		error: function (jqXHR, textStatus, errorThrown) {
			console.log(jqXHR);
			alert(textStatus);
		}
	});
}
