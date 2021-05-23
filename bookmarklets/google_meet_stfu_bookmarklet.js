console.log("STFU started");
function isMicrophoneMuted() {
    return document.querySelectorAll('[jscontroller="lCGUBd"]')[0].getAttribute("data-is-muted") === "true";
}

function toggleMicrophone()  {
    document.querySelectorAll('[jscontroller="lCGUBd"]')[0].click();
}

let timeoutId;
function onSomebodySpeaking() {
    if (!isMicrophoneMuted()) {
        // Mutes the user for some time.
        console.log("Muting");
        toggleMicrophone();
        timeoutId = setTimeout(toggleMicrophone, 2000);
    } else {
        // Extends the mute time
        console.log("Extending");
        clearTimeout(timeoutId);
        timeoutId = setTimeout(toggleMicrophone, 2000);
    }
}

// Selects all the bars in Google meet.
let barArray = Array.from(document.querySelectorAll('[jscontroller="ES310d"]'));

// Removes the first one, since it's the user's.
barArray.shift();

let mutationObserver = new MutationObserver(onSomebodySpeaking);
barArray.forEach(barElement => mutationObserver.observe(barElement, { attributes: true }));