/**
 * Created by Dean on 17/2/2017.
 */
var CARET_PROPERTY = '<span class="caret"></span>';
var REMOVE_BUTTON_SVG = '<td><svg class="pull-right"> <circle cx="12" cy="12" r="11" stroke="black" stroke-width="2" fill="white" /> <path stroke="black" stroke-width="4" fill="none" d="M6.25,6.25,17.75,17.75" /> <path stroke="black" stroke-width="4" fill="none" d="M6.25,17.75,17.75,6.25" /> </svg></td>';
var SITE_URL = "//localhost:8080";
var GAME_CREATOR_COMPONENTS_URL = "GameCreator/GameComponents";
var FIND_GAME_URL = "FindGame";
var GAME_ENTRY_URL = "GameEntry";
var GAME_LOBBY_URL = "GameLobby";
var RIDDLE_URL = "Riddle";
var PROFILE_URL = "User/Profile";
var maxImageRatioSize = 300;

$(document).on('click', 'td > svg', function() {
    $(this).parent().parent().remove();
    // This function can be implemented in specific script
    rowWasRemoved(this);
});

$(document).on('click', '.dropdown-selection', function() {
    updateDropdownValue($(this)[0].innerText);
});

function updateDropdownValue(value) {
    var dropdownBtn = $('.dropdown-btn');
    if (value !== dropdownBtn[0].innerText) {
        dropdownBtn[0].innerText = value;
        dropdownBtn.append(CARET_PROPERTY);
        dropdownChange(value);
    }
}

function getParameterByName(name, url) {
    if (!url) {
        url = window.location.href;
    }
    name = name.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function formatDate(date, monthName) {
    var monthNames = [
        "January", "February", "March",
        "April", "May", "June", "July",
        "August", "September", "October",
        "November", "December"
    ];

    var day = date.getDate();
    var monthIndex = date.getMonth();
    var year = date.getFullYear();
    var time = date.getHours() + ':' + ("0" + date.getMinutes()).slice(-2);
    var month = monthName ? monthNames[monthIndex] : monthIndex + 1;

    return day + '/' + month + '/' + year + ' at ' + time;
}

function getBase64Image(imgElem) {
    var canvas = document.createElement("canvas");
    var imgWidth = (imgElem.clientWidth / imgElem.clientHeight) * maxImageRatioSize;
    canvas.width = imgElem.clientWidth < imgWidth ? imgElem.clientWidth : imgWidth;
    canvas.height = imgElem.clientHeight < maxImageRatioSize ? imgElem.clientHeight : maxImageRatioSize;
    var ctx = canvas.getContext("2d");
    ctx.drawImage(imgElem, 0, 0, canvas.width, canvas.height);
    var dataURL = canvas.toDataURL("image/png");
    dataURL = dataURL.replace(new RegExp("\\+", "g"), "%2B");
    return dataURL !== 'data:,' ? dataURL : "";
    // return dataURL.replace(/^data:image\/(png|jpg);base64,/, "");
}

function tryAPIGeolocation() {
    jQuery.post( "https://www.googleapis.com/geolocation/v1/geolocate?key=AIzaSyAsfLflI-UcGro_hBwjIQyIFVndLphZjOE", function(success) {
        showPosition({coords: {latitude: success.location.lat, longitude: success.location.lng}});
    })
        .fail(function(err) {
            //alert("API Geolocation error! \n\n"+err);
        });
}

function browserGeolocationFail(error) {
    switch (error.code) {
        case error.TIMEOUT:
            //alert("Browser geolocation error !\n\nTimeout.");
            break;
        case error.PERMISSION_DENIED:
            if(error.message.indexOf("Only secure origins are allowed") == 0) {
                tryAPIGeolocation();
            }
            break;
        case error.POSITION_UNAVAILABLE:
            alert("Browser geolocation error !\n\nPosition unavailable.");
            break;
    }
}

function getLocation() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
            showPosition,
            browserGeolocationFail,
            {maximumAge: 50000, timeout: 20000, enableHighAccuracy: true});
    }
}

function stringToLatLng(pos) {
    var latLngStrArr = pos.split(/[ ,]+/);
    return [parseFloat(latLngStrArr[0]), parseFloat(latLngStrArr[1])];
}

function getDistanceFromLatLonInKm(lat1,lon1,lat2,lon2) {
    var R = 6371; // Radius of the earth in km
    var dLat = deg2rad(lat2-lat1);  // deg2rad below
    var dLon = deg2rad(lon2-lon1);
    var a =
            Math.sin(dLat/2) * Math.sin(dLat/2) +
            Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
            Math.sin(dLon/2) * Math.sin(dLon/2)
        ;
    var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    var d = R * c; // Distance in km
    return d;
}

function deg2rad(deg) {
    return deg * (Math.PI/180)
}

function isLocationWithinDistanceFromOtherLocation(lat1, lon1, lat2, lon2, maxDistance) {
    return maxDistance >= getDistanceFromLatLonInKm(lat1, lon1, lat2, lon2);
}

document.onload(function () {
    var token = sessionStorage.getItem("access token");
    if(token !== null) {
        $.ajax({
            url: "TokenVerification",
            type: 'POST',
            data: {token: token},
            fail: function () {
                window.location.href = "/login.html";
            }
        })
    }
    else{
        window.location.href = "/login.html";
    }
});