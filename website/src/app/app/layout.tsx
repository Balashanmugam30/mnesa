"use client";

import React, { useEffect, useState } from "react";
import { usePathname } from "next/navigation";
import { AuthProvider, useAuth } from "@/context/AuthContext";
import { UpdateBanner } from "@/components/pwa/UpdateBanner";
import { api } from "@/lib/api-client";
import { GlassHeader, GlassNavigation } from "@/components/glass";

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

  const isAuthScreen = pathname === "/app/login" || pathname === "/app/register";
  const userInitials = user?.fullName ? user.fullName[0].toUpperCase() : undefined;

  return (
    <div className="relative flex flex-col min-h-screen bg-[#F1F5F9] dark:bg-[#070B14] text-slate-900 dark:text-slate-100 overflow-x-hidden selection:bg-blue-500/20">
      {/* Dynamic Ambient Mesh Canvas for Authentic Glass Refraction */}
      <div className="ambient-mesh-canvas" aria-hidden="true">
        <div className="ambient-orb-primary" />
        <div className="ambient-orb-secondary" />
        <div className="ambient-orb-accent" />
        <div className="ambient-orb-glow" />
      </div>

      {/* Top Liquid Glass Header */}
      {!isAuthScreen && (
        <GlassHeader
          isOnline={isOnline}
          backendStatus={backendStatus}
          userInitials={userInitials}
        />
      )}

      {/* Main Screen Body */}
      <main className="relative z-10 flex-1 max-w-md w-full mx-auto pb-28 px-3.5 sm:px-4 pt-3">
        {children}
      </main>

      {/* Bottom Floating Liquid Glass Navigation Dock */}
      {!isAuthScreen && <GlassNavigation />}
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
