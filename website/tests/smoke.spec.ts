import { test, expect } from "@playwright/test";

test.describe("MNESA Web Platform Smoke Tests", () => {
  test("Health check endpoint returns status UP", async ({ request }) => {
    const response = await request.get("/api/health");
    expect(response.ok()).toBeTruthy();
    const data = await response.json();
    expect(data.status).toBe("UP");
    expect(data.service).toBe("mnesa-website");
  });

  test("Home page renders title and accessible semantic landmarks", async ({ page }) => {
    const response = await page.goto("/");
    expect(response?.ok()).toBeTruthy();

    // Verify Security Headers
    const headers = response?.headers() || {};
    expect(headers["x-content-type-options"]).toBe("nosniff");
    expect(headers["x-frame-options"]).toBe("DENY");

    // Verify Title
    await expect(page).toHaveTitle(/MNESA — Capture it. We'll remember./);

    // Verify Skip Link
    const skipLink = page.locator("a:has-text('Skip to main content')");
    await expect(skipLink).toHaveAttribute("href", "#main-content");

    // Verify Main Landmarks
    await expect(page.locator("header[role='banner']")).toBeVisible();
    await expect(page.locator("main[role='main']")).toBeVisible();
    await expect(page.locator("footer[role='contentinfo']")).toBeVisible();

    // Verify Brand Heading
    await expect(page.getByRole("heading", { level: 1 })).toContainText("Capture it. We'll remember.");

    // Verify Seven-step section
    await expect(page.getByText("The Seven-Step Transformation")).toBeVisible();

    // Verify Opportunity Cards
    await expect(page.getByText("Software Engineering Intern — Summer 2026")).toBeVisible();
    await expect(page.getByText("HackMIT 2026 — Global Collegiate Hackathon")).toBeVisible();
    await expect(page.getByText("Generation Google Scholarship (APAC)")).toBeVisible();
  });

  test("Robots.txt and Sitemap.xml are accessible and valid", async ({ request }) => {
    const robotsRes = await request.get("/robots.txt");
    expect(robotsRes.ok()).toBeTruthy();
    const robotsText = await robotsRes.text();
    expect(robotsText).toContain("User-Agent: *");
    expect(robotsText).toContain("Disallow: /account");

    const sitemapRes = await request.get("/sitemap.xml");
    expect(sitemapRes.ok()).toBeTruthy();
    const sitemapText = await sitemapRes.text();
    expect(sitemapText).toContain("<urlset");
    expect(sitemapText).toContain("https://mnesa.ai");
  });

  test("Custom 404 page renders for non-existent routes", async ({ page }) => {
    const response = await page.goto("/non-existent-route-404-test");
    expect(response?.status()).toBe(404);

    await expect(page.getByRole("heading", { level: 1 })).toContainText("Page Not Found");
    await expect(page.getByText("Error 404")).toBeVisible();
    await expect(page.locator("a:has-text('Return to Home')")).toBeVisible();
  });
});
