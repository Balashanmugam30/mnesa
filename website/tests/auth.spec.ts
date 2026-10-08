import { test, expect } from "@playwright/test";

test.describe("MNESA Web Platform Authentication & Account Tests", () => {
  test("Sign In page renders accessible heading, inputs, and actions", async ({ page }) => {
    await page.goto("/signin");

    // Verify Heading
    await expect(page.getByRole("heading", { level: 1 })).toContainText("Welcome back");

    // Verify Form Inputs
    await expect(page.locator("input#email")).toBeVisible();
    await expect(page.locator("input#password")).toBeVisible();

    // Verify Action Buttons
    await expect(page.getByRole("button", { name: "Sign In" })).toBeVisible();
    await expect(page.getByRole("button", { name: "Continue with Google" })).toBeVisible();

    // Verify Links
    await expect(page.getByRole("link", { name: "Create an account" })).toBeVisible();
  });

  test("Registration page renders accessible inputs and terms checkbox", async ({ page }) => {
    await page.goto("/register");

    // Verify Heading
    await expect(page.getByRole("heading", { level: 1 })).toContainText("Never miss what matters");

    // Verify Form Inputs
    await expect(page.locator("input#fullName")).toBeVisible();
    await expect(page.locator("input#email")).toBeVisible();
    await expect(page.locator("input#password")).toBeVisible();
    await expect(page.locator("input#agreed")).toBeVisible();

    // Verify Submit Button
    await expect(page.getByRole("button", { name: "Create Account" })).toBeVisible();
  });

  test("Account page renders profile and customizable preference controls", async ({ page }) => {
    await page.goto("/account");

    // Verify Profile Heading
    await expect(page.getByRole("heading", { level: 1 })).toContainText("Alex Chen");

    // Verify Section Headings
    await expect(page.getByRole("heading", { name: "Opportunity Focus Areas" })).toBeVisible();
    await expect(page.getByRole("heading", { name: "Default Reminder Urgency" })).toBeVisible();
    await expect(page.getByRole("heading", { name: "Notification Channels" })).toBeVisible();

    // Verify Interest Chips
    await expect(page.getByRole("button", { name: "Internships" })).toBeVisible();
    await expect(page.getByRole("button", { name: "Full-Time Jobs" })).toBeVisible();
    await expect(page.getByRole("button", { name: "Hackathons" })).toBeVisible();

    // Verify Actions
    await expect(page.getByRole("button", { name: "Save Preferences" })).toBeVisible();
    await expect(page.getByRole("button", { name: "Delete Account" })).toBeVisible();
  });
});
