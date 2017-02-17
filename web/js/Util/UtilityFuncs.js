/**
 * Created by Dean on 17/2/2017.
 */
$(document).on('click', 'svg', function() {
    $(this).parents('tr').remove();
});

$(document).on('click', '.dropdown-selection', function() {
    var value = $(this)[0].innerText,
        dropdownBtn = $('.dropdown-btn')[0];
    if (value !== dropdownBtn.innerText) {
        dropdownBtn.innerText = $(this)[0].innerText;
        dropdownChange(dropdownBtn.innerText);
    }
});