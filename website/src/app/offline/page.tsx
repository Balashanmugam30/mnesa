import React from "react";
import Link from "next/link";
import { Container } from "@/components/ui/Container";
import { Heading } from "@/components/ui/Heading";
import { Button } from "@/components/ui/Button";
import { WifiOff, Database, CheckCircle2, Smartphone, ArrowRight } from "lucide-react";

export const metadata = {
  title: "Offline Mode & Resilience — MNESA",
  description: "Learn how MNESA offline-first architecture preserves your opportunities without connectivity.",
};

export default function OfflinePage() {
  return (
    <div className="py-16 sm:py-24">
      <Container size="md">
        <div className="text-center">
          <div className="inline-flex items-center justify-center w-16 h-16 rounded-2xl bg-blue-50 dark:bg-blue-950/60 border border-blue-200 dark:border-blue-800 text-brand-primary mb-6 shadow-sm">
            <WifiOff className="w-8 h-8" />
          </div>

          <span className="text-xs font-mono font-bold uppercase tracking-widest text-brand-primary">
            Offline-First Guarantees
          </span>

          <Heading level={1} className="text-3xl sm:text-4xl font-extrabold mt-2 tracking-tight">
            You Are Viewing MNESA Offline Architecture
          </Heading>

          <p className="mt-4 text-base text-content-secondary dark:text-content-dark-secondary max-w-lg mx-auto leading-relaxed">
            In poor network conditions or airplane mode, MNESA ensures you never lose a single captured opportunity or deadline reminder.
          </p>
        </div>

        <div className="mt-12 space-y-6">
          <div className="p-6 rounded-xl bg-surface-card dark:bg-surface-dark-card border border-border-subtle dark:border-border-dark-subtle">
            <h2 className="text-base font-bold text-content-primary dark:text-content-dark-primary flex items-center space-x-2">
              <Database className="w-5 h-5 text-brand-primary" />
              <span>Native Android Room SQLite Database</span>
            </h2>
            <p className="mt-2 text-sm text-content-secondary dark:text-content-dark-secondary leading-relaxed">
              Every captured opportunity, reminder, and status transition is saved to on-device SQLite before any network request is attempted. You can browse, search, and edit your opportunities completely offline.
            </p>
          </div>

          <div className="p-6 rounded-xl bg-surface-card dark:bg-surface-dark-card border border-border-subtle dark:border-border-dark-subtle">
            <h2 className="text-base font-bold text-content-primary dark:text-content-dark-primary flex items-center space-x-2">
              <Smartphone className="w-5 h-5 text-brand-primary" />
              <span>Offline Share Intake Queue</span>
            </h2>
            <p className="mt-2 text-sm text-content-secondary dark:text-content-dark-secondary leading-relaxed">
              When you share links or screenshots while disconnected, MNESA persists raw captures locally with a pending status. Android WorkManager automatically triggers background synchronization when a reliable network connection is restored.
            </p>
          </div>

          <div className="p-6 rounded-xl bg-surface-card dark:bg-surface-dark-card border border-border-subtle dark:border-border-dark-subtle">
            <h2 className="text-base font-bold text-content-primary dark:text-content-dark-primary flex items-center space-x-2">
              <CheckCircle2 className="w-5 h-5 text-emerald-600 dark:text-emerald-400" />
              <span>Deterministic Conflict-Free Sync</span>
            </h2>
            <p className="mt-2 text-sm text-content-secondary dark:text-content-dark-secondary leading-relaxed">
              Server updates use timestamped revision tracking so your local edits are never overwritten by stale cloud responses.
            </p>
          </div>
        </div>

        <div className="mt-10 text-center flex flex-wrap justify-center gap-4">
          <Link href="/download">
            <Button variant="primary" size="md">
              Download Offline-Ready Android App
            </Button>
          </Link>
          <Link href="/">
            <Button variant="secondary" size="md">
              Return to Website
            </Button>
          </Link>
        </div>
      </Container>
    </div>
  );
}
