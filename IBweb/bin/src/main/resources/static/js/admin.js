var token = null;

$(document).ready(function(){
	var korisniciTable = $('#korisniciTable');

	token = localStorage.getItem("token");
	
	// preuzmi sve korisnike
	
	
	$('#logout').on('click',function(e){
		localStorage.removeItem("token");
		window.location.href = "https://localhost:8443/";
	});
	
	$(document).on('click', '#activate', function(){
		activateUser($(this).data('id'));
	});
	
	var emailInput = $('#email');
	getAllUsers(token);
	
	function getAllUsers(token){	
	var email = emailInput.val();
	$.ajax({
			headers:{"Authorization" :"Bearer " + token},
			contentType: 'application/json',
			type: 'GET',
			dataType:'json',
			crossDomain: true,
			url:'https://localhost:8443/api/users/all?'+'email='+email,
			success:function(data){
				korisniciTable.empty();
				

				for(const user of data){
					korisniciTable.append(
			        `<tr>'                      
                            '<td>${user.id}</td>'
                            '<td>${user.email}</td>'
                            '<td>${user.active ? "Aktivan" : "Neaktivan"}</td>'
							'<td><button id="activate" data-id=${user.id}>Aktiviraj</button></td>';
                       '</tr>`
					
					)
				
				}
			},
			error: function (jqXHR, textStatus, errorThrown) { 
				console.log(jqXHR);
				alert(textStatus);
			}
		});
}
    emailInput.keyup(function (e) {
        e.preventDefault();
        getAllUsers(token);
    });

	
});
 	

function activateUser(id){
	$.ajax({
		type: 'PUT',
		contentType: 'application/json',
		headers:{"Authorization" :"Bearer " + token},
		url: 'https://localhost:8443/api/users/activate/' + id,
		dataType: 'json',
		crossDomain: true,
		cache: false,
		processData: false,
		success:function(response){
				alert("User activated.");
				 location.reload();
		},
		error: function (jqXHR, textStatus, errorThrown) {
			console.log(jqXHR);
			alert("User already activated.");
		}
	});
}
