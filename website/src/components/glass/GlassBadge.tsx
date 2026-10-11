"use client";

import React from "react";

export interface GlassBadgeProps {
  children: React.ReactNode;
  variant?: "blue" | "emerald" | "amber" | "rose" | "purple" | "slate";
  size?: "xs" | "sm" | "md";
  className?: string;
  dot?: boolean;
}

export function GlassBadge({
  children,
  variant = "blue",
  size = "sm",
  className = "",
  dot = false,
}: GlassBadgeProps) {
  const sizeClasses =
    size === "xs"
      ? "px-2 py-0.5 text-[10px]"
      : size === "md"
      ? "px-3 py-1 text-xs"
      : "px-2.5 py-0.5 text-[11px]";

  const variantStyles = {
    blue: "bg-blue-500/15 text-blue-700 dark:text-blue-300 border-blue-400/30",
    emerald: "bg-emerald-500/15 text-emerald-700 dark:text-emerald-300 border-emerald-400/30",
    amber: "bg-amber-500/15 text-amber-700 dark:text-amber-300 border-amber-400/30",
    rose: "bg-rose-500/15 text-rose-700 dark:text-rose-300 border-rose-400/30",
    purple: "bg-purple-500/15 text-purple-700 dark:text-purple-300 border-purple-400/30",
    slate: "bg-slate-500/15 text-slate-700 dark:text-slate-300 border-slate-400/30",
  }[variant];

  const dotColors = {
    blue: "bg-blue-500",
    emerald: "bg-emerald-500",
    amber: "bg-amber-500",
    rose: "bg-rose-500",
    purple: "bg-purple-500",
    slate: "bg-slate-400",
  }[variant];

  return (
    <span
      className={`liquid-glass-pill inline-flex items-center gap-1.5 font-semibold tracking-tight border ${variantStyles} ${sizeClasses} ${className}`}
    >
      {dot && <span className={`w-1.5 h-1.5 rounded-full ${dotColors} animate-pulse`} />}
      {children}
    </span>
  );
}
