/*!
 * liquid-glass — Apple-style liquid glass refraction for any web component.
 * Upstream Source: https://github.com/deepika-builds/liquid-glass
 * Copyright (c) 2026 Deepika Rao (MIT License)
 *
 * Adapted for TypeScript and Next.js React environment in MNESA.
 */

export interface LiquidGlassOptions {
  /** Displacement strength; negative = magnifying bulge. Default: -112. (Subtle: -60, Dramatic: -180) */
  scale?: number;
  /** Per-channel scale stagger (prism chromatic fringe); 0 disables. Default: 6 */
  chroma?: number;
  /** Neutral interior inset as a fraction of the smaller side. Default: 0.07 */
  border?: number;
  /** Edge-curvature softness (px) of the displacement map gray inset. Default: 12 */
  mapBlur?: number;
  /** Backdrop blur (px) behind the glass interior. Default: 3 */
  blur?: number;
  /** Backdrop saturation boost. Default: 1.5 */
  saturate?: number;
  /** Corner radius override (px); default reads computed border-radius. */
  radius?: number | null;
  /** Frosted blur (px) where refraction is unsupported (Safari, Firefox). Default: 16 */
  fallbackBlur?: number;
}

export interface LiquidGlassInstance {
  supported: boolean;
  refresh: () => void;
  destroy: () => void;
}

const SVG_NS = "http://www.w3.org/2000/svg";
let uid = 0;
let svgDefs: SVGDefsElement | null = null;

export function isLiquidGlassSupported(): boolean {
  if (typeof window === "undefined" || typeof navigator === "undefined") {
    return false;
  }
  const ua = navigator.userAgent;
  const isSafari = /Safari/.test(ua) && !/Chrome|Chromium|Edg/.test(ua);
  const isFirefox = /Firefox/.test(ua);
  if (isSafari || isFirefox) return false;
  if (!window.CSS || !CSS.supports("backdrop-filter", "url(#lg)")) return false;
  try {
    const c = document.createElement("canvas");
    c.width = c.height = 4;
    const ctx = c.getContext("2d");
    if (!ctx) return false;
    ctx.getImageData(0, 0, 1, 1);
    return true;
  } catch {
    return false;
  }
}

function ensureDefs(): SVGDefsElement {
  if (svgDefs && svgDefs.isConnected) return svgDefs;
  const existingSvg = document.getElementById("mnesa-liquid-glass-svg-root");
  if (existingSvg) {
    const existingDefs = existingSvg.querySelector("defs");
    if (existingDefs) {
      svgDefs = existingDefs as SVGDefsElement;
      return svgDefs;
    }
  }

  const svg = document.createElementNS(SVG_NS, "svg");
  svg.id = "mnesa-liquid-glass-svg-root";
  svg.setAttribute("width", "0");
  svg.setAttribute("height", "0");
  svg.setAttribute("aria-hidden", "true");
  svg.style.position = "absolute";
  svg.style.pointerEvents = "none";
  svg.style.top = "-9999px";
  svg.style.left = "-9999px";

  svgDefs = document.createElementNS(SVG_NS, "defs") as SVGDefsElement;
  svg.appendChild(svgDefs);
  document.body.appendChild(svg);
  return svgDefs;
}

function makeMap(w: number, h: number, radius: number, border: number, mapBlur: number): string {
  const canvas = document.createElement("canvas");
  canvas.width = w;
  canvas.height = h;
  const ctx = canvas.getContext("2d");
  if (!ctx) return "";

  const gx = ctx.createLinearGradient(0, 0, w, 0);
  gx.addColorStop(0, "rgb(0,0,0)");
  gx.addColorStop(1, "rgb(255,0,0)");
  ctx.fillStyle = gx;
  ctx.fillRect(0, 0, w, h);

  const gy = ctx.createLinearGradient(0, 0, 0, h);
  gy.addColorStop(0, "rgb(0,0,0)");
  gy.addColorStop(1, "rgb(0,0,255)");
  ctx.globalCompositeOperation = "difference";
  ctx.fillStyle = gy;
  ctx.fillRect(0, 0, w, h);

  ctx.globalCompositeOperation = "source-over";
  const inset = border * Math.min(w, h);
  ctx.filter = `blur(${mapBlur}px)`;
  ctx.fillStyle = "rgba(128,128,128,0.93)";
  ctx.beginPath();
  if (typeof ctx.roundRect === "function") {
    ctx.roundRect(inset, inset, w - inset * 2, h - inset * 2, Math.max(radius - inset, 2));
  } else {
    // Fallback for browsers without roundRect
    ctx.rect(inset, inset, w - inset * 2, h - inset * 2);
  }
  ctx.fill();
  ctx.filter = "none";
  return canvas.toDataURL();
}

