"use client";

import { useEffect, useRef, useCallback } from "react";
import { liquidGlass, LiquidGlassInstance, LiquidGlassOptions } from "./index";

export interface UseLiquidGlassOptions extends LiquidGlassOptions {
  enabled?: boolean;
}

/**
 * React hook to apply Apple-style liquid glass refraction to a DOM element.
 * Manages element lifecycle, auto-cleanup on unmount, and dynamic refresh on resize.
 */
export function useLiquidGlass<T extends HTMLElement = HTMLDivElement>(
  options?: UseLiquidGlassOptions
) {
  const elementRef = useRef<T | null>(null);
  const instanceRef = useRef<LiquidGlassInstance | null>(null);
  const optionsRef = useRef(options);
  optionsRef.current = options;

  const refresh = useCallback(() => {
    if (instanceRef.current) {
      instanceRef.current.refresh();
    }
  }, []);

  useEffect(() => {
    const el = elementRef.current;
    if (!el) return;

    if (optionsRef.current?.enabled === false) {
      if (instanceRef.current) {
        instanceRef.current.destroy();
        instanceRef.current = null;
      }
      return;
    }

    try {
      instanceRef.current = liquidGlass(el, optionsRef.current);
    } catch (err) {
      console.warn("[liquid-glass] Failed to initialize liquid glass on element:", err);
    }

    return () => {
      if (instanceRef.current) {
        instanceRef.current.destroy();
        instanceRef.current = null;
      }
    };
  }, [options?.enabled, options?.scale, options?.chroma, options?.border, options?.mapBlur, options?.blur, options?.saturate]);

  return { ref: elementRef, refresh, instance: instanceRef.current };
}
