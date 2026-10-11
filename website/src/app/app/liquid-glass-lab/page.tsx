"use client";

import React, { useEffect, useRef, useState } from "react";
import Link from "next/link";
import { ArrowLeft, Sparkles, Sliders, Layers, Eye, CheckCircle2 } from "lucide-react";
import { liquidGlass, isLiquidGlassSupported } from "@/lib/liquid-glass";

export default function LiquidGlassLab() {
  const refractCardRef = useRef<HTMLDivElement>(null);
  const refractNavRef = useRef<HTMLDivElement>(null);
  const refractBtnRef = useRef<HTMLButtonElement>(null);

  const [supported, setSupported] = useState<boolean | null>(null);
  const [scale, setScale] = useState<number>(-140);
  const [chroma, setChroma] = useState<number>(6);
  const [borderInset, setBorderInset] = useState<number>(0.08);
  const [mapBlur, setMapBlur] = useState<number>(12);
  const [blur, setBlur] = useState<number>(4);
  const [alpha, setAlpha] = useState<number>(0.22);
  const [bgPattern, setBgPattern] = useState<"mesh" | "stripes" | "vibrant">("vibrant");
  const [diagnostics, setDiagnostics] = useState<Record<string, any>>({});

  useEffect(() => {
    const isSupp = isLiquidGlassSupported();
    setSupported(isSupp);

    const instances: Array<{ destroy: () => void; refresh: () => void }> = [];

    if (refractCardRef.current) {
      const inst = liquidGlass(refractCardRef.current, {
        scale,
        chroma,
        border: borderInset,
        mapBlur,
        blur,
        saturate: 1.6,
      });
      instances.push(inst);
    }

    if (refractNavRef.current) {
      const inst = liquidGlass(refractNavRef.current, {
        scale: scale * 0.9,
        chroma,
        border: borderInset,
        mapBlur,
        blur,
        saturate: 1.6,
      });
      instances.push(inst);
    }

    if (refractBtnRef.current) {
      const inst = liquidGlass(refractBtnRef.current, {
        scale: -90,
        chroma: 4,
        border: 0.1,
        mapBlur: 8,
        blur: 2,
        saturate: 1.5,
      });
      instances.push(inst);
    }

    // Capture diagnostics
    if (refractCardRef.current) {
      const cs = window.getComputedStyle(refractCardRef.current);
      setDiagnostics({
        supported: isSupp,
        assignedBackdrop: refractCardRef.current.style.backdropFilter,
        computedBackdrop: cs.backdropFilter,
        computedWebkitBackdrop: cs.getPropertyValue("-webkit-backdrop-filter") || (cs as any).webkitBackdropFilter || "",
        cardWidth: refractCardRef.current.offsetWidth,
        cardHeight: refractCardRef.current.offsetHeight,
      });
    }

    return () => {
      instances.forEach((inst) => inst.destroy());
    };
  }, [scale, chroma, borderInset, mapBlur, blur]);

  return (
    <div className="relative min-h-screen text-slate-900 dark:text-white p-4 sm:p-6 pb-28 select-none">
      {/* Background with strong visual patterns so refraction is unmistakably visible */}
      <div className="fixed inset-0 -z-10 pointer-events-none overflow-hidden">
        {bgPattern === "vibrant" && (
          <div className="absolute inset-0 bg-slate-950">
            {/* Luminous colorful orbs */}
            <div className="absolute top-[5%] left-[10%] w-[320px] h-[320px] rounded-full bg-blue-500/40 blur-[70px]" />
            <div className="absolute top-[30%] right-[5%] w-[380px] h-[380px] rounded-full bg-purple-600/40 blur-[80px]" />
            <div className="absolute bottom-[10%] left-[20%] w-[340px] h-[340px] rounded-full bg-emerald-500/35 blur-[75px]" />
            <div className="absolute top-[60%] right-[25%] w-[300px] h-[300px] rounded-full bg-amber-500/30 blur-[70px]" />
            {/* Geometric angled grid lines to reveal edge displacement bending */}
            <div
              className="absolute inset-0 opacity-25"
              style={{
                backgroundImage: `linear-gradient(45deg, rgba(255,255,255,0.15) 1px, transparent 1px), linear-gradient(-45deg, rgba(255,255,255,0.15) 1px, transparent 1px)`,
                backgroundSize: "40px 40px",
              }}
            />
          </div>
        )}

        {bgPattern === "stripes" && (
          <div className="absolute inset-0 bg-slate-900">
            {/* High-contrast colorful stripes */}
            <div
              className="absolute inset-0 opacity-40"
              style={{
                backgroundImage:
                  "repeating-linear-gradient(45deg, #3b82f6 0, #3b82f6 25px, #8b5cf6 25px, #8b5cf6 50px, #ec4899 50px, #ec4899 75px, #10b981 75px, #10b981 100px)",
              }}
            />
          </div>
        )}

        {bgPattern === "mesh" && (
          <div className="absolute inset-0 bg-gradient-to-tr from-slate-950 via-indigo-950 to-slate-900">
            <div className="absolute top-1/4 left-1/3 w-[450px] h-[450px] rounded-full bg-cyan-500/30 blur-[100px]" />
            <div className="absolute bottom-1/4 right-1/4 w-[400px] h-[400px] rounded-full bg-pink-500/30 blur-[100px]" />
          </div>
        )}
      </div>

      {/* Top Header */}
      <div className="max-w-2xl mx-auto space-y-4">
        <div className="flex items-center justify-between">
          <Link
            href="/app"
            className="inline-flex items-center space-x-1.5 text-xs font-bold text-blue-400 hover:text-blue-300"
          >
            <ArrowLeft className="w-4 h-4" />
            <span>Back to PWA</span>
          </Link>

          <span
            className={`px-3 py-1 rounded-full text-xs font-bold border ${
              supported
                ? "bg-emerald-500/20 text-emerald-300 border-emerald-400/40"
                : "bg-amber-500/20 text-amber-300 border-amber-400/40"
            }`}
          >
            {supported ? "Chromium SVG Refraction Active" : "Frosted Fallback Active"}
          </span>
        </div>

        <div>
          <h1 className="text-2xl font-black tracking-tight text-white flex items-center space-x-2">
            <Sparkles className="w-6 h-6 text-blue-400" />
            <span>Liquid Glass Optical Lab</span>
          </h1>
          <p className="text-xs text-slate-300 mt-1">
            Visual comparison proving real refraction and translucent material depth.
          </p>
        </div>

        {/* Live Diagnostics Card */}
        <div
          style={{
            background: "rgba(15, 23, 42, 0.45)",
            backdropFilter: "blur(16px)",
            borderRadius: "20px",
            boxShadow: "0 16px 36px rgba(0,0,0,0.5), inset 0 1px 1px rgba(255,255,255,0.3), inset 0 0 0 1px rgba(255,255,255,0.15)",
          }}
          className="p-4 text-xs font-mono text-slate-300 space-y-1.5"
        >
          <div className="flex justify-between">
            <span className="text-slate-400">Assigned Backdrop:</span>
            <span className="text-cyan-400 truncate max-w-[280px]">{diagnostics.assignedBackdrop || "None"}</span>
          </div>
          <div className="flex justify-between">
            <span className="text-slate-400">Computed Backdrop:</span>
            <span className="text-emerald-400 truncate max-w-[280px]">{diagnostics.computedBackdrop || "None"}</span>
          </div>
          <div className="flex justify-between">
            <span className="text-slate-400">Target Size:</span>
            <span>{diagnostics.cardWidth}px × {diagnostics.cardHeight}px</span>
          </div>
        </div>

        {/* Interactive Controls */}
        <div
          style={{
            background: "rgba(15, 23, 42, 0.45)",
            backdropFilter: "blur(16px)",
            borderRadius: "20px",
            boxShadow: "0 16px 36px rgba(0,0,0,0.5), inset 0 1px 1px rgba(255,255,255,0.3), inset 0 0 0 1px rgba(255,255,255,0.15)",
          }}
          className="p-4 space-y-3"
        >
          <div className="flex items-center justify-between text-xs font-bold text-white">
            <span className="flex items-center gap-1.5">
              <Sliders className="w-4 h-4 text-blue-400" />
              <span>Optical Parameters</span>
            </span>
            <div className="flex gap-1.5">
              {(["vibrant", "stripes", "mesh"] as const).map((bg) => (
                <button
                  key={bg}
                  onClick={() => setBgPattern(bg)}
                  className={`px-2.5 py-0.5 rounded-lg text-[10px] font-bold uppercase transition-all ${
                    bgPattern === bg ? "bg-blue-600 text-white" : "bg-white/10 text-slate-300"
                  }`}
                >
                  {bg}
                </button>
              ))}
            </div>
          </div>

          <div className="grid grid-cols-2 gap-3 text-xs">
            <div>
              <div className="flex justify-between text-[11px] text-slate-300 mb-1">
                <span>Displacement Scale</span>
                <span className="font-bold text-blue-400">{scale}</span>
              </div>
              <input
                type="range"
                min="-220"
                max="-40"
                value={scale}
                onChange={(e) => setScale(Number(e.target.value))}
                className="w-full accent-blue-500"
              />
            </div>

            <div>
              <div className="flex justify-between text-[11px] text-slate-300 mb-1">
                <span>Chroma Fringe</span>
                <span className="font-bold text-purple-400">{chroma}</span>
              </div>
              <input
                type="range"
                min="0"
                max="14"
                value={chroma}
                onChange={(e) => setChroma(Number(e.target.value))}
                className="w-full accent-purple-500"
              />
            </div>

            <div>
              <div className="flex justify-between text-[11px] text-slate-300 mb-1">
                <span>Glass Tint Alpha</span>
                <span className="font-bold text-cyan-400">{alpha.toFixed(2)}</span>
              </div>
              <input
                type="range"
                min="0.05"
                max="0.6"
                step="0.02"
                value={alpha}
                onChange={(e) => setAlpha(Number(e.target.value))}
                className="w-full accent-cyan-500"
              />
            </div>

            <div>
              <div className="flex justify-between text-[11px] text-slate-300 mb-1">
                <span>Backdrop Blur (px)</span>
                <span className="font-bold text-emerald-400">{blur}</span>
              </div>
              <input
                type="range"
                min="0"
                max="24"
                value={blur}
                onChange={(e) => setBlur(Number(e.target.value))}
                className="w-full accent-emerald-500"
              />
            </div>
          </div>
        </div>

        {/* COMPARISON MATRIX */}
        <div className="space-y-4 pt-2">
          {/* Card A: Plain White Card */}
          <div className="space-y-1">
            <span className="text-[11px] font-bold tracking-wider uppercase text-slate-400 ml-1">
              Reference A: Conventional Opaque Card
            </span>
            <div className="bg-white text-slate-900 rounded-[24px] p-5 shadow-xl border border-slate-200">
              <h3 className="font-black text-base">Conventional Card (Opaque White)</h3>
              <p className="text-xs text-slate-600 mt-1">
                Notice: Flat background, completely hides what is behind it. No light interaction or depth.
              </p>
            </div>
          </div>

          {/* Card B: CSS Frosted Blur Glass */}
          <div className="space-y-1">
            <span className="text-[11px] font-bold tracking-wider uppercase text-slate-400 ml-1">
              Reference B: Standard Frosted Blur (CSS backdrop-filter only)
            </span>
            <div
              style={{
                borderRadius: "24px",
                background: `linear-gradient(180deg, rgba(255, 255, 255, ${alpha}) 0%, rgba(255, 255, 255, ${alpha * 0.7}) 100%)`,
                backdropFilter: `blur(${blur + 12}px) saturate(1.8)`,
                boxShadow:
                  "0 20px 50px rgba(0, 0, 0, 0.4), inset 0 1.5px 2px rgba(255, 255, 255, 0.7), inset 0 0 0 1px rgba(255, 255, 255, 0.25)",
              }}
              className="p-5 text-white"
            >
              <h3 className="font-black text-base drop-shadow-sm">Frosted Blur Glass (No Displacement)</h3>
              <p className="text-xs text-slate-200 mt-1 leading-relaxed">
                Notice: Translucent and blurred, but flat straight edges without refractive curvature or rim bending.
              </p>
            </div>
          </div>

          {/* Card C: REAL Liquid Glass with SVG Displacement Map Refraction */}
          <div className="space-y-1">
            <span className="text-[11px] font-bold tracking-wider uppercase text-cyan-400 ml-1 flex items-center gap-1">
              <Sparkles className="w-3.5 h-3.5" />
              <span>Reference C: Real Liquid Glass (Displacement Map + Chromatic Aberration)</span>
            </span>
            <div
              ref={refractCardRef}
              style={{
                borderRadius: "28px",
                background: `linear-gradient(180deg, rgba(255, 255, 255, ${alpha}) 0%, rgba(255, 255, 255, ${alpha * 0.6}) 100%)`,
                boxShadow:
                  "0 24px 60px rgba(0, 0, 0, 0.5), inset 0 1.5px 2px rgba(255, 255, 255, 0.8), inset 0 -6px 16px rgba(255, 255, 255, 0.08), inset 0 0 0 1px rgba(255, 255, 255, 0.3)",
              }}
              className="p-6 text-white relative transition-all"
            >
              <div className="flex items-start justify-between">
                <div>
                  <h3 className="font-black text-lg text-white drop-shadow">
                    Liquid Glass Refractive Surface
                  </h3>
                  <p className="text-xs text-slate-200 mt-1 max-w-sm leading-relaxed">
                    Look closely at the curved rim: geometric grid lines behind the card visibly bend and magnify with subtle prism color separation!
                  </p>
                </div>
                <span className="px-2.5 py-1 rounded-full text-[10px] font-black bg-cyan-500/25 text-cyan-200 border border-cyan-400/40">
                  REAL OPTICS
                </span>
              </div>

              <div className="mt-4 pt-3 border-t border-white/20 flex items-center justify-between text-xs">
                <span className="font-semibold text-slate-300">Google Summer of Code 2026</span>
                <span className="px-2 py-0.5 rounded-full font-bold bg-amber-500/25 text-amber-200 border border-amber-400/40 text-[10px]">
                  🔥 2 days left
                </span>
              </div>
            </div>
          </div>

          {/* Card D: Floating Glass Navigation Pill Dock */}
          <div className="space-y-1 pt-2">
            <span className="text-[11px] font-bold tracking-wider uppercase text-slate-400 ml-1">
              Reference D: Floating Liquid Glass Dock
            </span>
            <div
              ref={refractNavRef}
              style={{
                borderRadius: "36px",
                background: `linear-gradient(180deg, rgba(255, 255, 255, ${alpha + 0.05}) 0%, rgba(255, 255, 255, ${alpha * 0.7}) 100%)`,
                boxShadow:
                  "0 24px 60px rgba(0, 0, 0, 0.5), inset 0 1.5px 2px rgba(255, 255, 255, 0.9), inset 0 -8px 20px rgba(255, 255, 255, 0.06), inset 0 0 0 1px rgba(255, 255, 255, 0.35)",
              }}
              className="h-16 px-6 flex items-center justify-between text-white"
            >
              <div className="flex items-center space-x-2">
                <div className="w-8 h-8 rounded-full bg-blue-600 flex items-center justify-center font-black text-xs shadow-md">
                  M
                </div>
                <span className="font-bold text-xs tracking-tight">Liquid Dock</span>
              </div>

              <div className="flex items-center space-x-2">
                <button
                  ref={refractBtnRef}
                  style={{
                    borderRadius: "18px",
                    background: "linear-gradient(180deg, #3b82f6 0%, #1d4ed8 100%)",
                    boxShadow:
                      "0 10px 24px -4px rgba(37, 99, 235, 0.5), inset 0 1.5px 2px rgba(255, 255, 255, 0.5), inset 0 0 0 1px rgba(255, 255, 255, 0.3)",
                  }}
                  className="px-4 py-2 text-xs font-bold text-white flex items-center space-x-1.5"
                >
                  <Sparkles className="w-3.5 h-3.5" />
                  <span>Action</span>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
