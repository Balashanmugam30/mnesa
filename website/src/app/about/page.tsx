import React from "react";
import Link from "next/link";
import { Container } from "@/components/ui/Container";
import { Heading } from "@/components/ui/Heading";
import { Button } from "@/components/ui/Button";
import { Compass, Target, ShieldCheck, Heart, Sparkles, ArrowRight, Smartphone } from "lucide-react";

export const metadata = {
  title: "About Us & Mission — MNESA",
  description: "Learn about the mission behind MNESA: eliminating missed career, academic, and grant deadlines through zero-friction capture and grounded AI.",
};

export default function AboutPage() {
  return (
    <div className="py-12 sm:py-20">
      <Container size="lg">
        {/* Header */}
        <div className="text-center max-w-3xl mx-auto">
          <div className="inline-flex items-center space-x-2 px-3 py-1 rounded-full bg-blue-50 dark:bg-blue-950/60 border border-blue-200 dark:border-blue-800 text-brand-primary dark:text-blue-300 text-xs font-semibold mb-6">
            <Target className="w-3.5 h-3.5" />
            <span>Our Mission & Principles</span>
          </div>

          <Heading level={1} className="text-3xl sm:text-5xl font-extrabold tracking-tight">
            Bridging Discovery to Execution
          </Heading>

          <p className="mt-4 text-base sm:text-lg text-content-secondary dark:text-content-dark-secondary leading-relaxed">
            Every day, thousands of students, researchers, and engineers discover career-altering opportunities—only to lose them in forgotten bookmarks, buried screenshots, or tab graveyards until the deadline has passed.
          </p>
        </div>

        {/* The Problem Narrative */}
        <div className="mt-16 max-w-3xl mx-auto space-y-6 text-sm text-content-primary dark:text-content-dark-primary leading-relaxed">
          <div className="p-8 rounded-2xl bg-surface-card dark:bg-surface-dark-card border border-border-subtle dark:border-border-dark-subtle">
            <h2 className="text-xl font-bold mb-3 text-content-primary dark:text-content-dark-primary">
              The Modern Opportunity Paradox
            </h2>
            <p className="text-content-secondary dark:text-content-dark-secondary leading-relaxed">
              Discovering opportunities has never been easier: social platforms, university newsletters, and online communities announce scholarships, hackathons, and internships constantly. But turning a fleeting discovery into a completed application requires remembering deadlines, tracking prerequisite documents, and managing calendar dates across multiple portals.
            </p>
            <p className="text-content-secondary dark:text-content-dark-secondary leading-relaxed mt-3">
              Most note-taking tools are passive silos. Bookmarking a link does not alert you when an application deadline is three days away. MNESA was built to bridge that exact gap: transforming an instantaneous share into structured, proactive follow-through.
            </p>
          </div>

          {/* Core Principles */}
          <div className="pt-6">
            <h2 className="text-xl font-bold mb-6 text-center text-content-primary dark:text-content-dark-primary">
              Our Core Engineering Principles
            </h2>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div className="p-6 rounded-xl bg-surface-card dark:bg-surface-dark-card border border-border-subtle dark:border-border-dark-subtle">
                <div className="w-9 h-9 rounded-lg bg-blue-50 dark:bg-blue-950 text-brand-primary flex items-center justify-center font-bold mb-3">
                  <Smartphone className="w-5 h-5" />
                </div>
                <h3 className="font-bold text-base text-content-primary dark:text-content-dark-primary mb-1">
                  Ultra-Fast Native Intake
                </h3>
                <p className="text-xs text-content-secondary dark:text-content-dark-secondary leading-relaxed">
                  Opportunity capture must be instantaneous. We built our Android client in pure native Java with Room SQLite so sharing takes less than 200 milliseconds without interrupting your workflow.
                </p>
              </div>

              <div className="p-6 rounded-xl bg-surface-card dark:bg-surface-dark-card border border-border-subtle dark:border-border-dark-subtle">
                <div className="w-9 h-9 rounded-lg bg-blue-50 dark:bg-blue-950 text-brand-primary flex items-center justify-center font-bold mb-3">
                  <ShieldCheck className="w-5 h-5" />
                </div>
                <h3 className="font-bold text-base text-content-primary dark:text-content-dark-primary mb-1">
                  Grounded Verifiable Truth
                </h3>
                <p className="text-xs text-content-secondary dark:text-content-dark-secondary leading-relaxed">
                  We reject AI hallucinations. Every extracted deadline cites the exact sentences where it was discovered, calibrated with confidence scores so users can verify dates with total confidence.
                </p>
              </div>

              <div className="p-6 rounded-xl bg-surface-card dark:bg-surface-dark-card border border-border-subtle dark:border-border-dark-subtle">
                <div className="w-9 h-9 rounded-lg bg-blue-50 dark:bg-blue-950 text-brand-primary flex items-center justify-center font-bold mb-3">
                  <Heart className="w-5 h-5" />
                </div>
                <h3 className="font-bold text-base text-content-primary dark:text-content-dark-primary mb-1">
                  User Sovereignty & Privacy
                </h3>
                <p className="text-xs text-content-secondary dark:text-content-dark-secondary leading-relaxed">
                  Your career aspirations belong to you. We maintain strict tenant isolation in PostgreSQL, never monetize user data, and provide instant one-click complete account and data deletion.
                </p>
              </div>

              <div className="p-6 rounded-xl bg-surface-card dark:bg-surface-dark-card border border-border-subtle dark:border-border-dark-subtle">
                <div className="w-9 h-9 rounded-lg bg-blue-50 dark:bg-blue-950 text-brand-primary flex items-center justify-center font-bold mb-3">
                  <Sparkles className="w-5 h-5" />
                </div>
                <h3 className="font-bold text-base text-content-primary dark:text-content-dark-primary mb-1">
                  Proactive Follow-Through
                </h3>
                <p className="text-xs text-content-secondary dark:text-content-dark-secondary leading-relaxed">
                  Capturing is only half the battle. MNESA calculates multi-stage reminder schedules so you are nudged to draft essays, collect recommendations, and submit well before the portal closes.
                </p>
              </div>
            </div>
          </div>
        </div>

        {/* Call to action */}
        <div className="mt-16 text-center">
          <Link href="/download">
            <Button variant="primary" size="md" className="space-x-2">
              <span>Try MNESA Today</span>
              <ArrowRight className="w-4 h-4" />
            </Button>
          </Link>
        </div>
      </Container>
    </div>
  );
}
