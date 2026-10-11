"use client";

import React from "react";
import Link from "next/link";
import { usePathname } from "next/navigation";
import { Layers, Sparkles, Bell, Settings } from "lucide-react";
import { GlassSurface } from "./GlassSurface";

export function GlassNavigation() {
  const pathname = usePathname();

  const navItems = [
    {
      label: "Feed",
      href: "/app",
      icon: Layers,
      isActive: pathname === "/app",
    },
    {
      label: "Capture",
      href: "/app/capture",
      icon: Sparkles,
      isAction: true,
      isActive: pathname === "/app/capture" || pathname?.startsWith("/app/capture"),
    },
    {
      label: "Reminders",
      href: "/app/reminders",
      icon: Bell,
      isActive: pathname === "/app/reminders",
    },
    {
      label: "Settings",
      href: "/app/settings",
      icon: Settings,
      isActive: pathname === "/app/settings",
    },
  ];

  return (
    <nav
      aria-label="Liquid Glass Main Navigation"
      className="fixed bottom-0 left-0 right-0 z-40 pb-safe px-3 sm:px-4 pointer-events-none mb-3"
    >
      <div className="max-w-md mx-auto pointer-events-auto">
        <GlassSurface
          refract={true}
          variant="dock"
          scale={-100}
          chroma={6}
          border={0.06}
          mapBlur={12}
          radius={32}
          className="h-16 px-3 flex items-center justify-around relative"
        >
          {navItems.map((item) => {
            const Icon = item.icon;

            if (item.isAction) {
              return (
                <Link
                  key={item.href}
                  href={item.href}
                  className="flex flex-col items-center justify-center -mt-6 group focus:outline-none"
                  aria-label={item.label}
                >
                  <div className="w-13 h-13 p-3 rounded-full bg-gradient-to-tr from-blue-600 via-indigo-600 to-cyan-500 text-white shadow-lg shadow-blue-500/40 flex items-center justify-center transition-all duration-200 group-hover:scale-105 group-active:scale-95 border-2 border-white/80 dark:border-slate-800">
                    <Icon className="w-6 h-6 animate-pulse" />
                  </div>
                  <span className="text-[10px] font-bold tracking-tight text-blue-600 dark:text-blue-400 mt-1">
                    {item.label}
                  </span>
                </Link>
              );
            }

            return (
              <Link
                key={item.href}
                href={item.href}
                className={`flex flex-col items-center justify-center min-w-[56px] min-h-[46px] py-1 px-2 rounded-2xl transition-all duration-200 relative ${
                  item.isActive
                    ? "text-blue-600 dark:text-blue-400 font-bold"
                    : "text-slate-500 dark:text-slate-400 hover:text-slate-900 dark:hover:text-slate-100"
                }`}
                aria-label={item.label}
                aria-current={item.isActive ? "page" : undefined}
              >
                {item.isActive && (
                  <span className="absolute inset-0 bg-blue-500/10 dark:bg-blue-400/10 rounded-2xl -z-10" />
                )}
                <Icon className={`w-5 h-5 mb-0.5 transition-transform ${item.isActive ? "scale-110" : ""}`} />
                <span className="text-[11px] leading-tight tracking-tight">{item.label}</span>
              </Link>
            );
          })}
        </GlassSurface>
      </div>
    </nav>
  );
}
