"use client";

import React, { useEffect, useState } from "react";
import Link from "next/link";
import { usePathname } from "next/navigation";
import {
  PlusCircle,
  Bell,
  Settings,
  Layers,
  Sparkles,
  WifiOff,
} from "lucide-react";
import { AuthProvider, useAuth } from "@/context/AuthContext";
import { UpdateBanner } from "@/components/pwa/UpdateBanner";
import { api } from "@/lib/api-client";

function AppNavigationWrapper({ children }: { children: React.ReactNode }) {
  const pathname = usePathname();
  const { user } = useAuth();
  const [isOnline, setIsOnline] = useState<boolean>(true);
  const [backendStatus, setBackendStatus] = useState<"UP" | "DOWN" | "CHECKING">("CHECKING");

  useEffect(() => {
    setIsOnline(navigator.onLine);
    const handleOnline = () => setIsOnline(true);
    const handleOffline = () => setIsOnline(false);

    window.addEventListener("online", handleOnline);
    window.addEventListener("offline", handleOffline);

    // Initial backend check
    api
      .checkHealth()
      .then(() => setBackendStatus("UP"))
      .catch(() => setBackendStatus("DOWN"));

    const interval = setInterval(() => {
      api
        .checkHealth()
        .then(() => setBackendStatus("UP"))
        .catch(() => setBackendStatus("DOWN"));
    }, 45000);

    return () => {
      window.removeEventListener("online", handleOnline);
      window.removeEventListener("offline", handleOffline);
      clearInterval(interval);
    };
  }, []);

  const navItems = [
    {
      label: "Feed",
      href: "/app",
      icon: Layers,
      isActive: pathname === "/app",
    },
    {
      label: "Capture",
      href: "/app/capture",
      icon: Sparkles,
      isAction: true,
      isActive: pathname === "/app/capture" || pathname?.startsWith("/app/capture"),
    },
    {
      label: "Reminders",
      href: "/app/reminders",
      icon: Bell,
      isActive: pathname === "/app/reminders",
    },
    {
      label: "Settings",
      href: "/app/settings",
      icon: Settings,
      isActive: pathname === "/app/settings",
    },
  ];

  const isAuthScreen = pathname === "/app/login" || pathname === "/app/register";

  return (
    <div className="flex flex-col min-h-screen bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100">
      {/* Top Mobile App Header */}
      {!isAuthScreen && (
        <header className="sticky top-0 z-30 bg-white/95 dark:bg-slate-900/95 backdrop-blur border-b border-slate-200 dark:border-slate-800 pt-safe">
          <div className="max-w-md mx-auto px-4 h-14 flex items-center justify-between">
            <Link href="/app" className="flex items-center space-x-2.5">
              <div className="w-8 h-8 rounded-lg bg-blue-600 flex items-center justify-center text-white font-extrabold text-base shadow-sm">
                M
              </div>
              <div className="flex flex-col">
                <span className="font-extrabold text-base tracking-tight leading-none text-slate-900 dark:text-white">
                  MNESA
                </span>
                <span className="text-[10px] text-slate-500 font-medium">Opportunity Copilot</span>
              </div>
            </Link>

            <div className="flex items-center space-x-3">
              <div
                className={`flex items-center space-x-1.5 px-2.5 py-1 rounded-full text-[11px] font-semibold border ${
                  !isOnline
                    ? "bg-red-50 text-red-700 border-red-200 dark:bg-red-950/50 dark:text-red-400 dark:border-red-900"
                    : backendStatus === "UP"
                    ? "bg-emerald-50 text-emerald-700 border-emerald-200 dark:bg-emerald-950/50 dark:text-emerald-400 dark:border-emerald-900"
                    : "bg-amber-50 text-amber-700 border-amber-200 dark:bg-amber-950/50 dark:text-amber-400 dark:border-amber-900"
                }`}
                title={!isOnline ? "Network offline" : `Backend: ${backendStatus}`}
              >
                {!isOnline ? (
                  <>
                    <WifiOff className="w-3 h-3 text-red-600" />
                    <span>Offline</span>
                  </>
                ) : (
                  <>
                    <span
                      className={`w-2 h-2 rounded-full ${
                        backendStatus === "UP"
                          ? "bg-emerald-500 animate-pulse"
                          : "bg-amber-500 animate-ping"
                      }`}
                    />
                    <span>{backendStatus === "UP" ? "Live" : "Connecting"}</span>
                  </>
                )}
              </div>

              {user && (
                <Link
                  href="/app/settings"
                  className="w-7 h-7 rounded-full bg-blue-100 dark:bg-blue-900/60 text-blue-700 dark:text-blue-300 font-bold text-xs flex items-center justify-center border border-blue-200 dark:border-blue-800"
                  aria-label="Account Settings"
                >
                  {user.fullName ? user.fullName[0].toUpperCase() : "U"}
                </Link>
              )}
            </div>
          </div>
        </header>
      )}

      {/* Main Screen Body */}
      <main className="flex-1 max-w-md w-full mx-auto pb-24 px-4 pt-4">{children}</main>

      {/* Bottom Floating Navigation Bar */}
      {!isAuthScreen && (
        <nav
          aria-label="PWA Main Navigation"
          className="fixed bottom-0 left-0 right-0 z-40 bg-white/95 dark:bg-slate-900/95 backdrop-blur border-t border-slate-200 dark:border-slate-800 pb-safe shadow-lg"
        >
          <div className="max-w-md mx-auto px-4 h-16 flex items-center justify-around">
            {navItems.map((item) => {
              const Icon = item.icon;
              if (item.isAction) {
                return (
                  <Link
                    key={item.href}
                    href={item.href}
                    className="flex flex-col items-center justify-center -mt-5 group"
                    aria-label={item.label}
                  >
                    <div className="w-12 h-12 rounded-full bg-blue-600 group-hover:bg-blue-700 text-white shadow-lg shadow-blue-500/30 flex items-center justify-center transition-transform group-active:scale-95 border-2 border-white dark:border-slate-900">
                      <Icon className="w-6 h-6" />
                    </div>
                    <span className="text-[10px] font-bold text-blue-600 dark:text-blue-400 mt-0.5">
                      {item.label}
                    </span>
                  </Link>
                );
              }

              return (
                <Link
                  key={item.href}
                  href={item.href}
                  className={`flex flex-col items-center justify-center min-w-[56px] min-h-[48px] py-1 rounded-lg transition-colors ${
                    item.isActive
                      ? "text-blue-600 dark:text-blue-400 font-bold"
                      : "text-slate-500 dark:text-slate-400 hover:text-slate-800 dark:hover:text-slate-200"
                  }`}
                  aria-label={item.label}
                  aria-current={item.isActive ? "page" : undefined}
                >
                  <Icon className="w-5 h-5 mb-1" />
                  <span className="text-[11px] leading-tight">{item.label}</span>
                </Link>
              );
            })}
          </div>
        </nav>
      )}
    </div>
  );
}

export default function PwaLayout({ children }: { children: React.ReactNode }) {
  return (
    <AuthProvider>
      <UpdateBanner />
      <AppNavigationWrapper>{children}</AppNavigationWrapper>
    </AuthProvider>
  );
}
