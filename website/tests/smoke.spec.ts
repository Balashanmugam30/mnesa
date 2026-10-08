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
    await page.goto("/");

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
});
