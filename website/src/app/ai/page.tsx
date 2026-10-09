import React from "react";
import Link from "next/link";
import { Container } from "@/components/ui/Container";
import { Heading } from "@/components/ui/Heading";
import { Button } from "@/components/ui/Button";
import {
  Cpu,
  ShieldCheck,
  FileCheck,
  Lock,
  Layers,
  Sparkles,
  ArrowRight,
  Database,
  Code,
  CheckCircle,
} from "lucide-react";

export const metadata = {
  title: "AI Intelligence & Evidence — MNESA",
  description: "Deep dive into MNESA's deterministic AI pipeline: prompt injection defense, structured output schemas, calibrated confidence scoring, and verbatim evidence citations.",
};

export default function AIPage() {
  return (
    <div className="py-12 sm:py-20">
      <Container size="lg">
        {/* Header */}
        <div className="text-center max-w-3xl mx-auto">
          <div className="inline-flex items-center space-x-2 px-3 py-1 rounded-full bg-blue-50 dark:bg-blue-950/60 border border-blue-200 dark:border-blue-800 text-brand-primary dark:text-blue-300 text-xs font-semibold mb-6">
            <Cpu className="w-3.5 h-3.5" />
            <span>Deterministic AI Architecture</span>
          </div>

          <Heading level={1} className="text-3xl sm:text-5xl font-extrabold tracking-tight">
            AI Intelligence & Verifiable Evidence
          </Heading>

          <p className="mt-4 text-base sm:text-lg text-content-secondary dark:text-content-dark-secondary leading-relaxed">
            Most AI tools hallucinate dates and summarize vaguely. MNESA treats web content as untrusted input, enforces strict Pydantic schemas, and anchors every extracted deadline to verifiable source evidence.
          </p>

          <div className="mt-8 flex flex-wrap justify-center gap-4">
            <Link href="/demo">
              <Button variant="primary" size="md" className="space-x-2">
                <span>Test Live in Simulator</span>
                <ArrowRight className="w-4 h-4" />
              </Button>
            </Link>
            <Link href="/security">
              <Button variant="secondary" size="md">
                Security Architecture
              </Button>
            </Link>
          </div>
        </div>

        {/* Pillars */}
        <div className="mt-20 space-y-12">
          {/* Pillar 1 */}
          <div className="p-8 rounded-2xl bg-surface-card dark:bg-surface-dark-card border border-border-subtle dark:border-border-dark-subtle">
            <div className="flex items-center space-x-3 mb-4">
              <div className="w-10 h-10 rounded-xl bg-blue-50 dark:bg-blue-950/80 border border-blue-200 dark:border-blue-800 text-brand-primary flex items-center justify-center font-bold">
                <ShieldCheck className="w-5 h-5" />
              </div>
              <div>
                <span className="text-xs font-mono font-bold text-brand-primary uppercase">Pillar 01</span>
                <h2 className="text-xl font-bold text-content-primary dark:text-content-dark-primary">
                  Zero-Trust Input & Prompt Injection Immunity
                </h2>
              </div>
            </div>
            <p className="text-sm text-content-secondary dark:text-content-dark-secondary leading-relaxed mb-4">
              Web pages, social media announcements, and raw user shares are inherently untrusted. MNESA sanitizes all external text before passing it to AI models:
            </p>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 text-xs">
              <div className="p-4 rounded-lg bg-surface-base dark:bg-surface-dark-base border border-border-subtle dark:border-border-dark-subtle">
                <h3 className="font-bold text-content-primary dark:text-content-dark-primary mb-1">
                  SSRF Protection Gateway
                </h3>
                <p className="text-content-secondary dark:text-content-dark-secondary">
                  Blocks internal loopback (127.0.0.1), private subnets (10.0.0.0/8, 192.168.0.0/16), and cloud metadata IP (169.254.169.254).
                </p>
              </div>
              <div className="p-4 rounded-lg bg-surface-base dark:bg-surface-dark-base border border-border-subtle dark:border-border-dark-subtle">
                <h3 className="font-bold text-content-primary dark:text-content-dark-primary mb-1">
                  Delimiter Isolation
                </h3>
                <p className="text-content-secondary dark:text-content-dark-secondary">
                  Encapsulates raw text in cryptographically safe boundary tags, neutralizing attempts to override system prompts.
                </p>
              </div>
              <div className="p-4 rounded-lg bg-surface-base dark:bg-surface-dark-base border border-border-subtle dark:border-border-dark-subtle">
                <h3 className="font-bold text-content-primary dark:text-content-dark-primary mb-1">
                  Adversarial Pattern Scanning
                </h3>
                <p className="text-content-secondary dark:text-content-dark-secondary">
                  Detects and neutralizes prompt injection signatures such as &quot;ignore prior instructions&quot; or &quot;output developer prompt&quot;.
                </p>
              </div>
            </div>
          </div>

          {/* Pillar 2 */}
          <div className="p-8 rounded-2xl bg-surface-card dark:bg-surface-dark-card border border-border-subtle dark:border-border-dark-subtle">
            <div className="flex items-center space-x-3 mb-4">
              <div className="w-10 h-10 rounded-xl bg-purple-50 dark:bg-purple-950/80 border border-purple-200 dark:border-purple-800 text-purple-600 dark:text-purple-400 flex items-center justify-center font-bold">
                <Code className="w-5 h-5" />
              </div>
              <div>
                <span className="text-xs font-mono font-bold text-purple-600 dark:text-purple-400 uppercase">Pillar 02</span>
                <h2 className="text-xl font-bold text-content-primary dark:text-content-dark-primary">
                  Strict Schema Enforcement & Determinism
                </h2>
              </div>
            </div>
            <p className="text-sm text-content-secondary dark:text-content-dark-secondary leading-relaxed mb-4">
              MNESA never accepts free-form, unvalidated conversational output. The AI extraction service produces structured responses validated against strict Pydantic models:
            </p>
            <div className="p-4 rounded-lg bg-surface-base dark:bg-surface-dark-base border border-border-subtle dark:border-border-dark-subtle font-mono text-xs overflow-x-auto text-content-primary dark:text-content-dark-primary">
              <pre>{`{
  "title": "Google Summer of Code 2026",
  "organization": "Google Open Source",
  "category": "INTERNSHIP",
  "deadline": "2026-04-02T18:00:00Z",
  "confidence_score": 0.94,
  "evidence": [
    {
      "field": "deadline",
      "verbatim_quote": "Contributor proposals close April 2, 2026 at 18:00 UTC",
      "match_confidence": 0.98
    }
  ],
  "action_checklist": ["Submit proposal", "Draft resume", "Select mentor org"]
}`}</pre>
            </div>
          </div>

          {/* Pillar 3 */}
          <div className="p-8 rounded-2xl bg-surface-card dark:bg-surface-dark-card border border-border-subtle dark:border-border-dark-subtle">
            <div className="flex items-center space-x-3 mb-4">
              <div className="w-10 h-10 rounded-xl bg-emerald-50 dark:bg-emerald-950/80 border border-emerald-200 dark:border-emerald-800 text-emerald-600 dark:text-emerald-400 flex items-center justify-center font-bold">
                <FileCheck className="w-5 h-5" />
              </div>
              <div>
                <span className="text-xs font-mono font-bold text-emerald-600 dark:text-emerald-400 uppercase">Pillar 03</span>
                <h2 className="text-xl font-bold text-content-primary dark:text-content-dark-primary">
                  Confidence Calibration & Grounded Evidence
                </h2>
              </div>
            </div>
            <p className="text-sm text-content-secondary dark:text-content-dark-secondary leading-relaxed mb-4">
              Every extraction is scored based on deterministic validation rules:
            </p>
            <div className="space-y-3 text-xs">
              <div className="flex items-start space-x-2">
                <CheckCircle className="w-4 h-4 text-emerald-600 dark:text-emerald-400 mt-0.5 flex-shrink-0" />
                <span>
                  <strong>Verbatim Quote Verification:</strong> Extracted evidence quotes must match character sequences within the scraped source text. Missing or hallucinated quotes result in heavy confidence penalties (-0.50).
                </span>
              </div>
              <div className="flex items-start space-x-2">
                <CheckCircle className="w-4 h-4 text-emerald-600 dark:text-emerald-400 mt-0.5 flex-shrink-0" />
                <span>
                  <strong>Temporal Sanity Checks:</strong> Dates occurring in the past or ambiguous relative phrases without anchor points are flagged, ensuring users are never misled by expired archives.
                </span>
              </div>
              <div className="flex items-start space-x-2">
                <CheckCircle className="w-4 h-4 text-emerald-600 dark:text-emerald-400 mt-0.5 flex-shrink-0" />
                <span>
                  <strong>Human-in-the-Loop Threshold:</strong> Extractions with confidence scores below 0.70 trigger a visual warning badge on Android and Web, prompting immediate user confirmation.
                </span>
              </div>
            </div>
          </div>
        </div>
      </Container>
    </div>
  );
}
