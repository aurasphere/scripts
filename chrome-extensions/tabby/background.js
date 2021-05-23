chrome.tabs.onUpdated.addListener(function (tabId, changeInfo, tab) {
    console.log(`The tab with ID ${tabId} has been changed!`);
});

chrome.tabs.onCreated.addListener(function (tab) {
    console.log("New tab has been created!");
});

chrome.runtime.onInstalled.addListener(function() {
    console.log("Extension installed!");
})