function buildFilter(id: string, scales: [number, number, number]) {
  const filter = document.createElementNS(SVG_NS, "filter");
  filter.setAttribute("id", id);
  filter.setAttribute("x", "0");
  filter.setAttribute("y", "0");
  filter.setAttribute("width", "100%");
  filter.setAttribute("height", "100%");
  // Load-bearing: filters default to linearRGB, which re-maps neutral gray 128 to ~0.216
  filter.setAttribute("color-interpolation-filters", "sRGB");

  const feImage = document.createElementNS(SVG_NS, "feImage");
  feImage.setAttribute("x", "0");
  feImage.setAttribute("y", "0");
  feImage.setAttribute("result", "map");
  feImage.setAttribute("preserveAspectRatio", "none");
  filter.appendChild(feImage);

  const keep = [
    "1 0 0 0 0  0 0 0 0 0  0 0 0 0 0  0 0 0 1 0",
    "0 0 0 0 0  0 1 0 0 0  0 0 0 0 0  0 0 0 1 0",
    "0 0 0 0 0  0 0 0 0 0  0 0 1 0 0  0 0 0 1 0",
  ];
  const channels: string[] = [];
  for (let i = 0; i < 3; i++) {
    const disp = document.createElementNS(SVG_NS, "feDisplacementMap");
    disp.setAttribute("in", "SourceGraphic");
    disp.setAttribute("in2", "map");
    disp.setAttribute("scale", String(scales[i]));
    disp.setAttribute("xChannelSelector", "R");
    disp.setAttribute("yChannelSelector", "B");
    disp.setAttribute("result", "d" + i);
    filter.appendChild(disp);

    const cm = document.createElementNS(SVG_NS, "feColorMatrix");
    cm.setAttribute("in", "d" + i);
    cm.setAttribute("type", "matrix");
    cm.setAttribute("values", keep[i]);
    cm.setAttribute("result", "c" + i);
    filter.appendChild(cm);
    channels.push("c" + i);
  }

  const blend1 = document.createElementNS(SVG_NS, "feBlend");
  blend1.setAttribute("in", channels[0]);
  blend1.setAttribute("in2", channels[1]);
  blend1.setAttribute("mode", "screen");
  blend1.setAttribute("result", "c01");
  filter.appendChild(blend1);

  const blend2 = document.createElementNS(SVG_NS, "feBlend");
  blend2.setAttribute("in", "c01");
  blend2.setAttribute("in2", channels[2]);
  blend2.setAttribute("mode", "screen");
  filter.appendChild(blend2);

  ensureDefs().appendChild(filter);
  return { filter, feImage };
}

function resolveRadius(el: HTMLElement, w: number, h: number, override?: number | null): number {
  if (override != null) return override;
  const raw = window.getComputedStyle(el).borderTopLeftRadius || "0px";
  const v = parseFloat(raw) || 0;
  return raw.trim().endsWith("%") ? (v / 100) * Math.min(w, h) : v;
}

export function liquidGlass(
  el: HTMLElement,
  opts?: LiquidGlassOptions
): LiquidGlassInstance {
  const o: Required<LiquidGlassOptions> = Object.assign(
    {
      scale: -112,
      chroma: 6,
      border: 0.07,
      mapBlur: 12,
      blur: 3,
      saturate: 1.5,
      radius: null,
      fallbackBlur: 16,
    },
    opts
  );

  const supported = isLiquidGlassSupported();

  if (!supported) {
    const frosted = `blur(${o.fallbackBlur}px) saturate(${o.saturate})`;
    el.style.backdropFilter = frosted;
    el.style.setProperty("-webkit-backdrop-filter", frosted);
    el.classList.add("lg-fallback");
    return {
      supported: false,
      refresh: () => {},
      destroy: () => {
        el.style.backdropFilter = "";
        el.style.removeProperty("-webkit-backdrop-filter");
        el.classList.remove("lg-fallback");
      },
    };
  }

  const id = "lg-filter-" + ++uid;
  const scales: [number, number, number] = [
    o.scale,
    o.scale + o.chroma,
    o.scale + 2 * o.chroma,
  ];
  const parts = buildFilter(id, scales);

  function refresh() {
    const w = el.offsetWidth;
    const h = el.offsetHeight;
    if (!w || !h) return;
    const radius = resolveRadius(el, w, h, o.radius);
    const mapDataUrl = makeMap(w, h, radius, o.border, o.mapBlur);
    parts.feImage.setAttribute("href", mapDataUrl);
    parts.feImage.setAttribute("width", String(w));
    parts.feImage.setAttribute("height", String(h));
  }

  refresh();
  const filterVal = `url(#${id}) blur(${o.blur}px) saturate(${o.saturate})`;
  el.style.backdropFilter = filterVal;
  el.style.setProperty("-webkit-backdrop-filter", filterVal);

  let timer: NodeJS.Timeout | null = null;
  const ro = new ResizeObserver(() => {
    if (timer) clearTimeout(timer);
    timer = setTimeout(refresh, 120);
  });
  ro.observe(el);

  return {
    supported: true,
    refresh,
    destroy: () => {
      ro.disconnect();
      if (timer) clearTimeout(timer);
      parts.filter.remove();
      el.style.backdropFilter = "";
      el.style.removeProperty("-webkit-backdrop-filter");
    },
  };
}
