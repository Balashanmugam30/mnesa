"use client";

import React, { forwardRef, useEffect, useImperativeHandle, useRef } from "react";
import { useLiquidGlass, UseLiquidGlassOptions } from "@/lib/liquid-glass/useLiquidGlass";

export interface GlassSurfaceProps
  extends React.HTMLAttributes<HTMLDivElement>,
    Omit<UseLiquidGlassOptions, "enabled"> {
  refract?: boolean;
  className?: string;
  variant?: "surface" | "dock" | "bar" | "card" | "plain";
  children?: React.ReactNode;
}

export const GlassSurface = forwardRef<HTMLDivElement, GlassSurfaceProps>(
  (
    {
      refract = false,
      variant = "surface",
      className = "",
      scale,
      chroma,
      border,
      mapBlur,
      blur,
      saturate,
      radius,
      fallbackBlur,
      children,
      ...rest
    },
    forwardedRef
  ) => {
    const { ref: internalRef, refresh } = useLiquidGlass<HTMLDivElement>({
      enabled: refract,
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

    const variantClass =
      variant === "dock"
        ? "liquid-glass-dock"
        : variant === "bar"
        ? "liquid-glass-bar"
        : variant === "card"
        ? "liquid-glass-card"
        : variant === "plain"
        ? ""
        : "liquid-glass-surface";

    return (
      <div
        ref={internalRef}
        className={`${variantClass} ${className}`}
        {...rest}
      >
        {children}
      </div>
    );
  }
);

GlassSurface.displayName = "GlassSurface";
