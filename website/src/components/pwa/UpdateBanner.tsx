"use client";

import React, { useEffect, useState } from "react";
import { RefreshCw, CloudLightning, X } from "lucide-react";
import { useAuth } from "@/context/AuthContext";

export const UpdateBanner: React.FC = () => {
  const [updateAvailable, setUpdateAvailable] = useState(false);
  const [waitingWorker, setWaitingWorker] = useState<ServiceWorker | null>(null);
  const [bannerDismissed, setBannerDismissed] = useState(false);
  const { isBackendWaking } = useAuth();

  useEffect(() => {
    if (typeof window === "undefined" || !("serviceWorker" in navigator)) {
      return;
    }

    // Register service worker
    navigator.serviceWorker
      .register("/sw.js")
      .then((reg) => {
        // Check if an updated service worker is already waiting
        if (reg.waiting) {
          setWaitingWorker(reg.waiting);
          setUpdateAvailable(true);
        }

        reg.addEventListener("updatefound", () => {
          const newWorker = reg.installing;
          if (newWorker) {
            newWorker.addEventListener("statechange", () => {
              if (newWorker.state === "installed" && navigator.serviceWorker.controller) {
                setWaitingWorker(newWorker);
                setUpdateAvailable(true);
              }
            });
          }
        });
      })
      .catch((err) => {
        console.warn("[PWA] Service Worker registration failed:", err);
      });

    let refreshing = false;
    navigator.serviceWorker.addEventListener("controllerchange", () => {
      if (!refreshing) {
        refreshing = true;
        window.location.reload();
      }
    });
  }, []);

  const handleUpdate = () => {
    if (waitingWorker) {
      waitingWorker.postMessage({ type: "SKIP_WAITING" });
    } else {
      window.location.reload();
    }
  };

  return (
    <>
      {/* Backend Waking Banner (Render Cold Start) */}
      {isBackendWaking && (
        <div className="bg-amber-500/90 backdrop-blur-md text-slate-950 px-4 py-2.5 text-xs font-semibold flex items-center justify-between shadow-lg sticky top-0 z-50 border-b border-amber-400/40">
          <div className="flex items-center space-x-2">
            <CloudLightning className="w-4 h-4 animate-bounce text-slate-950" />
            <span>Waking cloud backend... (Render cold start, ~30s)</span>
          </div>
          <RefreshCw className="w-3.5 h-3.5 animate-spin text-slate-950" />
        </div>
      )}

      {/* New UI Version Update Banner */}
      {updateAvailable && !bannerDismissed && (
        <div className="fixed bottom-24 left-4 right-4 sm:left-auto sm:right-6 sm:bottom-6 sm:max-w-md liquid-glass-surface !bg-gradient-to-r !from-blue-600/95 !to-indigo-600/95 text-white rounded-2xl p-3.5 shadow-2xl z-50 flex items-center justify-between border border-white/30 animate-in fade-in slide-in-from-bottom-4 duration-300">
          <div className="flex items-center space-x-3">
            <div className="p-2 bg-white/20 rounded-xl backdrop-blur-md">
              <RefreshCw className="w-4 h-4 animate-spin" />
            </div>
            <div>
              <p className="text-xs font-black tracking-tight">Liquid update available!</p>
              <p className="text-[11px] text-blue-100 font-medium">Tap to load the latest MNESA design</p>
            </div>
          </div>
          <div className="flex items-center space-x-2">
            <button
              onClick={handleUpdate}
              className="bg-white text-blue-700 hover:bg-blue-50 text-xs font-bold px-3 py-1.5 rounded-xl shadow transition-all active:scale-95"
            >
              Update
            </button>
            <button
              onClick={() => setBannerDismissed(true)}
              className="p-1.5 text-white/80 hover:text-white rounded-full transition-colors"
              aria-label="Dismiss banner"
            >
              <X className="w-4 h-4" />
            </button>
          </div>
        </div>
      )}
    </>
  );
};
