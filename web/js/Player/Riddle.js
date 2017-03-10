/**
 * Created by Dean on 03/03/2017.
 */
var mGameCode;
var mRiddleCode;
var mRiddleName;
var mQuestionBody;
var mAnswerBox;
var mSubmitBtn;

$(function () {
    sessionStorage.setItem("PrevPage", "Riddle");
    document.getElementById('riddle-answer').oninput = onAnswerInput;
    initGlobalVars();
    initPageElementsFromServer();
});

$(document).on("click", "#prevPage-btn", function() {
    goBackToLobby();
});

function initGlobalVars() {
    mGameCode = getParameterByName("gameCode");
    mRiddleCode = getParameterByName("riddle");
    mRiddleName = $('#riddle-name');
    mQuestionBody = $('#riddle-question-body');
    mAnswerBox = $('#riddle-answer')[0];
    mSubmitBtn = $("#riddle-submit-btn")[0];
}

function initPageElementsFromServer() {
    $.ajax({
        url: RIDDLE_URL,
        type: 'GET',
        data: {gameCode: mGameCode, riddleCode: mRiddleCode},
        success: function(gameData) {
            var optImg = new Image();
            optImg.src = gameData.optionalImage;
            mRiddleName[0].innerText = gameData.name;
            mQuestionBody[0].innerText = gameData.question;
            if (optImg.src) {
                mQuestionBody.append('<br>');
                mQuestionBody.append(optImg);
            }
            $(".loading-area").hide();
            $(".container").show();
        },
        error: function(err) {
            alert(err);
        }
    });
}

function onAnswerInput() {
    mSubmitBtn.disabled = mAnswerBox.value === "";
}

function submitAnswer() {
    mSubmitBtn.disabled = true;
    $.ajax({
        url: RIDDLE_URL,
        type: 'POST',
        data: {gameCode: mGameCode, riddleCode: mRiddleCode, answer: mAnswerBox.value},
        success: function(data) {
            if (data) {
                confirm("You got to answer right!");
                mSubmitBtn.disabled = true;
                mAnswerBox.disabled = true;
                goBackToLobby();
            }
            else {
                confirm("Nice try, but wrong answer!");
                mSubmitBtn.disabled = false;
            }
        },
        error: function(err) {
            alert("Server has encountered an error, please try again");
            window.location.reload();
        }
    });
}

function goBackToLobby() {
    window.location.href = SITE_URL + "/Player/GameLobby.html?gameCode=" + mGameCode;
}