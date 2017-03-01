/**
 * Created by Dean on 28/02/2017.
 */
var clickedRow;

$(function () {
    // $(".loader").hide();
    // $(".settings-container").show();
    // sessionStorage.setItem("PrevPage", "GameSettings");
    // initGlobalVars();
    // updateDropdownValue(TREASURE_CHEST);
});


var chosenRiddleIdx;
$(document).on("click", ".table > tbody > tr", function(clickedEvent) {
    if (clickedRow) {
        clickedRow.classList.remove("active");
    }
    clickedRow = clickedEvent.currentTarget;
    clickedRow.classList.add("active");
    chosenRiddleIdx = clickedEvent.currentTarget.rowIndex - 1;
});