/**
 * Created by Dean on 17/2/2017.
 */
$(document).on('change', 'input:radio[id^="radio_"]', function (event) {
    $('.area-limit-enabled').toggle();
});