import React from "react";
import Link from "next/link";
import { Container } from "@/components/ui/Container";
import { Heading } from "@/components/ui/Heading";
import { Button } from "@/components/ui/Button";
import { ServerCrash, AlertCircle, Home, Mail } from "lucide-react";

export const metadata = {
  title: "Service Unavailable — MNESA",
  description: "The requested MNESA service is temporarily unavailable.",
};

export default function UnavailablePage() {
  return (
    <div className="py-16 sm:py-24">
      <Container size="md" className="text-center">
        <div className="inline-flex items-center justify-center w-16 h-16 rounded-2xl bg-red-50 dark:bg-red-950/60 border border-red-200 dark:border-red-800 text-urgency-danger mb-6 shadow-sm">
          <ServerCrash className="w-8 h-8" />
        </div>

        <span className="text-xs font-mono font-bold uppercase tracking-widest text-urgency-danger">
          Error 503
        </span>

        <Heading level={1} className="text-3xl sm:text-4xl font-extrabold mt-2 tracking-tight">
          Service Temporarily Unavailable
        </Heading>

        <p className="mt-4 text-base text-content-secondary dark:text-content-dark-secondary max-w-lg mx-auto leading-relaxed">
          The requested backend endpoint or AI extraction service is experiencing high load or undergoing brief traffic balancing.
        </p>

        <div className="mt-8 p-4 bg-surface-card dark:bg-surface-dark-card border border-border-subtle dark:border-border-dark-subtle rounded-xl text-xs text-left max-w-md mx-auto space-y-2">
          <div className="flex items-center space-x-2 font-bold text-content-primary dark:text-content-dark-primary">
            <AlertCircle className="w-4 h-4 text-urgency-warning" />
            <span>Recommended Actions</span>
          </div>
          <p className="text-content-secondary dark:text-content-dark-secondary">
            1. Wait 30 seconds and refresh the page.
          </p>
          <p className="text-content-secondary dark:text-content-dark-secondary">
            2. If using the Android client, your capture has already been safely cached to local storage.
          </p>
          <p className="text-content-secondary dark:text-content-dark-secondary">
            3. Check our system maintenance status or contact support if the issue persists.
          </p>
        </div>

        <div className="mt-8 flex flex-wrap justify-center gap-4">
          <Link href="/">
            <Button variant="primary" size="md" className="space-x-2">
              <Home className="w-4 h-4" />
              <span>Back to Home</span>
            </Button>
          </Link>
          <Link href="/contact">
            <Button variant="secondary" size="md" className="space-x-2">
              <Mail className="w-4 h-4" />
              <span>Report Downtime</span>
            </Button>
          </Link>
        </div>
      </Container>
    </div>
  );
}
