import React from "react";
import Link from "next/link";
import { Container } from "@/components/ui/Container";
import { Heading } from "@/components/ui/Heading";
import { Button } from "@/components/ui/Button";
import {
  Download,
  Smartphone,
  CheckCircle2,
  ExternalLink,
  ShieldCheck,
  Cpu,
  Layers,
  AlertCircle,
} from "lucide-react";

export const metadata = {
  title: "Download Android App — MNESA",
  description: "Get the MNESA Android early access preview APK for instant opportunity capture, offline Room storage, and smart deadline notifications.",
};

export default function DownloadPage() {
  const systemSpecs = [
    { label: "Supported OS", value: "Android 8.0 (API 26) to Android 15 (API 35)" },
    { label: "Core Stack", value: "Pure Java 21, AndroidX, Material 3" },
    { label: "Local Database", value: "Room SQLite (v5 schema) with WorkManager sync" },
    { label: "Estimated Size", value: "~18 MB APK" },
    { label: "Permissions", value: "Internet, Push Notifications, Alarm Scheduling" },
  ];

  return (
    <div className="py-12 sm:py-20">
      <Container size="lg">
        {/* Header */}
        <div className="text-center max-w-3xl mx-auto">
          <div className="inline-flex items-center space-x-2 px-3 py-1 rounded-full bg-blue-50 dark:bg-blue-950/60 border border-blue-200 dark:border-blue-800 text-brand-primary dark:text-blue-300 text-xs font-semibold mb-6">
            <Smartphone className="w-3.5 h-3.5" />
            <span>Native Android Experience</span>
          </div>

          <Heading level={1} className="text-3xl sm:text-5xl font-extrabold tracking-tight">
            Get MNESA for Android
          </Heading>

          <p className="mt-4 text-base sm:text-lg text-content-secondary dark:text-content-dark-secondary leading-relaxed">
            Experience ultra-fast opportunity capture right from your Android Share Sheet. Offline-first, lightweight, and tailored for zero-friction follow-through.
          </p>
        </div>

        {/* Main Download Card */}
        <div className="mt-16 max-w-3xl mx-auto p-8 rounded-2xl bg-surface-card dark:bg-surface-dark-card border-2 border-brand-primary shadow-xl">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-6 pb-6 border-b border-border-subtle dark:border-border-dark-subtle">
            <div>
              <span className="text-xs font-mono font-bold text-brand-primary uppercase">
                Early Access Preview
              </span>
              <h2 className="text-2xl font-extrabold text-content-primary dark:text-content-dark-primary mt-1">
                MNESA Android Client v0.9.0
              </h2>
              <p className="text-xs text-content-secondary dark:text-content-dark-secondary mt-1">
                Compiled Debug & Release Candidate Build for Android 8.0+
              </p>
            </div>

            <a
              href="https://github.com/Balashanmugam30/mnesa/releases"
              target="_blank"
              rel="noopener noreferrer"
              className="inline-flex items-center justify-center space-x-2 bg-brand-primary hover:bg-brand-primary-hover active:bg-blue-800 text-white font-bold text-sm px-6 py-3.5 rounded-xl shadow-md transition-all flex-shrink-0"
            >
              <Download className="w-5 h-5" />
              <span>Download APK</span>
              <ExternalLink className="w-4 h-4 ml-1 opacity-75" />
            </a>
          </div>

          {/* Sideloading Instructions */}
          <div className="mt-8">
            <h3 className="text-sm font-bold uppercase tracking-wider text-content-primary dark:text-content-dark-primary mb-4 flex items-center space-x-2">
              <ShieldCheck className="w-4 h-4 text-brand-primary" />
              <span>How to Install on Your Android Device</span>
            </h3>
            <ol className="space-y-3 text-xs text-content-secondary dark:text-content-dark-secondary">
              <li className="flex items-start space-x-3">
                <span className="w-5 h-5 rounded-full bg-blue-100 dark:bg-blue-900 text-brand-primary font-bold flex items-center justify-center flex-shrink-0 text-[11px]">
                  1
                </span>
                <span>
                  Tap <strong>Download APK</strong> above or visit our GitHub Releases page to download the latest <code className="font-mono text-[11px] bg-slate-100 dark:bg-slate-800 px-1 py-0.5 rounded">app-debug.apk</code>.
                </span>
              </li>
              <li className="flex items-start space-x-3">
                <span className="w-5 h-5 rounded-full bg-blue-100 dark:bg-blue-900 text-brand-primary font-bold flex items-center justify-center flex-shrink-0 text-[11px]">
                  2
                </span>
                <span>
                  When prompted by Android, permit installation from your browser or file manager (Settings → Security → &quot;Install unknown apps&quot;).
                </span>
              </li>
              <li className="flex items-start space-x-3">
                <span className="w-5 h-5 rounded-full bg-blue-100 dark:bg-blue-900 text-brand-primary font-bold flex items-center justify-center flex-shrink-0 text-[11px]">
                  3
                </span>
                <span>
                  Open the installed MNESA app, sign in with your account or register, and grant notification permissions for scheduled reminders.
                </span>
              </li>
              <li className="flex items-start space-x-3">
                <span className="w-5 h-5 rounded-full bg-blue-100 dark:bg-blue-900 text-brand-primary font-bold flex items-center justify-center flex-shrink-0 text-[11px]">
                  4
                </span>
                <span>
                  Test the integration: open Chrome, navigate to any announcement, tap <strong>Share</strong>, and choose <strong>MNESA</strong>!
                </span>
              </li>
            </ol>
          </div>

          {/* Technical Specifications */}
          <div className="mt-8 pt-6 border-t border-border-subtle dark:border-border-dark-subtle">
            <h3 className="text-xs font-bold uppercase tracking-wider text-content-secondary dark:text-content-dark-secondary mb-3">
              System Specifications & Architecture
            </h3>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-2 text-xs">
              {systemSpecs.map((spec, i) => (
                <div
                  key={i}
                  className="p-3 rounded-lg bg-surface-base dark:bg-surface-dark-base border border-border-subtle dark:border-border-dark-subtle flex flex-col justify-between"
                >
                  <span className="text-[11px] text-content-secondary dark:text-content-dark-secondary">
                    {spec.label}
                  </span>
                  <span className="font-semibold text-content-primary dark:text-content-dark-primary mt-0.5">
                    {spec.value}
                  </span>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Play Store notice */}
        <div className="mt-12 p-6 rounded-2xl bg-blue-50/60 dark:bg-blue-950/30 border border-blue-200 dark:border-blue-900 text-center max-w-xl mx-auto text-xs text-content-secondary dark:text-content-dark-secondary space-y-2">
          <p className="font-bold text-content-primary dark:text-content-dark-primary">
            Google Play Store Release Status
          </p>
          <p>
            MNESA is currently distributed via GitHub Releases during our early-access phase while Play Console store listings and automated review preparations are completed.
          </p>
        </div>
      </Container>
    </div>
  );
}
