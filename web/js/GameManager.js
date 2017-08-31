$("#begin-game-btn").on('click', handleClick(1));

$("#end-game-btn").on('click', handleClick(2));

$("#remove-player-btn").on('click', handleClick(3));

$("#Ban-player-btn").on('click', handleClick(4));

$("#unban-player-btn").on('click', handleClick(5));

$("#remove-riddle-btn").on('click', handleClick(6));

$("#move-player-btn").on('click', handleClick(7));

$("#manage-statistics-btn").on('click', handleClick(8));

$("#add-manager-btn").on('click', handleClick(9));

$("#remove-manager-btn").on('click', handleClick(10));

function handleClick(buttonNum) {
    $.ajax({
        url: "game-manager",
        type: 'POST',
        data: {token: sessionStorage.getItem("access token"), button: buttonNum},
        success: function() {
            window.location.href = SITE_URL + "/Home.html";
        },
        error: function(msg) {
            alert(msg);
        }
    });
}

setInterval(function(){ 
    $.ajax({
        url: "game-manager",
        type: 'POST',
        success:refreshPlayerList()
}) }, 30000);


function refreshPlayerList(listData) {
    //clear all current gameRooms
    $("#player-riddle-tbl").empty();

    $.each(listData['GameRooms'] || [], function(index, data) {
        //console.log(/*"Adding GameRoom #" + i + ": " + gameRoom.key + " " + gameRoom.value*/ Object.keys(gameRooms));
        var tr = $('<tr>' + data['PlayerName'] + '</td>' + '<td>'+ data['Riddle']  +'</td>' );
        $("#player-riddle-tbl").append(tr);
    });
}