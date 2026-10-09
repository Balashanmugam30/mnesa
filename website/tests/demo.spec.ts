import { test, expect } from "@playwright/test";

test.describe("MNESA Web Platform Interactive Features", () => {
  test("Interactive demo simulation runs and renders extracted opportunity card", async ({ page }) => {
    await page.goto("/demo");

    // Check heading
    await expect(page.getByRole("heading", { level: 1 })).toContainText("Try MNESA Live in Your Browser");

    // Click on preset for Global Collegiate AI Hackathon
    await page.click("button:has-text('Global Collegiate AI Hackathon')");

    // Verify textarea updated with hackathon content
    const textarea = page.locator("textarea#demo-raw-text");
    await expect(textarea).toHaveValue(/Global Collegiate AI Hackathon/);

    // Trigger simulation
    const simulateButton = page.locator("button:has-text('Simulate MNESA Extraction')");
    await simulateButton.click();

    // Verify pipeline progress or result card appearance
    await expect(page.locator("h2:has-text('Global Collegiate AI Hackathon 2026')")).toBeVisible({ timeout: 5000 });

    // Verify confidence score and evidence citations
    await expect(page.getByText("94% Confidence")).toBeVisible();
    await expect(page.getByText("Verbatim Extracted Evidence Quotes")).toBeVisible();
    await expect(page.getByText("Automated Application Checklist")).toBeVisible();
  });

  test("Contact page form validation and successful submission", async ({ page }) => {
    await page.goto("/contact");

    // Try submitting empty form
    const submitButton = page.locator("button[type='submit']");
    await submitButton.click();

    // Fill form
    await page.fill("#contact-name", "Test Engineer");
    await page.fill("#contact-email", "engineer@example.com");
    await page.selectOption("#contact-category", "FEEDBACK");
    await page.fill("#contact-message", "MNESA Android share target is exceptionally fast. Great work.");

    // Submit filled form
    await submitButton.click();

    // Verify success confirmation
    await expect(page.getByText("Message Sent Successfully")).toBeVisible({ timeout: 5000 });
    await expect(page.getByText("engineer@example.com")).toBeVisible();
  });
});
