const { chromium } = require('@playwright/test');
const path = require('path');

async function verifyLiquidGlass() {
  const localUrl = 'http://localhost:3100';
  const artifactDir = 'C:\\Users\\balashanmugam\\.gemini\\antigravity\\brain\\7499c354-394e-4af2-a0fb-bdcf1a5ce799';

  console.log('Launching headless Chromium to verify Liquid Glass redesign...');
  const browser = await chromium.launch();

  // 1. Mobile Context (Pixel 7 / iPhone 14 - 390x844)
  const mobileCtx = await browser.newContext({
    viewport: { width: 390, height: 844 },
    userAgent: 'Mozilla/5.0 (Linux; Android 14; Pixel 7) AppleWebKit/537.36 Chrome/120.0 Mobile Safari/537.36'
  });
  const page = await mobileCtx.newPage();

  console.log('Testing /app/login on mobile...');
  await page.goto(`${localUrl}/app/login`, { waitUntil: 'domcontentloaded' });
  await page.waitForTimeout(1000);
  await page.screenshot({ path: path.join(artifactDir, 'liquid_glass_login_mobile.png') });

  // 2. Perform Login with harshabala47@gmail.com
  console.log('Signing in to backend...');
  await page.fill('input[type="email"]', 'harshabala47@gmail.com');
  await page.fill('input[type="password"]', 'MnesaSecurePassword2026!');
  await page.click('button:has-text("Sign In to MNESA")');

  try {
    await page.waitForURL('**/app', { timeout: 25000 });
  } catch (err) {
    console.warn('URL transition wait timed out, current URL:', page.url());
  }

  // Wait for dock to attach
  await page.waitForSelector('.liquid-glass-dock', { timeout: 15000 }).catch(() => {});
  await page.waitForTimeout(2000);

  // 3. Inspect SVG displacement filter and glass classes in DOM
  const glassAudit = await page.evaluate(() => {
    const dock = document.querySelector('.liquid-glass-dock');
    const header = document.querySelector('.liquid-glass-bar');
    const cards = document.querySelectorAll('.liquid-glass-card');
    const ambientCanvas = document.querySelector('.ambient-mesh-canvas');
    const svgFilters = document.querySelectorAll('svg defs filter');

    const dockComputed = dock ? window.getComputedStyle(dock).backdropFilter : null;
    const headerComputed = header ? window.getComputedStyle(header).backdropFilter : null;

    // Grab storage for sharing with other contexts
    const tokens = {
      accessToken: localStorage.getItem('mnesa_access_token'),
      refreshToken: localStorage.getItem('mnesa_refresh_token'),
      user: localStorage.getItem('mnesa_user'),
    };

    return {
      currentUrl: window.location.href,
      hasDock: !!dock,
      hasHeader: !!header,
      cardsCount: cards.length,
      hasAmbientCanvas: !!ambientCanvas,
      svgFiltersCount: svgFilters.length,
      dockBackdropFilter: dockComputed,
      headerBackdropFilter: headerComputed,
      tokens,
    };
  });

  console.log('Liquid Glass DOM Audit:', JSON.stringify({
    currentUrl: glassAudit.currentUrl,
    hasDock: glassAudit.hasDock,
    hasHeader: glassAudit.hasHeader,
    cardsCount: glassAudit.cardsCount,
    hasAmbientCanvas: glassAudit.hasAmbientCanvas,
    svgFiltersCount: glassAudit.svgFiltersCount,
    dockBackdropFilter: glassAudit.dockBackdropFilter,
    headerBackdropFilter: glassAudit.headerBackdropFilter,
    isAuthenticated: !!glassAudit.tokens.accessToken,
  }, null, 2));

  // 4. Capture Feed / Dashboard on 390x844
  console.log('Capturing feed...');
  await page.screenshot({ path: path.join(artifactDir, 'liquid_glass_feed_mobile.png') });

  // 5. Capture Capture Screen on 390x844
  console.log('Capturing capture page...');
  await page.goto(`${localUrl}/app/capture`, { waitUntil: 'domcontentloaded' });
  await page.waitForTimeout(1500);
  await page.screenshot({ path: path.join(artifactDir, 'liquid_glass_capture_mobile.png') });

  // 6. Capture Reminders Screen on 390x844
  console.log('Capturing reminders page...');
  await page.goto(`${localUrl}/app/reminders`, { waitUntil: 'domcontentloaded' });
  await page.waitForTimeout(1500);
  await page.screenshot({ path: path.join(artifactDir, 'liquid_glass_reminders_mobile.png') });

  // 7. Capture Settings Screen on 390x844
  console.log('Capturing settings page...');
  await page.goto(`${localUrl}/app/settings`, { waitUntil: 'domcontentloaded' });
  await page.waitForTimeout(1500);
  await page.screenshot({ path: path.join(artifactDir, 'liquid_glass_settings_mobile.png') });

  // 8. Capture Tablet & Desktop Responsive Viewports
  const tokens = glassAudit.tokens;

  console.log('Capturing tablet viewport...');
  const tabletCtx = await browser.newContext({ viewport: { width: 768, height: 1024 } });
  const tabletPage = await tabletCtx.newPage();
  await tabletPage.goto(`${localUrl}/app/login`, { waitUntil: 'domcontentloaded' });
  if (tokens.accessToken) {
    await tabletPage.evaluate((t) => {
      localStorage.setItem('mnesa_access_token', t.accessToken);
      localStorage.setItem('mnesa_refresh_token', t.refreshToken);
      localStorage.setItem('mnesa_user', t.user);
    }, tokens);
    await tabletPage.goto(`${localUrl}/app`, { waitUntil: 'domcontentloaded' });
    await tabletPage.waitForTimeout(1500);
  }
  await tabletPage.screenshot({ path: path.join(artifactDir, 'liquid_glass_feed_tablet.png') });

  console.log('Capturing desktop viewport...');
  const desktopCtx = await browser.newContext({ viewport: { width: 1280, height: 800 } });
  const desktopPage = await desktopCtx.newPage();
  await desktopPage.goto(`${localUrl}/app/login`, { waitUntil: 'domcontentloaded' });
  if (tokens.accessToken) {
    await desktopPage.evaluate((t) => {
      localStorage.setItem('mnesa_access_token', t.accessToken);
      localStorage.setItem('mnesa_refresh_token', t.refreshToken);
      localStorage.setItem('mnesa_user', t.user);
    }, tokens);
    await desktopPage.goto(`${localUrl}/app`, { waitUntil: 'domcontentloaded' });
    await desktopPage.waitForTimeout(1500);
  }
  await desktopPage.screenshot({ path: path.join(artifactDir, 'liquid_glass_feed_desktop.png') });

  await browser.close();
  console.log('All Liquid Glass verification screenshots captured successfully!');
}

verifyLiquidGlass().catch((err) => {
  console.error('Error during Liquid Glass verification:', err);
  process.exit(1);
});
