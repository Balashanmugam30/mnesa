import { test, expect } from "@playwright/test";

test.describe("MNESA Web Platform Page Route Tests", () => {
  const routes = [
    { path: "/how-it-works", heading: "How MNESA Works" },
    { path: "/features", heading: "Complete Feature Toolkit" },
    { path: "/ai", heading: "AI Intelligence & Verifiable Evidence" },
    { path: "/demo", heading: "Try MNESA Live in Your Browser" },
    { path: "/security", heading: "Security & Privacy Architecture" },
    { path: "/pricing", heading: "Simple, Transparent Access" },
    { path: "/faq", heading: "Frequently Asked Questions" },
    { path: "/download", heading: "Get MNESA for Android" },
    { path: "/about", heading: "Bridging Discovery to Execution" },
    { path: "/contact", heading: "Contact MNESA Team" },
    { path: "/privacy", heading: "MNESA Privacy Policy" },
    { path: "/terms", heading: "MNESA Terms of Service" },
    { path: "/data-deletion", heading: "Account & Data Deletion" },
    { path: "/maintenance", heading: "System Maintenance in Progress" },
    { path: "/offline", heading: "You Are Viewing MNESA Offline Architecture" },
    { path: "/unavailable", heading: "Service Temporarily Unavailable" },
  ];

  for (const { path, heading } of routes) {
    test(`Route ${path} loads successfully with correct heading and landmarks`, async ({ page }) => {
      const response = await page.goto(path);
      expect(response?.ok()).toBeTruthy();

      await expect(page.locator("header[role='banner']")).toBeVisible();
      await expect(page.locator("main[role='main']")).toBeVisible();
      await expect(page.locator("footer[role='contentinfo']")).toBeVisible();

      await expect(page.getByRole("heading", { level: 1 })).toContainText(heading);
    });
  }

  test("Mobile viewport renders responsive navigation menu", async ({ page }) => {
    // Set viewport to mobile phone (375x667)
    await page.setViewportSize({ width: 375, height: 667 });
    await page.goto("/");

    // Mobile menu toggle button should be visible
    const menuButton = page.locator("button[aria-label='Toggle navigation menu']");
    await expect(menuButton).toBeVisible();

    // Open mobile menu
    await menuButton.click();
    await expect(page.locator("nav[aria-label='Mobile Navigation']")).toBeVisible();

    // Verify key links in mobile navigation
    await expect(page.locator("nav[aria-label='Mobile Navigation'] >> text=How It Works")).toBeVisible();
    await expect(page.locator("nav[aria-label='Mobile Navigation'] >> text=Features")).toBeVisible();
    await expect(page.locator("nav[aria-label='Mobile Navigation'] >> text=Live Demo")).toBeVisible();

    // Close mobile menu
    await menuButton.click();
    await expect(page.locator("nav[aria-label='Mobile Navigation']")).not.toBeVisible();
  });
});
