"use client";

import React from "react";
import Link from "next/link";
import { WifiOff, Sparkles } from "lucide-react";
import { GlassSurface } from "./GlassSurface";

export interface GlassHeaderProps {
  isOnline: boolean;
  backendStatus: "UP" | "DOWN" | "CHECKING";
  userInitials?: string;
}

export function GlassHeader({ isOnline, backendStatus, userInitials }: GlassHeaderProps) {
  return (
    <header className="sticky top-0 z-30 pt-safe px-3 sm:px-4 pt-2">
      <GlassSurface
        refract={true}
        variant="bar"
        scale={-90}
        chroma={5}
        border={0.05}
        mapBlur={10}
        radius={22}
        className="max-w-md mx-auto px-4 h-14 rounded-2xl flex items-center justify-between shadow-lg"
      >
        <Link href="/app" className="flex items-center space-x-2.5 group">
          <div className="w-8 h-8 rounded-xl bg-gradient-to-tr from-blue-600 via-indigo-600 to-cyan-400 flex items-center justify-center text-white font-black text-sm shadow-md shadow-blue-500/25 border border-white/40 group-active:scale-95 transition-transform">
            M
          </div>
          <div className="flex flex-col">
            <span className="font-extrabold text-base tracking-tight leading-none text-slate-900 dark:text-white flex items-center gap-1">
              MNESA
              <span className="w-1.5 h-1.5 rounded-full bg-blue-500 inline-block animate-ping" />
            </span>
            <span className="text-[10px] text-slate-500 dark:text-slate-400 font-medium tracking-tight">
              Liquid Copilot
            </span>
          </div>
        </Link>

        <div className="flex items-center space-x-2.5">
          {/* Status Indicator */}
          <div
            className={`flex items-center space-x-1.5 px-2.5 py-1 rounded-full text-[11px] font-semibold border backdrop-blur-md ${
              !isOnline
                ? "bg-red-500/15 text-red-700 dark:text-red-300 border-red-400/30"
                : backendStatus === "UP"
                ? "bg-emerald-500/15 text-emerald-700 dark:text-emerald-300 border-emerald-400/30"
                : "bg-amber-500/15 text-amber-700 dark:text-amber-300 border-amber-400/30"
            }`}
            title={!isOnline ? "Network offline" : `Backend: ${backendStatus}`}
          >
            {!isOnline ? (
              <>
                <WifiOff className="w-3 h-3 text-red-500" />
                <span>Offline</span>
              </>
            ) : (
              <>
                <span
                  className={`w-2 h-2 rounded-full ${
                    backendStatus === "UP"
                      ? "bg-emerald-500 animate-pulse"
                      : "bg-amber-500 animate-ping"
                  }`}
                />
                <span className="font-medium tracking-tight">
                  {backendStatus === "UP" ? "Online" : "Syncing"}
                </span>
              </>
            )}
          </div>

          {/* User Avatar */}
          {userInitials ? (
            <Link
              href="/app/settings"
              className="w-8 h-8 rounded-full bg-gradient-to-tr from-blue-500/20 to-indigo-500/30 text-blue-700 dark:text-blue-300 font-bold text-xs flex items-center justify-center border border-white/60 dark:border-white/20 shadow-sm active:scale-95 transition-transform"
              aria-label="Account Settings"
            >
              {userInitials}
            </Link>
          ) : (
            <Link
              href="/app/login"
              className="text-xs font-semibold px-2.5 py-1 rounded-full text-blue-600 dark:text-blue-400 bg-blue-500/10 border border-blue-400/30"
            >
              Sign In
            </Link>
          )}
        </div>
      </GlassSurface>
    </header>
  );
}
