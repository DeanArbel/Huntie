/**
 * Created by I337243 on 12/04/2017.
 */

var MAX_IMG_WIDTH = 100;
var MAX_IMG_HEIGHT = 100;

function readPictureURL(input, imgId) {
    if (input.files && input.files[0]) {
        var reader = new FileReader();

        reader.onload = function (e) {
            var image = $(imgId);
            image.removeAttr("width").removeAttr("height");
            image.attr('src', e.target.result)[0].hidden = false;
            image[0].width = 100;
        };

        reader.readAsDataURL(input.files[0]);
    }
}

//
function drawImgCornersOnCanvas(imgId, canvasId) {
    tracking.Fast.THRESHOLD = 30;
    var img = document.getElementById(imgId);
    var canvas = document.getElementById(canvasId);
    canvas.width = img.width;
    canvas.height = img.height;
    var ctx = canvas.getContext('2d');
    ctx.drawImage(img, 0, 0, img.width, img.height);
    var imgData = ctx.getImageData(0, 0, canvas.width, canvas.height);
    var gray = tracking.Image.grayscale(imgData.data, canvas.width, canvas.height, true);
    var blurred4 = tracking.Image.blur(gray, canvas.width, canvas.height, 3);
    var blurred1 = new Array(blurred4.length / 4);
    for (var i = 0, j = 0; i < blurred4.length; i += 4, ++j) {
        blurred1[j] = blurred4[i];
    }
    var corners = tracking.Fast.findCorners(blurred1, canvas.width, canvas.height);
    for (i = 0; i < corners.length; i += 2) {
        ctx.fillStyle = '#0f0';
        ctx.fillRect(corners[i], corners[i + 1], 3, 3);
    }
}

function getGrayImg(img) {
    var canvas = document.createElement('canvas');
    canvas.width = img.width;
    canvas.height = img.height;
    var ctx = canvas.getContext('2d');
    ctx.drawImage(img, 0, 0, img.width, img.height);
    var imgData = ctx.getImageData(0, 0, canvas.width, canvas.height);
    var gray = tracking.Image.grayscale(imgData.data, canvas.width, canvas.height, true);
    var blurred4 = tracking.Image.blur(gray, canvas.width, canvas.height, 3);
    var blurred1 = new Array(blurred4.length / 4);
    for (var i = 0, j = 0; i < blurred4.length; i += 4, ++j) {
        blurred1[j] = blurred4[i];
    }
    return blurred1;
}

function pictureComparison(img1, img2, imgCmpBase) {
    tracking.Brief.N = 512;
    tracking.Fast.THRESHOLD = 30;
    var grayImg1 = getGrayImg(img1);
    var grayImg2 = getGrayImg(img2);
    var corners1 = tracking.Fast.findCorners(grayImg1, img1.width, img1.height);
    var corners2 = tracking.Fast.findCorners(grayImg2, img2.width, img2.height);
    var descriptors1 = tracking.Brief.getDescriptors(grayImg1, img1.width, corners1);
    var descriptors2 = tracking.Brief.getDescriptors(grayImg2, img2.width, corners2);
    var matches = tracking.Brief.reciprocalMatch(corners1, descriptors1, corners2, descriptors2);
    var smallestCornersCnt = corners1.length > corners2.length ? corners2.length / 2 : corners1.length / 2;
    console.log(matches.length / smallestCornersCnt);
    var cntr = 0;
    for (var i = 0; i < matches.length; i++) {
        if (matches[i].confidence > 0.7) {
            cntr++;
        }
    }
    var found = matches.length / smallestCornersCnt > imgCmpBase && cntr > matches.length / (0.5 / imgCmpBase);
    console.log(found ? "Match found" : "Match not found");
    return found;
}