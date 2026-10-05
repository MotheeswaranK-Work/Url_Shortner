const urlInput =
    document.getElementById("urlInput");

const shortenButton =
    document.getElementById("shortenButton");

const resultCard =
    document.getElementById("resultCard");

const shortUrlElement =
    document.getElementById("shortUrl");

const errorMessage =
    document.getElementById("errorMessage");

const copyMessage =
    document.getElementById("copyMessage");


async function shortenUrl() {

    const url =
        urlInput.value.trim();


    // Clear previous messages

    errorMessage.textContent = "";

    copyMessage.textContent = "";

    resultCard.style.display = "none";


    // Validate empty input

    if (!url) {

        errorMessage.textContent =
            "Please enter a URL.";

        return;
    }


    // Validate URL

    try {

        new URL(url);

    } catch {

        errorMessage.textContent =
            "Please enter a valid URL.";

        return;
    }


    // Loading state

    shortenButton.disabled = true;

    shortenButton.textContent =
        "Shortening...";


    try {

        const response = await fetch(
            "/api/urls/shorten",
            {
                method: "POST",

                headers: {
                    "Content-Type":
                        "application/json"
                },

                body: JSON.stringify({
                    originalUrl: url
                })
            }
        );


        if (!response.ok) {

            throw new Error(
                "Request failed"
            );
        }


        const data =
            await response.json();


        /*
         * Backend response:
         *
         * {
         *   shortUrl: "http://localhost:8080/r/aB72xK"
         * }
         */


        shortUrlElement.textContent =
            data.shortUrl;

        shortUrlElement.href =
            data.shortUrl;


        resultCard.style.display =
            "block";


    } catch (error) {

        console.error(error);

        errorMessage.textContent =
            "Unable to shorten the URL. Please check that the backend is running.";

    } finally {

        shortenButton.disabled = false;

        shortenButton.textContent =
            "Shorten URL";
    }
}


/*
 * Copy short URL
 */

async function copyUrl() {

    const url =
        shortUrlElement.textContent;


    if (!url) {
        return;
    }


    try {

        await navigator.clipboard.writeText(url);

        copyMessage.textContent =
            "✓ Copied to clipboard";


    } catch {

        copyMessage.textContent =
            "Unable to copy URL.";

    }
}


/*
 * Allow pressing Enter
 */

urlInput.addEventListener(
    "keydown",
    function(event) {

        if (event.key === "Enter") {

            shortenUrl();

        }

    }
);