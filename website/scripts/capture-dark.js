const { chromium } = require('@playwright/test');
const path = require('path');

async function captureDarkMode() {
  const localUrl = 'http://localhost:3100';
  const artifactDir = 'C:\\Users\\balashanmugam\\.gemini\\antigravity\\brain\\7499c354-394e-4af2-a0fb-bdcf1a5ce799';

  const browser = await chromium.launch();
  const ctx = await browser.newContext({
    viewport: { width: 390, height: 844 },
    colorScheme: 'dark'
  });
  const page = await ctx.newPage();

  // Dark Login
  await page.goto(`${localUrl}/app/login`, { waitUntil: 'domcontentloaded' });
  await page.evaluate(() => document.documentElement.classList.add('dark'));
  await page.waitForTimeout(600);
  await page.screenshot({ path: path.join(artifactDir, 'liquid_glass_dark_login.png') });

  // Sign In
  await page.fill('input[type="email"]', 'harshabala47@gmail.com');
  await page.fill('input[type="password"]', 'MnesaSecurePassword2026!');
  await page.click('button:has-text("Sign In to MNESA")');
  await page.waitForURL('**/app', { timeout: 25000 });
  await page.evaluate(() => document.documentElement.classList.add('dark'));
  await page.waitForTimeout(1500);

  // Dark Feed
  await page.screenshot({ path: path.join(artifactDir, 'liquid_glass_dark_feed.png') });

  // Dark Opportunity Detail
  const oppLink = await page.$('a[href^="/app/opportunities/"]');
  if (oppLink) {
    await oppLink.click();
    await page.waitForURL('**/app/opportunities/**', { timeout: 15000 });
    await page.evaluate(() => document.documentElement.classList.add('dark'));
    await page.waitForTimeout(1500);
    await page.screenshot({ path: path.join(artifactDir, 'liquid_glass_dark_opportunity_detail.png') });
  }

  await browser.close();
  console.log('Dark mode screenshots captured successfully!');
}

captureDarkMode().catch(console.error);
