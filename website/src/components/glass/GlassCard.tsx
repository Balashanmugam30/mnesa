"use client";

import React, { forwardRef, useImperativeHandle } from "react";
import { useLiquidGlass, UseLiquidGlassOptions } from "@/lib/liquid-glass/useLiquidGlass";

export interface GlassCardProps
  extends React.HTMLAttributes<HTMLDivElement>,
    Omit<UseLiquidGlassOptions, "enabled"> {
  className?: string;
  elevated?: boolean;
  interactive?: boolean;
  refract?: boolean;
  children?: React.ReactNode;
}

export const GlassCard = forwardRef<HTMLDivElement, GlassCardProps>(
  (
    {
      className = "",
      elevated = false,
      interactive = false,
      refract = false,
      scale = -120,
      chroma = 6,
      border = 0.07,
      mapBlur = 12,
      blur = 3,
      saturate = 1.6,
      radius,
      fallbackBlur,
      children,
      ...rest
    },
    forwardedRef
  ) => {
    const shouldRefract = refract || elevated;
    const { ref: internalRef } = useLiquidGlass<HTMLDivElement>({
      enabled: shouldRefract,
      scale,
      chroma,
      border,
      mapBlur,
      blur,
      saturate,
      radius,
      fallbackBlur,
    });

    useImperativeHandle(forwardedRef, () => internalRef.current as HTMLDivElement);

    return (
      <div
        ref={internalRef}
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
