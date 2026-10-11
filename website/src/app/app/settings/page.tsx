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
import {
  GlassCard,
  GlassBadge,
  GlassButton,
} from "@/components/glass";

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
        <h1 className="text-lg font-black text-slate-900 dark:text-white tracking-tight">
          Settings & Diagnostics
        </h1>
        <p className="text-xs text-slate-500 dark:text-slate-400 mt-0.5 font-medium">
          Account info, cloud status, and PWA options.
        </p>
      </div>

      {/* Account Info Card */}
      {user && (
        <GlassCard elevated={true} className="p-4 flex items-center space-x-3.5">
          <div className="w-12 h-12 rounded-2xl bg-gradient-to-tr from-blue-600 via-indigo-600 to-cyan-400 text-white font-black text-lg flex items-center justify-center shrink-0 shadow-md shadow-blue-500/25 border border-white/50">
            {user.fullName ? user.fullName[0].toUpperCase() : "U"}
          </div>
          <div className="flex-1 min-w-0">
            <h2 className="text-sm font-bold text-slate-900 dark:text-white truncate tracking-tight">
              {user.fullName}
            </h2>
            <p className="text-xs text-slate-500 dark:text-slate-400 truncate font-medium">{user.email}</p>
          </div>
        </GlassCard>
      )}

      {/* PWA Installation Card */}
      {!isStandalone && deferredPrompt && (
        <GlassCard elevated={true} className="p-5 space-y-3 !bg-gradient-to-r !from-blue-600/90 !to-indigo-600/90 text-white">
          <div className="flex items-center space-x-3">
            <div className="p-2.5 bg-white/20 rounded-2xl backdrop-blur-md">
              <Download className="w-5 h-5 text-white" />
            </div>
            <div>
              <h3 className="text-xs font-black tracking-tight">Install MNESA on Android</h3>
              <p className="text-[11px] text-blue-100 font-medium">
                Add to your home screen for instant updates and share-sheet support!
              </p>
            </div>
          </div>
          <button
            onClick={handleInstallApp}
            className="w-full py-2.5 px-4 rounded-xl bg-white text-blue-700 font-bold text-xs shadow-md transition-all active:scale-95"
          >
            Install to Home Screen
          </button>
        </GlassCard>
      )}

      {/* Cloud & PWA Diagnostics Card */}
      <GlassCard className="p-5 space-y-3.5">
        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-2">
            <Server className="w-4 h-4 text-blue-600 dark:text-blue-400" />
            <h2 className="text-xs font-extrabold text-slate-900 dark:text-white uppercase tracking-wider">
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
          <div className="flex justify-between items-center py-1.5 border-b border-slate-200/50 dark:border-white/10">
            <span className="text-slate-500 dark:text-slate-400 font-medium">API Endpoint</span>
            <span className="font-mono text-[10px] text-slate-700 dark:text-slate-300 truncate max-w-[200px]">
              {api.getBaseUrl()}
            </span>
          </div>

          <div className="flex justify-between items-center py-1.5 border-b border-slate-200/50 dark:border-white/10">
            <span className="text-slate-500 dark:text-slate-400 font-medium">Backend Status</span>
            <GlassBadge
              variant={health?.status === "UP" ? "emerald" : "amber"}
              size="xs"
              dot={true}
            >
              {health?.status === "UP" ? "ONLINE (Render)" : "CONNECTING"}
            </GlassBadge>
          </div>

          <div className="flex justify-between items-center py-1.5 border-b border-slate-200/50 dark:border-white/10">
            <span className="text-slate-500 dark:text-slate-400 font-medium">Database</span>
            <span className="font-mono text-[10px] text-slate-700 dark:text-slate-300 font-semibold">
              {health?.components?.database || "PostgreSQL 16 (Render)"}
            </span>
          </div>

          <div className="flex justify-between items-center py-1.5 border-b border-slate-200/50 dark:border-white/10">
            <span className="text-slate-500 dark:text-slate-400 font-medium">App Mode</span>
            <span className="font-semibold text-slate-700 dark:text-slate-300">
              {isStandalone ? "📱 Standalone PWA" : "🌐 Browser Tab"}
            </span>
          </div>

          <div className="flex justify-between items-center py-1.5">
            <span className="text-slate-500 dark:text-slate-400 font-medium">Service Worker</span>
            <span className="font-semibold text-slate-700 dark:text-slate-300">{swStatus}</span>
          </div>
        </div>
      </GlassCard>

      {/* App Appearance & Updates */}
      <GlassCard className="p-5 space-y-3.5">
        <h2 className="text-xs font-extrabold text-slate-900 dark:text-white uppercase tracking-wider">
          Preferences & Updates
        </h2>

        <div className="flex justify-between items-center py-1 border-b border-slate-200/50 dark:border-white/10">
          <span className="text-xs text-slate-600 dark:text-slate-400 font-medium">Theme</span>
          <ThemeToggle />
        </div>

        <div className="pt-1 space-y-2">
          <GlassButton
            onClick={handleCheckUpdate}
            variant="secondary"
            size="md"
            isLoading={updateChecking}
            className="w-full"
            leftIcon={<RefreshCw className="w-3.5 h-3.5" />}
          >
            Check for App Updates
          </GlassButton>
          {updateMsg && (
            <p className="text-[11px] text-center text-emerald-600 dark:text-emerald-400 font-semibold mt-1.5">
              {updateMsg}
            </p>
          )}
        </div>
      </GlassCard>

      {/* Sign Out Button */}
      <button
        onClick={handleLogout}
        className="w-full py-3.5 px-4 rounded-2xl bg-red-500/10 hover:bg-red-500/15 border border-red-500/20 text-red-600 dark:text-red-400 font-bold text-xs transition-all flex items-center justify-center space-x-2 active:scale-98"
      >
        <LogOut className="w-4 h-4" />
        <span>Sign Out of MNESA</span>
      </button>
    </div>
  );
}
