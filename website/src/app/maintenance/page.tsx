import React from "react";
import Link from "next/link";
import { Container } from "@/components/ui/Container";
import { Heading } from "@/components/ui/Heading";
import { Button } from "@/components/ui/Button";
import { Wrench, CheckCircle, Clock, Smartphone, ShieldCheck } from "lucide-react";

export const metadata = {
  title: "System Maintenance — MNESA",
  description: "MNESA cloud infrastructure is undergoing planned maintenance.",
};

export default function MaintenancePage() {
  return (
    <div className="py-16 sm:py-24">
      <Container size="md">
        <div className="text-center">
          <div className="inline-flex items-center justify-center w-16 h-16 rounded-2xl bg-amber-50 dark:bg-amber-950/60 border border-amber-200 dark:border-amber-800 text-urgency-warning mb-6 shadow-sm">
            <Wrench className="w-8 h-8" />
          </div>

          <span className="text-xs font-mono font-bold uppercase tracking-widest text-urgency-warning">
            Scheduled Infrastructure Upgrade
          </span>

          <Heading level={1} className="text-3xl sm:text-4xl font-extrabold mt-2 tracking-tight">
            System Maintenance in Progress
          </Heading>

          <p className="mt-4 text-base text-content-secondary dark:text-content-dark-secondary max-w-lg mx-auto leading-relaxed">
            We are performing planned database optimization and service upgrades to improve extraction speed and reliability.
          </p>
        </div>

        {/* Offline Protection Notice */}
        <div className="mt-10 p-6 rounded-xl bg-surface-card dark:bg-surface-dark-card border border-border-subtle dark:border-border-dark-subtle space-y-4">
          <h2 className="text-base font-bold text-content-primary dark:text-content-dark-primary flex items-center space-x-2">
            <Smartphone className="w-5 h-5 text-brand-primary" />
            <span>Your Android App Continues Working Offline</span>
          </h2>
          <p className="text-sm text-content-secondary dark:text-content-dark-secondary leading-relaxed">
            MNESA is architected offline-first. Even while cloud endpoints are briefly unavailable:
          </p>
          <ul className="space-y-2 text-xs text-content-secondary dark:text-content-dark-secondary">
            <li className="flex items-start space-x-2">
              <CheckCircle className="w-4 h-4 text-emerald-600 dark:text-emerald-400 mt-0.5 flex-shrink-0" />
              <span>All previously captured opportunities remain fully readable in your local Room SQLite database.</span>
            </li>
            <li className="flex items-start space-x-2">
              <CheckCircle className="w-4 h-4 text-emerald-600 dark:text-emerald-400 mt-0.5 flex-shrink-0" />
              <span>Scheduled local notification reminders will continue to fire on time on your device.</span>
            </li>
            <li className="flex items-start space-x-2">
              <CheckCircle className="w-4 h-4 text-emerald-600 dark:text-emerald-400 mt-0.5 flex-shrink-0" />
              <span>Any captures made via Android Share Target will be queued locally and synchronized automatically when maintenance finishes.</span>
            </li>
          </ul>
        </div>

        {/* Status card */}
        <div className="mt-6 p-4 rounded-lg bg-surface-base dark:bg-surface-dark-base border border-border-subtle dark:border-border-dark-subtle flex flex-col sm:flex-row items-center justify-between text-xs gap-3">
          <div className="flex items-center space-x-2 text-content-secondary dark:text-content-dark-secondary">
            <Clock className="w-4 h-4 text-urgency-warning" />
            <span>Estimated maintenance window: Under 30 minutes</span>
          </div>
          <span className="font-mono text-content-secondary dark:text-content-dark-secondary">
            Status: UPGRADING_FLYWAY_V7
          </span>
        </div>

        <div className="mt-8 text-center">
          <Link href="/">
            <Button variant="secondary" size="md">
              Check Again & Return Home
            </Button>
          </Link>
        </div>
      </Container>
    </div>
  );
}
