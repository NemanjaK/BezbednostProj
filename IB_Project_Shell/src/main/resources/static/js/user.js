$(document).ready(function(){

    var clicks = 0;

    $("#buttonSearch").click(function (e) {
        if(clicks == 0){
            $("#buttonSearch").html("Pretraga &#8679");
            $("#search").fadeIn( 1200 , function(){
                document.getElementById("search").style.display = "unset";
            });
        }else if (clicks % 2 !== 0) { 
            $("#buttonSearch").html("Pretraga &#8681");
            $("#search").hide();
         }else if(clicks % 2 == 0) {
            $("#buttonSearch").html("Pretraga &#8679");
            $("#search").show();
         }
         clicks++;
         e.preventDefault();
         return false;
    });
    
  	//Table
    var emailInput = $('#emailInput');
    var userTable = $('#userTable');
    var mySection = $('#mySection');
    
});