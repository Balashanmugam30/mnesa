const { chromium } = require('@playwright/test');
const path = require('path');

async function testLivePwa() {
  const targetUrl = 'https://mnesa-balashanmugam-ss-projects-171c4097.vercel.app';
  console.log(`Starting comprehensive live PWA test at: ${targetUrl}`);

  const browser = await chromium.launch({ headless: true });
  const context = await browser.newContext({
    viewport: { width: 390, height: 844 }, // Mobile Pixel 7 dimensions
    userAgent: 'Mozilla/5.0 (Linux; Android 14; Pixel 7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36'
  });
  const page = await context.newPage();
  page.on('console', msg => console.log('PAGE LOG:', msg.text()));
  page.on('pageerror', err => console.log('PAGE ERROR:', err.message));
  page.on('request', req => {
    if (req.url().includes('mnesa-backend') || req.url().includes('/auth/')) {
      console.log('NET REQ:', req.method(), req.url());
    }
  });
  page.on('requestfailed', req => {
    console.log('NET FAILED:', req.method(), req.url(), req.failure()?.errorText);
  });
  page.on('response', res => {
    if (res.url().includes('mnesa-backend') || res.url().includes('/auth/')) {
      console.log('NET RES:', res.status(), res.url());
    }
  });

  const artifactDir = 'C:\\Users\\balashanmugam\\.gemini\\antigravity\\brain\\7499c354-394e-4af2-a0fb-bdcf1a5ce799';

  // 1. Visit /app
  console.log('1. Navigating to /app...');
  await page.goto(`${targetUrl}/app`, { waitUntil: 'domcontentloaded' });
  await page.waitForTimeout(2000);
  console.log('Current URL:', page.url());

  // 2. Log in
  if (page.url().includes('/app/login')) {
    console.log('2. Entering credentials for harshabala47@gmail.com...');
    await page.fill('input[type="email"]', 'harshabala47@gmail.com');
    await page.fill('input[type="password"]', 'MnesaSecurePassword2026!');
    await page.click('button:has-text("Sign In")');

    // Wait for redirect or error message
    try {
      await page.waitForURL('**/app', { timeout: 8000 });
      console.log('Successfully authenticated and redirected to /app!');
    } catch {
      console.log('waitForURL to /app timed out; checking current page state...');
      console.log('Current URL is:', page.url());
      const bodyText = await page.innerText('body');
      console.log('Page body text snippet:', bodyText.substring(0, 300));
      await page.screenshot({ path: path.join(artifactDir, 'pwa_login_failure.png') });
      console.log('Saved pwa_login_failure.png');
    }
  }

  await page.waitForTimeout(2000);
  await page.screenshot({ path: path.join(artifactDir, 'pwa_live_dashboard.png') });
  console.log('Captured pwa_live_dashboard.png');

  // 3. Navigate to Capture Screen
  console.log('3. Navigating to /app/capture...');
  await page.goto(`${targetUrl}/app/capture`, { waitUntil: 'domcontentloaded' });
  await page.waitForTimeout(1500);
  await page.screenshot({ path: path.join(artifactDir, 'pwa_live_capture.png') });
  console.log('Captured pwa_live_capture.png');

  // 4. Capture a live opportunity
  console.log('4. Pasting opportunity link and triggering AI analysis...');
  await page.fill('textarea', 'https://unstop.com/internships/software-engineer-intern-google-2026');
  await page.click('button:has-text("Analyze & Extract with AI")');

  // Wait for review screen
  console.log('Waiting for AI extraction and review transition...');
  await page.waitForURL('**/app/review**', { timeout: 45000 });
  console.log('Successfully reached review page:', page.url());
  await page.waitForTimeout(1500);
  await page.screenshot({ path: path.join(artifactDir, 'pwa_live_review.png') });
  console.log('Captured pwa_live_review.png');

  // 5. Confirm opportunity
  console.log('5. Confirming and saving opportunity to PostgreSQL...');
  await page.click('button:has-text("Confirm & Save Opportunity")');

  // Wait for detail view or feed
  await page.waitForURL('**/app/opportunities/**', { timeout: 20000 });
  console.log('Successfully saved to PostgreSQL! Detail URL:', page.url());
  await page.waitForTimeout(1500);
  await page.screenshot({ path: path.join(artifactDir, 'pwa_live_opportunity_detail.png') });
  console.log('Captured pwa_live_opportunity_detail.png');

  // 6. Check Settings & Diagnostics
  console.log('6. Navigating to /app/settings...');
  await page.goto(`${targetUrl}/app/settings`, { waitUntil: 'domcontentloaded' });
  await page.waitForTimeout(2500);
  await page.screenshot({ path: path.join(artifactDir, 'pwa_live_settings.png') });
  console.log('Captured pwa_live_settings.png');

  await browser.close();
  console.log('All live PWA flows tested and verified successfully!');
}

testLivePwa().catch((err) => {
  console.error('Test failed:', err);
  process.exit(1);
});
