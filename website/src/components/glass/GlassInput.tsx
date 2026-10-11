"use client";

import React, { forwardRef } from "react";

export interface GlassInputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
  helperText?: string;
  leftIcon?: React.ReactNode;
  rightIcon?: React.ReactNode;
}

export const GlassInput = forwardRef<HTMLInputElement, GlassInputProps>(
  ({ label, error, helperText, leftIcon, rightIcon, className = "", id, ...rest }, ref) => {
    const inputId = id || (label ? label.toLowerCase().replace(/\s+/g, "-") : undefined);

    return (
      <div className="w-full flex flex-col space-y-1.5">
        {label && (
          <label
            htmlFor={inputId}
            className="text-xs font-semibold text-slate-700 dark:text-slate-300 ml-1 tracking-wide"
          >
            {label}
          </label>
        )}
        <div className="relative flex items-center">
          {leftIcon && (
            <div className="absolute left-3.5 text-slate-400 dark:text-slate-500 pointer-events-none flex items-center">
              {leftIcon}
            </div>
          )}
          <input
            ref={ref}
            id={inputId}
            className={`w-full liquid-glass-input text-slate-900 dark:text-white placeholder:text-slate-400 dark:placeholder:text-slate-500 text-sm font-medium px-4 py-3 min-h-[48px] outline-none transition-all duration-150 ${
              leftIcon ? "pl-10" : ""
            } ${rightIcon ? "pr-10" : ""} ${
              error
                ? "!border-red-500/80 !ring-2 !ring-red-500/20"
                : ""
            } ${className}`}
            {...rest}
          />
          {rightIcon && (
            <div className="absolute right-3.5 text-slate-400 dark:text-slate-500 flex items-center">
              {rightIcon}
            </div>
          )}
        </div>
        {error && (
          <span className="text-xs font-medium text-red-600 dark:text-red-400 ml-1">
            {error}
          </span>
        )}
        {!error && helperText && (
          <span className="text-xs text-slate-500 dark:text-slate-400 ml-1">
            {helperText}
          </span>
        )}
      </div>
    );
  }
);

GlassInput.displayName = "GlassInput";
