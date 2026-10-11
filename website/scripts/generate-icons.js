const fs = require('fs');
const path = require('path');
const { chromium } = require('@playwright/test');

const outputDir = path.join(__dirname, '..', 'public', 'icons');
if (!fs.existsSync(outputDir)) {
  fs.mkdirSync(outputDir, { recursive: true });
}

// MNESA Brand SVG Logo definition
const createSvg = (size, isMaskable = false) => {
  const padding = isMaskable ? size * 0.2 : size * 0.1;
  const contentSize = size - padding * 2;
  return `
    <svg xmlns="http://www.w3.org/2000/svg" width="${size}" height="${size}" viewBox="0 0 ${size} ${size}">
      <rect width="${size}" height="${size}" rx="${isMaskable ? '0' : Math.round(size * 0.22)}" fill="#2563EB" />
      <g transform="translate(${padding}, ${padding}) scale(${contentSize / 48})">
        <!-- Brand M-mark with capture target styling -->
        <path
          fill="#FFFFFF"
          d="M8,40 L8,8 L16,8 L24,24 L32,8 L40,8 L40,40 L32,40 L32,20 L24,34 L16,20 L16,40 Z"
        />
        <!-- Accent urgency marker -->
        <polygon
          fill="#F59E0B"
          points="24,26 26,30 22,30"
        />
      </g>
    </svg>
  `;
};

async function generate() {
  console.log('Generating PWA icons with Playwright...');
  const browser = await chromium.launch();
  const page = await browser.newPage();

  const configs = [
    { name: 'icon-192.png', size: 192, maskable: false },
    { name: 'icon-512.png', size: 512, maskable: false },
    { name: 'icon-maskable-192.png', size: 192, maskable: true },
    { name: 'icon-maskable-512.png', size: 512, maskable: true },
    { name: 'apple-touch-icon.png', size: 180, maskable: false },
  ];

  for (const config of configs) {
    const svg = createSvg(config.size, config.maskable);
    const html = `
      <!DOCTYPE html>
      <html>
        <head>
          <style>
            body { margin: 0; padding: 0; background: transparent; display: flex; }
          </style>
        </head>
        <body>${svg}</body>
      </html>
    `;
    await page.setViewportSize({ width: config.size, height: config.size });
    await page.setContent(html);
    const outputPath = path.join(outputDir, config.name);
    await page.screenshot({ path: outputPath, omitBackground: !config.maskable });
    console.log(`Created ${config.name} (${config.size}x${config.size})`);
  }

  // Also save favicon.svg in public
  const faviconSvg = createSvg(48, false);
  fs.writeFileSync(path.join(__dirname, '..', 'public', 'favicon.svg'), faviconSvg.trim());
  console.log('Created public/favicon.svg');

  await browser.close();
  console.log('All icons generated successfully!');
}

generate().catch((err) => {
  console.error('Failed to generate icons:', err);
  process.exit(1);
});
