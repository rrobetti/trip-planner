package com.tripplanner.fetch;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Fetches web pages using a headless Chrome browser so that JavaScript-rendered
 * content (such as flight/hotel prices on Skyscanner and Booking.com) is visible
 * in the DOM before we attempt to extract it.
 *
 * <p>One {@code BrowserFetcher} instance holds a single ChromeDriver session;
 * call {@link #close()} when done to release the browser process.</p>
 */
public class BrowserFetcher implements AutoCloseable {

    private static final int DEFAULT_TIMEOUT_SEC = 30;

    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
            + "AppleWebKit/537.36 (KHTML, like Gecko) "
            + "Chrome/120.0.0.0 Safari/537.36";

    private final WebDriver driver;

    public BrowserFetcher() {
        // Suppress CDP version-mismatch warnings emitted when the installed Chrome
        // version is newer than the devtools artifacts bundled with selenium-java.
        // Our code uses only standard W3C WebDriver commands and does not require CDP.
        Logger.getLogger("org.openqa.selenium.devtools.CdpVersionFinder").setLevel(Level.SEVERE);
        Logger.getLogger("org.openqa.selenium.chromium.ChromiumDriver").setLevel(Level.SEVERE);

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--user-agent=" + USER_AGENT);
        this.driver = new ChromeDriver(options);
    }

    /**
     * Loads {@code url} in the headless browser, waits up to
     * {@value #DEFAULT_TIMEOUT_SEC} seconds for at least one of the
     * {@code waitForSelectors} to appear, then returns the text of the first
     * element matched by any of the {@code priceSelectors}.
     *
     * @param url              page to load
     * @param waitForSelectors CSS selectors that signal the results have loaded
     * @param priceSelectors   CSS selectors from which to extract the price text
     * @return price text, or {@code null} if not found
     */
    public String fetchPrice(String url, String[] waitForSelectors, String[] priceSelectors) {
        driver.get(url);

        // Wait for the page JavaScript to finish initial rendering
        waitForPageLoad();

        // Additionally wait for one of the expected result selectors to appear
        waitForAnySelector(waitForSelectors, DEFAULT_TIMEOUT_SEC);

        // Try to extract a price from the given selectors
        for (String selector : priceSelectors) {
            List<WebElement> elements = driver.findElements(By.cssSelector(selector));
            for (WebElement el : elements) {
                String text = el.getText().trim();
                if (isPriceText(text)) {
                    return text;
                }
            }
        }
        return null;
    }

    /**
     * Loads {@code url} and, if {@code hotelName} is non-empty, looks for a
     * property card whose text contains that name and returns the price shown
     * in that card.  Falls back to the cheapest price visible on the page.
     *
     * @param url             Booking.com search results URL
     * @param hotelName       optional hotel name to match (may be {@code null})
     * @param cardSelector    CSS selector for individual property cards
     * @param priceSelectors  CSS selectors for price elements (within a card or page)
     * @return price text, or {@code null} if not found
     */
    public String fetchBookingPrice(String url, String hotelName,
                                    String cardSelector, String[] priceSelectors) {
        driver.get(url);
        waitForPageLoad();
        waitForSelector(cardSelector, DEFAULT_TIMEOUT_SEC);

        if (hotelName != null && !hotelName.isEmpty()) {
            String lowerName = hotelName.toLowerCase();
            List<WebElement> cards = driver.findElements(By.cssSelector(cardSelector));
            for (WebElement card : cards) {
                if (card.getText().toLowerCase().contains(lowerName)) {
                    for (String selector : priceSelectors) {
                        List<WebElement> priceEls = card.findElements(By.cssSelector(selector));
                        for (WebElement priceEl : priceEls) {
                            String text = priceEl.getText().trim();
                            if (isPriceText(text)) {
                                return text;
                            }
                        }
                    }
                }
            }
        }

        // Fallback: first price on the page
        for (String selector : priceSelectors) {
            List<WebElement> elements = driver.findElements(By.cssSelector(selector));
            for (WebElement el : elements) {
                String text = el.getText().trim();
                if (isPriceText(text)) {
                    return text;
                }
            }
        }
        return null;
    }

    // -----------------------------------------------------------------------

    private void waitForPageLoad() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT_SEC))
                    .until(d -> "complete".equals(
                            ((JavascriptExecutor) d).executeScript("return document.readyState")));
        } catch (Exception ignored) {
            // proceed even if timed out
        }
    }

    private void waitForSelector(String selector, int timeoutSec) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(timeoutSec))
                    .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(selector)));
        } catch (Exception ignored) {
            // proceed even if selector never appeared
        }
    }

    private void waitForAnySelector(String[] selectors, int timeoutSec) {
        for (String selector : selectors) {
            try {
                new WebDriverWait(driver, Duration.ofSeconds(timeoutSec))
                        .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(selector)));
                return; // found one – stop waiting
            } catch (Exception ignored) {
                // try next selector
            }
        }
    }

    private boolean isPriceText(String text) {
        if (text == null || text.isEmpty()) return false;
        return text.contains("€") || text.contains("$") || text.contains("£")
                || text.matches(".*\\d+.*");
    }

    @Override
    public void close() {
        try {
            driver.quit();
        } catch (Exception ignored) {
        }
    }
}
