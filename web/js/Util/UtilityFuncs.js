/**
 * Created by Dean on 17/2/2017.
 */
var CARET_PROPERTY = ' <span class="caret"></span>';
var REMOVE_BUTTON_SVG = '<td><svg class="pull-right"> <circle cx="12" cy="12" r="11" stroke="black" stroke-width="2" fill="white" /> <path stroke="black" stroke-width="4" fill="none" d="M6.25,6.25,17.75,17.75" /> <path stroke="black" stroke-width="4" fill="none" d="M6.25,17.75,17.75,6.25" /> </svg></td>';
var SITE_URL = "//localhost:8080";
var GAME_CREATOR_COMPONENTS_URL = "GameCreator/GameComponents";

$(document).on('click', 'td > svg', function() {
    $(this).parents('tr').remove();
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

