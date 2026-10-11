const { chromium } = require('@playwright/test');

async function testVercelLogin() {
  const liveUrl = 'https://mnesa-567hw8u3b-balashanmugam-ss-projects-171c4097.vercel.app';
  const browser = await chromium.launch();
  const page = await browser.newPage();

  page.on('console', msg => console.log('[BROWSER CONSOLE]', msg.type(), msg.text()));
  page.on('pageerror', err => console.log('[BROWSER ERROR]', err.message));

  console.log('Navigating to login...');
  await page.goto(`${liveUrl}/app/login`);
  await page.waitForTimeout(1000);

  console.log('Filling form...');
  await page.fill('input[type="email"]', 'harshabala47@gmail.com');
  await page.fill('input[type="password"]', 'MnesaSecurePassword2026!');

  console.log('Clicking submit...');
  await page.click('button[type="submit"]');

  for (let i = 0; i < 15; i++) {
    await page.waitForTimeout(1000);
    const url = page.url();
    const errorText = await page.evaluate(() => {
      const el = document.querySelector('.text-red-700, .text-red-300, .bg-red-500\\/10');
      return el ? el.textContent : null;
    });
    console.log(`Sec ${i+1}: URL=${url}, ErrorMsg=${errorText || 'none'}`);
    if (url.includes('/app') && !url.includes('/login')) {
      console.log('Successfully reached /app!');
      break;
    }
  }

  await browser.close();
}

testVercelLogin().catch(console.error);
