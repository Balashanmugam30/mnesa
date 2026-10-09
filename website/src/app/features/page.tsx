import React from "react";
import Link from "next/link";
import { Container } from "@/components/ui/Container";
import { Heading } from "@/components/ui/Heading";
import { Button } from "@/components/ui/Button";
import {
  Share2,
  Image as ImageIcon,
  Cpu,
  FileCheck,
  Search,
  BellRing,
  Bot,
  BarChart3,
  Database,
  ShieldCheck,
  ArrowRight,
  Sparkles,
} from "lucide-react";

export const metadata = {
  title: "Features & Capabilities — MNESA",
  description: "Explore MNESA's complete opportunity management toolkit: native share intake, screenshot OCR, grounded AI, reminders, and offline Room database.",
};

export default function FeaturesPage() {
  const featureList = [
    {
      title: "System-Level Android Share Target",
      icon: Share2,
      badge: "Mobile Native",
      desc: "One-tap intake from any browser, social media app, or communication platform via Android's native ACTION_SEND intent. Captures URL or raw text in under 200 milliseconds without forcing you to switch workspaces.",
    },
    {
      title: "Screenshot & OCR Engine",
      icon: ImageIcon,
      badge: "Multimodal Vision",
      desc: "Upload event flyers, Instagram stories, or conference slides. Our OCR pipeline uses strict magic-byte validation, decompression limit guards, and automated text extraction with graceful fallback handling.",
    },
    {
      title: "Grounded AI Extraction",
      icon: Cpu,
      badge: "Zero Hallucination",
      desc: "Transforms raw HTML or unstructured announcements into strict JSON schemas: exact deadlines, hosting entities, eligibility requirements, stipend/prize amounts, and submission links.",
    },
    {
      title: "Calibrated Confidence & Evidence",
      icon: FileCheck,
      badge: "Verifiable Truth",
      desc: "Every extraction provides an overall confidence score and highlights the exact verbatim sentences where deadlines and criteria were discovered. Low-confidence extractions are flagged for human review.",
    },
    {
      title: "Opportunity Library & Smart Filters",
      icon: Search,
      badge: "Full-Text Search",
      desc: "Filter opportunities by urgency (Critical, Impending, Normal), category (Internship, Hackathon, Scholarship, Grant, Job), and lifecycle status. Fast full-text indexing lets you find what you need instantly.",
    },
    {
      title: "Intelligent Reminders & Push Engine",
      icon: BellRing,
      badge: "Proactive Follow-Through",
      desc: "Calculates smart reminder cadences (e.g. 7 days, 48 hours, 12 hours) automatically. Backed by Firebase Cloud Messaging (FCM), timezone safety, and flexible snooze controls so deadlines never slip.",
    },
    {
      title: "Personal AI Opportunity Assistant",
      icon: Bot,
      badge: "Scoped Context",
      desc: "Ask questions about your saved opportunities (e.g., 'Which scholarships require a letter of recommendation due this month?'). Answers are strictly scoped to your private library with zero data leakage.",
    },
    {
      title: "Velocity & Application Insights",
      icon: BarChart3,
      badge: "Action Analytics",
      desc: "Track your progress from discovery to application. Visual dashboards display your submission rate, upcoming deadline distribution, and historical acceptance conversion metrics.",
    },
    {
      title: "Local-First Room SQLite Engine",
      icon: Database,
      badge: "100% Offline Capable",
      desc: "Built on Android Room SQLite with bidirectional PostgreSQL synchronization. Browse, search, filter, and modify opportunities whether you have high-speed 5G or zero connectivity.",
    },
  ];

  return (
    <div className="py-12 sm:py-20">
      <Container size="lg">
        {/* Header */}
        <div className="text-center max-w-3xl mx-auto">
          <div className="inline-flex items-center space-x-2 px-3 py-1 rounded-full bg-blue-50 dark:bg-blue-950/60 border border-blue-200 dark:border-blue-800 text-brand-primary dark:text-blue-300 text-xs font-semibold mb-6">
            <Sparkles className="w-3.5 h-3.5" />
            <span>Built for High-Stakes Deadlines</span>
          </div>

          <Heading level={1} className="text-3xl sm:text-5xl font-extrabold tracking-tight">
            Complete Feature Toolkit
          </Heading>

          <p className="mt-4 text-base sm:text-lg text-content-secondary dark:text-content-dark-secondary leading-relaxed">
            Everything you need to capture, structure, remember, and execute career-defining opportunities across mobile and web.
          </p>

          <div className="mt-8 flex flex-wrap justify-center gap-4">
            <Link href="/demo">
              <Button variant="primary" size="md" className="space-x-2">
                <span>Try Live Demo</span>
                <ArrowRight className="w-4 h-4" />
              </Button>
            </Link>
            <Link href="/ai">
              <Button variant="secondary" size="md">
                Read AI Architecture
              </Button>
            </Link>
          </div>
        </div>

        {/* Feature Grid */}
        <div className="mt-16 grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {featureList.map((feat) => {
            const Icon = feat.icon;
            return (
              <div
                key={feat.title}
                className="p-6 rounded-2xl bg-surface-card dark:bg-surface-dark-card border border-border-subtle dark:border-border-dark-subtle hover:border-brand-primary/50 transition-all flex flex-col justify-between shadow-sm group"
              >
                <div>
                  <div className="flex items-center justify-between mb-4">
                    <div className="w-10 h-10 rounded-xl bg-blue-50 dark:bg-blue-950/80 border border-blue-200 dark:border-blue-800 text-brand-primary flex items-center justify-center transition-transform group-hover:scale-105">
                      <Icon className="w-5 h-5" />
                    </div>
                    <span className="text-[11px] font-bold px-2.5 py-0.5 rounded-full bg-surface-base dark:bg-surface-dark-base border border-border-subtle dark:border-border-dark-subtle text-content-secondary dark:text-content-dark-secondary">
                      {feat.badge}
                    </span>
                  </div>
                  <h2 className="text-base font-bold text-content-primary dark:text-content-dark-primary mb-2">
                    {feat.title}
                  </h2>
                  <p className="text-xs text-content-secondary dark:text-content-dark-secondary leading-relaxed">
                    {feat.desc}
                  </p>
                </div>
              </div>
            );
          })}
        </div>

        {/* Callout */}
        <div className="mt-16 p-8 rounded-2xl bg-gradient-to-r from-blue-50 to-indigo-50 dark:from-slate-900 dark:to-blue-950/60 border border-blue-200 dark:border-blue-900 text-center max-w-3xl mx-auto">
          <h2 className="text-xl font-bold text-content-primary dark:text-content-dark-primary">
            Ready to experience frictionless opportunity capture?
          </h2>
          <p className="mt-2 text-sm text-content-secondary dark:text-content-dark-secondary max-w-xl mx-auto">
            Test the extraction pipeline right now directly in your browser without creating an account.
          </p>
          <div className="mt-6">
            <Link href="/demo">
              <Button variant="primary" size="md">
                Launch Interactive Demo
              </Button>
            </Link>
          </div>
        </div>
      </Container>
    </div>
  );
}
