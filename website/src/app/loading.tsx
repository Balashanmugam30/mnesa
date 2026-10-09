import React from "react";
import { Container } from "@/components/ui/Container";

export default function Loading() {
  return (
    <div className="py-20 flex items-center justify-center min-h-[60vh]">
      <Container size="md" className="space-y-6">
        <div className="flex flex-col items-center justify-center space-y-4">
          <div className="w-12 h-12 rounded-xl bg-brand-primary/10 border-2 border-brand-primary border-t-transparent animate-spin" />
          <p className="text-xs font-semibold uppercase tracking-wider text-content-secondary dark:text-content-dark-secondary animate-pulse">
            Loading MNESA Experience...
          </p>
        </div>

        {/* Skeleton cards */}
        <div className="w-full max-w-lg mx-auto space-y-3 pt-6">
          <div className="h-6 bg-slate-200 dark:bg-slate-800 rounded-md animate-pulse w-3/4 mx-auto" />
          <div className="h-4 bg-slate-100 dark:bg-slate-850 rounded-md animate-pulse w-1/2 mx-auto" />
          <div className="h-28 bg-surface-card dark:bg-surface-dark-card border border-border-subtle dark:border-border-dark-subtle rounded-lg animate-pulse" />
        </div>
      </Container>
    </div>
  );
}
