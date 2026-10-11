const { chromium } = require('@playwright/test');
const path = require('path');

async function captureLiveVercel() {
  const liveUrl = 'https://mnesa-567hw8u3b-balashanmugam-ss-projects-171c4097.vercel.app';
  const artifactDir = 'C:\\Users\\balashanmugam\\.gemini\\antigravity\\brain\\7499c354-394e-4af2-a0fb-bdcf1a5ce799';

  console.log('Testing live Vercel Liquid Glass deployment:', liveUrl);
  const browser = await chromium.launch();
  const ctx = await browser.newContext({
    viewport: { width: 390, height: 844 },
    userAgent: 'Mozilla/5.0 (Linux; Android 14; Pixel 7) AppleWebKit/537.36 Chrome/120.0 Mobile Safari/537.36'
  });
  const page = await ctx.newPage();

  // 1. Live Login screenshot
  console.log('Capturing login screen...');
  await page.goto(`${liveUrl}/app/login`);
  await page.waitForTimeout(1000);
  await page.screenshot({ path: path.join(artifactDir, 'liquid_glass_vercel_login.png') });

  // 2. Perform live login
  console.log('Signing in...');
  await page.fill('input[type="email"]', 'harshabala47@gmail.com');
  await page.fill('input[type="password"]', 'MnesaSecurePassword2026!');
  await page.click('button[type="submit"]');

  // Wait for transition to /app
  let navigated = false;
  for (let i = 0; i < 20; i++) {
    await page.waitForTimeout(1000);
    if (page.url().includes('/app') && !page.url().includes('/login')) {
      navigated = true;
      break;
    }
  }

  if (!navigated) {
    throw new Error('Failed to navigate to /app after login');
  }

  await page.waitForSelector('.liquid-glass-dock', { timeout: 10000 }).catch(() => {});
  await page.waitForTimeout(2000);

  // 3. Live Feed
  console.log('Capturing live feed...');
  await page.screenshot({ path: path.join(artifactDir, 'liquid_glass_vercel_feed.png') });

  // 4. Live Capture Screen
  console.log('Capturing live capture page...');
  await page.goto(`${liveUrl}/app/capture`);
  await page.waitForTimeout(1500);
  await page.screenshot({ path: path.join(artifactDir, 'liquid_glass_vercel_capture.png') });

  // 5. Live Reminders Screen
  console.log('Capturing live reminders page...');
  await page.goto(`${liveUrl}/app/reminders`);
  await page.waitForTimeout(1500);
  await page.screenshot({ path: path.join(artifactDir, 'liquid_glass_vercel_reminders.png') });

  // 6. Live Settings Screen
  console.log('Capturing live settings page...');
  await page.goto(`${liveUrl}/app/settings`);
  await page.waitForTimeout(1500);
  await page.screenshot({ path: path.join(artifactDir, 'liquid_glass_vercel_settings.png') });

  // 7. Live Opportunity Detail Screen
  console.log('Capturing live detail page...');
  await page.goto(`${liveUrl}/app`);
  await page.waitForTimeout(1500);
  const detailLink = await page.$('a[href^="/app/opportunities/"]');
  if (detailLink) {
    await detailLink.click();
    await page.waitForTimeout(2000);
    await page.screenshot({ path: path.join(artifactDir, 'liquid_glass_vercel_detail.png') });
  }

  await browser.close();
  console.log('All live Vercel Liquid Glass screenshots captured successfully!');
}

captureLiveVercel().catch((err) => {
  console.error('Error in captureLiveVercel:', err);
  process.exit(1);
});
