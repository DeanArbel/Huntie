var mInfoWindow;
var mUserPos;

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
            window.location.href = "/home.html";
        },
        error: function(msg) {
            alert(msg);
        }
    });
}

// setInterval(function(){
//     $.ajax({
//         url: "game-manager",
//         type: 'POST',
//         success:refreshPlayerList()
// }) }, 30000);


function refreshPlayerList(listData) {
    //clear all current gameRooms
    $("#player-riddle-tbl").empty();

    $.each(listData['GameRooms'] || [], function(index, data) {
        //console.log(/*"Adding GameRoom #" + i + ": " + gameRoom.key + " " + gameRoom.value*/ Object.keys(gameRooms));
        var tr = $('<tr>' + data['PlayerName'] + '</td>' + '<td>'+ data['Riddle']  +'</td>' );
        $("#player-riddle-tbl").append(tr);
    });
}

function initMap() {
    var map_container = $('#map');
    map_container.show();
    var mapWidth = 640;
    var mapHeight = 480;
    map_container.height(mapHeight);
    map_container.width(mapWidth);
    eMap = new google.maps.Map(map_container[0], {
        center: {lat: 32.109333, lng: 34.855499},
        zoom: 16,
        disableDefaultUI: true
    });
    mInfoWindow = new google.maps.InfoWindow;
    getLocation();
}

function showPosition(position) {
    mUserPos = {
        lat: position.coords.latitude,
        lng: position.coords.longitude
    };
    mInfoWindow.setPosition(mUserPos);
    mInfoWindow.setContent('You Are Here');
    mInfoWindow.open(eMap);
    var icon = {
        url: "../assets/images/locationCircle.png", // url
        scaledSize: new google.maps.Size(20, 20), // scaled size
        origin: new google.maps.Point(0,0), // origin
        anchor: new google.maps.Point(10, 10) // anchor
    };
    eMap.setCenter(mUserPos);
    mapMarker = new google.maps.Marker({position:mUserPos, icon: icon});
    mapMarker.setMap(eMap);
}