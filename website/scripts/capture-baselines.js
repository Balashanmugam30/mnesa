const { chromium } = require('@playwright/test');
const path = require('path');

async function captureBaselines() {
  const targetUrl = 'https://mnesa-balashanmugam-ss-projects-171c4097.vercel.app';
  const artifactDir = 'C:\\Users\\balashanmugam\\.gemini\\antigravity\\brain\\7499c354-394e-4af2-a0fb-bdcf1a5ce799';

  const browser = await chromium.launch();

  // Mobile Viewport (Pixel 7 / iPhone 14)
  const mobileCtx = await browser.newContext({
    viewport: { width: 390, height: 844 },
    userAgent: 'Mozilla/5.0 (Linux; Android 14; Pixel 7) AppleWebKit/537.36 Chrome/120.0 Mobile Safari/537.36'
  });
  const mobilePage = await mobileCtx.newPage();

  // Desktop Viewport (1280x800)
  const desktopCtx = await browser.newContext({
    viewport: { width: 1280, height: 800 }
  });
  const desktopPage = await desktopCtx.newPage();

  console.log('Capturing mobile and desktop baselines...');

  // 1. Login screen
  await mobilePage.goto(`${targetUrl}/app/login`, { waitUntil: 'domcontentloaded' });
  await mobilePage.waitForTimeout(1000);
  await mobilePage.screenshot({ path: path.join(artifactDir, 'baseline_mobile_login.png') });

  await desktopPage.goto(`${targetUrl}/app/login`, { waitUntil: 'domcontentloaded' });
  await desktopPage.waitForTimeout(1000);
  await desktopPage.screenshot({ path: path.join(artifactDir, 'baseline_desktop_login.png') });

  // Sign in on mobile & desktop to capture authenticated screens
  for (const page of [mobilePage, desktopPage]) {
    await page.fill('input[type="email"]', 'harshabala47@gmail.com');
    await page.fill('input[type="password"]', 'MnesaSecurePassword2026!');
    await page.click('button:has-text("Sign In")');
    await page.waitForURL('**/app', { timeout: 15000 }).catch(() => {});
    await page.waitForTimeout(2000);
  }

  // 2. Feed / Dashboard
  await mobilePage.screenshot({ path: path.join(artifactDir, 'baseline_mobile_dashboard.png') });
  await desktopPage.screenshot({ path: path.join(artifactDir, 'baseline_desktop_dashboard.png') });

  // 3. Reminders
  await mobilePage.goto(`${targetUrl}/app/reminders`, { waitUntil: 'domcontentloaded' });
  await mobilePage.waitForTimeout(1500);
  await mobilePage.screenshot({ path: path.join(artifactDir, 'baseline_mobile_reminders.png') });

  await desktopPage.goto(`${targetUrl}/app/reminders`, { waitUntil: 'domcontentloaded' });
  await desktopPage.waitForTimeout(1500);
  await desktopPage.screenshot({ path: path.join(artifactDir, 'baseline_desktop_reminders.png') });

  await browser.close();
  console.log('Baseline screenshots captured successfully!');
}

captureBaselines().catch(console.error);
