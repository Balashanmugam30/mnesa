"use client";

import React, { useEffect } from "react";
import Link from "next/link";
import { Container } from "@/components/ui/Container";
import { Heading } from "@/components/ui/Heading";
import { Button } from "@/components/ui/Button";
import { AlertTriangle, RefreshCw, Home } from "lucide-react";

export default function ErrorBoundary({
  error,
  reset,
}: {
  error: Error & { digest?: string };
  reset: () => void;
}) {
  useEffect(() => {
    // Log error to console in client
    console.error("MNESA Client Error Caught:", error);
  }, [error]);

  return (
    <div className="py-20 lg:py-32 flex items-center justify-center">
      <Container size="md" className="text-center">
        <div className="inline-flex items-center justify-center w-16 h-16 rounded-full bg-red-50 dark:bg-red-950/60 border border-red-200 dark:border-red-800 text-urgency-danger mb-6">
          <AlertTriangle className="w-8 h-8" />
        </div>

        <span className="text-xs font-mono font-bold uppercase tracking-widest text-urgency-danger">
          System Anomaly
        </span>

        <Heading level={1} className="text-3xl sm:text-4xl font-extrabold mt-2 tracking-tight">
          Something went wrong
        </Heading>

        <p className="mt-4 text-base text-content-secondary dark:text-content-dark-secondary max-w-lg mx-auto leading-relaxed">
          An unexpected error occurred while rendering this interface. Your stored opportunities and data remain secure in PostgreSQL and local offline storage.
        </p>

        {error.digest && (
          <div className="mt-4 p-3 bg-surface-card dark:bg-surface-dark-card border border-border-subtle dark:border-border-dark-subtle rounded-md text-xs font-mono text-content-secondary dark:text-content-dark-secondary inline-block">
            Error Digest: {error.digest}
          </div>
        )}

        <div className="mt-8 flex flex-wrap justify-center gap-4">
          <Button variant="primary" size="md" onClick={() => reset()} className="space-x-2">
            <RefreshCw className="w-4 h-4" />
            <span>Try Again</span>
          </Button>
          <Link href="/">
            <Button variant="secondary" size="md" className="space-x-2">
              <Home className="w-4 h-4" />
              <span>Return to Home</span>
            </Button>
          </Link>
        </div>
      </Container>
    </div>
  );
}
