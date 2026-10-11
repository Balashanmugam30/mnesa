"use client";

import React, { forwardRef } from "react";
import { Loader2 } from "lucide-react";

export interface GlassButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: "primary" | "secondary" | "danger" | "ghost";
  size?: "sm" | "md" | "lg";
  isLoading?: boolean;
  leftIcon?: React.ReactNode;
  rightIcon?: React.ReactNode;
  children: React.ReactNode;
}

export const GlassButton = forwardRef<HTMLButtonElement, GlassButtonProps>(
  (
    {
      variant = "primary",
      size = "md",
      isLoading = false,
      leftIcon,
      rightIcon,
      className = "",
      disabled,
      children,
      ...rest
    },
    ref
  ) => {
    const sizeClasses =
      size === "sm"
        ? "px-3.5 py-1.5 text-xs min-h-[36px]"
        : size === "lg"
        ? "px-6 py-3.5 text-base min-h-[52px]"
        : "px-5 py-2.5 text-sm min-h-[46px]";

    const variantClasses =
      variant === "primary"
        ? "liquid-glass-btn-primary"
        : variant === "danger"
        ? "bg-gradient-to-b from-red-500 to-red-600 text-white shadow-lg shadow-red-500/25 border border-red-400/40 active:scale-95"
        : variant === "ghost"
        ? "bg-transparent hover:bg-slate-500/10 active:bg-slate-500/20 text-slate-700 dark:text-slate-200 border-transparent"
        : "liquid-glass-btn-secondary text-slate-800 dark:text-slate-100";

    return (
      <button
        ref={ref}
        disabled={disabled || isLoading}
        className={`inline-flex items-center justify-center font-semibold rounded-2xl select-none transition-all duration-150 disabled:opacity-50 disabled:pointer-events-none active:scale-[0.98] tap-highlight-transparent ${sizeClasses} ${variantClasses} ${className}`}
        {...rest}
      >
        {isLoading && <Loader2 className="w-4 h-4 mr-2 animate-spin shrink-0" />}
        {!isLoading && leftIcon && <span className="mr-2 shrink-0">{leftIcon}</span>}
        <span className="truncate">{children}</span>
        {!isLoading && rightIcon && <span className="ml-2 shrink-0">{rightIcon}</span>}
      </button>
    );
  }
);

GlassButton.displayName = "GlassButton";
