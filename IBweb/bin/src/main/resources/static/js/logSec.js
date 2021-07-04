$(document).ready(function(){
	
	// ako korisnik nije ulogovan, preusmeravamo ga na pocetnu stranicu
	if(!localStorage.getItem("token"))
		window.location.href = "https://localhost:8443/";
	
});