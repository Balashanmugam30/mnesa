import React from "react";
import Link from "next/link";
import { Container } from "@/components/ui/Container";
import { Heading } from "@/components/ui/Heading";
import { Button } from "@/components/ui/Button";
import {
  Compass,
  Share2,
  Cpu,
  ShieldCheck,
  BellRing,
  CheckCircle2,
  ArrowRight,
  Database,
  Smartphone,
  Eye,
  FileCheck,
} from "lucide-react";

export const metadata = {
  title: "How It Works — MNESA",
  description: "The complete journey from spotting a fleeting opportunity to structured execution in 7 distinct steps.",
};

export default function HowItWorksPage() {
  const steps = [
    {
      num: "01",
      title: "SEE — Spot an Opportunity Anywhere",
      icon: Eye,
      tag: "Intake Discovery",
      desc: "Whether you are browsing LinkedIn, Twitter/X, Reddit, university portals, or Discord, opportunities appear when you least expect them. Instead of cluttering your bookmarks or losing another screenshot in your photo album, MNESA captures it instantly.",
      detail: "Supports web URLs, raw announcement text, or screenshots containing promotional flyers and deadlines.",
    },
    {
      num: "02",
      title: "SHARE — Instant Android Share Target (<200ms)",
      icon: Share2,
      tag: "System Integration",
      desc: "Tap the standard Android Share icon in any app and select MNESA. Our native Android CaptureActivity receives the ACTION_SEND intent, streams it into local Room storage, and acknowledges your capture immediately without blocking your browsing flow.",
      detail: "Pure native Java 21 architecture ensures zero app stutter, minimal memory overhead, and instant responsiveness.",
    },
    {
      num: "03",
      title: "UNDERSTAND — SSRF-Safe Ingestion & AI Structuring",
      icon: Cpu,
      tag: "Automated Intelligence",
      desc: "MNESA's extraction pipeline safely fetches external web content using strict SSRF defenses (blocking private subnets and cloud metadata endpoints). Content is normalized, stripped of navigation boilerplate, and analyzed by Google Gemini or local Ollama models.",
      detail: "Extracts title, hosting organization, deadline timestamp, eligibility criteria, and key application steps into a deterministic JSON schema.",
    },
    {
      num: "04",
      title: "VERIFY — Calibrated Confidence & Highlighted Evidence",
      icon: FileCheck,
      tag: "Grounded Truth",
      desc: "Never trust a hallucinating AI model. MNESA cross-examines every extracted deadline against verbatim sentences in the original text. You receive a transparent confidence score (e.g. 96%) alongside clickable evidence snippets.",
      detail: "If an extraction falls below confidence thresholds, MNESA flags the opportunity for quick human review.",
    },
    {
      num: "05",
      title: "SAVE — Local-First Room SQLite & Cloud Sync",
      icon: Database,
      tag: "Offline Reliability",
      desc: "Your opportunities are immediately persisted in on-device SQLite database. You can search, edit, and organize them whether you are on high-speed fiber or completely offline in airplane mode. Changes synchronize seamlessly with our PostgreSQL backend.",
      detail: "Full conflict-free resolution and automatic background retries powered by Android WorkManager.",
    },
    {
      num: "06",
      title: "REMIND — Smart Notification Cadences",
      icon: BellRing,
      tag: "Proactive Follow-Through",
      desc: "MNESA calculates intelligent reminder cadences tailored to the deadline urgency (e.g. 7 days prior for essay prep, 48 hours prior for final review, and 12 hours prior for submission). Local alarms and Firebase Cloud Messaging ensure you never miss a date.",
      detail: "Includes timezone safety, configurable snooze options, and urgency escalation badges.",
    },
    {
      num: "07",
      title: "ACT & COMPLETE — Direct Portals & Status Tracking",
      icon: CheckCircle2,
      tag: "Achieve the Goal",
      desc: "When it's time to apply, tap the direct portal link directly from the opportunity card. Check off requirement items (resume, transcript, reference letters) and transition your status from SAVED to APPLIED to ACCEPTED.",
      detail: "Review velocity analytics and celebrate closed opportunities in your personalized insights dashboard.",
    },
  ];

  return (
    <div className="py-12 sm:py-20">
      <Container size="lg">
        {/* Hero Header */}
        <div className="text-center max-w-3xl mx-auto">
          <div className="inline-flex items-center space-x-2 px-3 py-1 rounded-full bg-blue-50 dark:bg-blue-950/60 border border-blue-200 dark:border-blue-800 text-brand-primary dark:text-blue-300 text-xs font-semibold mb-6">
            <Compass className="w-3.5 h-3.5" />
            <span>The Complete MNESA Lifecycle</span>
          </div>

          <Heading level={1} className="text-3xl sm:text-5xl font-extrabold tracking-tight">
            How MNESA Works
          </Heading>

          <p className="mt-4 text-base sm:text-lg text-content-secondary dark:text-content-dark-secondary leading-relaxed">
            From discovering a messy link to confirmed application submission. Here is how our native mobile client, secure backend, and AI extraction engine orchestrate your opportunities.
          </p>

          <div className="mt-8 flex flex-wrap justify-center gap-4">
            <Link href="/demo">
              <Button variant="primary" size="md" className="space-x-2">
                <span>Try Interactive Demo</span>
                <ArrowRight className="w-4 h-4" />
              </Button>
            </Link>
            <Link href="/download">
              <Button variant="secondary" size="md">
                Get Android Preview
              </Button>
            </Link>
          </div>
        </div>

        {/* Seven Steps Detailed Grid */}
        <div className="mt-20 space-y-12">
          {steps.map((s, idx) => {
            const Icon = s.icon;
            return (
              <div
                key={s.num}
                className="p-6 sm:p-8 rounded-2xl bg-surface-card dark:bg-surface-dark-card border border-border-subtle dark:border-border-dark-subtle relative overflow-hidden transition-all hover:border-brand-primary/40 shadow-sm"
              >
                <div className="flex flex-col md:flex-row md:items-start justify-between gap-6">
                  <div className="flex items-start space-x-4">
                    <div className="w-12 h-12 rounded-xl bg-blue-50 dark:bg-blue-950/80 border border-blue-200 dark:border-blue-800 text-brand-primary flex items-center justify-center font-bold text-lg flex-shrink-0 shadow-sm">
                      <Icon className="w-6 h-6" />
                    </div>
                    <div>
                      <div className="flex items-center space-x-3 mb-1">
                        <span className="font-mono text-xs font-bold text-brand-primary uppercase">
                          Phase {s.num}
                        </span>
                        <span className="text-[11px] font-semibold px-2 py-0.5 rounded-full bg-surface-base dark:bg-surface-dark-base border border-border-subtle dark:border-border-dark-subtle text-content-secondary dark:text-content-dark-secondary">
                          {s.tag}
                        </span>
                      </div>
                      <h2 className="text-xl sm:text-2xl font-bold text-content-primary dark:text-content-dark-primary">
                        {s.title}
                      </h2>
                      <p className="mt-3 text-sm text-content-secondary dark:text-content-dark-secondary leading-relaxed max-w-3xl">
                        {s.desc}
                      </p>
                      <div className="mt-4 p-3 rounded-lg bg-surface-base dark:bg-surface-dark-base border border-border-subtle dark:border-border-dark-subtle text-xs text-content-secondary dark:text-content-dark-secondary flex items-start space-x-2">
                        <span className="font-bold text-brand-primary flex-shrink-0">Architecture:</span>
                        <span>{s.detail}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            );
          })}
        </div>

        {/* Human in the loop & limitations disclaimer */}
        <div className="mt-16 p-6 rounded-2xl bg-blue-50/60 dark:bg-blue-950/30 border border-blue-200 dark:border-blue-900 text-xs text-content-secondary dark:text-content-dark-secondary space-y-2">
          <div className="flex items-center space-x-2 text-sm font-bold text-content-primary dark:text-content-dark-primary">
            <ShieldCheck className="w-5 h-5 text-brand-primary" />
            <span>Built Around Grounded Evidence & Verification</span>
          </div>
          <p className="leading-relaxed">
            MNESA empowers you with automated intelligence, but keeps you in full control. Extracted deadlines cite exact sentences from source pages and calculate confidence scores so you can verify application portals with complete peace of mind.
          </p>
        </div>
      </Container>
    </div>
  );
}
