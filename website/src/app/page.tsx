import React from "react";
import Link from "next/link";
import { Container } from "@/components/ui/Container";
import { Heading } from "@/components/ui/Heading";
import { Button } from "@/components/ui/Button";
import { Card } from "@/components/ui/Card";
import { Badge } from "@/components/ui/Badge";
import {
  Clock,
  CheckCircle2,
  ShieldCheck,
  Zap,
  ArrowRight,
  Smartphone,
  Cpu,
  Database,
  BellRing,
  Sparkles,
  ExternalLink,
} from "lucide-react";

export default function HomePage() {
  return (
    <div className="py-12 sm:py-20">
      <Container>
        {/* Hero Section */}
        <div className="text-center max-w-4xl mx-auto">
          <div className="inline-flex items-center space-x-2 px-3.5 py-1.5 rounded-full bg-blue-50 dark:bg-blue-950/60 border border-blue-200 dark:border-blue-800 text-brand-primary dark:text-blue-300 text-xs font-semibold mb-6 shadow-sm">
            <Zap className="w-3.5 h-3.5" />
            <span>AI-Powered Opportunity Capture & Follow-Through</span>
          </div>

          <Heading level={1} className="text-4xl sm:text-6xl font-extrabold tracking-tight">
            Capture it. <span className="text-brand-primary">We&apos;ll remember.</span>
          </Heading>

          <p className="mt-5 text-lg sm:text-xl text-content-secondary dark:text-content-dark-secondary max-w-2xl mx-auto leading-relaxed">
            Turn fleeting career discoveries, scholarships, and hackathons into structured, proactive follow-through in less than 200 milliseconds.
          </p>

          <div className="mt-8 flex flex-wrap justify-center gap-4">
            <Link href="/download">
              <Button variant="primary" size="lg" className="space-x-2">
                <Smartphone className="w-5 h-5" />
                <span>Get Android App (Preview)</span>
              </Button>
            </Link>
            <Link href="/demo">
              <Button variant="secondary" size="lg" className="space-x-2">
                <Sparkles className="w-5 h-5 text-brand-primary" />
                <span>Try Live Demo</span>
              </Button>
            </Link>
          </div>

          {/* Value props pill row */}
          <div className="mt-10 flex flex-wrap justify-center items-center gap-4 text-xs font-semibold text-content-secondary dark:text-content-dark-secondary">
            <span className="inline-flex items-center space-x-1.5">
              <CheckCircle2 className="w-4 h-4 text-emerald-600 dark:text-emerald-400" />
              <span>Native Android Share Target</span>
            </span>
            <span className="hidden sm:inline text-slate-300 dark:text-slate-700">•</span>
            <span className="inline-flex items-center space-x-1.5">
              <CheckCircle2 className="w-4 h-4 text-emerald-600 dark:text-emerald-400" />
              <span>Grounded Evidence Citations</span>
            </span>
            <span className="hidden sm:inline text-slate-300 dark:text-slate-700">•</span>
            <span className="inline-flex items-center space-x-1.5">
              <CheckCircle2 className="w-4 h-4 text-emerald-600 dark:text-emerald-400" />
              <span>100% Offline-First SQLite</span>
            </span>
          </div>
        </div>

        {/* Core Interaction Flow: The Seven-Step Transformation */}
        <section aria-labelledby="interaction-heading" className="mt-24">
          <div className="text-center mb-12">
            <h2 id="interaction-heading" className="text-xs font-bold tracking-widest text-brand-primary uppercase">
              The Seven-Step Transformation
            </h2>
            <p className="mt-2 text-2xl sm:text-3xl font-extrabold text-content-primary dark:text-content-dark-primary">
              From Forgetting to Execution
            </p>
            <p className="mt-2 text-sm text-content-secondary dark:text-content-dark-secondary max-w-xl mx-auto">
              How MNESA shepherds your discoveries through a reliable, automated pipeline.
            </p>
          </div>

          <div className="grid grid-cols-2 sm:grid-cols-4 lg:grid-cols-7 gap-3 text-center">
            {[
              { step: "01", name: "SEE", desc: "Discover online", icon: "🌐" },
              { step: "02", name: "SHARE", desc: "Native Android share", icon: "📲" },
              { step: "03", name: "UNDERSTAND", desc: "AI metadata extraction", icon: "🧠" },
              { step: "04", name: "SAVE", desc: "Offline Room storage", icon: "💾" },
              { step: "05", name: "REMIND", desc: "Proactive cadences", icon: "⏰" },
              { step: "06", name: "ACT", desc: "Apply with 1-tap", icon: "⚡" },
              { step: "07", name: "COMPLETE", desc: "Opportunity achieved", icon: "🎯" },
            ].map((item) => (
              <div
                key={item.step}
                className="p-4 rounded-xl bg-surface-card dark:bg-surface-dark-card border border-border-subtle dark:border-border-dark-subtle hover:border-brand-primary/50 transition-all shadow-sm"
              >
                <div className="text-xl mb-1">{item.icon}</div>
                <span className="text-[10px] font-mono font-bold text-brand-primary">{item.step}</span>
                <p className="font-bold text-sm mt-0.5 text-content-primary dark:text-content-dark-primary">{item.name}</p>
                <p className="text-[11px] text-content-secondary dark:text-content-dark-secondary mt-1">{item.desc}</p>
              </div>
            ))}
          </div>

          <div className="mt-6 text-center">
            <Link
              href="/how-it-works"
              className="text-xs font-bold text-brand-primary hover:underline inline-flex items-center space-x-1"
            >
              <span>Explore the deep technical architecture of each step</span>
              <ArrowRight className="w-3.5 h-3.5" />
            </Link>
          </div>
        </section>

        {/* Opportunity Cards & Design Tokens */}
        <section aria-labelledby="design-system-heading" className="mt-24">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between mb-8 pb-4 border-b border-border-subtle dark:border-border-dark-subtle">
            <div>
              <h2 id="design-system-heading" className="text-2xl font-bold text-content-primary dark:text-content-dark-primary">
                Design System Preview
              </h2>
              <p className="text-sm text-content-secondary dark:text-content-dark-secondary mt-1">
                Centralized cross-platform tokens rendered natively in Next.js Tailwind
              </p>
            </div>
            <div className="mt-4 sm:mt-0 flex items-center space-x-2 text-xs font-semibold text-emerald-600 dark:text-emerald-400">
              <CheckCircle2 className="w-4 h-4" />
              <span>WCAG 2.1 AA Compliant</span>
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            {/* Card 1: Internship */}
            <Card variant="opportunity" className="flex flex-col justify-between">
              <div>
                <div className="flex items-center justify-between mb-3">
                  <Badge type="INTERNSHIP" />
                  <span className="inline-flex items-center text-xs font-semibold text-urgency-warning">
                    <Clock className="w-3.5 h-3.5 mr-1" />
                    5 days left
                  </span>
                </div>
                <h3 className="font-bold text-base text-content-primary dark:text-content-dark-primary">
                  Software Engineering Intern — Summer 2026
                </h3>
                <p className="text-xs text-content-secondary dark:text-content-dark-secondary mt-1">
                  Google LLC • Mountain View, CA
                </p>
                <p className="text-xs text-content-secondary dark:text-content-dark-secondary mt-3 line-clamp-2">
                  Building next-generation distributed systems and developer tooling. Requires proficiency in Java or C++.
                </p>
              </div>
              <div className="mt-6 pt-4 border-t border-border-subtle dark:border-border-dark-subtle flex items-center justify-between text-xs">
                <span className="font-mono text-content-secondary dark:text-content-dark-secondary">Due: May 15, 2026</span>
                <span className="font-bold text-brand-primary">Apply Now →</span>
              </div>
            </Card>

            {/* Card 2: Hackathon */}
            <Card variant="opportunity" className="flex flex-col justify-between">
              <div>
                <div className="flex items-center justify-between mb-3">
                  <Badge type="HACKATHON" />
                  <span className="inline-flex items-center text-xs font-semibold text-urgency-danger">
                    <Clock className="w-3.5 h-3.5 mr-1" />
                    2 days left
                  </span>
                </div>
                <h3 className="font-bold text-base text-content-primary dark:text-content-dark-primary">
                  HackMIT 2026 — Global Collegiate Hackathon
                </h3>
                <p className="text-xs text-content-secondary dark:text-content-dark-secondary mt-1">
                  MIT Tech Club • Cambridge, MA
                </p>
                <p className="text-xs text-content-secondary dark:text-content-dark-secondary mt-3 line-clamp-2">
                  48-hour student innovation sprint with tracks in Artificial Intelligence, HealthTech, and Sustainability.
                </p>
              </div>
              <div className="mt-6 pt-4 border-t border-border-subtle dark:border-border-dark-subtle flex items-center justify-between text-xs">
                <span className="font-mono text-content-secondary dark:text-content-dark-secondary">Due: April 20, 2026</span>
                <span className="font-bold text-brand-primary">Register Team →</span>
              </div>
            </Card>

            {/* Card 3: Scholarship */}
            <Card variant="opportunity" className="flex flex-col justify-between">
              <div>
                <div className="flex items-center justify-between mb-3">
                  <Badge type="SCHOLARSHIP" />
                  <span className="inline-flex items-center text-xs font-semibold text-status-success">
                    <Clock className="w-3.5 h-3.5 mr-1" />
                    30 days left
                  </span>
                </div>
                <h3 className="font-bold text-base text-content-primary dark:text-content-dark-primary">
                  Generation Google Scholarship (APAC)
                </h3>
                <p className="text-xs text-content-secondary dark:text-content-dark-secondary mt-1">
                  Google Education • Global
                </p>
                <p className="text-xs text-content-secondary dark:text-content-dark-secondary mt-3 line-clamp-2">
                  Supporting undergraduate women in computer science and technology. Financial award plus mentoring.
                </p>
              </div>
              <div className="mt-6 pt-4 border-t border-border-subtle dark:border-border-dark-subtle flex items-center justify-between text-xs">
                <span className="font-mono text-content-secondary dark:text-content-dark-secondary">Due: June 30, 2026</span>
                <span className="font-bold text-brand-primary">View Details →</span>
              </div>
            </Card>
          </div>
        </section>

        {/* Feature Highlights Grid */}
        <section aria-labelledby="highlights-heading" className="mt-24">
          <div className="text-center mb-12">
            <h2 id="highlights-heading" className="text-xs font-bold tracking-widest text-brand-primary uppercase">
              Engineered for Reliability
            </h2>
            <p className="mt-2 text-2xl sm:text-3xl font-extrabold text-content-primary dark:text-content-dark-primary">
              Why MNESA Outperforms Bookmarks & Notes
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div className="p-6 rounded-2xl bg-surface-card dark:bg-surface-dark-card border border-border-subtle dark:border-border-dark-subtle">
              <div className="w-10 h-10 rounded-xl bg-blue-50 dark:bg-blue-950 text-brand-primary flex items-center justify-center font-bold mb-4">
                <Smartphone className="w-5 h-5" />
              </div>
              <h3 className="font-bold text-base text-content-primary dark:text-content-dark-primary mb-2">
                Sub-200ms Android Share Target
              </h3>
              <p className="text-xs text-content-secondary dark:text-content-dark-secondary leading-relaxed">
                Native Java 21 architecture captures links or text directly from Android&apos;s system share sheet without launching a heavy webview or stalling your phone.
              </p>
            </div>

            <div className="p-6 rounded-2xl bg-surface-card dark:bg-surface-dark-card border border-border-subtle dark:border-border-dark-subtle">
              <div className="w-10 h-10 rounded-xl bg-purple-50 dark:bg-purple-950 text-purple-600 dark:text-purple-400 flex items-center justify-center font-bold mb-4">
                <Cpu className="w-5 h-5" />
              </div>
              <h3 className="font-bold text-base text-content-primary dark:text-content-dark-primary mb-2">
                Grounded Verifiable Citations
              </h3>
              <p className="text-xs text-content-secondary dark:text-content-dark-secondary leading-relaxed">
                Zero hallucinated deadlines. Every extracted date is cross-verified against verbatim sentences with transparent confidence scores.
              </p>
            </div>

            <div className="p-6 rounded-2xl bg-surface-card dark:bg-surface-dark-card border border-border-subtle dark:border-border-dark-subtle">
              <div className="w-10 h-10 rounded-xl bg-emerald-50 dark:bg-emerald-950 text-emerald-600 dark:text-emerald-400 flex items-center justify-center font-bold mb-4">
                <Database className="w-5 h-5" />
              </div>
              <h3 className="font-bold text-base text-content-primary dark:text-content-dark-primary mb-2">
                Offline-First Room SQLite
              </h3>
              <p className="text-xs text-content-secondary dark:text-content-dark-secondary leading-relaxed">
                Your entire opportunity library is stored on your device. Search, filter, and review your applications seamlessly on a plane or offline.
              </p>
            </div>
          </div>
        </section>

        {/* Live Simulator Teaser Callout */}
        <section className="mt-24 p-8 sm:p-12 rounded-3xl bg-gradient-to-r from-blue-50 to-indigo-50 dark:from-slate-900 dark:to-blue-950/60 border border-blue-200 dark:border-blue-900 text-center max-w-4xl mx-auto shadow-md">
          <div className="inline-flex items-center space-x-1.5 px-3 py-1 rounded-full bg-blue-100 dark:bg-blue-900 text-brand-primary dark:text-blue-200 text-xs font-bold mb-4">
            <Sparkles className="w-3.5 h-3.5" />
            <span>Interactive Web Simulator</span>
          </div>
          <h2 className="text-2xl sm:text-4xl font-extrabold text-content-primary dark:text-content-dark-primary tracking-tight">
            See the AI Extraction Engine in Action
          </h2>
          <p className="mt-3 text-sm sm:text-base text-content-secondary dark:text-content-dark-secondary max-w-2xl mx-auto leading-relaxed">
            Choose from sample fellowships, hackathons, and scholarships to test our SSRF sanitization, Pydantic schema structuring, and confidence scoring right in your browser.
          </p>
          <div className="mt-8">
            <Link href="/demo">
              <Button variant="primary" size="lg" className="space-x-2">
                <span>Launch Interactive Demo</span>
                <ArrowRight className="w-4 h-4" />
              </Button>
            </Link>
          </div>
        </section>
      </Container>
    </div>
  );
}
