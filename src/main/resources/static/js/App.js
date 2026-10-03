document.addEventListener("DOMContentLoaded", () => {
    const statusBox = document.getElementById("js-status");
    if (statusBox) {
        statusBox.textContent = "JavaScript is successfully connected and initialized!";
        statusBox.classList.add("status-success");
    }
    console.log("Application foundation ready.");
});