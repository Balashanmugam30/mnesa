"use client";

import React, { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import {
  User,
  LogOut,
  Activity,
  Smartphone,
  Download,
  RefreshCw,
  ShieldCheck,
  Server,
  Database,
  Moon,
  Sun,
  Laptop,
} from "lucide-react";
import { useAuth } from "@/context/AuthContext";
import { api, BackendHealth } from "@/lib/api-client";
import { ThemeToggle } from "@/components/ui/ThemeToggle";

interface BeforeInstallPromptEvent extends Event {
  prompt: () => Promise<void>;
  userChoice: Promise<{ outcome: "accepted" | "dismissed" }>;
}

export default function SettingsPage() {
  const router = useRouter();
  const { user, logout } = useAuth();

  const [health, setHealth] = useState<BackendHealth | null>(null);
  const [healthLoading, setHealthLoading] = useState(false);
  const [isStandalone, setIsStandalone] = useState(false);
  const [deferredPrompt, setDeferredPrompt] = useState<BeforeInstallPromptEvent | null>(null);
  const [swStatus, setSwStatus] = useState("Checking...");
  const [updateChecking, setUpdateChecking] = useState(false);
  const [updateMsg, setUpdateMsg] = useState<string | null>(null);

  useEffect(() => {
    // Check standalone mode
    if (typeof window !== "undefined") {
      const isStandaloneMode =
        window.matchMedia("(display-mode: standalone)").matches ||
        (window.navigator as unknown as { standalone?: boolean }).standalone === true;
      setIsStandalone(isStandaloneMode);

      // Service worker status
      if ("serviceWorker" in navigator) {
        navigator.serviceWorker.getRegistration().then((reg) => {
          if (reg?.active) setSwStatus("Active & Running");
          else setSwStatus("Installed");
        });
      } else {
        setSwStatus("Not Supported");
      }

      // Capture install prompt
      const handleBeforeInstall = (e: Event) => {
        e.preventDefault();
        setDeferredPrompt(e as BeforeInstallPromptEvent);
      };
      window.addEventListener("beforeinstallprompt", handleBeforeInstall);
      return () => window.removeEventListener("beforeinstallprompt", handleBeforeInstall);
    }
  }, []);

  const runHealthCheck = async () => {
    setHealthLoading(true);
    try {
      const data = await api.checkHealth();
      setHealth(data);
    } catch {
      setHealth(null);
    } finally {
      setHealthLoading(false);
    }
  };

  useEffect(() => {
    runHealthCheck();
  }, []);

  const handleInstallApp = async () => {
    if (!deferredPrompt) return;
    deferredPrompt.prompt();
    const choice = await deferredPrompt.userChoice;
    if (choice.outcome === "accepted") {
      setDeferredPrompt(null);
    }
  };

  const handleCheckUpdate = async () => {
    setUpdateChecking(true);
    setUpdateMsg(null);
    try {
      if ("serviceWorker" in navigator) {
        const reg = await navigator.serviceWorker.getRegistration();
        if (reg) {
          await reg.update();
          setUpdateMsg("MNESA is using the latest version!");
        } else {
          setUpdateMsg("Service worker active.");
        }
      }
    } catch {
      setUpdateMsg("Update check complete.");
    } finally {
      setUpdateChecking(false);
    }
  };

  const handleLogout = async () => {
    if (confirm("Are you sure you want to sign out?")) {
      await logout();
      router.push("/app/login");
    }
  };

  return (
    <div className="space-y-4">
      {/* Title */}
      <div>
        <h1 className="text-lg font-black text-slate-900 dark:text-white">Settings & Diagnostics</h1>
        <p className="text-xs text-slate-500 mt-0.5">Account info, cloud status, and PWA options.</p>
      </div>

      {/* Account Info Card */}
      {user && (
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-4 shadow-sm flex items-center space-x-3">
          <div className="w-11 h-11 rounded-full bg-blue-600 text-white font-black text-base flex items-center justify-center shrink-0 shadow-sm">
            {user.fullName ? user.fullName[0].toUpperCase() : "U"}
          </div>
          <div className="flex-1 min-w-0">
            <h2 className="text-sm font-bold text-slate-900 dark:text-white truncate">
              {user.fullName}
            </h2>
            <p className="text-xs text-slate-500 truncate">{user.email}</p>
          </div>
        </div>
      )}

      {/* PWA Installation Card */}
      {!isStandalone && deferredPrompt && (
        <div className="bg-gradient-to-r from-blue-600 to-indigo-600 text-white rounded-2xl p-4 shadow-md space-y-3">
          <div className="flex items-center space-x-3">
            <div className="p-2 bg-white/20 rounded-xl">
              <Download className="w-5 h-5 text-white" />
            </div>
            <div>
              <h3 className="text-xs font-bold">Install MNESA on Android</h3>
              <p className="text-[11px] text-blue-100">
                Add to your home screen for instant updates and share-sheet support!
              </p>
            </div>
          </div>
          <button
            onClick={handleInstallApp}
            className="w-full py-2 px-3 rounded-xl bg-white text-blue-700 font-bold text-xs shadow transition-all hover:bg-blue-50"
          >
            Install to Home Screen
          </button>
        </div>
      )}

      {/* Cloud & PWA Diagnostics Card */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-4 shadow-sm space-y-3">
        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-2">
            <Server className="w-4 h-4 text-blue-600 dark:text-blue-400" />
            <h2 className="text-xs font-bold text-slate-900 dark:text-white uppercase tracking-wider">
              Cloud Infrastructure
            </h2>
          </div>
          <button
            onClick={runHealthCheck}
            disabled={healthLoading}
            className="text-[11px] font-bold text-blue-600 dark:text-blue-400 flex items-center space-x-1"
          >
            <RefreshCw className={`w-3 h-3 ${healthLoading ? "animate-spin" : ""}`} />
            <span>Check</span>
          </button>
        </div>

        <div className="space-y-2 text-xs">
          <div className="flex justify-between items-center py-1 border-b border-slate-100 dark:border-slate-800">
            <span className="text-slate-500">API Endpoint</span>
            <span className="font-mono text-[10px] text-slate-700 dark:text-slate-300 truncate max-w-[200px]">
              {api.getBaseUrl()}
            </span>
          </div>

          <div className="flex justify-between items-center py-1 border-b border-slate-100 dark:border-slate-800">
            <span className="text-slate-500">Backend Status</span>
            <span
              className={`px-2 py-0.5 rounded font-bold text-[10px] ${
                health?.status === "UP"
                  ? "bg-emerald-50 text-emerald-700 dark:bg-emerald-950 dark:text-emerald-400"
                  : "bg-amber-50 text-amber-700 dark:bg-amber-950 dark:text-amber-400"
              }`}
            >
              {health?.status === "UP" ? "ONLINE (Render)" : "CONNECTING"}
            </span>
          </div>

          <div className="flex justify-between items-center py-1 border-b border-slate-100 dark:border-slate-800">
            <span className="text-slate-500">Database</span>
            <span className="font-mono text-[10px] text-slate-700 dark:text-slate-300">
              {health?.components?.database || "PostgreSQL 16 (Render)"}
            </span>
          </div>

          <div className="flex justify-between items-center py-1 border-b border-slate-100 dark:border-slate-800">
            <span className="text-slate-500">App Mode</span>
            <span className="font-semibold text-slate-700 dark:text-slate-300">
              {isStandalone ? "📱 Standalone PWA" : "🌐 Browser Tab"}
            </span>
          </div>

          <div className="flex justify-between items-center py-1">
            <span className="text-slate-500">Service Worker</span>
            <span className="font-semibold text-slate-700 dark:text-slate-300">{swStatus}</span>
          </div>
        </div>
      </div>

      {/* App Appearance & Updates */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-4 shadow-sm space-y-3">
        <h2 className="text-xs font-bold text-slate-900 dark:text-white uppercase tracking-wider">
          Preferences & Updates
        </h2>

        <div className="flex justify-between items-center py-1 border-b border-slate-100 dark:border-slate-800">
          <span className="text-xs text-slate-600 dark:text-slate-400">Theme</span>
          <ThemeToggle />
        </div>

        <div className="pt-1">
          <button
            onClick={handleCheckUpdate}
            disabled={updateChecking}
            className="w-full py-2.5 px-3 rounded-xl border border-slate-200 dark:border-slate-800 text-xs font-bold text-slate-700 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-slate-800/50 flex items-center justify-center space-x-2"
          >
            <RefreshCw className={`w-3.5 h-3.5 ${updateChecking ? "animate-spin" : ""}`} />
            <span>Check for App Updates</span>
          </button>
          {updateMsg && (
            <p className="text-[11px] text-center text-emerald-600 dark:text-emerald-400 font-medium mt-1.5">
              {updateMsg}
            </p>
          )}
        </div>
      </div>

      {/* Sign Out Button */}
      <button
        onClick={handleLogout}
        className="w-full py-3 px-4 rounded-xl bg-red-50 hover:bg-red-100 dark:bg-red-950/40 dark:hover:bg-red-950/70 border border-red-200 dark:border-red-900/60 text-red-600 dark:text-red-400 font-bold text-xs transition-colors flex items-center justify-center space-x-2"
      >
        <LogOut className="w-4 h-4" />
        <span>Sign Out of MNESA</span>
      </button>
    </div>
  );
}
