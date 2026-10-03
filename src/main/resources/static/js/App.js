document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("registration-form");
    const messageBox = document.getElementById("message-box");

    if (form) {
        form.addEventListener("submit", (e) => {
            // Prevent the browser from refreshing the page on submit
            e.preventDefault();
            
            // Gather form data and encode it for the URL (standard web format)
            const formData = new FormData(form);
            const urlEncodedData = new URLSearchParams(formData).toString();
            
            // Disable button while processing
            const submitBtn = form.querySelector('button[type="submit"]');
            submitBtn.disabled = true;
            submitBtn.textContent = "Registering...";

            // Send to our Java backend
            fetch("/api/register", {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded"
                },
                body: urlEncodedData
            })
            .then(async (response) => {
                const text = await response.text();
                messageBox.style.display = "block";
                
                if (response.ok) {
                    messageBox.textContent = "Success! " + text;
                    messageBox.className = "status-box status-success";
                    form.reset(); // Clear the form
                } else {
                    messageBox.textContent = "Error: " + text;
                    messageBox.className = "status-box";
                }
            })
            .catch((error) => {
                messageBox.style.display = "block";
                messageBox.textContent = "Network error occurred.";
                messageBox.className = "status-box";
            })
            .finally(() => {
                // Re-enable button
                submitBtn.disabled = false;
                submitBtn.textContent = "Register";
            });
        });
    }
});