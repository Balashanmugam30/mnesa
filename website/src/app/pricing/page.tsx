import React from "react";
import Link from "next/link";
import { Container } from "@/components/ui/Container";
import { Heading } from "@/components/ui/Heading";
import { Button } from "@/components/ui/Button";
import { CheckCircle2, Sparkles, ArrowRight, ShieldCheck, HelpCircle } from "lucide-react";

export const metadata = {
  title: "Pricing & Early Access — MNESA",
  description: "Transparent, honest pricing. 100% free during our early access preview with all core features included.",
};

export default function PricingPage() {
  const freeFeatures = [
    "Native Android Share Target (sub-200ms intake)",
    "Screenshot & flyer OCR extraction",
    "Grounded AI structuring (Gemini / Ollama)",
    "Calibrated confidence scores & evidence citations",
    "Full-text opportunity library search & filtering",
    "Smart proactive notification cadences",
    "Local-first Room SQLite storage (100% offline access)",
    "Personal AI opportunity assistant queries",
    "Zero ads, zero data tracking or brokering",
  ];

  const roadmapFeatures = [
    "Collaborative opportunity workspaces for teams",
    "Google Calendar & Outlook bi-directional sync",
    "Multi-user application deadline alerts",
    "Export to Notion, Obsidian & CSV",
    "Custom webhook intake endpoints",
  ];

  return (
    <div className="py-12 sm:py-20">
      <Container size="lg">
        {/* Header */}
        <div className="text-center max-w-3xl mx-auto">
          <div className="inline-flex items-center space-x-2 px-3 py-1 rounded-full bg-blue-50 dark:bg-blue-950/60 border border-blue-200 dark:border-blue-800 text-brand-primary dark:text-blue-300 text-xs font-semibold mb-6">
            <Sparkles className="w-3.5 h-3.5" />
            <span>Honest & Transparent</span>
          </div>

          <Heading level={1} className="text-3xl sm:text-5xl font-extrabold tracking-tight">
            Simple, Transparent Access
          </Heading>

          <p className="mt-4 text-base sm:text-lg text-content-secondary dark:text-content-dark-secondary leading-relaxed">
            During our Early Access Developer Preview, MNESA is 100% free. No credit card required, no artificial rate limits, and no hidden subscriptions.
          </p>
        </div>

        {/* Pricing Cards */}
        <div className="mt-16 grid grid-cols-1 lg:grid-cols-2 gap-8 max-w-5xl mx-auto items-stretch">
          {/* Card 1: Early Access Plan */}
          <div className="p-8 rounded-2xl bg-surface-card dark:bg-surface-dark-card border-2 border-brand-primary shadow-lg flex flex-col justify-between relative overflow-hidden">
            <div className="absolute top-0 right-0 bg-brand-primary text-white text-[10px] font-extrabold uppercase px-4 py-1 rounded-bl-lg tracking-wider">
              Active Tier
            </div>

            <div>
              <span className="text-xs font-mono font-bold text-brand-primary uppercase">Current Release</span>
              <h2 className="text-2xl font-extrabold text-content-primary dark:text-content-dark-primary mt-1">
                Early Access Preview
              </h2>
              <p className="text-xs text-content-secondary dark:text-content-dark-secondary mt-2">
                Full access to all native mobile and AI extraction capabilities for individual builders and applicants.
              </p>

              <div className="mt-6 flex items-baseline space-x-2">
                <span className="text-4xl sm:text-5xl font-extrabold text-content-primary dark:text-content-dark-primary">
                  $0
                </span>
                <span className="text-xs font-semibold text-content-secondary dark:text-content-dark-secondary">
                  / forever during preview
                </span>
              </div>

              <div className="mt-8 border-t border-border-subtle dark:border-border-dark-subtle pt-6">
                <p className="text-xs font-bold uppercase tracking-wider text-content-primary dark:text-content-dark-primary mb-4">
                  What is included:
                </p>
                <ul className="space-y-3 text-xs text-content-secondary dark:text-content-dark-secondary">
                  {freeFeatures.map((item, idx) => (
                    <li key={idx} className="flex items-start space-x-2.5">
                      <CheckCircle2 className="w-4 h-4 text-emerald-600 dark:text-emerald-400 mt-0.5 flex-shrink-0" />
                      <span>{item}</span>
                    </li>
                  ))}
                </ul>
              </div>
            </div>

            <div className="mt-8 pt-6 border-t border-border-subtle dark:border-border-dark-subtle">
              <Link href="/download" className="block w-full">
                <Button variant="primary" size="lg" className="w-full space-x-2">
                  <span>Get Started for Free</span>
                  <ArrowRight className="w-4 h-4" />
                </Button>
              </Link>
              <p className="text-[11px] text-center text-content-secondary dark:text-content-dark-secondary mt-2">
                No payment information requested. Ever.
              </p>
            </div>
          </div>

          {/* Card 2: Roadmap Pro Tier */}
          <div className="p-8 rounded-2xl bg-surface-card dark:bg-surface-dark-card border border-border-subtle dark:border-border-dark-subtle flex flex-col justify-between opacity-85">
            <div>
              <span className="text-xs font-mono font-bold text-content-secondary dark:text-content-dark-secondary uppercase">
                Future Roadmap
              </span>
              <h2 className="text-2xl font-extrabold text-content-primary dark:text-content-dark-primary mt-1">
                MNESA Pro & Teams
              </h2>
              <p className="text-xs text-content-secondary dark:text-content-dark-secondary mt-2">
                Designed for student cohorts, research labs, hackathon teams, and career centers.
              </p>

              <div className="mt-6 flex items-baseline space-x-2">
                <span className="text-3xl sm:text-4xl font-extrabold text-content-primary dark:text-content-dark-primary">
                  Planned
                </span>
                <span className="text-xs font-semibold text-content-secondary dark:text-content-dark-secondary">
                  for General Availability
                </span>
              </div>

              <div className="mt-8 border-t border-border-subtle dark:border-border-dark-subtle pt-6">
                <p className="text-xs font-bold uppercase tracking-wider text-content-primary dark:text-content-dark-primary mb-4">
                  Roadmap Highlights:
                </p>
                <ul className="space-y-3 text-xs text-content-secondary dark:text-content-dark-secondary">
                  {roadmapFeatures.map((item, idx) => (
                    <li key={idx} className="flex items-start space-x-2.5">
                      <Sparkles className="w-4 h-4 text-brand-primary mt-0.5 flex-shrink-0" />
                      <span>{item}</span>
                    </li>
                  ))}
                </ul>
              </div>
            </div>

            <div className="mt-8 pt-6 border-t border-border-subtle dark:border-border-dark-subtle">
              <Link href="/contact" className="block w-full">
                <Button variant="secondary" size="lg" className="w-full">
                  Join Team Waitlist
                </Button>
              </Link>
              <p className="text-[11px] text-center text-content-secondary dark:text-content-dark-secondary mt-2">
                Contact us to provide feedback or suggest features.
              </p>
            </div>
          </div>
        </div>

        {/* Pricing Guarantee */}
        <div className="mt-16 p-6 rounded-2xl bg-blue-50/60 dark:bg-blue-950/40 border border-blue-200 dark:border-blue-900 text-xs text-content-secondary dark:text-content-dark-secondary max-w-3xl mx-auto flex items-start space-x-3">
          <ShieldCheck className="w-5 h-5 text-brand-primary mt-0.5 flex-shrink-0" />
          <p className="leading-relaxed">
            <strong>Data Guarantee:</strong> When MNESA introduces paid tiers in the future, your existing saved opportunities, reminders, and personal libraries will always remain accessible to you without forced paywalls or data hostage tactics.
          </p>
        </div>
      </Container>
    </div>
  );
}
