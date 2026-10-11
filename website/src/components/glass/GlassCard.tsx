"use client";

import React, { forwardRef } from "react";

export interface GlassCardProps extends React.HTMLAttributes<HTMLDivElement> {
  className?: string;
  elevated?: boolean;
  interactive?: boolean;
  children?: React.ReactNode;
}

export const GlassCard = forwardRef<HTMLDivElement, GlassCardProps>(
  ({ className = "", elevated = false, interactive = false, children, ...rest }, ref) => {
    return (
      <div
        ref={ref}
        className={`liquid-glass-card p-4 sm:p-5 relative overflow-hidden ${
          elevated ? "shadow-xl dark:shadow-2xl" : ""
        } ${interactive ? "cursor-pointer" : ""} ${className}`}
        {...rest}
      >
        {children}
      </div>
    );
  }
);

GlassCard.displayName = "GlassCard";
