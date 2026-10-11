import { test, expect } from "@playwright/test";

test.describe("MNESA Progressive Web App (PWA) Tests", () => {
  test("Manifest is accessible, valid JSON and specifies PWA standalone configuration", async ({ request }) => {
    const res = await request.get("/manifest.json");
    expect(res.status()).toBe(200);

    const manifest = await res.json();
    expect(manifest.name).toBe("MNESA — Opportunity Copilot");
    expect(manifest.short_name).toBe("MNESA");
    expect(manifest.start_url).toBe("/app");
    expect(manifest.scope).toBe("/app");
    expect(manifest.display).toBe("standalone");
    expect(manifest.theme_color).toBe("#2563EB");
    expect(manifest.share_target).toBeDefined();
    expect(manifest.share_target.action).toBe("/app/share-target");
    expect(manifest.share_target.method).toBe("GET");
    expect(manifest.icons.length).toBeGreaterThanOrEqual(4);
  });

  test("Service worker script is served and accessible", async ({ request }) => {
    const res = await request.get("/sw.js");
    expect(res.status()).toBe(200);
    const text = await res.text();
    expect(text).toContain("mnesa-pwa");
    expect(text).toContain("SKIP_WAITING");
  });

  test("PWA icons are accessible with HTTP 200", async ({ request }) => {
    const icon192 = await request.get("/icons/icon-192.png");
    expect(icon192.status()).toBe(200);

    const icon512 = await request.get("/icons/icon-512.png");
    expect(icon512.status()).toBe(200);

    const maskable = await request.get("/icons/icon-maskable-192.png");
    expect(maskable.status()).toBe(200);
  });

  test("PWA Login page renders without marketing Navbar and Footer", async ({ page }) => {
    await page.goto("/app/login");
    // Ensure marketing nav links are absent
    await expect(page.locator("nav[aria-label='Main Navigation']")).toHaveCount(0);
    await expect(page.locator("footer[role='contentinfo']")).toHaveCount(0);

    // Ensure PWA login form elements are present
    await expect(page.getByRole("heading", { name: "Welcome to MNESA" })).toBeVisible();
    await expect(page.getByPlaceholder("you@example.com")).toBeVisible();
    await expect(page.getByRole("button", { name: "Sign In" })).toBeVisible();
  });

  test("PWA Capture screen renders mobile input and paste controls", async ({ page }) => {
    await page.goto("/app/capture");
    await expect(page.getByRole("heading", { name: /Capture Opportunity/i })).toBeVisible();
    await expect(page.getByPlaceholder(/https:\/\/example.com/i)).toBeVisible();
    await expect(page.getByRole("button", { name: /Analyze & Extract with AI/i })).toBeVisible();
  });

  test("PWA Settings screen renders cloud status and PWA diagnostic fields", async ({ page }) => {
    await page.goto("/app/settings");
    await expect(page.getByRole("heading", { name: /Settings & Diagnostics/i })).toBeVisible();
    await expect(page.getByText("Cloud Infrastructure")).toBeVisible();
    await expect(page.getByText("App Mode")).toBeVisible();
    await expect(page.getByRole("button", { name: /Check for App Updates/i })).toBeVisible();
  });
});
