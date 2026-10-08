import React from "react";
import { Container } from "@/components/ui/Container";
import { Heading } from "@/components/ui/Heading";
import { Button } from "@/components/ui/Button";
import { Card } from "@/components/ui/Card";
import { Badge } from "@/components/ui/Badge";
import { Clock, CheckCircle2, ShieldCheck, Zap } from "lucide-react";

export default function HomePage() {
  return (
    <div className="py-12 sm:py-20">
      <Container>
        {/* Hero Section */}
        <div className="text-center max-w-3xl mx-auto">
          <div className="inline-flex items-center space-x-2 px-3 py-1 rounded-full bg-blue-50 dark:bg-blue-950/60 border border-blue-200 dark:border-blue-800 text-brand-primary dark:text-blue-300 text-xs font-semibold mb-6">
            <Zap className="w-3.5 h-3.5" />
            <span>AI-Powered Opportunity Capture & Follow-Through</span>
          </div>

          <Heading level={1} className="text-4xl sm:text-5xl font-extrabold tracking-tight">
            Capture it. <span className="text-brand-primary">We&apos;ll remember.</span>
          </Heading>

          <p className="mt-4 text-lg text-content-secondary dark:text-content-dark-secondary">
            Never miss what matters. Turn fleeting discoveries into structured, proactive follow-through in less than 200 milliseconds.
          </p>

          <div className="mt-8 flex flex-wrap justify-center gap-4">
            <Button variant="primary" size="md">
              Explore Opportunities
            </Button>
            <Button variant="secondary" size="md">
              View Design System
            </Button>
          </div>
        </div>

        {/* Core Interaction Flow */}
        <section aria-labelledby="interaction-heading" className="mt-20">
          <div className="text-center mb-10">
            <h2 id="interaction-heading" className="text-xs font-bold tracking-widest text-brand-primary uppercase">
              The Seven-Step Transformation
            </h2>
            <p className="mt-2 text-2xl font-bold text-content-primary dark:text-content-dark-primary">
              From Forgetting to Execution
            </p>
          </div>

          <div className="grid grid-cols-2 sm:grid-cols-4 lg:grid-cols-7 gap-3 text-center">
            {[
              { step: "01", name: "SEE", desc: "Discover online" },
              { step: "02", name: "SHARE", desc: "Native Android share" },
              { step: "03", name: "UNDERSTAND", desc: "AI metadata extraction" },
              { step: "04", name: "SAVE", desc: "Offline Room storage" },
              { step: "05", name: "REMIND", desc: "Proactive cadences" },
              { step: "06", name: "ACT", desc: "Apply with 1-tap" },
              { step: "07", name: "COMPLETE", desc: "Opportunity achieved" },
            ].map((item) => (
              <div
                key={item.step}
                className="p-4 rounded-lg bg-surface-card dark:bg-surface-dark-card border border-border-subtle dark:border-border-dark-subtle"
              >
                <span className="text-xs font-mono font-bold text-brand-primary">{item.step}</span>
                <p className="font-bold text-sm mt-1 text-content-primary dark:text-content-dark-primary">{item.name}</p>
                <p className="text-[11px] text-content-secondary dark:text-content-dark-secondary mt-1">{item.desc}</p>
              </div>
            ))}
          </div>
        </section>

        {/* Design System Primitives & Sample Cards */}
        <section aria-labelledby="design-system-heading" className="mt-20">
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
                  Google Inc. • Mountain View, CA
                </p>
              </div>
              <div className="mt-6 pt-4 border-t border-border-subtle dark:border-border-dark-subtle flex items-center justify-between">
                <span className="text-[11px] font-medium text-content-muted">Confidence: 98%</span>
                <Button variant="primary" size="sm">
                  Apply Now
                </Button>
              </div>
            </Card>

            {/* Card 2: Hackathon */}
            <Card variant="opportunity" className="flex flex-col justify-between">
              <div>
                <div className="flex items-center justify-between mb-3">
                  <Badge type="HACKATHON" />
                  <span className="inline-flex items-center text-xs font-semibold text-status-success">
                    <Clock className="w-3.5 h-3.5 mr-1" />
                    14 days left
                  </span>
                </div>
                <h3 className="font-bold text-base text-content-primary dark:text-content-dark-primary">
                  HackMIT 2026 — Global Collegiate Hackathon
                </h3>
                <p className="text-xs text-content-secondary dark:text-content-dark-secondary mt-1">
                  MIT TechX • Cambridge, MA
                </p>
              </div>
              <div className="mt-6 pt-4 border-t border-border-subtle dark:border-border-dark-subtle flex items-center justify-between">
                <span className="text-[11px] font-medium text-content-muted">Confidence: 94%</span>
                <Button variant="secondary" size="sm">
                  Set Reminder
                </Button>
              </div>
            </Card>

            {/* Card 3: Scholarship */}
            <Card variant="opportunity" className="flex flex-col justify-between">
              <div>
                <div className="flex items-center justify-between mb-3">
                  <Badge type="SCHOLARSHIP" />
                  <span className="inline-flex items-center text-xs font-semibold text-urgency-danger">
                    <Clock className="w-3.5 h-3.5 mr-1" />
                    48 hours left
                  </span>
                </div>
                <h3 className="font-bold text-base text-content-primary dark:text-content-dark-primary">
                  Generation Google Scholarship (APAC)
                </h3>
                <p className="text-xs text-content-secondary dark:text-content-dark-secondary mt-1">
                  Google for Education • Global
                </p>
              </div>
              <div className="mt-6 pt-4 border-t border-border-subtle dark:border-border-dark-subtle flex items-center justify-between">
                <span className="text-[11px] font-medium text-content-muted">Confidence: 99%</span>
                <Button variant="danger" size="sm">
                  Urgent Action
                </Button>
              </div>
            </Card>
          </div>
        </section>

        {/* Architectural Readiness Status */}
        <section aria-labelledby="readiness-heading" className="mt-16 p-6 rounded-xl bg-surface-card dark:bg-surface-dark-card border border-border-subtle dark:border-border-dark-subtle">
          <div className="flex items-center space-x-3 mb-4">
            <ShieldCheck className="w-5 h-5 text-status-success" />
            <h3 id="readiness-heading" className="text-base font-bold text-content-primary dark:text-content-dark-primary">
              Phase 01 Platform Foundations Verified
            </h3>
          </div>
          <div className="grid grid-cols-1 sm:grid-cols-4 gap-4 text-xs">
            <div className="p-3 rounded-lg bg-surface-base dark:bg-surface-dark-base border border-border-subtle dark:border-border-dark-subtle">
              <span className="font-bold text-content-primary dark:text-content-dark-primary block">Android Client</span>
              <span className="text-status-success font-medium">SDK 35 (Pure Java) • Verified</span>
            </div>
            <div className="p-3 rounded-lg bg-surface-base dark:bg-surface-dark-base border border-border-subtle dark:border-border-dark-subtle">
              <span className="font-bold text-content-primary dark:text-content-dark-primary block">Backend Core</span>
              <span className="text-status-success font-medium">Spring Boot 3.4 (Java 21) • Verified</span>
            </div>
            <div className="p-3 rounded-lg bg-surface-base dark:bg-surface-dark-base border border-border-subtle dark:border-border-dark-subtle">
              <span className="font-bold text-content-primary dark:text-content-dark-primary block">AI Service</span>
              <span className="text-status-success font-medium">FastAPI (Python 3.13) • Verified</span>
            </div>
            <div className="p-3 rounded-lg bg-surface-base dark:bg-surface-dark-base border border-border-subtle dark:border-border-dark-subtle">
              <span className="font-bold text-content-primary dark:text-content-dark-primary block">Web Platform</span>
              <span className="text-status-success font-medium">Next.js 15 (App Router) • Verified</span>
            </div>
          </div>
        </section>
      </Container>
    </div>
  );
}
