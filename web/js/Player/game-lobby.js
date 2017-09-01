/**
 * Created by Dean on 28/02/2017.
 */
var RADIUS_LEN_IN_KM = 0.05;

var mGameEndedMessage;
var mGameName;
var mRiddlesIds;
var mPlayerMessage;
var mGameTimeMessage;
var mTreasureContainer;
var mTeamTable;
var mOthersTable;
var mRiddleTable;
var mClickedRow;
var mChosenRiddleId;
var mGameCode;
var mIsGameActive;
var mHasGameStarted;
var mSolveRiddleBtn;
var eMap, mInfoWindow;
var playerPos;
var mapInit;
var mapMarker;
var mIsTreasureLevel;
var mTreasureType;
var mAsyncRiddlesLocationToUpdate = [];

$(function () {
    sessionStorage.setItem("PrevPage", "GameLobby");
    $(".lobby-container").hide();
    $(".riddle-content").hide();
    initGlobalVars();
    initPageElementsFromServer();
});

$(document).on("click", "#riddle-table > tbody > tr", function(clickedEvent) {
    if (mClickedRow) {
        mClickedRow.classList.remove("active");
    }
    mClickedRow = clickedEvent.currentTarget;
    mClickedRow.classList.add("active");
    mChosenRiddleId = mRiddlesIds[clickedEvent.currentTarget.rowIndex - 1];
    mSolveRiddleBtn.disabled = false;
});

$(document).on("click", "#prevPage-btn", function() {
    window.location.href =   "/home.html";
});

$(document).on("click", "#solveRiddle-btn", function() {
   $("#solveRiddle-btn")[0].disabled = true;
    if (!mIsTreasureLevel) {
       window.location.href = "/Player/riddle.html?gameCode=" + mGameCode + "&riddle=" + mChosenRiddleId;
   } else {
       $.ajax({
           url: RIDDLE_URL,
           type: 'POST',
           data: {gameCode: mGameCode, token: sessionStorage.getItem("access token")},
           success: showTreasure(),
           error: function(err) {
               alert("Server has encountered an error, please try again");
               window.location.reload();
           }
       });
   }
});

function initGlobalVars() {
    mGameEndedMessage = $("#game-ended-message");
    mGameName = $('#game-name');
    mPlayerMessage = $("#player-message");
    mGameTimeMessage = $('#game-start-time');
    mTreasureContainer = $('.treasure-container');
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
        data: {request: "getGameInfo", gameCode: mGameCode, token: sessionStorage.getItem("access token")},
        success: function(gameData) {
            var now = new Date();
            var startTime = new Date(gameData.startTime);
            var endTime = new Date(gameData.endTime);
            mIsTreasureLevel = gameData.isTreasureLevel;
            if (mIsTreasureLevel) {
                mSolveRiddleBtn.innerText = "Look For Treasure";
            }
            mTreasureType = gameData.treasureType;
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
                showTreasure();
            } else {
                if (mIsGameActive && mHasGameStarted) {
                    mPlayerMessage[0].innerText = "Level " + gameData.myLevel;
                    initRiddleTable(gameData.riddlesNamesAndLocations, gameData.riddlesNamesAndIds);
                    getNonCrucialPageElementsFromServer();
                    setInterval(getNonCrucialPageElementsFromServer, 15000);
                }
                else if (!mHasGameStarted) {
                    mPlayerMessage[0].innerText = "The Game hasn't started yet, please come again later";
                }
                else {
                    mGameEndedMessage.show();
                }
            }
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
        data: {request: "getPlayerTables", gameCode: mGameCode, token: sessionStorage.getItem("access token")},
        success: function(gameData) {
            if (gameData.isTeamGame) {
                $('.score-content').show();
                $('#message-my-team-score')[0].innerHTML = gameData.myTeamName + " Scores";
                initTeamTable(gameData.myTeamScore);
                initOthersTable(gameData.otherTeamsScore);
            }
        }
    });
}

function initRiddleTable(riddlesNameAndLocations, riddleNamesAndIds) {
    if ({} !== riddlesNameAndLocations) {
        $('.riddle-content').show();
        mRiddleTable.show();
        mRiddlesIds = [];
        for (var name in riddlesNameAndLocations) {
            if (riddlesNameAndLocations[name] !== "") {
                var positionArr = stringToLatLng(riddlesNameAndLocations[name]);
                addIconToMap(name, positionArr[0], positionArr[1]);
                if (!playerPos) {
                    mAsyncRiddlesLocationToUpdate.push({location: positionArr, name: name, riddle: riddleNamesAndIds[name]});
                } else {
                    if (isPlayerInAreaRadius(positionArr[0], positionArr[1])) {
                        addItemToRiddleTable(name, riddleNamesAndIds[name]);
                    }
                }
            } else {        // If riddle has no position
                addItemToRiddleTable(name, riddleNamesAndIds[name]);
            }
        }
    }
}

function addItemToRiddleTable(name, id) {
    var $eRow = $('<tr>');
    $eRow.append('<td>' + name + '</td>');
    mRiddleTable.append($eRow);
    mRiddlesIds.push(id);
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
    tableBody.empty();
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
    mInfoWindow = new google.maps.InfoWindow;
    getLocation();
    setInterval( getLocation, 5000);
}

function showPosition(position) {
    playerPos = {
        lat: position.coords.latitude,
        lng: position.coords.longitude
    };
    if (!mapInit) {
        mapInit = true;
        mInfoWindow.setPosition(playerPos);
        mInfoWindow.setContent('You Are Here');
        mInfoWindow.open(eMap);
        var icon = {
            url: "/assets/images/locationCircle.png", // url
            scaledSize: new google.maps.Size(20, 20), // scaled size
            origin: new google.maps.Point(0, 0), // origin
            anchor: new google.maps.Point(10, 10) // anchor
        };
        eMap.setCenter(playerPos);
        mapMarker = new google.maps.Marker({position: playerPos, icon: icon});
        mapMarker.setMap(eMap);
        for (var i = 0; i < mAsyncRiddlesLocationToUpdate.length; i++) {
            var riddleObj = mAsyncRiddlesLocationToUpdate[i];
            if (isPlayerInAreaRadius(riddleObj.location[0], riddleObj.location[1])) {
                addItemToRiddleTable(riddleObj.name, riddleObj.riddle);
            }
        }
        mAsyncRiddlesLocationToUpdate = [];
    }
    mapMarker.setPosition(playerPos);
}

function addIconToMap(name, lat, lng) {
    var iconPos = new google.maps.LatLng(lat, lng);
    var circle = new google.maps.Circle({
        center:iconPos,
        radius:RADIUS_LEN_IN_KM * 1000,
        strokeColor:"#0000FF",
        strokeOpacity:0.8,
        strokeWeight:2,
        fillColor:"#0000FF",
        fillOpacity:0.2
    });
    circle.setMap(eMap);
}

function isPlayerInAreaRadius(lat, lng) {
    return playerPos && isLocationWithinDistanceFromOtherLocation(playerPos.lat, playerPos.lng, lat, lng, RADIUS_LEN_IN_KM);
}

function showTreasure() {
    var treasureImg = $("#treasure-img");
    if (mTreasureType === "Treasure Chest") {
        treasureImg[0].src = "/assets/gifs/animated-treasure-chest.gif";
    } else if (mTreasureType === "Trophy") {
        treasureImg[0].src = "/assets/gifs/animated-trophy.gif";
    }
    $(".container").hide();
    $(".navbar-container").show();
    mTreasureContainer.show();
}