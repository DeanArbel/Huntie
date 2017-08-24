/**
 * Created by Dean on 28/02/2017.
 */
var mGameEndedMessage;
var mGameName;
var mPlayerMessage;
var mGameTimeMessage;
var mPlayerWonMessage;
var mTeamTable;
var mOthersTable;
var mRiddleTable;
var mClickedRow;
var mChosenRiddleIdx;
var mGameCode;
var mIsGameActive;
var mHasGameStarted;
var mSolveRiddleBtn;
var eMap, infoWindow;
var playerPos;
var RADIUS_LEN = 50;

$(function () {
    sessionStorage.setItem("PrevPage", "GameLobby");
    initGlobalVars();
    initPageElementsFromServer();
});

$(document).on("click", "#riddle-table > tbody > tr", function(clickedEvent) {
    if (mClickedRow) {
        mClickedRow.classList.remove("active");
    }
    mClickedRow = clickedEvent.currentTarget;
    mClickedRow.classList.add("active");
    mChosenRiddleIdx = clickedEvent.currentTarget.rowIndex - 1;
    mSolveRiddleBtn.disabled = false;
});

$(document).on("click", "#prevPage-btn", function() {
    window.location.href =   "/Home.html";
});

$(document).on("click", "#solveRiddle-btn", function() {
   window.location.href =   "/Player/Riddle.html?gameCode=" + mGameCode + "&riddle=" + mChosenRiddleIdx;
});

function initGlobalVars() {
    mGameEndedMessage = $("#game-ended-message");
    mGameName = $('#game-name');
    mPlayerMessage = $("#player-message");
    mGameTimeMessage = $('#game-start-time');
    mPlayerWonMessage = $('#player-message-won');
    mTeamTable = $('#table-my-score');
    mOthersTable = $('#table-others-score');
    mRiddleTable = $('#riddle-table > tbody');
    mGameCode = getParameterByName("gameCode");
    mSolveRiddleBtn = $("#solveRiddle-btn")[0];
}

function initPageElementsFromServer() {
    $.ajax({
        url: GAME_LOBBY_URL,
        type: 'GET',
        data: {request: "getGameInfo", gameCode: mGameCode},
        success: function(gameData) {
            var now = new Date();
            var startTime = new Date(gameData.startTime);
            var endTime = new Date(gameData.endTime);
            mIsGameActive = endTime >= now;
            mHasGameStarted = startTime <= now;
            mGameName[0].innerText = gameData.gameName;
            if (mHasGameStarted) {
                mGameTimeMessage.hide();
            } else {
                mGameTimeMessage[0].innerText = "Start Time: " + formatDate(startTime);
            }
            $(".loading-area").hide();
            $(".lobby-container").show();
            if (gameData.playerHasWon) {
                mPlayerWonMessage.show();
            }
            if (mIsGameActive && mHasGameStarted) {
                initRiddleTable(gameData.riddlesNameAndLocations);
            }
            else if (!mHasGameStarted) {
                mPlayerMessage[0].innerText = "The Game hasn't started yet, please come again later";
            }
            else {
                mGameEndedMessage.show();
            }
            getNonCrucialPageElementsFromServer();
        },
        error: function(err) {
            mPlayerMessage[0].innerText = err;
        }
    });
}

function getNonCrucialPageElementsFromServer() {
    $.ajax({
        url: GAME_LOBBY_URL,
        type: 'GET',
        data: {request: "getPlayerTables", gameCode: mGameCode},
        success: function(gameData) {
            if (gameData.isTeamGame) {
                $('.score-content').show();
                $('#message-my-team-score')[0].innerHTML = gameData.myTeamName + " Scores";
                mPlayerMessage[0].innerText = "Level " + gameData.myTeamLevel;
                initTeamTable(gameData.myTeamScore);
                initOthersTable(gameData.otherTeamsScore);
            }
        }
    });
}

function initRiddleTable(riddlesNameAndLocations) {
    if ({} !== riddlesNameAndLocations) {
        $('.riddle-content').show();
        mRiddleTable.show();
        for (var name in riddlesNameAndLocations) {
            if (riddlesNameAndLocations[name] !== "") {
                var positionArr = stringToLatLng(riddlesNameAndLocations[name]);
                addIconToMap(name, positionArr[0], positionArr[1]);
                if (isPlayerInAreaRadius(positionArr[0], positionArr[1])) {
                    addItemToRiddleTable(name);
                }
            } else {        // If riddle has no position
                addItemToRiddleTable(name);
            }
        }
    }
}

function addItemToRiddleTable(name) {
    var $eRow = $('<tr>');
    $eRow.append('<td>' + name + '</td>');
    mRiddleTable.append($eRow);
}

function initTeamTable(teamScores) {
    var tableBody = $('#table-my-score > tbody');
    mTeamTable.show();
    initTeamsTable(teamScores, tableBody);
}

function initOthersTable(otherTeamsScore) {
    var tableBody = $('#table-others-score > tbody');
    mOthersTable.show();
    initTeamsTable(otherTeamsScore, tableBody);
}

function initTeamsTable(teamMap, tableBody) {
    for (var name in teamMap) {
        var $eRow = $('<tr>');
        $eRow.append('<td>' + name + '</td>');
        $eRow.append('<td>' + teamMap[name] + '</td>');
        tableBody.append($eRow);
    }
}

function initMap() {
    var map_container = $('#map');
    var mapWidth = map_container.width();
    var mapHeight = mapWidth < 500 ? 250 : 500;
    map_container.height(mapHeight);
    eMap = new google.maps.Map(map_container[0], {
        center: {lat: 32.109333, lng: 34.855499},
        zoom: 16,
        disableDefaultUI: true
    });
    infoWindow = new google.maps.InfoWindow;
    getLocation();
}

function showPosition(position) {
    playerPos = {
        lat: position.coords.latitude,
        lng: position.coords.longitude
    };
    infoWindow.setPosition(playerPos);
    infoWindow.setContent('You Are Here');
    infoWindow.open(eMap);
    var icon = {
        url: "/assets/images/locationCircle.png", // url
        scaledSize: new google.maps.Size(20, 20), // scaled size
        origin: new google.maps.Point(0,0), // origin
        anchor: new google.maps.Point(10, 10) // anchor
    };
    var marker = new google.maps.Marker({position:playerPos, icon: icon});
    marker.setMap(eMap);
    eMap.setCenter(playerPos);
}

function addIconToMap(name, lat, lng) {
    var iconPos = new google.maps.LatLng(lat, lng);
    var circle = new google.maps.Circle({
        center:iconPos,
        radius:RADIUS_LEN,
        strokeColor:"#0000FF",
        strokeOpacity:0.8,
        strokeWeight:2,
        fillColor:"#0000FF",
        fillOpacity:0.2
    });
    circle.setMap(eMap);
}

function isPlayerInAreaRadius(lat, lng) {
    if (!playerPos) getLocation();
    return playerPos && isLocationWithinDistanceFromOtherLocation(playerPos.lat, playerPos.lng, lat, lng, RADIUS_LEN);
}